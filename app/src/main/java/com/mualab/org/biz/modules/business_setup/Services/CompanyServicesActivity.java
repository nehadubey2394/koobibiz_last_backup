package com.mualab.org.biz.modules.business_setup.Services;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.business_setup.Services.adapter.CompanyServicesAdapter;
import com.mualab.org.biz.modules.business_setup.new_add_staff.AddStaffServiceDetailActivity;
import com.mualab.org.biz.modules.profile_setup.activity.ServiceDetailActivity;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;
import com.mualab.org.biz.modules.profile_setup.modle.AddedCategory;
import com.mualab.org.biz.modules.profile_setup.modle.MyBusinessType;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyServicesActivity extends AppCompatActivity {
    private List<Services> servicesList;
    private TextView tvNoRecord;
    private RecyclerView rvServices;
    private CompanyServicesAdapter servicesListAdapter;
    private String staffId,businessId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_services);
        initView();
    }

    private void initView() {

        Intent intent = getIntent();
        if (intent!=null){
            staffId =  intent.getStringExtra("staffId");
            businessId =  intent.getStringExtra("businessId");
        }

        servicesList = new ArrayList<>();
        servicesListAdapter = new CompanyServicesAdapter(CompanyServicesActivity.this,
                servicesList, new CompanyServicesAdapter.onClickListener() {
            @Override
            public void onClick(int pos) {
                Services item = servicesList.get(pos);
                Intent intent = new Intent(CompanyServicesActivity.this,ServiceDetailActivity.class);
                intent.putExtra("serviceItem",item);
                startActivity(intent);
            }
        });
        tvNoRecord = findViewById(R.id.tvNoRecord);
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.text_services));

        rvServices = findViewById(R.id.rvServices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CompanyServicesActivity.this);
        rvServices.setLayoutManager(layoutManager);
        rvServices.setAdapter(servicesListAdapter);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        apiForGetService();
    }

    private void apiForGetService(){
        Progress.hide(CompanyServicesActivity.this);
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(CompanyServicesActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetService();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("staffId", staffId);
        params.put("businessId", businessId);

        HttpTask task = new HttpTask(new HttpTask.Builder(CompanyServicesActivity.this, "staffServices",
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        try {
                            JSONObject js = new JSONObject(response);
                            String status = js.getString("status");
                            String message = js.getString("message");

                            if (status.equalsIgnoreCase("success")) {
                                tvNoRecord.setVisibility(View.GONE);
                                rvServices.setVisibility(View.VISIBLE);

                                JSONArray allServiceArray = js.getJSONArray("artistServices");

                                if (allServiceArray!=null) {
                                    for (int j=0; j<allServiceArray.length(); j++){
                                        JSONObject object = allServiceArray.getJSONObject(j);
                                        MyBusinessType businessType = new MyBusinessType();
                                        businessType.serviceId = Integer.parseInt(object.getString("serviceId"));
                                        businessType.serviceName = object.getString("serviceName");

                                        JSONArray subServiesArray = object.getJSONArray("subServies");
                                        if (subServiesArray!=null) {
                                            for (int k=0; k<subServiesArray.length(); k++){
                                                JSONObject jObj = subServiesArray.getJSONObject(k);
                                                AddedCategory subServices = new AddedCategory();
                                                subServices._id = Integer.parseInt(jObj.getString("_id"));
                                                subServices.serviceId = Integer.parseInt(jObj.getString("serviceId"));
                                                subServices.subServiceId = jObj.getString("subServiceId");
                                                subServices.subServiceName = jObj.getString("subServiceName");
                                                // services.categoryList.add(subServices);

                                                JSONArray artistservices = jObj.getJSONArray("artistservices");
                                                for (int m=0; m<artistservices.length(); m++){
                                                    Services services = new Services();
                                                    JSONObject jsonObject3 = artistservices.getJSONObject(m);
                                                    services.serviceId = businessType.serviceId;
                                                    services.bizTypeName = businessType.serviceName;
                                                    services.subserviceId = Integer.parseInt(subServices.subServiceId);
                                                    services.subserviceName = subServices.subServiceName;

                                                    services.id = Integer.parseInt(jsonObject3.getString("_id"));
                                                    services.serviceName = jsonObject3.getString("title");
                                                    services.completionTime = jsonObject3.getString("completionTime");
                                                    services.description = jsonObject3.getString("description");

                                                    services.inCallPrice = Double.parseDouble(jsonObject3.getString("inCallPrice"));
                                                    services.outCallPrice = Double.parseDouble(jsonObject3.getString("outCallPrice"));

                                                    if (services.inCallPrice!=0.0 && services.outCallPrice!=0)
                                                        services.bookingType = "Both";
                                                    else  if (services.inCallPrice!=0.0)
                                                        services.bookingType = "Incall";
                                                    else if (services.outCallPrice!=0.0)
                                                        services.bookingType = "Outcall";

                                                    servicesList.add(services);
                                                }
                                            }
                                        }
                                        // servicesList.add(services);
                                    }
                                }else {
                                    tvNoRecord.setVisibility(View.VISIBLE);
                                    rvServices.setVisibility(View.GONE);
                                }

                            }else {
                                tvNoRecord.setVisibility(View.VISIBLE);
                                rvServices.setVisibility(View.GONE);
                            }
                            shortList();

                        } catch (Exception e) {
                            Progress.hide(CompanyServicesActivity.this);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        try {
                            Helper helper = new Helper();
                            if (helper.error_Messages(error).contains("Session")) {
                                Mualab.getInstance().getSessionManager().logout();
                            }else
                                MyToast.getInstance(CompanyServicesActivity.this).showDasuAlert(helper.error_Messages(error));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }})
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        task.execute(this.getClass().getName());
    }

    private void shortList() {
        Collections.sort(servicesList, new Comparator<Services>() {

            @Override
            public int compare(Services a1, Services a2) {

                if (a1.serviceId == 0 || a2.serviceId == 0)
                    return -1;
                else {
                    Long long1 = (long) a1.serviceId;
                    Long long2 = (long) a2.serviceId;
                    return long1.compareTo(long2);
                }
            }
        });
        servicesListAdapter.notifyDataSetChanged();

        if (servicesList.size()!=0) {
            rvServices.setVisibility(View.VISIBLE);
            tvNoRecord.setVisibility(View.GONE);
        }
        else {
            rvServices.setVisibility(View.GONE);
            tvNoRecord.setVisibility(View.VISIBLE);
        }

        //if (tempServicesList.size()==0)
        // apiForGetService();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

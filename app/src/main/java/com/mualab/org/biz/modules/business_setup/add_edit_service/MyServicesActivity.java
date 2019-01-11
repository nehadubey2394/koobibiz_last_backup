package com.mualab.org.biz.modules.business_setup.add_edit_service;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.business_setup.add_edit_service.business_type.AllBusinessTypeActivity;
import com.mualab.org.biz.modules.profile_setup.adapter.AddedServicesAdapter;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;
import com.mualab.org.biz.modules.profile_setup.modle.AddedCategory;
import com.mualab.org.biz.modules.profile_setup.modle.MyBusinessType;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Constant;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyServicesActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvNoRecord,tvInCall,tvOutCall;
    private long mLastClickTime = 0;
    private RecyclerView rvServices;
    private View topLine;
    private AddedServicesAdapter servicesListAdapter;
    private List<Services>mainServicesList,tempList,inCallServiceList,outCallServiceList;
    protected User user;
    private LinearLayout tabIncall, tabOutcall;
    private String serviceType = "Incall";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_services);
        init();
    }

    private void init(){
        if(user==null) user = Mualab.getInstance().getSessionManager().getUser(); // get session object

        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.text_services));

        mainServicesList = new ArrayList<>();
        tempList = new ArrayList<>();
        inCallServiceList = new ArrayList<>();
        outCallServiceList = new ArrayList<>();

        AppCompatButton btnNext = findViewById(R.id.btnNext);
        LinearLayout llAddService = findViewById(R.id.llAddService);
        LinearLayout llAddCategory = findViewById(R.id.llAddCategory);
        tvNoRecord = findViewById(R.id.tvNoRecord);
        topLine = findViewById(R.id.topLine);

        tvInCall = findViewById(R.id.tvInCall);
        tvOutCall = findViewById(R.id.tvOutCall);
        tabIncall = findViewById(R.id.tabIncall);
        tabOutcall = findViewById(R.id.tabOutcall);

        servicesListAdapter = new AddedServicesAdapter(MyServicesActivity.this, tempList,
                new AddedServicesAdapter.onClickListener() {
                    @Override
                    public void onEditClick(int pos) {
                        Bundle args = new Bundle();
                        Intent intent = new Intent(MyServicesActivity.this,EditServicesActivity.class);
                        args.putSerializable("servicesList",(Serializable)tempList);
                        args.putSerializable("serviceItem",tempList.get(pos));
                        intent.putExtra("bundle",args);
                        startActivityForResult(intent, Constant.EDIT_SERVICE);
                    }

                    @Override
                    public void onDelClick(int pos) {

                        Services tempService = tempList.get(pos);
                        if (tempService.bookingType.equals("Both")) {
                            if (serviceType.equals("Incall")) {
                                tempService.inCallPrice = 0.0;
                            }else {
                                tempService.outCallPrice = 0.0;
                            }
                            apiEditService(tempService);

                        }else {
                            apiDeleteService(tempService,pos);
                        }
                        // deleteService(servicesList.get(pos));
                        //apiDeleteService(tempService,pos);

                    }
                });

        rvServices = findViewById(R.id.listViewServices);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MyServicesActivity.this, LinearLayoutManager.VERTICAL, false);
        rvServices.setLayoutManager(layoutManager);
//        rycRecipeList.setHasFixedSize(true);
        rvServices.setAdapter(servicesListAdapter);

        btnNext.setOnClickListener(this);
        llAddCategory.setOnClickListener(this);
        llAddService.setOnClickListener(this);
        ivHeaderBack.setOnClickListener(this);
        tabIncall.setOnClickListener(this);
        tabOutcall.setOnClickListener(this);

        if (mainServicesList.size()==0)
            apiForGetService();
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 600) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        String bizTypeId = "";
        String categoryId = "";
        switch (view.getId()){
            case R.id.btnNext:
                finish();

                break;
            case R.id.llAddCategory:
                startActivity(new Intent(MyServicesActivity.this,AllBusinessTypeActivity.class));
                //finish();

                break;

            case R.id.ivHeaderBack:
                finish();
                break;
            case R.id.llAddService:
                Bundle args = new Bundle();
                Intent intent = new Intent(MyServicesActivity.this,AddMoreServiceActivity.class);
                // intent.putExtra("bizTypeId", bizTypeId);
                //intent.putExtra("categoryId", categoryId);
                args.putSerializable("servicesList",(Serializable)mainServicesList);
                intent.putExtra("bundle",args);
                intent.putExtra("commingFrom","MyServicesActivity");
                startActivityForResult(intent, Constant.REQUEST_NEW_SERVICE);
                break;
            case R.id.tabIncall:
                tempList.clear();
                tvInCall.setTextColor(getResources().getColor(R.color.white));
                tvOutCall.setTextColor(getResources().getColor(R.color.text_color));
                tabIncall.setBackgroundResource(R.drawable.bg_tab_selected);
                tabOutcall.setBackgroundResource(R.drawable.bg_tab_unselected);
                tempList.clear();
                serviceType = "Incall";
                tempList.addAll(inCallServiceList);
                servicesListAdapter.notifyDataSetChanged();
                noDataFound();

                break;
            case R.id.tabOutcall:
                tvOutCall.setTextColor(getResources().getColor(R.color.white));
                tvInCall.setTextColor(getResources().getColor(R.color.text_color));
                tabOutcall.setBackgroundResource(R.drawable.bg_second_tab_selected);
                tabIncall.setBackgroundResource(R.drawable.bg_second_tab_unselected);
                tempList.clear();
                serviceType = "Outcall";
                tempList.addAll(outCallServiceList);
                servicesListAdapter.notifyDataSetChanged();
                noDataFound();

                break;
        }
    }

    private void  noDataFound(){
        if (tempList.size()!=0) {
            rvServices.setVisibility(View.VISIBLE);
            tvNoRecord.setVisibility(View.GONE);
        }
        else {
            rvServices.setVisibility(View.GONE);
            tvNoRecord.setVisibility(View.VISIBLE);
        }
    }

    private void apiForGetService(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyServicesActivity.this, new NoConnectionDialog.Listner() {
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
        params.put("artistId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(MyServicesActivity.this, "artistService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        mainServicesList.clear();
                        inCallServiceList.clear();
                        outCallServiceList.clear();
                        tempList.clear();

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
                                            services.bookingType = jsonObject3.getString("bookingType");
                                            services.completionTime = jsonObject3.getString("completionTime");
                                            services.description = jsonObject3.getString("description");
                                            services.inCallPrice = Double.parseDouble(jsonObject3.getString("inCallPrice"));
                                            services.outCallPrice = Double.parseDouble(jsonObject3.getString("outCallPrice"));
                                            mainServicesList.add(services);
                                        }
                                    }
                                }
                                // mainServicesList.add(services);
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
                    Progress.hide(MyServicesActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try{
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")){
                        Mualab.getInstance().getSessionManager().logout();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }})
                .setAuthToken(user.authToken)
                .setProgress(true).setRetryPolicy(10000, 0, 1f)
                .setBody(params,HttpTask.ContentType.APPLICATION_JSON));
        task.execute(this.getClass().getName());
    }

    private void apiDeleteService(final Services services, final int pos) {
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyServicesActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiDeleteService(services,pos);
                    }
                }
            }).show();
        }

        //  pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();
        body.put("id", String.valueOf(services.id));


        HttpTask task = new HttpTask(new HttpTask.Builder(MyServicesActivity.this,
                "deleteService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    Progress.hide(MyServicesActivity.this);
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        tempList.remove(pos);
                        servicesListAdapter.notifyItemRemoved(pos);

                        for (int i=0;i<mainServicesList.size();i++){
                            if (mainServicesList.get(i).serviceId==services.serviceId) {
                                mainServicesList.remove(i);
                                break;
                            }
                        }
                        if (serviceType.equals("Incall")) {
                            for (int i = 0; i < inCallServiceList.size(); i++) {
                                if (inCallServiceList.get(i).serviceId == services.serviceId) {
                                    inCallServiceList.remove(i);
                                    break;
                                }
                            }
                        }else {
                            for (int i = 0; i < outCallServiceList.size(); i++) {
                                if (outCallServiceList.get(i).serviceId == services.serviceId) {
                                    outCallServiceList.remove(i);
                                    break;
                                }
                            }
                        }
                        MyToast.getInstance(MyServicesActivity.this).showDasuAlert(message);
                    }else {
                        MyToast.getInstance(MyServicesActivity.this).showDasuAlert(message);
                    }
                    //    pbLoder.setVisibility(View.GONE);


                }catch (Exception e) {
                    Progress.hide(MyServicesActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try {
                    Progress.hide(MyServicesActivity.this);
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                    }else
                        MyToast.getInstance(MyServicesActivity.this).showDasuAlert(helper.error_Messages(error));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        })
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setParam(body));

        task.execute(MyServicesActivity.class.getName());
    }

    private void apiEditService(final Services services) {
        User user = Mualab.getInstance().getSessionManager().getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyServicesActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiEditService(services);
                    }
                }
            }).show();
        }

        //  pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();
        body.put("serviceId", String.valueOf(services.serviceId));
        body.put("subserviceId", String.valueOf(services.subserviceId));
        body.put("title", services.serviceName);
        body.put("description", services.description);
        body.put("inCallPrice", String.valueOf(services.inCallPrice));
        body.put("outCallPrice", String.valueOf(services.outCallPrice));
        body.put("completionTime", services.completionTime);
        body.put("id", String.valueOf(services.id));


        HttpTask task = new HttpTask(new HttpTask.Builder(MyServicesActivity.this,
                "addService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    Progress.hide(MyServicesActivity.this);
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        MyToast.getInstance(MyServicesActivity.this).showDasuAlert("Service deleted successfully");
                        apiForGetService();
                    }else {
                        MyToast.getInstance(MyServicesActivity.this).showDasuAlert(message);
                    }
                    //    pbLoder.setVisibility(View.GONE);


                }catch (Exception e) {
                    Progress.hide(MyServicesActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try {
                    Progress.hide(MyServicesActivity.this);
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                    }else
                        MyToast.getInstance(MyServicesActivity.this).showDasuAlert(helper.error_Messages(error));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        })
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setParam(body));

        task.execute(AddMoreServiceActivity.class.getName());
    }

    private void shortList() {
        Collections.sort(mainServicesList, new Comparator<Services>() {

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

        for (Services services : mainServicesList){
            switch (services.bookingType) {
                case "Both":
                    try {
                        Services clonedEmp = (Services) services.clone();
                        clonedEmp.tempBookingType = "Incall";
                        inCallServiceList.add(clonedEmp);
                        services.tempBookingType = "Outcall";
                        outCallServiceList.add(services);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }

                    break;
                case "Incall":
                    services.tempBookingType = "Incall";
                    inCallServiceList.add(services);
                    break;
                case "Outcall":
                    services.tempBookingType = "Outcall";
                    outCallServiceList.add(services);
                    break;
            }
        }
        if (serviceType.equals("Incall"))
            tempList.addAll(inCallServiceList);
        else
            tempList.addAll(outCallServiceList);

        servicesListAdapter.notifyDataSetChanged();

        noDataFound();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQUEST_NEW_SERVICE:
                    apiForGetService();
                    break;

                case Constant.EDIT_SERVICE:
                    apiForGetService();
                    break;
            }
        }

    }

}

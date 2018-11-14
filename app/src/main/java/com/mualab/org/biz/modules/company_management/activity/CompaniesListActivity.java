package com.mualab.org.biz.modules.company_management.activity;

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
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.model.booking.Company;
import com.mualab.org.biz.model.company_management.ComapnySelectedServices;
import com.mualab.org.biz.model.company_management.CompanyDetail;
import com.mualab.org.biz.modules.booking.adapter.CompanyListAdapter;
import com.mualab.org.biz.modules.booking.listner.StaffSelectionListener;
import com.mualab.org.biz.modules.company_management.adapter.CompaniesListAdapter;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.StatusBarUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompaniesListActivity extends AppCompatActivity implements StaffSelectionListener {
    private TextView tvNoDataFound;
    private RecyclerView rvAllCompany;
    private List<CompanyDetail> companyList;
    private CompaniesListAdapter companyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies_list2);
    //    StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));

        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        ivHeaderBack.setVisibility(View.VISIBLE);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(R.string.company);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initView();
    }

    private void initView(){

        companyList = new ArrayList<>();
        companyAdapter = new CompaniesListAdapter(CompaniesListActivity.this, companyList);

        rvAllCompany = findViewById(R.id.rvAllCompany);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CompaniesListActivity.this);
        rvAllCompany.setLayoutManager(layoutManager);
        rvAllCompany.setAdapter(companyAdapter);
        companyAdapter.setStaffSelectionListner(CompaniesListActivity.this);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);

        apiForCompanyDetail();
    }

    private void apiForCompanyDetail(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(CompaniesListActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForCompanyDetail();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(CompaniesListActivity.this, "companyInfo", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        companyList.clear();
                        rvAllCompany.setVisibility(View.VISIBLE);
                        tvNoDataFound.setVisibility(View.GONE);

                        JSONArray jsonArray = js.getJSONArray("businessList");
                        if (jsonArray!=null && jsonArray.length()!=0) {
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                CompanyDetail item = new CompanyDetail();
                                item._id = object.getString("_id");
                                item.artistId = object.getString("artistId");
                                item.businessId = object.getString("businessId");
                                item.holiday = object.getString("holiday");
                                item.job = object.getString("job");
                                item.mediaAccess = object.getString("mediaAccess");
                                item.businessName = object.getString("businessName");
                                item.profileImage = object.getString("profileImage");
                                item.address = object.getString("address");
                                item.userName = object.getString("userName");

                                JSONArray staffHoursArray = object.getJSONArray("staffHours");
                                if (staffHoursArray!=null && staffHoursArray.length()!=0) {
                                    for (int j=0; j<staffHoursArray.length(); j++){
                                        JSONObject object2 = staffHoursArray.getJSONObject(j);
                                        BusinessDayForStaff item2 = new BusinessDayForStaff();
                                        item2.day = Integer.parseInt(object2.getString("day"));
                                        item2.endTime = object2.getString("endTime");
                                        item2.startTime = object2.getString("startTime");
                                        item.staffHoursList.add(item2);
                                    }
                                }

                                JSONArray staffServiceArray = object.getJSONArray("staffService");
                                for (int k=0; k<staffServiceArray.length(); k++){
                                    JSONObject object3 = staffServiceArray.getJSONObject(k);
                                    Gson gson = new Gson();
                                    ComapnySelectedServices item3 = gson.fromJson(String.valueOf(object3), ComapnySelectedServices.class);
                                    item.staffService.add(item3);
                                }

                                companyList.add(item);
                            }
                        }else {
                            rvAllCompany.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                        companyAdapter.notifyDataSetChanged();
                    }else {
                        rvAllCompany.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    Progress.hide(CompaniesListActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try{
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")){
                        Mualab.getInstance().getSessionManager().logout();
                        // MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }})
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    @Override
    public void onStaffSelect(int position, String staffId) {
        CompanyDetail company = companyList.get(position);
        Intent intent = new Intent(CompaniesListActivity.this,CompanyDetailActivity.class );
        Bundle args = new Bundle();
        args.putSerializable("companyDetail",company );
        intent.putExtra("BUNDLE", args);
        startActivity(intent);
    }
}

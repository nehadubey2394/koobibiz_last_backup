package com.mualab.org.biz.modules.booking.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
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
import com.mualab.org.biz.model.booking.Company;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.ArtistServices;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.modules.booking.adapter.CompanyListAdapter;
import com.mualab.org.biz.modules.booking.listner.StaffSelectionListener;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyListActivity extends AppCompatActivity implements StaffSelectionListener {
    private TextView tvNoDataFound;
    private RecyclerView rvAllCompany;
    private List<Company> companyList;
    private CompanyListAdapter companyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);

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
        companyAdapter = new CompanyListAdapter(CompanyListActivity.this, companyList);

        rvAllCompany = findViewById(R.id.rvAllCompany);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CompanyListActivity.this);
        rvAllCompany.setLayoutManager(layoutManager);
        rvAllCompany.setAdapter(companyAdapter);
        companyAdapter.setStaffSelectionListner(CompanyListActivity.this);
        tvNoDataFound = findViewById(R.id.tvNoDataFound);
        AppCompatButton btnClear = findViewById(R.id.btnClear);

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("businessId", "");
                intent.putExtra("businessName", "");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        apiForGetArtistStaff();
    }

    private void apiForGetArtistStaff(){
        Session session = Mualab.getInstance().getSessionManager();
        final User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(CompanyListActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetArtistStaff();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(CompanyListActivity.this, "companyInfo", new HttpResponceListner.Listener() {
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
                                Company item = new Company();
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
                                    ArtistServices item3 = gson.fromJson(String.valueOf(object3), ArtistServices.class);
                                    item3.setSelected(true);
                                    item.staffService.add(item3);
                                }
                                companyList.add(item);
                            }
                        }else {
                            rvAllCompany.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }

                        Company item1 = new Company();
                        item1.profileImage = user.profileImage;
                        item1.businessName = "My Booking";
                        companyList.add(item1);
                        companyAdapter.notifyDataSetChanged();
                    }else {
                        rvAllCompany.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    Progress.hide(CompanyListActivity.this);
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
        String companyName = companyList.get(position).businessName;
        if (staffId==null){
            staffId = "";
            companyName = "";
        }
        Intent intent = new Intent();
        intent.putExtra("businessId", staffId);
        intent.putExtra("businessName",companyName );
        setResult(RESULT_OK, intent);
        finish();
    }
}

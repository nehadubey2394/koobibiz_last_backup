package com.mualab.org.biz.modules.business_setup.business_info;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.my_profile.model.UserProfileData;
import com.mualab.org.biz.modules.profile_setup.activity.BreakTimeActivity;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BusinessInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvBookingType,tvEmail,tvCountryCode,tvContactNo,tvBizAddress,
            tvRadius,tvInCallBreakTime,tvOutCallBreakTime;
    private EditText etBusinessName;
    private String businessId;
    private UserProfileData profileData;
    private RelativeLayout rlRadius;
    private View radiusLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_info);
        initView();
    }

    private void initView() {
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.business_info));

        businessId = getIntent().getStringExtra("businessId");

        RelativeLayout rlserviceBreak = findViewById(R.id.rlserviceBreak);

        tvBookingType = findViewById(R.id.tvBookingType);
        etBusinessName = findViewById(R.id.etBusinessName);
        etBusinessName.setEnabled(false);
        tvEmail = findViewById(R.id.tvEmail);
        tvCountryCode = findViewById(R.id.tvCountryCode);
        tvContactNo = findViewById(R.id.tvContactNo);
        tvBizAddress = findViewById(R.id.tvBizAddress);
        tvRadius = findViewById(R.id.tvRadius);
        tvInCallBreakTime = findViewById(R.id.tvInCallBreakTime);
        tvOutCallBreakTime = findViewById(R.id.tvOutCallBreakTime);
        rlRadius = findViewById(R.id.rlRadius);
        radiusLine = findViewById(R.id.radiusLine);

        ivHeaderBack.setOnClickListener(this);
        rlserviceBreak.setOnClickListener(this);

        apiForGetProfile();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivHeaderBack:
                finish();
                break;

            case R.id.rlserviceBreak:
                Intent intent5 = new Intent(BusinessInfoActivity.this,
                        BreakTimeActivity.class);
                intent5.putExtra("profileData",profileData);
                intent5.putExtra("comingFrom","BusinessInfoActivity");
                startActivity(intent5);
                break;
        }
    }

    private void apiForGetProfile() {
        User user = Mualab.getInstance().getSessionManager().getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(BusinessInfoActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiForGetProfile();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();

        params.put("userId", businessId);
        params.put("loginUserId", String.valueOf(user.id));
        //  params.put("viewBy", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(BusinessInfoActivity.this,
                "getProfile", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                Progress.hide(BusinessInfoActivity.this);
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    Progress.hide(BusinessInfoActivity.this);
                    if (status.equalsIgnoreCase("success")) {
                        JSONArray userDetail = js.getJSONArray("userDetail");
                        JSONObject object = userDetail.getJSONObject(0);
                        Gson gson = new Gson();

                        profileData = gson.fromJson(String.valueOf(object), UserProfileData.class);

                        setProfileData(profileData);

                    } else {
                        MyToast.getInstance(BusinessInfoActivity.this).showDasuAlert(message);
                    }

                } catch (Exception e) {
                    Progress.hide(BusinessInfoActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Progress.hide(BusinessInfoActivity.this);
                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                        //      MyToast.getInstance(BookingActivity.this).showSmallCustomToast(helper.error_Messages(error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        })
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(getClass().getName());
    }

    private void setProfileData(UserProfileData profileData) {
        if (profileData != null) {
            tvEmail.setText(profileData.email);
            etBusinessName.setText(profileData.businessName);
            tvCountryCode.setText(profileData.countryCode);
            tvContactNo.setText(profileData.contactNo);
            tvBizAddress.setText(profileData.address);
            tvRadius.setText(profileData.radius + " Miles");

            if (profileData.radius.equals("0")) {
                rlRadius.setVisibility(View.GONE);
                radiusLine.setVisibility(View.GONE);
            }
            else {
                rlRadius.setVisibility(View.VISIBLE);
                radiusLine.setVisibility(View.VISIBLE);
            }



            switch (profileData.serviceType) {
                case "1":
                    tvBookingType.setText("Incall");
                    splitTime(tvInCallBreakTime,profileData.inCallpreprationTime,"Incall");
                    break;
                case "2":
                    tvBookingType.setText("Outcall");
                    splitTime(tvOutCallBreakTime,profileData.outCallpreprationTime,"Outcall");
                    break;
                case "3":
                    tvBookingType.setText("Incall / Outcall");
                    splitTime(tvInCallBreakTime,profileData.inCallpreprationTime,"Incall");
                    splitTime(tvOutCallBreakTime,profileData.outCallpreprationTime,"Outcall");
                    break;
            }
        }
    }

    private void splitTime(TextView textView,String time,String type){
        textView.setVisibility(View.VISIBLE);
        if (time.contains(":")){
            String[] separated = time.split(":");
            String hours = separated[0]+" hr ";
            String min = separated[1]+" min";

            if (hours.equals("00 hr "))
                textView.setText(type+" - "+min);
            else if (!hours.equals("00 hr ") && min.equals("00 min"))
                textView.setText(type+" - "+hours);
            else
                textView.setText(type+" - "+hours+min);

        }

    }
}

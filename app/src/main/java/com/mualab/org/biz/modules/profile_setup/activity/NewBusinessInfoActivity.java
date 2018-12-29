package com.mualab.org.biz.modules.profile_setup.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewBusinessInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etBusinessName,etBusinessEmail,etBusinessContact;
    private String bookingType="";
    private Address bizAddress;
    private RelativeLayout rlAddress,rlAreaOfCoverage;
    private TextView tvAddressType;
    private View radiusLineView;
    private User user;
    private PreRegistrationSession bpSession;
    private boolean isHoursUpdate = false;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_business_info);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        init();
    }

    private void init(){
        BusinessProfile businessProfile =  bpSession.getBusinessProfile();
        if (businessProfile.businessDays.size()!=0)
            isHoursUpdate = true;

        user = Mualab.getInstance().getSessionManager().getUser();
        ImageView iv_back = findViewById(R.id.iv_back);
        ImageView ivBasicInfo = findViewById(R.id.ivBasicInfo);
        ivBasicInfo.setImageDrawable(getResources().getDrawable(R.drawable.active_circle_img));
        ImageView ivBusinessInfo = findViewById(R.id.ivBusinessInfo);
        ivBusinessInfo.setImageDrawable(getResources().getDrawable(R.drawable.inactive_green_circle_img));
        View line1 = findViewById(R.id.line1);
        line1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        RelativeLayout rlBookingType = findViewById(R.id.rlBookingType);
        rlAddress = findViewById(R.id.rlAddress);
        rlAreaOfCoverage = findViewById(R.id.rlAreaOfCoverage);
        radiusLineView = findViewById(R.id.radiusLineView);
        RelativeLayout rlBusinessHours = findViewById(R.id.rlBusinessHours);
        RelativeLayout rlBreak = findViewById(R.id.rlBreak);
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);
        etBusinessName = findViewById(R.id.etBusinessName);
        etBusinessEmail = findViewById(R.id.etBusinessEmail);
        etBusinessContact = findViewById(R.id.etBusinessContact);
        tvAddressType = findViewById(R.id.tvAddressType);

        etBusinessEmail.setText(user.email);
        etBusinessContact.setText(user.contactNo);
        etBusinessName.setText(user.businessName);

        if (!bpSession.getBusinessName().isEmpty())
            etBusinessName.setText(bpSession.getBusinessName());

        int serviceType = bpSession.getServiceType();
        if (serviceType==1){
            bookingType = "1";
            rlAddress.setVisibility(View.VISIBLE);
            rlAreaOfCoverage.setVisibility(View.GONE);
            radiusLineView.setVisibility(View.GONE);
            tvAddressType.setText("Enter Business Address");
        }else if (serviceType==2){
            bookingType = "2";
            rlAddress.setVisibility(View.VISIBLE);
            rlAreaOfCoverage.setVisibility(View.VISIBLE);
            radiusLineView.setVisibility(View.VISIBLE);
            tvAddressType.setText("Return Location Address");
        }else if (serviceType==3){
            bookingType = "3";
            rlAddress.setVisibility(View.VISIBLE);
            rlAreaOfCoverage.setVisibility(View.VISIBLE);
            radiusLineView.setVisibility(View.VISIBLE);
            tvAddressType.setText("Enter Business Address / Return Location");
        }

        if (!bpSession.getAddress().latitude.equals("") && !bpSession.getAddress().latitude.equals(""))
            bizAddress = bpSession.getAddress();

        iv_back.setOnClickListener(this);
        rlBookingType.setOnClickListener(this);
        rlAddress.setOnClickListener(this);
        rlBusinessHours.setOnClickListener(this);
        rlBreak.setOnClickListener(this);
        rlAreaOfCoverage.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NewBusinessInfoActivity.this,NewBusinessSetUpActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        int serviceType = bpSession.getServiceType();
        switch (view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.rlBookingType:
                Intent intent = new Intent(NewBusinessInfoActivity.this,
                        BookingTypeActivity.class);
                startActivityForResult(intent,101);
                break;

            case R.id.rlAddress:
                Intent intent2 = new Intent(NewBusinessInfoActivity.this,
                        NewAddressActivity.class);
                intent2.putExtra("commingFrom","address");
                startActivityForResult(intent2,102);
                break;

            case R.id.rlAreaOfCoverage:
                if (bizAddress!=null && !bizAddress.latitude.equals("") && !bizAddress.latitude.equals("0")){
                    Intent intent3 = new Intent(NewBusinessInfoActivity.this,
                            AddBusinessRadiusActivity.class);
                    intent3.putExtra("commingFrom","areaOfCoverage");
                    startActivityForResult(intent3,103);
                }else
                    MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert("Please enter your business address");

                break;

            case R.id.rlBusinessHours:
                Intent intent4 = new Intent(NewBusinessInfoActivity.this,
                        WorkingHoursActivity.class);
                startActivityForResult(intent4,104);
                break;

            case R.id.rlBreak:
                if (serviceType!=0){
                    Intent intent5 = new Intent(NewBusinessInfoActivity.this,
                            BreakTimeActivity.class);
                    intent5.putExtra("comingFrom","NewBusinessInfoActivity");
                    startActivity(intent5);
                }else {
                    MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert("Please select booking type");
                }

                break;

            case R.id.btnContinue:
                if (serviceType!=0){
                    BusinessProfile businessProfile =  bpSession.getBusinessProfile();
                    if (checkNotempty(etBusinessName,"Please enter business name")){
                        if (validateEmail()){
                            if (validatePhone()){
                                if (bizAddress!=null && bpSession.getAddress()!=null &&
                                        !bizAddress.latitude.equals("") && !bizAddress.latitude.equals("0")) {
                                    if (serviceType == 2 || serviceType == 3) {
                                        int i = bpSession.getRadius();
                                        if (bpSession.getRadius()!=1){
                                            if (validatePreperationTime()){
                                                updateDatatoServer();
                                            }
                                        }else
                                            MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert("Please select your area coverage");

                                    }else {
                                        if (validatePreperationTime()){
                                            updateDatatoServer();
                                        }
                                    }
                                }else
                                    MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert("Please enter your business address");

                            }
                        }
                    }

                }else {
                    MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert("Please select booking type");
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null && resultCode != 0){
            switch (requestCode){
                case 101:
                    bookingType =  data.getStringExtra("bookingType");
                    switch (bookingType) {
                        case "1":
                            rlAddress.setVisibility(View.VISIBLE);
                            rlAreaOfCoverage.setVisibility(View.GONE);
                            radiusLineView.setVisibility(View.GONE);
                            tvAddressType.setText("Enter Business Address");
                            break;
                        case "2":
                            rlAddress.setVisibility(View.VISIBLE);
                            rlAreaOfCoverage.setVisibility(View.VISIBLE);
                            radiusLineView.setVisibility(View.VISIBLE);
                            tvAddressType.setText("Return Location Address");
                            break;
                        case "3":
                            rlAddress.setVisibility(View.VISIBLE);
                            rlAreaOfCoverage.setVisibility(View.VISIBLE);
                            radiusLineView.setVisibility(View.VISIBLE);
                            tvAddressType.setText("Enter Business Address / Return Location");
                            break;
                    }
                    break;

                case 102:
                    bizAddress = (Address) data.getSerializableExtra("address");
                    break;

                case 103:

                    break;

                case 104:
                    isHoursUpdate = (boolean) data.getSerializableExtra("isHoursUpdate");
                    break;

            }
        }
    }

    private boolean checkNotempty(EditText editText,String msg) {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert(msg);
            editText.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        String email = etBusinessEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert(getString(R.string.error_email_required));
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert(getString(R.string.error_invalid_email));
            return false;
        }

        return true;
    }

    private boolean validatePhone() {
        String phone = etBusinessContact.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert(getString(R.string.error_phone_no_reuired));
            return false;
        } else if (phone.length() < 4 || phone.length()>15) {
            MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert(getString(R.string.error_phone_no_length));
            return false;
        }
        return true;
    }

    private boolean validatePreperationTime() {
        int serviceType = bpSession.getServiceType();
        BusinessProfile businessProfile =  bpSession.getBusinessProfile();
        if(businessProfile!=null && businessProfile.businessDays!=null){
            if (!isHoursUpdate){
                MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert("Please select your business operation hours");
                return false;
            }
            else if (serviceType == 1) {
                if (bpSession.getInCallPreprationTime().equals("HH:MM")){
                    MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert("Please select your break time");
                    return false;
                }

            }else  if (serviceType == 2) {
                if (bpSession.getOutCallPreprationTime().equals("HH:MM")){
                    MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert("Please select your break time");
                    return false;
                }

            }else  if (serviceType == 3) {
                if (bpSession.getInCallPreprationTime().equals("HH:MM") ||
                        bpSession.getOutCallPreprationTime().equals("HH:MM")){
                    MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert("Please select your break time");
                    return false;
                }
            }

        }else
            MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert("Please select your business operation hours");

        return true;
    }

    private boolean validateAddress() {
        // String phone = tvAddress.getText().toString().trim();
       /* if (address==null) {
            tvAddress.setText(String.format("%s*", getString(R.string.address_activity)));
            showToast(getString(R.string.error_address_required));
            return false;
        }*/
        return true;
    }

    private void updateDatatoServer(){
        BusinessProfile businessProfile =  bpSession.getBusinessProfile();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(NewBusinessInfoActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        updateDatatoServer();
                    }
                }
            }).show();
        }

        Map<String,String> body = new HashMap<>();
        body.put("radius", String.valueOf(bpSession.getRadius()));
        body.put("serviceType", String.valueOf(bpSession.getServiceType()));//1:  Incall , 2: Outcall , 3: Both
        body.put("inCallpreprationTime", bpSession.getInCallPreprationTime());
        body.put("outCallpreprationTime", bpSession.getOutCallPreprationTime());
        body.put("businessName", etBusinessName.getText().toString().trim());
        body.put("businessEmail", etBusinessEmail.getText().toString().trim());
        body.put("appType", "biz");
        body.put("businessContactNo", etBusinessContact.getText().toString().trim());
       /* body.put("address", "");
        body.put("location", "");//{"22.705138200000004","75.9090618"}
        body.put("address2", "");
        body.put("city", "");
        body.put("state", "");
        body.put("country", "");
        body.put("latitude", "");
        body.put("longitude", "");*/

        ArrayList<String> location = new ArrayList<>();
        location.add(bizAddress.latitude);
        location.add(bizAddress.longitude);

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String jsonString = gson.toJson(location);

        body.put("address", bizAddress.stAddress1);
        body.put("address2", bizAddress.stAddress1);
        body.put("city", bizAddress.city);
        body.put("state", bizAddress.state);
        body.put("country", bizAddress.country);
        body.put("businessPostCode", bizAddress.postalCode);
        body.put("latitude", bizAddress.latitude);
        body.put("longitude", bizAddress.longitude);
        body.put("location", jsonString);
        body.put("isDocument", "1");

        new HttpTask(new HttpTask.Builder(NewBusinessInfoActivity.this, "updateRecord",
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        Log.d("res:", response);
                        bpSession.updateBusinessName(etBusinessName.getText().toString().trim());
                        //  bpSession.updateRegStep(3);
                        Intent intent = new Intent(NewBusinessInfoActivity.this,MyBusinessTypeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        try {
                            Helper helper = new Helper();
                            if (helper.error_Messages(error).contains("Session")) {
                                Mualab.getInstance().getSessionManager().logout();
                            }else
                                MyToast.getInstance(NewBusinessInfoActivity.this).showDasuAlert(helper.error_Messages(error));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }})
                .setMethod(Request.Method.POST)
                .setParam(body)
                .setBodyContentType( HttpTask.ContentType.APPLICATION_JSON)
                .setBody(body)
                .setProgress(true)
                .setAuthToken(user.authToken)).execute("updateRecord");
    }

    @Override
    protected void onStop() {
        PreRegistrationSession preSession = new PreRegistrationSession(this);
        preSession.updateRegStep(2);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        PreRegistrationSession preSession = new PreRegistrationSession(this);
        preSession.updateRegStep(2);
        super.onDestroy();
    }
}

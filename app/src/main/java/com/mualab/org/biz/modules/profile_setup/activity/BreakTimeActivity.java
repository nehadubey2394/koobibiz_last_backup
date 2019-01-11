package com.mualab.org.biz.modules.profile_setup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.my_profile.model.UserProfileData;
import com.mualab.org.biz.session.PreRegistrationSession;

import views.pickerview.MyTimePickerDialog;
import views.pickerview.timepicker.TimePicker;

public class BreakTimeActivity extends AppCompatActivity implements View.OnClickListener {
    private PreRegistrationSession bpSession;
    private User user;
    private LinearLayout llInCallOutCall,llInCallTimePicker,llOutCallTimePicker;
    private TextView tvInCall,tvOutCall,tvInCallHours,tvInCallMins,tvOutCallHours,tvOutCallMins;
    private String incallPreprationTime, outcallPreprationTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_time);
        init();
    }

    private void init(){
        if(user==null) user = Mualab.getInstance().getSessionManager().getUser();
        bpSession  = Mualab.getInstance().getBusinessProfileSession();

        String comingFrom = getIntent().getStringExtra("comingFrom");

        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        ivKoobiLogo.setVisibility(View.GONE);
        ImageView iv_back = findViewById(R.id.iv_back);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        tvInCall = findViewById(R.id.tvInCall);
        tvOutCall = findViewById(R.id.tvOutCall);
        tvInCallHours = findViewById(R.id.tvInCallHours);
        tvInCallMins = findViewById(R.id.tvInCallMins);
        tvOutCallHours = findViewById(R.id.tvOutCallHours);
        tvOutCallMins = findViewById(R.id.tvOutCallMins);
        llInCallOutCall = findViewById(R.id.llInCallOutCall);
        llInCallTimePicker = findViewById(R.id.llInCallTimePicker);
        llOutCallTimePicker = findViewById(R.id.llOutCallTimePicker);
        View bottomLine = findViewById(R.id.bottomLine);
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);
        tvHeaderText.setVisibility(View.VISIBLE);
        tvHeaderText.setText("Service Break");

        BusinessProfile businessProfile =  bpSession.getBusinessProfile();

        if (comingFrom.equals("BusinessInfoActivity")) {
            UserProfileData profileData = (UserProfileData) getIntent().getSerializableExtra("profileData");

            switch (profileData.serviceType) {
                case "3": {
                    String time = profileData.outCallpreprationTime;
                    String[] timeList = time.split(":");
                    String hours = timeList[0];
                    String min = timeList[1];
                    tvOutCallHours.setText(hours);
                    tvOutCallMins.setText(min);

                    String inTime = profileData.inCallpreprationTime;
                    String[] timeList2 = inTime.split(":");
                    String inHours = timeList2[0];
                    String inMin = timeList2[1];
                    tvInCallHours.setText(inHours);
                    tvInCallMins.setText(inMin);

                    tvInCall.setTextColor(getResources().getColor(R.color.colorPrimary));
                    llInCallOutCall.setVisibility(View.VISIBLE);
                    bottomLine.setVisibility(View.VISIBLE);


                    break;
                }
                case "2": {
                    String time = profileData.outCallpreprationTime;
                    String[] timeList = time.split(":");
                    String hours = timeList[0];
                    String min = timeList[1];
                    tvOutCallHours.setText(hours);
                    tvOutCallMins.setText(min);

                    llInCallOutCall.setVisibility(View.GONE);
                    llInCallTimePicker.setVisibility(View.GONE);
                    llOutCallTimePicker.setVisibility(View.VISIBLE);
                    bottomLine.setVisibility(View.GONE);

                    break;
                }
                case "1": {
                    String inTime = profileData.inCallpreprationTime;
                    String[] timeList2 = inTime.split(":");
                    String inHours = timeList2[0];
                    String inMin = timeList2[1];
                    tvInCallHours.setText(inHours);
                    tvInCallMins.setText(inMin);

                    llInCallOutCall.setVisibility(View.GONE);
                    bottomLine.setVisibility(View.GONE);
                    llOutCallTimePicker.setVisibility(View.GONE);
                    llInCallTimePicker.setVisibility(View.VISIBLE);
                    break;
                }
            }

            btnContinue.setVisibility(View.GONE);
        }else {

            btnContinue.setVisibility(View.VISIBLE);
            llInCallTimePicker.setOnClickListener(this);
            llOutCallTimePicker.setOnClickListener(this);

            int serviceType = bpSession.getServiceType();
            if (serviceType==1){
                llInCallOutCall.setVisibility(View.GONE);
                bottomLine.setVisibility(View.GONE);
                llOutCallTimePicker.setVisibility(View.GONE);
                llInCallTimePicker.setVisibility(View.VISIBLE);

                if (!bpSession.getInCallPreprationTime().equals("HH:MM") && !bpSession.getInCallPreprationTime().equals("")){
                    String time = bpSession.getInCallPreprationTime();
                    String[] timeList = time.split(":");
                    String hours = timeList[0];
                    String min = timeList[1];
                    tvInCallHours.setText(hours);
                    tvInCallMins.setText(min);
                }

            }else if (serviceType==2){
                llInCallOutCall.setVisibility(View.GONE);
                llInCallTimePicker.setVisibility(View.GONE);
                llOutCallTimePicker.setVisibility(View.VISIBLE);
                bottomLine.setVisibility(View.GONE);

                if (!bpSession.getOutCallPreprationTime().equals("HH:MM") && !bpSession.getOutCallPreprationTime().equals("")){
                    String time = bpSession.getOutCallPreprationTime();
                    String[] timeList = time.split(":");
                    String hours = timeList[0];
                    String min = timeList[1];
                    tvOutCallHours.setText(hours);
                    tvOutCallMins.setText(min);
                }

            }else if (serviceType==3){
                tvInCall.setTextColor(getResources().getColor(R.color.colorPrimary));
                llInCallOutCall.setVisibility(View.VISIBLE);
                bottomLine.setVisibility(View.VISIBLE);

                if (!bpSession.getOutCallPreprationTime().equals("HH:MM") &&
                        !bpSession.getOutCallPreprationTime().equals("")){
                    String time = bpSession.getOutCallPreprationTime();
                    String[] timeList = time.split(":");
                    String hours = timeList[0];
                    String min = timeList[1];
                    tvOutCallHours.setText(hours);
                    tvOutCallMins.setText(min);
                }

                if (!bpSession.getInCallPreprationTime().equals("HH:MM") &&
                        !bpSession.getInCallPreprationTime().equals("")){
                    String time = bpSession.getInCallPreprationTime();
                    String[] timeList = time.split(":");
                    String hours = timeList[0];
                    String min = timeList[1];
                    tvInCallHours.setText(hours);
                    tvInCallMins.setText(min);
                }
            }
        }

        iv_back.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        tvInCall.setOnClickListener(this);
        tvOutCall.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;

            case R.id.btnContinue:

                if (validateInputValue()) {
                    bpSession.updateIncallPreprationTime(incallPreprationTime);
                    bpSession.updateOutcallPreprationTime(outcallPreprationTime);
                    Intent intent2 = new Intent();
                    setResult(RESULT_OK, intent2);
                    finish();
                }

                break;

            case R.id.tvInCall:
                llOutCallTimePicker.setVisibility(View.GONE);
                llInCallTimePicker.setVisibility(View.VISIBLE);
                tvInCall.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvOutCall.setTextColor(getResources().getColor(R.color.text_color));
                break;

            case R.id.tvOutCall:
                llOutCallTimePicker.setVisibility(View.VISIBLE);
                llInCallTimePicker.setVisibility(View.GONE);
                tvOutCall.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvInCall.setTextColor(getResources().getColor(R.color.text_color));
                break;

            case R.id.llInCallTimePicker:
                showPicker(tvInCallHours,tvInCallMins);
                break;

            case R.id.llOutCallTimePicker:
                showPicker(tvOutCallHours,tvOutCallMins);
                break;
        }
    }

    public void showPicker(final TextView tvHours,final TextView tvMins){
        int hours = 01;
        // String tmpTime = tvTime.getText().toString();
       /* if(!tmpTime.equals("HH:MM")){
            try {
                String[] arrayTime = tmpTime.split(":");
                hours = Integer.parseInt(arrayTime[0]);
                //minute = Integer.parseInt(arrayTime[1]);
            }catch (Exception ex){
                hours = 01;
            }
        }*/

        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(BreakTimeActivity.this,
                new MyTimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hours, int minute) {
                        if (hours>3){
                            MyToast.getInstance(BreakTimeActivity.this).showDasuAlert("Maximum break time limit is 3:00 hours");
                        }else {
                            tvHours.setText(String.format("%02d", hours));
                            tvMins.setText(String.format("%02d", minute));
                        }

                    }
                }, "", hours, 00,10);
       /* MyTimePickerDialog mTimePicker = new MyTimePickerDialog(BreakTimeActivity.this, new MyTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hours, int minute) {

            }
        }, "", hours, 00, 10, 3);*/
        mTimePicker.show();
    }

    private boolean validateInputValue(){
        String inCallHours = tvInCallHours.getText().toString();
        String inCallMin = tvInCallMins.getText().toString();
        String outCallHours = tvOutCallHours.getText().toString();
        String outCallMin = tvOutCallMins.getText().toString();

        int serviceType = bpSession.getServiceType();

        switch (serviceType){

            case 1:
                if(inCallHours.equals("00") && inCallMin.equals("00")){
                    showToast(R.string.select_incall_preparation_time);
                    return false;
                }else{
                    incallPreprationTime = inCallHours+":"+inCallMin;
                    outcallPreprationTime = "";
                }

                break;

            case 2:

                if(outCallHours.equals("00") && outCallMin.equals("00")){
                    showToast(R.string.select_outcall_preparation_time);
                    return false;
                }else{
                    incallPreprationTime = "";
                    outcallPreprationTime = outCallHours+":"+outCallMin;
                }

                break;

            case 3:
                if(inCallHours.equals("00") && inCallMin.equals("00")){
                    MyToast.getInstance(BreakTimeActivity.this).showDasuAlert("Please select incall time");
                    return false;
                }else if(outCallHours.equals("00") && outCallMin.equals("00")){
                    MyToast.getInstance(BreakTimeActivity.this).showDasuAlert("Please select outcall time");
                    return false;
                }
                else {
                    incallPreprationTime = inCallHours+":"+inCallMin;
                    outcallPreprationTime = outCallHours+":"+outCallMin;
                }
                break;

            default:
                MyToast.getInstance(BreakTimeActivity.this).showDasuAlert("please checked one service type");
                return false;
        }

        return true;
    }

    protected void showToast(@StringRes int id){
        MyToast.getInstance(BreakTimeActivity.this).showDasuAlert(getString(id));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

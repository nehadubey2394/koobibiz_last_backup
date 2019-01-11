package com.mualab.org.biz.modules.business_setup.business_info;

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
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.Country;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.modules.authentication.ChooseCountryActivity;
import com.mualab.org.biz.modules.profile_setup.activity.AddBusinessRadiusActivity;
import com.mualab.org.biz.modules.profile_setup.activity.BookingTypeActivity;
import com.mualab.org.biz.modules.profile_setup.activity.BreakTimeActivity;
import com.mualab.org.biz.modules.profile_setup.activity.NewAddressActivity;
import com.mualab.org.biz.modules.profile_setup.activity.WorkingHoursActivity;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditMyBusinessInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvBookingType,tvCountryCode,tvAddressType,tvRadius,tvInCallBreakTime,tvOutCallBreakTime;
    private EditText etBusinessName,etBusinessEmail,etBusinessContact;
    private RelativeLayout rlAreaOfCoverage,rlAddress;
    private View radiusLineView;
    private long mLastClickTime = 0;
    private String bookingType="";
    private Address bizAddress;
    private User user;
    private  String countryCode;
    private PreRegistrationSession bpSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_business_info);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        initView();
    }

    private void initView() {

        user = Mualab.getInstance().getSessionManager().getUser();

        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.business_info));

        RelativeLayout rlBookingType = findViewById(R.id.rlBookingType);
        RelativeLayout rlserviceBreak = findViewById(R.id.rlserviceBreak);
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);
        rlAddress = findViewById(R.id.rlAddress);
        rlAreaOfCoverage = findViewById(R.id.rlAreaOfCoverage);
        radiusLineView = findViewById(R.id.radiusLineView);

        tvBookingType = findViewById(R.id.tvBookingType);
        tvCountryCode = findViewById(R.id.tvCountryCode);
        tvAddressType = findViewById(R.id.tvBizAddress);
        tvRadius = findViewById(R.id.tvRadius);

        tvInCallBreakTime = findViewById(R.id.tvInCallBreakTime);
        tvOutCallBreakTime = findViewById(R.id.tvOutCallBreakTime);

        etBusinessName = findViewById(R.id.etBusinessName);
        etBusinessEmail = findViewById(R.id.etBusinessEmail);
        etBusinessContact = findViewById(R.id.etBusinessContact);

        getBusinessProfile();

        rlBookingType.setOnClickListener(this);
        rlAddress.setOnClickListener(this);
        rlAreaOfCoverage.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        ivHeaderBack.setOnClickListener(this);
        rlserviceBreak.setOnClickListener(this);
        tvCountryCode.setOnClickListener(this);

    }

    private void  updateView(){
        etBusinessEmail.setText(user.email);
        etBusinessContact.setText(user.contactNo);
        tvCountryCode.setText(user.countryCode);
        etBusinessName.setText(bpSession.getBusinessName());
        countryCode = user.countryCode;

        //tvRadius.setText(bpSession.getRadius());

        if (!bpSession.getBusinessName().isEmpty())
            etBusinessName.setText(bpSession.getBusinessName());
        else  if (!user.businessName.isEmpty())
            etBusinessName.setText(user.businessName);

        if (!bpSession.getBusinessEmail().isEmpty())
            etBusinessEmail.setText(bpSession.getBusinessEmail());
        else  if (!user.businessEmail.isEmpty())
            etBusinessEmail.setText(user.businessEmail);

        if (!bpSession.getBusinessContact().isEmpty())
            etBusinessContact.setText(bpSession.getBusinessContact());
        else  if (!user.businessContactNo.isEmpty())
            etBusinessContact.setText(user.businessContactNo);

        if (!bpSession.getBusinessCountryCode().isEmpty())
            tvCountryCode.setText(bpSession.getBusinessCountryCode());
        else  if (!user.country.isEmpty())
            tvCountryCode.setText(user.countryCode);

        int serviceType = bpSession.getServiceType();
        if (serviceType==1){
            bookingType = "1";
            splitTime(tvInCallBreakTime,bpSession.getInCallPreprationTime());
            tvBookingType.setText("Incall");
            rlAddress.setVisibility(View.VISIBLE);
            rlAreaOfCoverage.setVisibility(View.GONE);
            radiusLineView.setVisibility(View.GONE);
            tvAddressType.setText("Enter Business Address");
        }else if (serviceType==2){
            tvBookingType.setText("Outcall");
            splitTime(tvOutCallBreakTime,bpSession.getOutCallPreprationTime());
            bookingType = "2";
            rlAddress.setVisibility(View.VISIBLE);
            rlAreaOfCoverage.setVisibility(View.VISIBLE);
            radiusLineView.setVisibility(View.VISIBLE);
            tvAddressType.setText("Return Location Address");
        }else if (serviceType==3){
            splitTime(tvInCallBreakTime,bpSession.getInCallPreprationTime());
            splitTime(tvOutCallBreakTime,bpSession.getOutCallPreprationTime());
            tvBookingType.setText("Incall/Outcall");
            bookingType = "3";
            rlAddress.setVisibility(View.VISIBLE);
            rlAreaOfCoverage.setVisibility(View.VISIBLE);
            radiusLineView.setVisibility(View.VISIBLE);
            tvAddressType.setText("Enter Business Address / Return Location");
        }

        if (!bpSession.getAddress().latitude.equals("") && !bpSession.getAddress().latitude.equals(""))
            bizAddress = bpSession.getAddress();

        if (bizAddress!=null ) {
            tvAddressType.setText(String.format("%s",
                    TextUtils.isEmpty(bizAddress.placeName)
                            ? bizAddress.stAddress1 : bizAddress.placeName));
        }

        if (bpSession.getRadius()!=0)
            tvRadius.setText(bpSession.getRadius()+" Miles");
    }

    private void splitTime(TextView textView,String time){
        //tvInCallBreakTime.setVisibility(View.GONE);
        //tvOutCallBreakTime.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        if (time.contains(":")){
            String[] separated = time.split(":");
            String hours = separated[0];
            String min = separated[1];

            textView.setText(hours+" hours "+min+" min");

        }
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        int serviceType = bpSession.getServiceType();
        switch (view.getId()){
            case R.id.ivHeaderBack:
                finish();
                break;

            case R.id.tvCountryCode:
                startActivityForResult(new Intent(EditMyBusinessInfoActivity.this, ChooseCountryActivity.class), 1003);

                break;

            case R.id.rlBookingType:
                Intent intent = new Intent(EditMyBusinessInfoActivity.this,
                        BookingTypeActivity.class);
                startActivityForResult(intent,101);
                break;

            case R.id.rlAddress:
                Intent intent2 = new Intent(EditMyBusinessInfoActivity.this,
                        NewAddressActivity.class);
                intent2.putExtra("commingFrom","address");
                startActivityForResult(intent2,102);
                break;

            case R.id.rlAreaOfCoverage:
                if (bizAddress!=null && !bizAddress.latitude.equals("") && !bizAddress.latitude.equals("0")){
                    Intent intent3 = new Intent(EditMyBusinessInfoActivity.this,
                            AddBusinessRadiusActivity.class);
                    intent3.putExtra("commingFrom","areaOfCoverage");
                    startActivityForResult(intent3,103);
                }else
                    MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert("Please enter your business address");

                break;

            case R.id.rlBusinessHours:
                Intent intent4 = new Intent(EditMyBusinessInfoActivity.this,
                        WorkingHoursActivity.class);
                startActivityForResult(intent4,104);
                break;

            case R.id.rlserviceBreak:
                if (serviceType!=0){
                    Intent intent5 = new Intent(EditMyBusinessInfoActivity.this,
                            BreakTimeActivity.class);
                    intent5.putExtra("comingFrom","NewBusinessInfoActivity");
                    startActivityForResult(intent5,105);
                }else {
                    MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert("Please select booking type");
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
                                            MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert("Please select your area coverage");

                                    }else {
                                        if (validatePreperationTime()){
                                            updateDatatoServer();
                                        }
                                    }
                                }else
                                    MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert("Please enter your business address");

                            }
                        }
                    }

                }else {
                    MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert("Please select booking type");
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
                            tvBookingType.setText("Incall");
                            rlAddress.setVisibility(View.VISIBLE);
                            rlAreaOfCoverage.setVisibility(View.GONE);
                            radiusLineView.setVisibility(View.GONE);
                            tvAddressType.setText("Enter Business Address");
                            break;
                        case "2":
                            tvBookingType.setText("Outcall");
                            rlAddress.setVisibility(View.VISIBLE);
                            rlAreaOfCoverage.setVisibility(View.VISIBLE);
                            radiusLineView.setVisibility(View.VISIBLE);
                            tvAddressType.setText("Return Location Address");
                            break;
                        case "3":
                            tvBookingType.setText("Both");
                            rlAddress.setVisibility(View.VISIBLE);
                            rlAreaOfCoverage.setVisibility(View.VISIBLE);
                            radiusLineView.setVisibility(View.VISIBLE);
                            tvAddressType.setText("Enter Business Address / Return Location");
                            break;

                    }
                    if (bizAddress!=null ) {
                        tvAddressType.setText(String.format("%s",
                                TextUtils.isEmpty(bizAddress.placeName)
                                        ? bizAddress.stAddress1 : bizAddress.placeName));
                    }
                    break;

                case 102:
                    bizAddress = (Address) data.getSerializableExtra("address");
                    tvAddressType.setText(String.format("%s",
                            TextUtils.isEmpty(bizAddress.placeName)
                                    ? bizAddress.stAddress1 : bizAddress.placeName));

                    break;

                case 103:
                    tvRadius.setText(bpSession.getRadius()+" Miles");
                    break;

                case 1003: {
                    Country country = (Country) data.getSerializableExtra("country");
                    tvCountryCode.setText(String.format("+%s", country.phone_code));
                    countryCode = "+"+country.phone_code;
                    break;
                }

            }
        }
    }

    private boolean checkNotempty(EditText editText,String msg) {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert(msg);
            editText.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateEmail() {
        String email = etBusinessEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert(getString(R.string.error_email_required));
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert(getString(R.string.error_invalid_email));
            return false;
        }

        return true;
    }

    private boolean validatePhone() {
        String phone = etBusinessContact.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert(getString(R.string.error_phone_no_reuired));
            return false;
        } else if (phone.length() < 4 || phone.length()>15) {
            MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert(getString(R.string.error_phone_no_length));
            return false;
        }
        return true;
    }

    private boolean validatePreperationTime() {
        int serviceType = bpSession.getServiceType();
        BusinessProfile businessProfile =  bpSession.getBusinessProfile();
        if(businessProfile!=null && businessProfile.businessDays!=null){
           /* if (!isHoursUpdate){
                MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert("Please select your business operation hours");
                return false;
            }
            else */
            if (serviceType == 1) {
                if (bpSession.getInCallPreprationTime().equals("HH:MM")){
                    MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert("Please select your break time");
                    return false;
                }

            }else  if (serviceType == 2) {
                if (bpSession.getOutCallPreprationTime().equals("HH:MM")){
                    MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert("Please select your break time");
                    return false;
                }

            }else  if (serviceType == 3) {
                if (bpSession.getInCallPreprationTime().equals("HH:MM") ||
                        bpSession.getOutCallPreprationTime().equals("HH:MM")){
                    MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert("Please select your break time");
                    return false;
                }
            }

        }else
            MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert("Please select your business operation hours");

        return true;
    }

    private void updateDatatoServer(){
        BusinessProfile businessProfile =  bpSession.getBusinessProfile();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(EditMyBusinessInfoActivity.this, new NoConnectionDialog.Listner() {
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
        body.put("isDocument", "3");
        body.put("countryCode", countryCode);

        new HttpTask(new HttpTask.Builder(EditMyBusinessInfoActivity.this, "updateRecord",
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        Log.d("res:", response);
                        bpSession.updateBusinessName(etBusinessName.getText().toString().trim());
                        bpSession.updateBusinessEmail(etBusinessEmail.getText().toString().trim());
                        bpSession.updateBusinessContact(etBusinessContact.getText().toString().trim());
                        bpSession.updateBusinessCountryCode(tvCountryCode.getText().toString().trim());
                        //  bpSession.updateRegStep(3);

                        finish();
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        try {
                            Helper helper = new Helper();
                            if (helper.error_Messages(error).contains("Session")) {
                                Mualab.getInstance().getSessionManager().logout();
                            }else
                                MyToast.getInstance(EditMyBusinessInfoActivity.this).showDasuAlert(helper.error_Messages(error));
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

    private void getBusinessProfile(){
        Session session = Mualab.getInstance().getSessionManager();
        //progress_bar.setVisibility(View.VISIBLE);
        new HttpTask(new HttpTask.Builder(EditMyBusinessInfoActivity.this, "getbusinessProfile", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    // progress_bar.setVisibility(View.GONE);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("artistRecord");

                    if(jsonArray.length()>0){
                        PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
                        JSONObject obj = jsonArray.getJSONObject(0);
                        BusinessProfile bsp = new BusinessProfile();

                        bsp.address = new Address();
                        bsp.address.postalCode = obj.getString("businesspostalCode");
                        bsp.address.stAddress1 = obj.getString("address");
                        bsp.address.stAddress2 = obj.getString("address2");
                        bsp.address.city = obj.getString("city");
                        bsp.address.state = obj.getString("state");
                        bsp.address.country = obj.getString("country");
                        bsp.address.latitude = obj.getString("latitude");
                        bsp.address.longitude = obj.getString("longitude");

                        bsp.bio = obj.getString("bio");
                        bsp.bankStatus = obj.getInt("bankStatus");

                        if(obj.has("radius")){
                            String r = obj.getString("radius");
                            if(!TextUtils.isEmpty(r))
                                bsp.radius = obj.getInt("radius");
                        }

                        if(obj.has("serviceType"))
                            bsp.serviceType = obj.getInt("serviceType");
                        if(obj.has("inCallpreprationTime"))
                            bsp.inCallpreprationTime = obj.getString("inCallpreprationTime");
                        if(obj.has("outCallpreprationTime"))
                            bsp.outCallpreprationTime = obj.getString("outCallpreprationTime");

                        pSession.updateRadius(bsp.radius);
                        pSession.updateAddress(bsp.address);
                        pSession.updateOutcallPreprationTime(bsp.outCallpreprationTime);
                        pSession.updateIncallPreprationTime(bsp.inCallpreprationTime);
                        pSession.updateServiceType(bsp.serviceType);
                        pSession.updateBankStatus(bsp.bankStatus);

                        // List<BusinessDayForStaff>dayArrayList = new ArrayList<>();

                        JSONArray businessArray = obj.getJSONArray("businessHour");
                        bsp.businessDays = getBusinessDay();

                        for(int i =0; i<businessArray.length();  i++){
                            BusinessDayForStaff businessDayForStaff = new BusinessDayForStaff();
                            JSONObject objSlots = businessArray.getJSONObject(i);
                            int dayId = objSlots.getInt("day");
                            TimeSlot slot = new TimeSlot(dayId);
                            slot.id = objSlots.getInt("_id");
                            slot.startTime = objSlots.getString("startTime");
                            slot.endTime = objSlots.getString("endTime");

                            slot.minStartTime = objSlots.getString("startTime");
                            slot.maxEndTime = objSlots.getString("endTime");

                            slot.edtStartTime = objSlots.getString("startTime");

                            slot.edtEndTime = objSlots.getString("endTime");
                            slot.status = objSlots.getInt("status");

                            businessDayForStaff.day = objSlots.getInt("day");
                            businessDayForStaff.startTime = objSlots.getString("startTime");
                            businessDayForStaff.endTime = objSlots.getString("endTime");
                            bsp.dayForStaffs.add(businessDayForStaff);

                            for(BusinessDay tmpDay : bsp.businessDays){
                                if(tmpDay.dayId == dayId){
                                    tmpDay.isOpen = true;
                                    tmpDay.addTimeSlot(slot);
                                    break;
                                }
                            }
                        }

                        if(businessArray.length()>0)
                            pSession.createBusinessProfile(bsp);

                        updateView();

                       /* PreRegistrationSession bpSession = Mualab.getInstance().getBusinessProfileSession();
                        bpSession.updateRegStep(6);*/
                    }



                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {

                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                        //      MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }})
                .setMethod(Request.Method.GET).setProgress(true)
                .setAuthToken(session.getAuthToken()))
                .execute("getbusinessProfile");
    }

    private List<BusinessDay> getBusinessDay(){

        List<BusinessDay>businessDays = new ArrayList<>();

        BusinessDay day1 = new BusinessDay();
        day1.dayName = getString(R.string.monday);
        day1.dayId = 0;
        //day1.addTimeSlot(new TimeSlot(1));

        BusinessDay day2 = new BusinessDay();
        day2.dayName = getString(R.string.tuesday);
        day2.dayId = 1;

        BusinessDay day3 = new BusinessDay();
        day3.dayName = getString(R.string.wednesday);
        day3.dayId = 2;

        BusinessDay day4 = new BusinessDay();
        day4.dayName = getString(R.string.thursday);
        day4.dayId = 3;

        BusinessDay day5 = new BusinessDay();
        day5.dayName = getString(R.string.frieday);
        day5.dayId = 4;

        BusinessDay day6 = new BusinessDay();
        day6.dayName = getString(R.string.saturday);
        day6.dayId = 5;

        BusinessDay day7 = new BusinessDay();
        day7.dayName = getString(R.string.sunday);
        day7.dayId = 6;

        businessDays.add(day1);
        businessDays.add(day2);
        businessDays.add(day3);
        businessDays.add(day4);
        businessDays.add(day5);
        businessDays.add(day6);
        businessDays.add(day7 );
        return businessDays;
    }

}

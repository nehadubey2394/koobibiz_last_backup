package com.mualab.org.biz.modules.profile.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.custom_dob_picker.DatePicker;
import com.mualab.org.biz.custom_dob_picker.DatePickerDialog;
import com.mualab.org.biz.custom_dob_picker.OnDateChangedListener;
import com.mualab.org.biz.custom_dob_picker.SpinnerDatePickerDialogBuilder;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NewBusinessSetUpActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, OnDateChangedListener {
    private EditText etFirstName,etLastName;
    private TextView tvDob;
    private String dob="";
    private User user;
    private Session session;
    private int yearShow = 1980, monthShow = 0, dayShow = 1;
    private SimpleDateFormat simpleDateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_business_set_up);
        init();
    }

    private void init(){
        session = new Session(this);
        user = Mualab.getInstance().getSessionManager().getUser();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvContactNo = findViewById(R.id.tvContactNo);
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        RelativeLayout rlDob = findViewById(R.id.rlDob);
        tvDob = findViewById(R.id.tvDob);

        if (user!=null){
            etFirstName.setText(user.firstName);
            etLastName.setText(user.lastName);
            tvUserName.setText(user.userName);
            tvEmail.setText(user.email);
            dob = Utils.changeDateFormate(user.dob);
            tvDob.setText(user.dob);
            tvContactNo.setText(user.countryCode+" - "+user.contactNo);

            String tempStr = dob;
            String[] strings = tempStr.split("-");
            yearShow = Integer.parseInt(strings[0]);
            monthShow = Integer.parseInt(strings[1]);
            monthShow = monthShow-1;
            dayShow = Integer.parseInt(strings[2]);
        }

        rlDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate(yearShow, monthShow, dayShow);
                //datePicker();
            }
        });

        AppCompatButton btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkNotempty(etFirstName)
                        && checkNotempty(etLastName)
                        && checkNotemptyDob()){

                    User userInfo = new User();
                    userInfo.firstName = etFirstName.getText().toString().trim();
                    userInfo.lastName = etLastName.getText().toString().trim();
                    userInfo.fullName = userInfo.firstName.concat(" ").concat(userInfo.lastName);
                    userInfo.dob = dob;

                    apiUpdateUserInfo(userInfo);

                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void showDate(int year, int month, int day) {
        new SpinnerDatePickerDialogBuilder()
                .context(NewBusinessSetUpActivity.this)
                .callback(NewBusinessSetUpActivity.this)
                .isActivity("NewBusinessSetUpActivity")
                .dialogTheme(R.style.AppTheme)
                .defaultDate(year, month, day)
                .build()
                .show();
    }

/*
    private void datePicker(){
        // Get Current Date
        final Calendar c = GregorianCalendar.getInstance();
        int mYear = c.get(GregorianCalendar.YEAR);
        int mMonth = c.get(GregorianCalendar.MONTH);
        int mDay = c.get(GregorianCalendar.DAY_OF_MONTH);
        final int[] dayId = {c.get(GregorianCalendar.DAY_OF_WEEK) - 1};
        String weekday = new DateFormatSymbols().getShortWeekdays()[dayId[0]];

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Date date = new Date(year, monthOfYear, dayOfMonth-1);
                        dayId[0] = date.getDay()-1;
                        String sDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        dob = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        tvDob.setText(sDate);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }
*/

    private boolean checkNotemptyDob() {
        String text = tvDob.getText().toString().trim();
        if (dob==null && TextUtils.isEmpty(text) || dob.isEmpty()) {
            showToast(getString(R.string.error_required_field));
            return false;
        }
        return true;
    }

    private void showToast(String msg){
        if (!TextUtils.isEmpty(msg)){
            MyToast.getInstance(this).showDasuAlert(msg);
        }
    }

    private boolean checkNotempty(EditText editText) {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            showToast(getString(R.string.error_required_field));
            //inputLayout.setError(getString(R.string.error_required_field));
            editText.requestFocus();
            return false;
        }
        return true;
    }

    private void apiUpdateUserInfo(final User userInfo) {

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(NewBusinessSetUpActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiUpdateUserInfo(userInfo);
                    }
                }
            }).show();
        }

        // Bitmap profileImageBitmap = Utils.getBitmapFromURL(user.profileImage);
        //  pbLoder.setVisibility(View.VISIBLE);
        String deviceToken = FirebaseInstanceId.getInstance().getToken();//"android test token";
        Map<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(user.id));
        params.put("userName", user.userName);
        params.put("firstName", userInfo.firstName);
        params.put("lastName", userInfo.lastName);
        params.put("email", user.email);
        params.put("countryCode", user.countryCode);
        params.put("contactNo", user.contactNo);
        params.put("isDocument", "1");
        //   params.put("gender", user.gender);
        params.put("dob", userInfo.dob);
        params.put("deviceToken", deviceToken);
        params.put("firebaseToken", deviceToken);

        Bitmap profileImageBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.defoult_user_img);

        /*Bitmap profileImageBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.defoult_user_img);
        try {
            URL url = new URL(user.profileImage);
            profileImageBitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }
*/


     /*   params.put("address", "");
        params.put("address2", "");
        params.put("city", "");
        params.put("state", "");
        params.put("country", "");
        params.put("buildingNo", "");
        params.put("businessPostCode", "");
        params.put("latitude", "");
        params.put("longitude", "");
        params.put("userType", "artist");
        params.put("businessType", user.businessType);
        params.put("deviceType", "2");
        params.put("deviceToken", deviceToken);
        params.put("firebaseToken", deviceToken);
        params.put("appType", "biz");*/

        HttpTask task = new HttpTask(new HttpTask.Builder(this, "profileUpdate", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        Progress.hide(NewBusinessSetUpActivity.this);
                        Gson gson = new Gson();
                        JSONObject userObj = js.getJSONObject("users");
                        User user = gson.fromJson(String.valueOf(userObj), User.class);

                        Session session = new Session(NewBusinessSetUpActivity.this);
                        session.createSession(user);

                        Intent intent = new Intent(getApplicationContext(), NewBusinessInfoActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }else {
                        showToast(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Progress.hide(NewBusinessSetUpActivity.this);
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Progress.hide(NewBusinessSetUpActivity.this);
                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                    }else
                        MyToast.getInstance(NewBusinessSetUpActivity.this).showDasuAlert(helper.error_Messages(error));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).setParam(params)
                .setAuthToken(user.authToken)
                .setProgress(true)
                //.setBodyContentType(HttpTask.ContentType.FORM_DATA)
        );
        task.postImage("", null);
        // task.execute(this.getClass().getName());
    }

    @Override
    protected void onDestroy() {
        PreRegistrationSession preSession = new PreRegistrationSession(this);
        preSession.updateRegStep(1);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        PreRegistrationSession preSession = new PreRegistrationSession(this);
        preSession.updateRegStep(1);
        super.onStop();
    }

    private void getBusinessProfile(){
        //progress_bar.setVisibility(View.VISIBLE);
        new HttpTask(new HttpTask.Builder(this, "getbusinessProfile", new HttpResponceListner.Listener() {
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
                    }

                   /* if(!session.isBusinessProfileComplete()){
                        // startActivity(new Intent(MainActivity.this, BusinessProfileActivity.class));
                        startActivity(new Intent(MainActivity.this, NewBusinessSetUpActivity.class));
                        finish();
                    }else {
                        //getDeviceLocation();
                        replaceFragment(BookingsFragment.newInstance(""), false);
                    }*/
                    int index = Mualab.getInstance().getBusinessProfileSession().getStepIndex();

                    if (index==0 || index == 1)
                    {
                        startActivity(new Intent(NewBusinessSetUpActivity.this, NewBusinessSetUpActivity.class));
                        finish();
                    }else if (index==2)
                    {
                        startActivity(new Intent(NewBusinessSetUpActivity.this, NewBusinessInfoActivity.class));
                        finish();
                    }

                } catch (JSONException e) {
                    Progress.hide(NewBusinessSetUpActivity.this);
                    e.printStackTrace();
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Progress.hide(NewBusinessSetUpActivity.this);
                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                        //      MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //progress_bar.setVisibility(View.GONE);

                // Log.d("", error.getLocalizedMessage());
            }})
                .setMethod(Request.Method.GET)
                .setProgress(true)
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

    @Override
    public void onDateSet(final com.mualab.org.biz.custom_dob_picker.DatePicker view, int year, int monthOfYear, int dayOfMonth, int type, boolean isClicked) {

        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);

        if (type == 0) {
            if (!isClicked) {
                if (!tvDob.getText().toString().equals("")) {
                    Calendar calendar_new = new GregorianCalendar(yearShow, monthShow, dayShow);
                    tvDob.setText(simpleDateFormat.format(calendar_new.getTime()));
                } else tvDob.setText("");

            } else {
                Calendar calendar_new = new GregorianCalendar(yearShow, monthShow, dayShow);
                tvDob.setText(simpleDateFormat.format(calendar_new.getTime()));
            }

        } else {
            tvDob.setText(simpleDateFormat.format(calendar.getTime()));
            user.dob = simpleDateFormat.format(calendar.getTime());
            yearShow = year;
            monthShow = monthOfYear;
            dayShow = dayOfMonth;
            dob = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

        }

    }

    @Override
    public void onDateChanged(com.mualab.org.biz.custom_dob_picker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }

}

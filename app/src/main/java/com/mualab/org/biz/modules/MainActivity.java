package com.mualab.org.biz.modules;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.helper.MySnackBar;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.modules.base.BaseActivity;
import com.mualab.org.biz.modules.booking.fragments.AddFragment;
import com.mualab.org.biz.modules.booking.listner.OnRefreshListener;
import com.mualab.org.biz.modules.business_setup.BaseBusinessSetupFragment;
import com.mualab.org.biz.modules.new_booking.activity.PendingBookingActivity;
import com.mualab.org.biz.modules.new_booking.fragment.BookingsFragment;
import com.mualab.org.biz.modules.new_my_profile.MyProfileActivity;
import com.mualab.org.biz.modules.profile_setup.activity.NewBusinessSetUpActivity;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.LocationDetector;
import com.mualab.org.biz.util.PermissionUtils;
import com.mualab.org.biz.util.network.NetworkChangeReceiver;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener,OnRefreshListener {
    private Session session;
    private User user;
    private long mLastClickTime = 0;
    private ImageButton ibtnBookings,ibtnChart,ibtnAdd,ibtnNotification,ibtnUser;
    private ImageView ivHeaderBack, imgNotif, imgRight;
    private ImageView ivDropDown,ivUserProfile;
    private TextView tvHeaderTitle;
    private int clickedId = 0;
    private CardView topLayout1;
    private Spinner spBizName;
    //private TextView tv_msg;
    private String lat="",lng="";
    // private ProgressBar progress_bar;
    private boolean doubleBackToExitPressedOnce;
    private Runnable runnable;

    public void setBackButtonVisibility(int visibility){
        if(ivHeaderBack!=null)
            ivHeaderBack.setVisibility(visibility);
    }

    public void setTitle(String text){
        if(tvHeaderTitle!=null)
            tvHeaderTitle.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new Session(this);
        user = session.getUser();

        // progress_bar = findViewById(R.id.progress_bar);
        initView();

        final NoConnectionDialog network = new NoConnectionDialog(MainActivity.this, (dialog, isConnected) -> {
            if (isConnected) {
                dialog.dismiss();
            }
        });

        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.setListner(isConnected -> {
            if (isConnected) {
                network.dismiss();
            } else network.show();
        });

        initLocation();
    }

    private void initLocation() {
        new Thread(() -> {
            final boolean isPermissionAllow = PermissionUtils.checkLocationPermission(getActivity());

            runOnUiThread(() -> {
                if (isPermissionAllow && Mualab.currentLat != 0.0) {
                    updateLocation();
                }
            });
        }).start();
    }

    private void initView() {
        topLayout1 = findViewById(R.id.topLayout1);
        ibtnBookings = findViewById(R.id.ibtnBookings);
        ibtnChart = findViewById(R.id.ibtnChart);
        ibtnAdd = findViewById(R.id.ibtnAdd);
        ibtnNotification = findViewById(R.id.ibtnNotification);
        ibtnUser = findViewById(R.id.ibtnUser);
        ivHeaderBack = findViewById(R.id.ivHeaderBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        ivDropDown = findViewById(R.id.ivDropDown);
        imgNotif = findViewById(R.id.imgNotif);
        imgRight = findViewById(R.id.imgRight);

        ivUserProfile = findViewById(R.id.ivUserProfile);
        // tv_msg = findViewById(R.id.tv_msg);
        ibtnBookings.setImageResource(R.drawable.active_calender_ico);
        spBizName = findViewById(R.id.spBizName);

        if (!user.profileImage.isEmpty()) {
            Picasso.with(MainActivity.this).load(user.profileImage).placeholder(R.drawable.defoult_user_img).
                    fit().into(ivUserProfile);
        }

        setOnClickListener(ibtnBookings, ibtnChart, ibtnAdd, ibtnNotification, ibtnUser, ivHeaderBack, imgNotif, imgRight,ivUserProfile);

        int index = Mualab.getInstance().getBusinessProfileSession().getStepIndex();

        if(index==5 || !session.isBusinessProfileComplete()) {
            getBusinessProfile();
        } else ibtnBookings.callOnClick();

    }

    private void setOnClickListener(View... views) {
        for (View view : views) {
            view.setOnClickListener(this);
        }
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
                    }

                    if(!session.isBusinessProfileComplete()){
                        // startActivity(new Intent(MainActivity.this, BusinessProfileActivity.class));
                        startActivity(new Intent(MainActivity.this, NewBusinessSetUpActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                    }else {
                        PreRegistrationSession bpSession = Mualab.getInstance().getBusinessProfileSession();
                        bpSession.updateRegStep(6);
                        //getDeviceLocation();
                        //replaceFragment(BookingsFragment.newInstance(), false);
                        ibtnBookings.callOnClick();
                    }


                } catch (JSONException e) {
                    Progress.hide(MainActivity.this);
                    e.printStackTrace();
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Progress.hide(MainActivity.this);
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
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()) {
            case R.id.ivHeaderBack:
                onBackPressed();
                break;

            case R.id.ibtnBookings:
                spBizName.setVisibility(View.GONE);
                if (clickedId!=1){
                    clickedId = 1;
                    ivDropDown.setVisibility(View.GONE);
                    setInactiveTab();
                    ibtnBookings.setImageResource(R.drawable.active_calender_ico);
                    setTitle(getString(R.string.title_bookings));
                    setBackButtonVisibility(8);
                    replaceFragment(BookingsFragment.newInstance(), false);
                }
                break;

            case R.id.ibtnChart:
                if (clickedId!=2){
                    clickedId = 2;
                    ivDropDown.setVisibility(View.GONE);
                    setInactiveTab();
                    ibtnChart.setImageResource(R.drawable.active_chart_ico);
                    replaceFragment(new AddFragment(), false);
                }
                break;

            case R.id.ibtnAdd:
                if (clickedId!=3){
                    clickedId = 3;
                    ivDropDown.setVisibility(View.GONE);
                    setInactiveTab();
                    ibtnAdd.setImageResource(R.drawable.active_add_ico);
                    replaceFragment(new AddFragment(), false);
                }
                break;

            case R.id.ibtnNotification:
                if (clickedId!=4){
                    clickedId = 4;
                    ivDropDown.setVisibility(View.GONE);
                    setInactiveTab();
                    ibtnNotification.setImageResource(R.drawable.active_notification_ico);
                    replaceFragment(new AddFragment(), false);
                }
                break;

            case R.id.ibtnUser:
                if (clickedId!=5){
                    // ivDropDown.setVisibility(View.VISIBLE);
                    clickedId = 5;
                    setInactiveTab();
                    ibtnUser.setImageResource(R.drawable.active_user_ico);
                    ivUserProfile.setVisibility(View.VISIBLE);
                    replaceFragment(new BaseBusinessSetupFragment(), false);
                }
                break;

            case R.id.imgNotif:
                startActivity(new Intent(this, PendingBookingActivity.class));
                break;

            case R.id.ivUserProfile:
                startActivity(new Intent(this, MyProfileActivity.class));
                break;
        }
    }

    private void setInactiveTab(){
        spBizName.setVisibility(View.GONE);
        ivUserProfile.setVisibility(View.GONE);
        Mualab.getInstance().cancelAllPendingRequests();

        if (clickedId == 1) {
            imgNotif.setVisibility(View.VISIBLE);
            if (!user.businessType.equals("independent")) {
                imgRight.setVisibility(View.VISIBLE);
            } else {
                imgRight.setVisibility(View.GONE);
            }
        } else {
            imgNotif.setVisibility(View.GONE);
            imgRight.setVisibility(View.GONE);
        }

        ibtnBookings.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_calender_ico));
        ibtnChart.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_chart_ico));
        ibtnAdd.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_add_ico));
        ibtnNotification.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_notification_ico));
        ibtnUser.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_user_ico));
    }

    private void getDeviceLocation() {

        if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                LocationDetector locationDetector = new LocationDetector();
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                if (locationDetector.isLocationEnabled(MainActivity.this) &&
                        locationDetector.checkLocationPermission(MainActivity.this)) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            Mualab.currentLng = latitude;
                            Mualab.currentLng = longitude;

                            if (lng.equals("") && lat.equals("")) {
                                lat = String.valueOf(latitude);
                                lng = String.valueOf(longitude);
                            }
                        }
                    });

                }else {
                    //  tv_msg.setText(R.string.gps_permission_alert);
                    locationDetector.showLocationSettingDailod(MainActivity.this);
                }
            }
        }else {
            LocationDetector locationDetector = new LocationDetector();
            // tv_msg.setText(R.string.gps_permission_alert);
            locationDetector.showLocationSettingDailod(MainActivity.this);
        }
        ibtnBookings.callOnClick();
        //replaceFragment(BookingsFragment.newInstance(), false);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
           /* case Constants.MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getDeviceLocation();
                    }

                } else {
                    //Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_LONG).show();
                    //replaceFragment(BookingsFragment.newInstance(), false);
                    ibtnBookings.callOnClick();
                }
            }*/

        }
    }

    @Override
    public void onBackPressed() {
        /* Handle double click to finish modules*/
        Handler handler = new Handler();
        FragmentManager fm = getSupportFragmentManager();
        int i = fm.getBackStackEntryCount();
        if (i > 0) {
            fm.popBackStack();
        } else if (!doubleBackToExitPressedOnce) {

            doubleBackToExitPressedOnce = true;
            //Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();
            MySnackBar.showSnackbar(this, findViewById(R.id.lyCoordinatorLayout), "Click again to exit");
            handler.postDelayed(runnable = () -> doubleBackToExitPressedOnce = false, 2000);

        } else {
            handler.removeCallbacks(runnable);
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestChanged(boolean isShow) {
        BookingsFragment frag = ((BookingsFragment) getSupportFragmentManager().findFragmentByTag("com.mualab.org.biz.modules.booking.fragments.BookingFragment2"));
        //  frag.refreshData(true);
    }
}

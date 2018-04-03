package com.mualab.org.biz.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mualab.org.biz.R;
import com.mualab.org.biz.activity.booking.fragments.AddFragment;
import com.mualab.org.biz.activity.booking.fragments.BookingsFragment;
import com.mualab.org.biz.activity.profile.BusinessProfileActivity;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.helper.MySnackBar;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.LocationDetector;
import com.mualab.org.biz.util.network.NetworkChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Session session;
    private ImageButton ibtnBookings,ibtnChart,ibtnAdd,ibtnLogo,ibtnUser;
    private int clickedId = 0;
    //private TextView tv_msg;
    private String lat="",lng="";
   // private ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new Session(this);
       // progress_bar = findViewById(R.id.progress_bar);
        initView();

        if(!session.isBusinessProfileComplete()){
            startActivity(new Intent(this, BusinessProfileActivity.class));
            finish();
        }else {
            getBusinessProfile();
        }


        final NoConnectionDialog network =  new NoConnectionDialog(MainActivity.this, new NoConnectionDialog.Listner() {
            @Override
            public void onNetworkChange(Dialog dialog, boolean isConnected) {
                if(isConnected){
                    dialog.dismiss();
                }
            }
        });

        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
        networkChangeReceiver.setListner(new NetworkChangeReceiver.Listner() {
            @Override
            public void onNetworkChange(boolean isConnected) {
                if(isConnected){
                    network.dismiss();
                }else network.show();
            }
        });
    }

    private void initView() {
        ibtnBookings = findViewById(R.id.ibtnBookings);
        ibtnChart = findViewById(R.id.ibtnChart);
        ibtnAdd = findViewById(R.id.ibtnAdd);
        ibtnLogo = findViewById(R.id.ibtnLogo);
        ibtnUser = findViewById(R.id.ibtnUser);
       // tv_msg = findViewById(R.id.tv_msg);
        ibtnBookings.setImageResource(R.drawable.active_calender_ico);

        ibtnBookings.setOnClickListener(this);
        ibtnChart.setOnClickListener(this);
        ibtnAdd.setOnClickListener(this);
        ibtnLogo.setOnClickListener(this);
        ibtnUser.setOnClickListener(this);
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


                        JSONArray businessArray = obj.getJSONArray("businessHour");
                        bsp.businessDays = getBusinessDay();
                        for(int i =0; i<businessArray.length();  i++){
                            JSONObject objSlots = businessArray.getJSONObject(i);
                            int dayId = objSlots.getInt("day");

                            TimeSlot slot = new TimeSlot(dayId);
                            slot.id = objSlots.getInt("_id");
                            slot.startTime = objSlots.getString("startTime");
                            slot.endTime = objSlots.getString("endTime");
                            slot.status = objSlots.getInt("status");

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
                        startActivity(new Intent(MainActivity.this, BusinessProfileActivity.class));
                        finish();
                    }else {
                        //getDeviceLocation();
                        replaceFragment(BookingsFragment.newInstance(""), false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
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
        switch (view.getId()) {
            case R.id.ibtnBookings:
                if (clickedId!=1){
                    setInactiveTab();
                    clickedId = 1;
                    ibtnBookings.setImageResource(R.drawable.active_calender_ico);                     replaceFragment(new BookingsFragment(), false);
                }
                break;

            case R.id.ibtnChart:
                if (clickedId!=2){
                    setInactiveTab();
                    clickedId = 2;
                    ibtnChart.setImageResource(R.drawable.active_chart_ico);
                    replaceFragment(new AddFragment(), false);
                }
                break;

            case R.id.ibtnAdd:
                if (clickedId!=3){
                    setInactiveTab();
                    clickedId = 3;
                    ibtnAdd.setImageResource(R.drawable.active_add_ico);
                    replaceFragment(new AddFragment(), false);
                }
                break;

            case R.id.ibtnLogo:
                if (clickedId!=4){
                    setInactiveTab();
                    clickedId = 4;
                    ibtnLogo.setImageResource(R.drawable.active_logo_ico);
                    replaceFragment(new AddFragment(), false);
                }
                break;

            case R.id.ibtnUser:
                if (clickedId!=5){
                    setInactiveTab();
                    clickedId = 5;
                    ibtnUser.setImageResource(R.drawable.active_user_ico);
                    replaceFragment(new AddFragment(), false);
                }
                break;
        }
    }

    private void setInactiveTab(){
        ibtnBookings.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_calender_ico));
        ibtnChart.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_chart_ico));
        ibtnAdd.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_add_ico));
        ibtnLogo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.inactive_logo_ico));
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

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                Mualab.currentLocation.lat = latitude;
                                Mualab.currentLocation.lng = longitude;

                                if (lng.equals("") && lat.equals("")){
                                    lat = String.valueOf(latitude);
                                    lng = String.valueOf(longitude);
                                }
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
        replaceFragment(BookingsFragment.newInstance(""), false);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getDeviceLocation();
                    }

                } else {
                    //Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_LONG).show();
                    replaceFragment(BookingsFragment.newInstance(""), false);
                }
            }

        }
    }

    private boolean doubleBackToExitPressedOnce;
    private Runnable runnable;
    @Override
    public void onBackPressed() {
          /* Handle double click to finish activity*/
        Handler handler = new Handler();
        FragmentManager fm = getSupportFragmentManager();
        int i = fm.getBackStackEntryCount();
        if (i > 0) {
            fm.popBackStack();
        } else if (!doubleBackToExitPressedOnce) {

            doubleBackToExitPressedOnce = true;
            //Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();
            MySnackBar.showSnackbar(this, findViewById(R.id.lyCoordinatorLayout), "Click again to exit");
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        } else {
            handler.removeCallbacks(runnable);
            super.onBackPressed();
        }
    }


}

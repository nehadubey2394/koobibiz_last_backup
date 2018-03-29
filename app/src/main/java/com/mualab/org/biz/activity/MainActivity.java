package com.mualab.org.biz.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.activity.profile.BusinessProfileActivity;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.network.NetworkChangeReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new Session(this);
        //getBusinessProfile();


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
                if(isConnected && network!=null){
                    network.dismiss();
                }else network.show();
            }
        });


        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mualab.getInstance().getSessionManager().logout();
            }
        });

    }

    private void getBusinessProfile(){

        new HttpTask(new HttpTask.Builder(this, "getbusinessProfile", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {

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
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
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
}

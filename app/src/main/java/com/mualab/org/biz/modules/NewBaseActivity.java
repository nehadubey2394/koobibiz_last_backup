package com.mualab.org.biz.modules;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.modules.profile.activity.AddBankAccountActivity;
import com.mualab.org.biz.modules.profile.activity.MyBusinessTypeActivity;
import com.mualab.org.biz.modules.profile.activity.NewBusinessInfoActivity;
import com.mualab.org.biz.modules.profile.activity.NewBusinessSetUpActivity;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewBaseActivity extends AppCompatActivity {
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_base);
        session = new Session(this);

        getBusinessProfile();
    }

    private void getBusinessProfile(){
        Progress.show(NewBaseActivity.this);
        //progress_bar.setVisibility(View.VISIBLE);
        new HttpTask(new HttpTask.Builder(this, "getbusinessProfile", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    // progress_bar.setVisibility(View.GONE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Progress.hide(NewBaseActivity.this);
                        }
                    },700);

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

                    if(!session.isBusinessProfileComplete()){
                        int index = Mualab.getInstance().getBusinessProfileSession().getStepIndex();

                        if (index==0 || index == 1)
                        {
                            startActivity(new Intent(NewBaseActivity.this, NewBusinessSetUpActivity.class));
                            overridePendingTransition(0,0);
                            finish();
                        }else if (index==2)
                        {
                            startActivity(new Intent(NewBaseActivity.this, NewBusinessInfoActivity.class));
                            overridePendingTransition(0,0);
                            finish();
                        }else if (index==3)
                        {
                            startActivity(new Intent(NewBaseActivity.this, MyBusinessTypeActivity.class));
                            overridePendingTransition(0,0);
                            finish();
                        }else if (index==4)
                        {
                            startActivity(new Intent(NewBaseActivity.this, AddBankAccountActivity.class));
                            overridePendingTransition(0,0);
                            finish();
                           /* if(Mualab.getInstance().getBusinessProfileSession().getBankStatus()!=1) {
                                startActivity(new Intent(NewBaseActivity.this, AddBankAccountActivity.class));
                                overridePendingTransition(0,0);
                                finish();
                            }else {
                                startActivity(new Intent(NewBaseActivity.this, MainActivity.class));
                                finish();
                                overridePendingTransition(0,0);
                            }*/
                        }

                    }else {
                        startActivity(new Intent(NewBaseActivity.this, MainActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                    }


                } catch (JSONException e) {
                    Progress.hide(NewBaseActivity.this);
                    e.printStackTrace();
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {
                Progress.hide(NewBaseActivity.this);
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
                .setProgress(false)
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

package com.mualab.org.biz.modules.profile.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.model.serializer.TimeSlotSerializer;
import com.mualab.org.biz.modules.profile.adapter.AdapterBusinessDays;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.CalanderUtils;
import com.mualab.org.biz.util.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkingHoursActivity extends AppCompatActivity implements View.OnClickListener {
    private PreRegistrationSession bpSession;
    private List<BusinessDay> businessDays;
    protected User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_working_hours);
        init();
    }

    private void init(){
        if(user==null) user = Mualab.getInstance().getSessionManager().getUser();
        bpSession  = Mualab.getInstance().getBusinessProfileSession();
        businessDays =  getBusinessdays();
        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        ivKoobiLogo.setVisibility(View.GONE);
        ImageView iv_back = findViewById(R.id.iv_back);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);
        tvHeaderText.setVisibility(View.VISIBLE);
        tvHeaderText.setText("Operation Hours");

        AdapterBusinessDays adapter = new AdapterBusinessDays(WorkingHoursActivity.this, businessDays);
        RecyclerView rvBusinessDay = findViewById(R.id.rvBusinessDay);
        rvBusinessDay.setLayoutManager(new LinearLayoutManager(WorkingHoursActivity.this));
        rvBusinessDay.setAdapter(adapter);


       /* int serviceType = bpSession.getServiceType();
        if (serviceType==1){

        }else if (serviceType==2){

        }else if (serviceType==3){
        }*/

        iv_back.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;

            case R.id.btnContinue:

                if(isvalidBusinessHours()){
                    if (!ConnectionDetector.isConnected()) {
                        new NoConnectionDialog(WorkingHoursActivity.this, new NoConnectionDialog.Listner() {
                            @Override
                            public void onNetworkChange(Dialog dialog, boolean isConnected) {
                                if(isConnected){
                                    dialog.dismiss();
                                    bpSession.setBusinessHours(businessDays);
                                    updateDataIntoServerDb();
                                }
                            }
                        }).show();
                    }else {
                        bpSession.setBusinessHours(businessDays);
                        updateDataIntoServerDb();
                    }

                }

                break;
        }
    }

    private boolean  isvalidBusinessHours(){

        for(int k = 0; k<businessDays.size(); k++){

            BusinessDay tmpDay = businessDays.get(k);

            if(tmpDay.slots.size()>1){

                TimeSlot tmpTimeSlot = tmpDay.slots.get(0);
                TimeSlot tmpTimeSlot2 = tmpDay.slots.get(1);
                boolean bool = new CalanderUtils().compareTime(tmpTimeSlot.startTime, tmpTimeSlot.endTime, tmpTimeSlot2.startTime);

                if(bool){
                    MyToast.getInstance(WorkingHoursActivity.this).showDasuAlert(getString(R.string.error_timeslot_intersect));
                    return false;
                }
            }
        }
        return true;
    }

    private List<BusinessDay> getBusinessdays(){
        BusinessProfile businessProfile =  bpSession.getBusinessProfile();
        if(businessProfile!=null && businessProfile.businessDays!=null)
            if (businessProfile.businessDays.size()!=0)
                return businessProfile.businessDays;
            else
                return createDefaultBusinessHours();
        else return  createDefaultBusinessHours();
    }

    private  List<BusinessDay> createDefaultBusinessHours(){
        List<BusinessDay>businessDays = new ArrayList<>();
        BusinessDay day1 = new BusinessDay();
        day1.dayName = getString(R.string.monday);
        day1.isOpen = true;
        day1.dayId = 0;
        day1.addTimeSlot(new TimeSlot(0));

        BusinessDay day2 = new BusinessDay();
        day2.dayName = getString(R.string.tuesday);
        day2.isOpen = true;
        day2.dayId = 1;
        day2.addTimeSlot(new TimeSlot(1));

        BusinessDay day3 = new BusinessDay();
        day3.dayName = getString(R.string.wednesday);
        day3.isOpen = true;
        day3.dayId = 2;
        day3.addTimeSlot(new TimeSlot(2));

        BusinessDay day4 = new BusinessDay();
        day4.dayName = getString(R.string.thursday);
        day4.isOpen = true;
        day4.dayId = 3;
        day4.addTimeSlot(new TimeSlot(3));

        BusinessDay day5 = new BusinessDay();
        day5.dayName = getString(R.string.frieday);
        day5.isOpen = true;
        day5.dayId = 4;
        day5.addTimeSlot(new TimeSlot(4));

        BusinessDay day6 = new BusinessDay();
        day6.dayName = getString(R.string.saturday);
        day6.isOpen = true;
        day6.dayId = 5;
        day6.addTimeSlot(new TimeSlot(5));

        BusinessDay day7 = new BusinessDay();
        day7.dayName = getString(R.string.sunday);
        day7.isOpen = false;
        day7.dayId = 6;
        day7.addTimeSlot(new TimeSlot(6));

        businessDays.add(day1);
        businessDays.add(day2);
        businessDays.add(day3);
        businessDays.add(day4);
        businessDays.add(day5);
        businessDays.add(day6);
        businessDays.add(day7 );
        return businessDays;
    }

    /*update data into server db_modle*/
    private void updateDataIntoServerDb(){

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(WorkingHoursActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        updateDataIntoServerDb();
                    }
                }
            }).show();
        }

        //  List<BusinessDay> businessDays = getBusinessdays(); // getting business hours slots like opening/closing time
        ArrayList<TimeSlot> slotList = new ArrayList<>();
        for(BusinessDay tmp : businessDays){
            if(tmp.isOpen){
                for(TimeSlot slot:tmp.slots){
                    //slot.dayId = tmp.dayId-1;
                    slot.status = 1;
                    slotList.add(slot);
                }
            }
        }

        if (slotList.size()!=0){

            // dynamic serialization on product
            Gson gson = new GsonBuilder().registerTypeAdapter(TimeSlot.class, new TimeSlotSerializer()).create();
            Map<String,String> body = new HashMap<>();
            body.put("businessHour", gson.toJson(slotList));

            new HttpTask(new HttpTask.Builder(WorkingHoursActivity.this, "addBusinessHour", new HttpResponceListner.Listener() {
                @Override
                public void onResponse(String response, String apiName) {
                    Log.d("res:", response);
                    PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");

                        if (status.equals("success")){

                            BusinessProfile bsp = new BusinessProfile();
                           /*
                            JSONArray businessArray = jsonObject.getJSONArray("message");
                          //  bsp.businessDays = getBusinessdays();

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

                                for(BusinessDay tmpDay : businessDays){
                                    if(tmpDay.dayId == dayId){
                                        tmpDay.isOpen = true;
                                        tmpDay.addTimeSlot(slot);
                                        break;
                                    }
                                }
                            }

                            if(businessArray.length()>0)
                                pSession.createBusinessProfile(bsp);
*/
                            bsp.businessDays.addAll(businessDays);

                            bsp.radius = pSession.getRadius();
                            bsp.address = pSession.getAddress();
                            bsp.outCallpreprationTime = pSession.getOutCallPreprationTime();
                            bsp.inCallpreprationTime = pSession.getInCallPreprationTime();
                            bsp.serviceType = pSession.getServiceType();
                            bsp.bankStatus = pSession.getBankStatus();
                            pSession.createBusinessProfile(bsp);

                            Intent intent = new Intent();
                            intent.putExtra("isHoursUpdate",true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // listener.onNext();
                }

                @Override
                public void ErrorListener(VolleyError error) {
                    // Log.d("res:", error.getLocalizedMessage());
                }})
                    .setMethod(Request.Method.POST)
                    .setParam(body)
                    .setBodyContentType( HttpTask.ContentType.APPLICATION_JSON)
                    .setBody(body)
                    .setAuthToken(user.authToken)).execute("AddBusinessHour");
        }else {
            bpSession.updateRegStep(1);
            MyToast.getInstance(WorkingHoursActivity.this).showDasuAlert("Select at least one day working hours");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

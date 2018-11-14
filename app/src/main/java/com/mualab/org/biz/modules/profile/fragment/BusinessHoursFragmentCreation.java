package com.mualab.org.biz.modules.profile.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mualab.org.biz.R;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.modules.profile.adapter.AdapterBusinessDays;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.model.serializer.TimeSlotSerializer;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.CalanderUtils;
import com.mualab.org.biz.util.ConnectionDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessHoursFragmentCreation extends ProfileCreationBaseFragment {

    private List<BusinessDay> businessDays;
    private PreRegistrationSession preSession;

    public BusinessHoursFragmentCreation() {
        // Required empty public constructor
    }

    public static BusinessHoursFragmentCreation newInstance() {
        return new BusinessHoursFragmentCreation();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preSession = new PreRegistrationSession(mContext);
        businessDays =  getBusinessdays();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_hours, container, false);

        view.findViewById(R.id.btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listener!=null && isvalidBusinessHours()){
                    if(ConnectionDetector.isConnected()){
                        preSession.setBusinessHours(businessDays);
                        updateDataIntoServerDb();
                    } else showToast(R.string.error_msg_network);
                }
            }
        });

        return view;
    }

    private boolean  isvalidBusinessHours(){

        for(int k = 0; k<businessDays.size(); k++){

            BusinessDay tmpDay = businessDays.get(k);

            if(tmpDay.slots.size()>1){

                TimeSlot tmpTimeSlot = tmpDay.slots.get(0);
                TimeSlot tmpTimeSlot2 = tmpDay.slots.get(1);
                boolean bool = new CalanderUtils().compareTime(tmpTimeSlot.startTime, tmpTimeSlot.endTime, tmpTimeSlot2.startTime);

                if(bool){
                    MyToast.getInstance(mContext).showDasuAlert(getString(R.string.error_timeslot_intersect));
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AdapterBusinessDays adapter = new AdapterBusinessDays(mContext, businessDays);
        RecyclerView rvBusinessDay = view.findViewById(R.id.rvBusinessDay);
        rvBusinessDay.setLayoutManager(new LinearLayoutManager(mContext));
        rvBusinessDay.setAdapter(adapter);
    }

    private List<BusinessDay> getBusinessdays(){
        BusinessProfile businessProfile =  preSession.getBusinessProfile();
        if(businessProfile!=null && businessProfile.businessDays!=null)
            return businessProfile.businessDays;
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

    /*update data into server db*/
    private void updateDataIntoServerDb(){

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
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

            new HttpTask(new HttpTask.Builder(mContext, "addBusinessHour", new HttpResponceListner.Listener() {
                @Override
                public void onResponse(String response, String apiName) {
                    Log.d("res:", response);
                    listener.onNext();
                    preSession.updateRegStep(1);
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
            preSession.updateRegStep(0);
            MyToast.getInstance(mContext).showDasuAlert("Select at least one day working hours");
        }
    }
}

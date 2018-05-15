package com.mualab.org.biz.modules.add_staff.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mualab.org.biz.R;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.model.serializer.EditTimeSlotSerializer;
import com.mualab.org.biz.modules.add_staff.activity.AddStaffDetailActivity;
import com.mualab.org.biz.modules.add_staff.adapter.AdapterEditBusinessDays;
import com.mualab.org.biz.modules.add_staff.listner.EditWorkingHours;
import com.mualab.org.biz.modules.profile.fragment.ProfileCreationBaseFragment;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.util.CalanderUtils;
import com.mualab.org.biz.util.ConnectionDetector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EditBusinessHoursFragment extends ProfileCreationBaseFragment {

    private List<BusinessDay> edtBusinessDays;
    private PreRegistrationSession preSession;

    public EditBusinessHoursFragment() {
        // Required empty public constructor
    }

    public static EditBusinessHoursFragment newInstance() {
        return new EditBusinessHoursFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preSession = new PreRegistrationSession(mContext);

        edtBusinessDays = new ArrayList<>();

        StaffDetail staffDetail  = preSession.getStaffBusinessHours();
        if (staffDetail.businessDays.size()!=0){
            edtBusinessDays = staffDetail.businessDays;
        }
        else {
            edtBusinessDays = getBusinessdays();
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_hours, container, false);
        AppCompatButton btnDone = view.findViewById(R.id.btnNext) ;
        btnDone.setWidth(180);
        btnDone.setHeight(32);
        btnDone.setText(getString(R.string.done));
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listener!=null && isvalidBusinessHours()){
                    if(ConnectionDetector.isConnected()){
                        // preSession.setBusinessHours(businessDays);
                        updateDataIntoServerDb();
                        listener.onNext();
                    } else showToast(R.string.error_msg_network);
                }
            }
        });

        return view;
    }

    private boolean  isvalidBusinessHours(){

        for(int k = 0; k<edtBusinessDays.size(); k++){

            BusinessDay tmpDay = edtBusinessDays.get(k);

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
        ((AddStaffDetailActivity)mContext).setHeaderVisibility(8);
        ((AddStaffDetailActivity)mContext).setTitle("Working Hours");

        Iterator<BusinessDay> it = edtBusinessDays.iterator();
        List<BusinessDay> tmpBusinessDays =new ArrayList<>();

        while(it.hasNext()) {
            BusinessDay day = it.next();
            if (!day.isOpen)
                tmpBusinessDays.add(day);
        }
        edtBusinessDays.removeAll(tmpBusinessDays);

        AdapterEditBusinessDays adapter = new AdapterEditBusinessDays(mContext, edtBusinessDays);
        RecyclerView rvBusinessDay = view.findViewById(R.id.rvBusinessDay);
        rvBusinessDay.setLayoutManager(new LinearLayoutManager(mContext));
        rvBusinessDay.setAdapter(adapter);
    }


    private List<BusinessDay> getBusinessdays(){
        BusinessProfile businessProfile =  preSession.getBusinessProfile();
        if(businessProfile!=null && businessProfile.businessDays!=null)
            return businessProfile.businessDays;
        else
            return  null;
    }

    /*update data into server db*/
    private void updateDataIntoServerDb(){
        //List<BusinessDay> businessDays = getBusinessdays(); // getting business hours slots like opening/closing time
        ArrayList<TimeSlot> slotList = new ArrayList<>();
        for(BusinessDay tmp : edtBusinessDays){
            if(tmp.isOpen){
                for(TimeSlot slot:tmp.slots){
                    //slot.dayId = tmp.dayId-1;
                    slot.status = 1;
                    slotList.add(slot);
                }
            }
        }

        // dynamic serialization on product
        Gson gson = new GsonBuilder().registerTypeAdapter(TimeSlot.class, new EditTimeSlotSerializer()).create();
        String whJsonArray = gson.toJson(slotList);

        EditWorkingHours listener = null;
        if(mContext instanceof EditWorkingHours) listener = (EditWorkingHours) mContext;

        if(listener!=null)
            listener.onHorusChange(whJsonArray);

    }

    @Override
    public void onDestroyView() {
        ((AddStaffDetailActivity)mContext).setHeaderVisibility(0);
        ((AddStaffDetailActivity)mContext).setTitle(getString(R.string.text_staff));
        super.onDestroyView();
    }
}

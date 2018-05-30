package com.mualab.org.biz.modules.add_staff.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.util.CalanderUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import views.pickerview.DayOfWeek;
import views.pickerview.popwindow.DualTimePickerPopWin;

public class AdapterEditBusinessDays extends RecyclerView.Adapter<AdapterEditBusinessDays.ViewHolder>{

    private List<BusinessDay> businessDaysList ;
    private Context mContext;
    private int parentPostion;
    private Date st1 = null,et1=null;
    private String dayId="";
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");


    public AdapterEditBusinessDays(Context mContext, List<BusinessDay> businessHours) {
        this.mContext = mContext;
        this.businessDaysList = businessHours;
    }

    @Override
    public AdapterEditBusinessDays.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_business_days, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AdapterEditBusinessDays.ViewHolder holder, int position) {
        BusinessDay day = businessDaysList.get(position);

        // if (day.isOpen){
        holder.tv_dayName.setText(day.dayName);
        holder.tv_workingStatus.setVisibility(day.isOpen?View.INVISIBLE:View.VISIBLE);
        holder.ll_addTimeSlot.setVisibility(day.isOpen?View.VISIBLE:View.INVISIBLE);
        holder.listView.setVisibility(day.isOpen?View.VISIBLE:View.GONE);
        holder.checkbox.setChecked(day.isOpen);

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final boolean isChecked = holder.checkbox.isChecked();
                // Do something here.
                int pos = holder.getAdapterPosition();
                BusinessDay day = businessDaysList.get(pos);
                day.isOpen = isChecked;

                if(isChecked && (day.slots==null || day.slots.size()==0)){
                    day.slots.add(new TimeSlot(day.dayId));
                }else if(!isChecked){
                    day.slots.clear();
                    //day.addTimeSlot(new TimeSlot(-1));
                }
                notifyItemChanged(pos);
            }
        });


        // }

        if(day.isOpen){
            AdapterTimeSlot adapterTimeSlot = new AdapterTimeSlot(mContext, day.slots);
            holder.listView.setAdapter(adapterTimeSlot);
            setListViewHeightBasedOnChildren(holder.listView);
        }
    }

    @Override
    public int getItemCount() {
        return businessDaysList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CheckBox checkbox;
        // ImageView ivAddTimeSlot;
        LinearLayout ll_addTimeSlot;
        ListView listView;
        TextView tv_dayName, tv_workingStatus;

        private ViewHolder(View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            tv_dayName = itemView.findViewById(R.id.tv_dayName);
            tv_workingStatus = itemView.findViewById(R.id.tv_workingStatus);
            //ivAddTimeSlot =  itemView.findViewById(R.id.ivAddTimeSlot);
            ll_addTimeSlot =  itemView.findViewById(R.id.ll_addTimeSlot);
            listView = itemView.findViewById(R.id.listView);
            checkbox.setOnClickListener(this);
            ll_addTimeSlot.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            parentPostion = getAdapterPosition();

            switch (v.getId()){

                case R.id.checkbox:
                    break;

                case R.id.ll_addTimeSlot:
                    synchronized (this){
                        BusinessDay day = businessDaysList.get(position);
                     /*   PreRegistrationSession preSession = new PreRegistrationSession(mContext);
                        StaffDetail staffDetail  = preSession.getStaffBusinessHours();
                        List<BusinessDay>edtStaffDays = staffDetail.edtStaffDays;
                        BusinessDay day = edtStaffDays.get(position);*/

                        if(day.getTimeSlotSize()>=2){
                            MyToast.getInstance(mContext).showDasuAlert("Max time slot reached!");
                        }else {
                           /* TimeSlot tmpSlot1 = day.slots.get(0);
                            day.addTimeSlot(tmpSlot1);

                            if (day.slots.size()>1) {
                                TimeSlot tmpSlot2 = day.slots.get(1);
                                day.addTimeSlot(tmpSlot2);
                                day.addTimeSlot(tmpSlot1);
                            }*/
                            day.addTimeSlot(new TimeSlot(day.dayId));
                            notifyItemChanged(position);
                        }
                    }
                    break;

                case R.id.action_search:
                    break;
            }
        }
    }

    private void picTimeSlot(final AdapterTimeSlot adapter, final ArrayList<TimeSlot> timeSlots, final int position){

        if(timeSlots.size()>0){

            final TimeSlot timeSlot = timeSlots.get(position);

            DualTimePickerPopWin timePickerPopWin = new DualTimePickerPopWin.Builder(mContext,
                    new DualTimePickerPopWin.OnTimePickListener() {
                        @Override
                        public void onTimePickCompleted(String startTime, String endTime, String time) {
                            Date st2 = null,et2 = null;
                            try {
                                BusinessDay day = businessDaysList.get(parentPostion);

                                //      st1 = sdf.parse(timeSlot.startTime);
                                //     et1 = sdf.parse(timeSlot.endTime);
                                st2 = sdf.parse(startTime);
                                et2 = sdf.parse(endTime);

                                if(day.getTimeSlotSize()<=1){
                                    if ((st2.after(st1) || st2.equals(st1)) && (et2.before(et1) || et2.equals(et1))){
                                        setTime(timeSlot,position,startTime,endTime,time,timeSlots,adapter);
                                    }else
                                        MyToast.getInstance(mContext).showDasuAlert("You can't select other time apart from your business timing");
                                }else {
                                    if ((st2.after(st1) || st2.equals(st1)) && (et2.before(et1) || et2.equals(et1))){
                                        setTime(timeSlot,position,startTime,endTime,time,timeSlots,adapter);
                                    }else
                                        MyToast.getInstance(mContext).showDasuAlert("You can't select other time apart from your business timing");

                                    //  setTime(timeSlot,position,startTime,endTime,time,timeSlots,adapter);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .textCancel(mContext.getString(R.string.cancel))
                    .textConfirm(mContext.getString(R.string.done))
                    .setHeader(DayOfWeek.of(timeSlot.dayId+1).name())
                    .setTimeSlot(timeSlot)
                    .build();
            timePickerPopWin.showPopWin((Activity) mContext);
        }
    }

    private void setTime(TimeSlot timeSlot,int position,String startTime,String endTime,String time,
                         final ArrayList<TimeSlot> timeSlots,final AdapterTimeSlot adapter){
        if(position == 0){
            timeSlot.startTime = startTime;
            timeSlot.endTime = endTime;
            timeSlot.edtStartTime = startTime;
            timeSlot.edtEndTime = endTime;
            timeSlot.slotTime = time;
            adapter.notifyDataSetChanged();
        }else {

            for(int i=0; i<timeSlots.size(); i++){

                TimeSlot tmp = timeSlots.get(i);

                if(i==position) {
                    continue;
                }
                else {

                    if(new CalanderUtils().compareTime(tmp.startTime, tmp.endTime, startTime)){
                        MyToast.getInstance(mContext).showDasuAlert(mContext.getString(R.string.error_timeslot_intersect));
                        break;
                    }else {

                        timeSlot.startTime = startTime;
                        timeSlot.endTime = endTime;
                        timeSlot.slotTime = time;
                        timeSlot.edtStartTime = startTime;
                        timeSlot.edtEndTime = endTime;
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    public class AdapterTimeSlot extends ArrayAdapter<TimeSlot> {

        private ArrayList<TimeSlot> timeSlots = new ArrayList<>();

        private AdapterTimeSlot(Context context, ArrayList<TimeSlot> objects) {
            super(context,0, objects);
            timeSlots = objects;
        }

        @Override
        public int getCount() {
            return timeSlots.size();
        }

        @NonNull
        @Override
        public View getView(final int position, View v, @NonNull ViewGroup parent) {
            // Get the data item for this position
            final TimeSlot timeSlot = timeSlots.get(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.adapter_business_hours, parent, false);
            }
            // Lookup view for data population

            TextView tv_from =  v.findViewById(R.id.tv_from);
            TextView tv_to =  v.findViewById(R.id.tv_to);
            View viewDivider = v.findViewById(R.id.viewDivider);
            //ImageView iv_delete = v.findViewById(R.id.iv_delete);
            LinearLayout ll_delete = v.findViewById(R.id.ll_delete);
            // Populate the data into the template view using the data object

            tv_from.setText(String.format("From: %s", timeSlot.edtStartTime));
            tv_to.setText(String.format("To: %s", timeSlot.edtEndTime));

            viewDivider.setVisibility(timeSlots.size()==1?View.GONE:View.VISIBLE);
            ll_delete.setVisibility(timeSlots.size()==1?View.GONE:View.VISIBLE);
            ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(timeSlots.size()>position){
                        timeSlots.remove(position);
                        AdapterTimeSlot.this.notifyDataSetChanged();
                        AdapterEditBusinessDays.this.notifyItemChanged(timeSlot.dayId-1);
                        // dayId= "";
                    }
                }
            });

            tv_from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusinessDay day = businessDaysList.get(position);

                    if (dayId.isEmpty() || !dayId.equals(String.valueOf(day.dayId))){
                        if (st1==null || et1==null){
                            try {
                                dayId = String.valueOf(day.dayId);
                                st1 = sdf.parse(timeSlot.startTime);
                                et1 = sdf.parse(timeSlot.endTime);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    picTimeSlot(AdapterTimeSlot.this,timeSlots,position);
                }
            });

            tv_to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusinessDay day = businessDaysList.get(position);
                    if (dayId.isEmpty() || !dayId.equals(String.valueOf(day.dayId))){
                        if (st1==null || et1==null){
                            try {
                                dayId = String.valueOf(day.dayId);
                                st1 = sdf.parse(timeSlot.startTime);
                                et1 = sdf.parse(timeSlot.endTime);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    picTimeSlot(AdapterTimeSlot.this,timeSlots,position);
                }
            });

            return v;
        }

    }

    private synchronized static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}

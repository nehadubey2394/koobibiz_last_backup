package com.mualab.org.biz.modules.business_setup.new_add_staff.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

public class WorkingHoursAdapter extends RecyclerView.Adapter<WorkingHoursAdapter.ViewHolder>{

    private List<BusinessDay> businessDaysList;
    private Context mContext;
    private Date st1 = null,et1=null;
    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

    public WorkingHoursAdapter(Context mContext, List<BusinessDay> businessHours) {
        this.mContext = mContext;
        this.businessDaysList = businessHours;
    }

    @Override
    public WorkingHoursAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_business_days, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final WorkingHoursAdapter.ViewHolder holder, int position) {
        BusinessDay day = businessDaysList.get(position);
        holder.tv_dayName.setText(day.dayName);
        holder.tv_workingStatus.setVisibility(day.isOpen?View.INVISIBLE:View.VISIBLE);
        holder.ll_addTimeSlot.setVisibility(day.isOpen?View.VISIBLE:View.INVISIBLE);
        holder.listView.setVisibility(day.isOpen?View.VISIBLE:View.GONE);
        holder.checkbox.setChecked(day.isOpen);

        if (day.isExpand) {
            RelativeLayout.LayoutParams layout_description =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,  RelativeLayout.LayoutParams.WRAP_CONTENT);

            holder.rlParent.setLayoutParams(layout_description);
        }else {
            RelativeLayout.LayoutParams layout_description =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            holder.rlParent.setLayoutParams(layout_description);
        }

        if (day.isOpen){
            holder.tv_dayName.setTextColor(mContext.getResources().getColor(R.color.black));
        }else {
            holder.tv_dayName.setTextColor(mContext.getResources().getColor(R.color.gray));
        }

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final boolean isChecked = holder.checkbox.isChecked();
                // Do something here.
                int pos = holder.getAdapterPosition();
                BusinessDay day = businessDaysList.get(pos);
                day.isOpen = isChecked;

            /*    if(isChecked && (day.slots==null || day.slots.size()==0)){
                    assert day.slots != null;
                    if (day.tempSlots.size()!=0)
                        day.slots.addAll(day.tempSlots);
                    else
                        day.slots.add(new TimeSlot(day.dayId));

                }else if(!isChecked){
                    day.slots.clear();
                }
*/
               /* if(isChecked){
                    day.isOpen = true;
                }else {
                    day.isOpen = false;
                    //day.slots.clear();
                }*/
                notifyItemChanged(pos);
            }
        });

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
        RelativeLayout rlParent;
        ListView listView;
        TextView tv_dayName, tv_workingStatus;

        private ViewHolder(View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            tv_dayName = itemView.findViewById(R.id.tv_dayName);
            tv_workingStatus = itemView.findViewById(R.id.tv_workingStatus);
            //ivAddTimeSlot =  itemView.findViewById(R.id.ivAddTimeSlot);
            ll_addTimeSlot =  itemView.findViewById(R.id.ll_addTimeSlot);
            rlParent =  itemView.findViewById(R.id.rlParent);
            listView = itemView.findViewById(R.id.listView);
            checkbox.setOnClickListener(this);
            ll_addTimeSlot.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            switch (v.getId()){

                case R.id.checkbox:
                    break;

                case R.id.ll_addTimeSlot:
                    synchronized (this){
                        BusinessDay day = businessDaysList.get(position);

                        if(day.getTimeSlotSize()>=2){
                            MyToast.getInstance(mContext).showDasuAlert("Max time slot reached!");
                        }else {
                            day.isExpand = true;
                            //day.addTimeSlot(new TimeSlot(day.dayId,position));
                            if (day.tempSlots.size()>1) {
                                TimeSlot timeSlot = day.tempSlots.get(1);
                                day.addTimeSlot(timeSlot);
                            }else {
                                day.addTimeSlot(new TimeSlot(day.dayId,position));
                            }
                            notifyItemChanged(position);
                        }
                    }
                    break;

                case R.id.action_search:
                    break;
            }
        }
    }

    private synchronized void picTimeSlot(final AdapterTimeSlot adapter, final ArrayList<TimeSlot> timeSlots, final int position){

        if(timeSlots.size()>0){
            final TimeSlot timeSlot = timeSlots.get(position);

            DualTimePickerPopWin timePickerPopWin = new DualTimePickerPopWin.Builder(mContext,
                    new DualTimePickerPopWin.OnTimePickListener() {
                        @Override
                        public void onTimePickCompleted(String startTime, String endTime, String time) {
                            Date st2 = null,et2 = null;
                            Date minStartTime = null,minEndTime;

                            try {
                              /*  TimeSlot timeSlot = timeSlots.get(position);
                                BusinessDay day = businessDaysList.get(timeSlot.bizdayPosition);  //parentPostion */
                                //      st1 = sdf.parse(timeSlot.startTime);
                                //     et1 = sdf.parse(timeSlot.endTime);
                                st2 = sdf.parse(startTime);
                                et2 = sdf.parse(endTime);

                                minStartTime = sdf.parse(timeSlot.minStartTime);
                                minEndTime = sdf.parse(timeSlot.maxEndTime);

                                Log.e("Date Old", st1+" "+et1);
                                Log.e("Date NEw", st2+" "+et2);

                                if ((st2.after(minStartTime) || st2.equals(minStartTime)) &&
                                        (et2.before(minEndTime) || et2.equals(minEndTime))){
                                    setTime(timeSlot,position,startTime,endTime,time,timeSlots,adapter);
                                }else
                                    MyToast.getInstance(mContext).showDasuAlert("You can't select other time apart from your business timing");


                             /*   if ((st2.after(st1) || st2.equals(st1)) && (et2.before(et1) || et2.equals(et1))){
                                    setTime(timeSlot,position,startTime,endTime,time,timeSlots,adapter);
                                }else
                                    MyToast.getInstance(mContext).showDasuAlert("You can't select other time apart from your business timing");
*/
                                /*if(day.getTimeSlotSize()<=1){
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
                                }*/
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

                if(i==position)
                    continue;
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

    //child list adapter
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
            // View viewDivider = v.findViewById(R.id.viewDivider);
            //ImageView iv_delete = v.findViewById(R.id.iv_delete);
            LinearLayout ll_delete = v.findViewById(R.id.ll_delete);

            //  if (timeSlot.isFirst){
            ll_delete.setVisibility(View.GONE);
            //  }else
            //     ll_delete.setVisibility(View.VISIBLE);

            // Populate the data into the template view using the data object
            //  tv_from.setText(String.format("From: %s", timeSlot.startTime));
            //   tv_to.setText(String.format("To: %s", timeSlot.endTime));

            tv_from.setText(timeSlot.startTime);
            tv_to.setText(timeSlot.endTime);
            //   viewDivider.setVisibility(timeSlots.size()==1?View.GONE:View.VISIBLE);
            ll_delete.setVisibility(timeSlots.size()==1?View.GONE:View.VISIBLE);
            ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(timeSlots.size()>position){
                        timeSlots.remove(position);
                        timeSlot.isCancle = true;
                        AdapterTimeSlot.this.notifyDataSetChanged();
                        businessDaysList.get(timeSlot.bizdayPosition).isExpand = false;
                        WorkingHoursAdapter.this.notifyItemChanged(timeSlot.bizdayPosition);
                    }
                }
            });

            tv_from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //picTimeSlot(AdapterTimeSlot.this,timeSlots,position);
                    BusinessDay day = businessDaysList.get(position);

                    //if (dayId.isEmpty() || !dayId.equals(String.valueOf(day.dayId))){
                    //  if (st1==null || et1==null){
                    try {
                        st1 = sdf.parse(timeSlot.startTime);
                        et1 = sdf.parse(timeSlot.endTime);
                        picTimeSlot(AdapterTimeSlot.this,timeSlots,position);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        MyToast.getInstance(mContext).showDasuAlert("Error");
                    }
                    // }
                    // }


                }
            });

            tv_to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // picTimeSlot(AdapterTimeSlot.this,timeSlots,position);
                    BusinessDay day = businessDaysList.get(position);
                    // if (dayId.isEmpty() || !dayId.equals(String.valueOf(day.dayId))){
                    // if (st1==null || et1==null){
                    try {
                        st1 = sdf.parse(timeSlot.startTime);
                        et1 = sdf.parse(timeSlot.endTime);
                        picTimeSlot(AdapterTimeSlot.this,timeSlots,position);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        MyToast.getInstance(mContext).showDasuAlert("Error");
                    }
                    //  }
                    // }
                }
            });

           /* v.findViewById(R.id.ll_timeslotView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    picTimeSlot(AdapterTimeSlot.this,timeSlots,position);
                }
            });
*/
            // Return the completed view to render on screen
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

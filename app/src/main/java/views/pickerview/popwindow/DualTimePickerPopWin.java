package views.pickerview.popwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.mualab.org.biz.R;
import com.mualab.org.biz.model.TimeSlot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import views.pickerview.LoopScrollListener;
import views.pickerview.LoopView;

public class DualTimePickerPopWin extends PopupWindow implements View.OnClickListener {

    private Button cancelBtn;
    private Button confirmBtn;
    private LoopView picker_hour1;
    private LoopView picker_hour2;
    private TextView tvFromTo;
    //private LoopView meridianLoopView;
    private View pickerContainerV;
    private View contentView;

    private int hourPos1 = 0;
    private int hourPos2 = 0;
    //private int meridianPos = 0;

    private Context mContext;
    private String textCancel;
    private String textConfirm;
    private String textHeader;
    private TimeSlot timeSlot;

    private int TIME_INTERVEL = 10;
    private List<String> timeSlotList = new ArrayList();
    private List<String> timeSlot2List = new ArrayList();

    public static class Builder {
        private Context context;
        private OnTimePickListener listener;

        public Builder(Context context, OnTimePickListener listener) {
            this.context = context;
            this.listener = listener;
        }


        //Optional Parameters
        private String textCancel = "Cancel";
        private String textConfirm = "Confirm";
        private String textHeader = "MONDAY";
        private TimeSlot timeSlot;

        public Builder textCancel(String textCancel){
            this.textCancel = textCancel;
            return this;
        }

        public Builder textConfirm(String textConfirm){
            this.textConfirm = textConfirm;
            return this;
        }


        public Builder setHeader(String textHeader){
            this.textHeader = textHeader;
            return this;
        }

        public Builder setTimeSlot(TimeSlot timeSlot){
            this.timeSlot = timeSlot;
            return this;
        }

        public DualTimePickerPopWin build(){
            return new DualTimePickerPopWin(this);
        }
    }

    public DualTimePickerPopWin(Builder builder){
        this.textCancel = builder.textCancel;
        this.textConfirm = builder.textConfirm;
        this.mContext = builder.context;
        this.mListener = builder.listener;
        this.textHeader = builder.textHeader;
        this.timeSlot = builder.timeSlot;
        initView();
    }

    private OnTimePickListener mListener;

    private void initView(){
        contentView= LayoutInflater.from(mContext).inflate(R.layout.layout_dual_time_picker,null);
        TextView tvDayName = contentView.findViewById(R.id.tvDayName);
        tvDayName.setText(textHeader);
        cancelBtn= contentView.findViewById(R.id.btn_cancel);
        confirmBtn= contentView.findViewById(R.id.btn_confirm);
        tvFromTo= contentView.findViewById(R.id.tvFromTo);
        picker_hour1 = contentView.findViewById(R.id.picker_hour1);
        picker_hour2 = contentView.findViewById(R.id.picker_hour2);
        pickerContainerV = contentView.findViewById(R.id.container_picker);

        picker_hour1.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                hourPos1 = item;
                String secondItem = timeSlot2List.get(hourPos2);
                timeSlot2List.clear();

                for (int pos =item+1 ; pos<timeSlotList.size(); pos++){
                    timeSlot2List.add(timeSlotList.get(pos));
                }

                int minute = TIME_INTERVEL*timeSlotList.size();
                Calendar cal = getCalendar();
                cal.add(Calendar.MINUTE, minute-1);
                DateFormat df = new SimpleDateFormat("hh:mm a");
                String lastTimeSlot = df.format(cal.getTime()).toUpperCase().replace(".","");

                timeSlot2List.add(lastTimeSlot);
                picker_hour2.setDataList(timeSlot2List);

                int newOffset = timeSlot2List.indexOf(secondItem);
                picker_hour2.setInitPosition((hourPos2 = newOffset!=-1?newOffset:0));
                updateFromTo();
            }
        });

        picker_hour2.setLoopListener(new LoopScrollListener() {
            @Override
            public void onItemSelect(int item) {
                hourPos2=item;
                updateFromTo();
            }
        });

        initPickerViews();  // init hour and minute loop view


        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        contentView.setOnClickListener(this);

        if(!TextUtils.isEmpty(textConfirm)){
            confirmBtn.setText(textConfirm);
        }

        if(!TextUtils.isEmpty(textCancel)){
            cancelBtn.setText(textCancel);
        }

        setTouchable(true);
        setFocusable(true);

        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.FadeInPopWin);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void updateFromTo(){
        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf(timeSlotList.get(hourPos1)));
        sb.append(" - ");
        sb.append(String.valueOf(timeSlot2List.get(hourPos2)));
        tvFromTo.setText(sb);

    }

    private Calendar getCalendar(){
        //DateFormat df = new SimpleDateFormat("hh:mm a");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal;
    }

    private void initPickerViews(){

        hourPos1 = Calendar.getInstance().get(Calendar.HOUR)-1;
        hourPos2 = Calendar.getInstance().get(Calendar.HOUR)-1;
        DateFormat df = new SimpleDateFormat("hh:mm a");

        Calendar cal = getCalendar();
        int startDate = cal.get(Calendar.DATE);

        while (cal.get(Calendar.DATE) == startDate) {
            String timeSlot = df.format(cal.getTime()).toUpperCase().replace(".","");
            timeSlotList.add(timeSlot);
            //System.out.println(timeSlot);
            cal.add(Calendar.MINUTE, TIME_INTERVEL);

            /*String timeSlot2 = df.format(cal.getTime()).toUpperCase().replace(".","");
            timeSlot2List.add(timeSlot2);*/
        }

        if(timeSlot!=null){
            hourPos1 = timeSlotList.indexOf(timeSlot.startTime);
        }

        timeSlot2List.clear();
        for (int pos =hourPos1+1 ; pos<timeSlotList.size(); pos++){
            timeSlot2List.add(timeSlotList.get(pos));
        }

        cal.set(Calendar.MINUTE, -1);
        String lastTimeSlot = df.format(cal.getTime()).toUpperCase().replace(".","");
        timeSlot2List.set(timeSlot2List.size()-1, lastTimeSlot);


        if(timeSlot!=null){
            hourPos1 = timeSlotList.indexOf(timeSlot.startTime);
            hourPos2 = timeSlot2List.indexOf(timeSlot.endTime);
        }

        picker_hour1.setDataList(timeSlotList);
        picker_hour1.setInitPosition(hourPos1);

        picker_hour2.setDataList(timeSlot2List);
        picker_hour2.setInitPosition(hourPos2);
    }



    @Override
    public void onClick(View v) {

        if (v == contentView || v == cancelBtn) {
            dismissPopWin();
        } else if (v == confirmBtn) {

            if (null != mListener) {
                StringBuffer sb = new StringBuffer();
                String time1 = String.valueOf(timeSlotList.get(hourPos1));
                String time2 = String.valueOf(timeSlot2List.get(hourPos2));
                sb.append(time1);
                sb.append(" - ");
                sb.append(time2);
                mListener.onTimePickCompleted(time1,time2,sb.toString());
            }
            dismissPopWin();
        }
    }

    /**
     * Show time picker popWindow
     *
     * @param activity
     */
    public void showPopWin(Activity activity) {

        if (null != activity) {

            TranslateAnimation trans = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    0, Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0);

            showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
            trans.setDuration(400);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());
            pickerContainerV.startAnimation(trans);
        }
    }

    /**
     * Dismiss time picker popWindow
     */
    public void dismissPopWin() {

        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);

        trans.setDuration(300);
        trans.setInterpolator(new AccelerateInterpolator());
        trans.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                dismiss();
            }
        });

        pickerContainerV.startAnimation(trans);
    }

    /**
     * Transform int to String with prefix "0" if less than 10
     * @param num
     * @return
     */
    public static String format2LenStr(int num) {

        return (num < 10) ? "0" + num : String.valueOf(num);
    }

    public interface OnTimePickListener {

        /**
         * Listener when date been selected
         *
         * @param time
         */
         void onTimePickCompleted(String openingTime, String closingTime, String time);
    }
}

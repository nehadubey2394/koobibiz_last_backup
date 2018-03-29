package views.pickerview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.mualab.org.biz.R;

import views.pickerview.timepicker.TimePicker;

/**
 * Created by dharmraj on 23/1/18.
 */

public class MyTimePickerDialog extends AlertDialog implements OnClickListener {
    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Set' button).
     */
    public interface OnTimeSetListener {

        /**
         * @param view      The view associated with this listener.
         * @param hourOfDay The hour that was set.
         * @param minute    The minute that was set.
         */
        void onTimeSet(TimePicker view, int hourOfDay, int minute);
    }

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";

    private final TimePicker mTimePicker;
    private final OnTimeSetListener mCallback;

    private int mInitialHourOfDay = 1;
    private int mInitialMinute = 0;
    public static int mIntervel = 10;

    /**
     * @param context      Parent.
     * @param callBack     How parent is notified.
     */
    public MyTimePickerDialog(Context context,
                              OnTimeSetListener callBack, String title, int hours, int minute) {

        this(context, 0, callBack,title, hours, minute, 1, 23);
    }

    /**
     * @param context      Parent.
     * @param callBack     How parent is notified.
     */
    public MyTimePickerDialog(Context context,
                              OnTimeSetListener callBack, String title, int hours, int minute, int intervel) {

        this(context, 0, callBack,title, hours, minute, intervel, 23);
    }


    /**
     * @param context      Parent.
     * @param callBack     How parent is notified.
     */
    public MyTimePickerDialog(Context context,
                              OnTimeSetListener callBack, String title, int hours, int minute, int intervel, int mMaxHours) {

        this(context, 0, callBack,title, hours, minute, intervel, mMaxHours);
    }


    /**
     * @param context      Parent.
     * @param theme        the theme to apply to this dialog
     * @param callBack     How parent is notified.
     */
    public MyTimePickerDialog(Context context,
                              int theme,
                              OnTimeSetListener callBack, String title, int hours, int minute, int intervel, int mMaxHours) {
        super(context, theme);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCallback = callBack;
        mInitialHourOfDay = hours;
        mIntervel = intervel;
        mInitialMinute = minute/mIntervel;
        TimePicker.mMaxHours = mMaxHours;
        updateTitle(title);
        setButton(context.getText(R.string.time_set), this);
        setButton2(context.getText(R.string.cancel), (OnClickListener) null);
        //setIcon(android.R.drawable.ic_dialog_time);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.time_picker_dialog, null);
        setView(view);
        mTimePicker = view.findViewById(R.id.timePicker);

        // initialize state
        mTimePicker.setCurrentHour(mInitialHourOfDay);
        mTimePicker.setCurrentMinute(mInitialMinute);
        mTimePicker.clearFocus();
    }

    public void onClick(DialogInterface dialog, int which) {
        if (mCallback != null) {
            mTimePicker.clearFocus();
            mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                    mTimePicker.getCurrentMinute()*mIntervel);
        }
    }

    public void updateTime(int hourOfDay, int minutOfHour) {
        mTimePicker.setCurrentHour(hourOfDay);
        mTimePicker.setCurrentMinute(minutOfHour);
    }

    private void updateTitle(String title) {
        setTitle(title);
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, mTimePicker.getCurrentHour());
        state.putInt(MINUTE, mTimePicker.getCurrentMinute());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int hour = savedInstanceState.getInt(HOUR);
        int minute = savedInstanceState.getInt(MINUTE);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
    }

}

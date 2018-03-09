package views.pickerview.timepicker;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.Formatter;
import com.mualab.org.biz.R;
import java.util.Locale;
import static views.pickerview.MyTimePickerDialog.mIntervel;


/**
 * Created by dharmraj on 23/1/18.
 **/

public class TimePicker extends FrameLayout {

    public static final NumberPicker.Formatter TWO_DIGIT_FORMATTER = new Formatter() {
                @Override
                public String format(int value) {
                    return String.format(Locale.getDefault(),"%02d", value);
                }
            };

    public static final NumberPicker.Formatter FIVE_MINUTE_FORMATTER = new Formatter() {
        @Override
        public String format(int value) {
            int temp = value * mIntervel;
            return "" + temp;
            //return String.format(Locale.getDefault(),"%02d", temp);
        }
    };

    // state
    private int mCurrentHour = 0; // 0-23
    private int mCurrentMinute = 0; // 0-59

    // ui components
    private final NumberPicker mHourPicker;
    private final NumberPicker mMinutePicker;

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.time_picker_widget,
                this, // we are the parent
                true);

        // hour
        mHourPicker = findViewById(R.id.hour);
        mHourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mCurrentHour = newVal;
                if(newVal == 0){
                    mMinutePicker.setMinValue(1);
                }else if(oldVal==0){
                    mMinutePicker.setMinValue(0);
                }
            }
        });

        // digits of minute
        mMinutePicker = findViewById(R.id.minute);
        mMinutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker spinner, int oldVal, int newVal) {
                mCurrentMinute = newVal;
            }
        });

        //mMinutePicker.setOnLongPressUpdateInterval(1);
        // now that the hour/minute picker objects have been initialized, set
        // the hour range properly based on the 12/24 hour display mode.
        configurePickerRanges();
    }

    private void configurePickerRanges() {
        mHourPicker.setMinValue(0);
        mHourPicker.setMaxValue(23);
        mHourPicker.setFormatter(TWO_DIGIT_FORMATTER);

        mMinutePicker.setMinValue(0);
        mMinutePicker.setMaxValue((60/mIntervel)-1);
        mMinutePicker.setFormatter(FIVE_MINUTE_FORMATTER);

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mMinutePicker.setEnabled(enabled);
        mHourPicker.setEnabled(enabled);
    }

    /**
     * Used to save / restore state of time picker
     */
    private class SavedState extends BaseSavedState {

        private final int mHour;
        private final int mMinute;

        private SavedState(Parcelable superState, int hour, int minute) {
            super(superState);
            mHour = hour;
            mMinute = minute;
        }

        private SavedState(Parcel in) {
            super(in);
            mHour = in.readInt();
            mMinute = in.readInt();
        }

        public int getHour() {
            return mHour;
        }

        public int getMinute() {
            return mMinute;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mHour);
            dest.writeInt(mMinute);
        }

        public final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, mCurrentHour, mCurrentMinute);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setCurrentHour(ss.getHour());
        setCurrentMinute(ss.getMinute());
    }


    /**
     * @return The current hour (0-23).
     */
    public Integer getCurrentHour() {
        return mCurrentHour;
    }

    /**
     * Set the current hour.
     */
    public void setCurrentHour(Integer currentHour) {
        this.mCurrentHour = currentHour;
        updateHourDisplay();
    }

    /**
     * @return The current minute.
     */
    public Integer getCurrentMinute() {
        return mCurrentMinute;
    }

    /**
     * Set the current minute (0-59).
     */
    public void setCurrentMinute(Integer currentMinute) {
        this.mCurrentMinute = currentMinute;
        updateMinuteDisplay();
    }


    @Override
    public int getBaseline() {
        return mHourPicker.getBaseline();
    }

    /**
     * Set the state of the spinners appropriate to the current hour.
     */
    private void updateHourDisplay() {
        int currentHour = mCurrentHour;
        mHourPicker.setValue(currentHour);
    }

    /**
     * Set the state of the spinners appropriate to the current minute.
     */
    private void updateMinuteDisplay() {
        int currentMinute = mCurrentMinute;
        mMinutePicker.setValue(currentMinute);
    }
}

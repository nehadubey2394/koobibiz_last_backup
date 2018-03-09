package views.progressview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.mualab.org.biz.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmraj on 17/1/18.
 */

public class ProgressView extends LinearLayout {

    private final LinearLayout.LayoutParams PROGRESS_BAR_LAYOUT_PARAM = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
    private final LinearLayout.LayoutParams SPACE_LAYOUT_PARAM = new LinearLayout.LayoutParams(5, LinearLayout.LayoutParams.WRAP_CONTENT);

    private final List<Progress> progressBars = new ArrayList<>();
    private int pvCount = -1;
    /**
     * pointer of running animation
     */
    private int current = 0;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StoriesProgressView);
        pvCount = typedArray.getInt(R.styleable.StoriesProgressView_progressCount, 0);
        typedArray.recycle();
        bindViews();
    }

    private void bindViews() {
        progressBars.clear();
        removeAllViews();

        for (int i = 0; i < pvCount; i++) {
            final Progress p = createProgressBar();
            progressBars.add(p);
            addView(p);
            if ((i + 1) < pvCount) {
                addView(createSpace());
            }
        }

        setProgressIndex(0);
    }

    private Progress createProgressBar() {
        Progress p = new Progress(getContext());
        p.setLayoutParams(PROGRESS_BAR_LAYOUT_PARAM);
        return p;
    }

    private View createSpace() {
        View v = new View(getContext());
        v.setLayoutParams(SPACE_LAYOUT_PARAM);
        return v;
    }

    /**
     * Set story count and create views
     *
     * @param pvCount progressView count
     */
    public void setProgressViewCount(int pvCount) {
        this.pvCount = pvCount;
        bindViews();
    }

    /**
     * set Prev progress
     */
    public void Prev() {
        current = current--;
        if(progressBars.size()>0 && progressBars.size()<=current && current>0){
            progressBars.get(current).setSelect(true);
        }
    }

    /**
     * Reverse current story
     */
    public void next() {
        current = current++;
        if(progressBars.size()>0 && progressBars.size()<=current){
            progressBars.get(current).setSelect(true);
        }
    }


    public void setProgressIndex(final int index){
        if(progressBars.size()>0 && progressBars.size()>=index){
            resetView();
            progressBars.get(index).setSelect(true);
            current = index;
        }
    }

    private void resetView(){
        for(Progress progress :progressBars)
            progress.setSelect(false);
    }

    /**
     * Need to call when Activity or Fragment destroy
     */
    public void destroy() {
        for (Progress p : progressBars) {
            p.removeAllViews();
        }
    }
}
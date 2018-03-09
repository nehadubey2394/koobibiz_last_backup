package views.dotsview;

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
 * Created by dharmraj on 2/2/18.
 */

public class DotsView extends LinearLayout {

    private final LinearLayout.LayoutParams PROGRESS_BAR_LAYOUT_PARAM = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    private final LinearLayout.LayoutParams SPACE_LAYOUT_PARAM = new LinearLayout.LayoutParams(5, LinearLayout.LayoutParams.WRAP_CONTENT);

    private final List<Dots> dotsList = new ArrayList<>();
    private int pvCount = -1;
    /**
     * pointer of running animation
     */
    private int current = 0;

    public DotsView(Context context) {
        this(context, null);
    }

    public DotsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DotsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DotsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        setOrientation(LinearLayout.HORIZONTAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StoriesProgressView);
        pvCount = 0; //typedArray.getInt(R.styleable.StoriesProgressView_progressCount, 0);
        typedArray.recycle();
        bindViews();
    }

    private void bindViews() {
        dotsList.clear();
        removeAllViews();

        for (int i = 0; i < pvCount; i++) {
            final Dots p = createProgressBar();
            dotsList.add(p);
            addView(p);
            if ((i + 1) < pvCount) {
                addView(createSpace());
            }
        }

        setProgressIndex(0);
    }

    private Dots createProgressBar() {
        Dots p = new Dots(getContext());
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
        if (dotsList.size() > 0 && dotsList.size() <= current && current > 0) {
            dotsList.get(current).setSelect(true);
        }
    }

    /**
     * Reverse current story
     */
    public void next() {
        current = current++;
        if (dotsList.size() > 0 && dotsList.size() <= current) {
            dotsList.get(current).setSelect(true);
        }
    }


    public void setProgressIndex(final int index) {
        if (dotsList.size() > 0 && dotsList.size() >= index) {
            resetView();
            dotsList.get(index).setSelect(true);
            current = index;
        }
    }

    public int getSize() {
        return dotsList.size();
    }


    private void resetView() {
        for (Dots dots : dotsList)
            dots.setSelect(false);
    }

    /**
     * Need to call when Activity or Fragment destroy
     */
    public void destroy() {
        for (Dots d : dotsList) {
            d.removeAllViews();
        }
    }
}
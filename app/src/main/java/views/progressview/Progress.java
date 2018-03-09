package views.progressview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import com.mualab.org.biz.R;

/***
 * Dharmraj Acharya
 **/
final class Progress extends FrameLayout {
   // private View frontProgressView;
    private View maxProgressView;

    public Progress(Context context) {
        this(context, null);
    }

    public Progress(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Progress(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_progress_view, this);
       // frontProgressView = findViewById(R.id.unselected_progress);
        maxProgressView = findViewById(R.id.selected_progress); // work around
    }


    void setSelect(boolean isSelected) {
        maxProgressView.setVisibility(isSelected?View.VISIBLE:View.GONE);
    }
}

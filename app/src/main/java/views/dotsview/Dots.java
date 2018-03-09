package views.dotsview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.mualab.org.biz.R;

/**
 * Created by dharmraj on 2/2/18.
 */

public class Dots extends FrameLayout {

    // private View frontProgressView;
    private View maxProgressView;

    public Dots(Context context) {
        this(context, null);
    }

    public Dots(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Dots(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_dots_layout, this);
        // frontProgressView = findViewById(R.id.unselected_progress);
        maxProgressView = findViewById(R.id.selected_progress); // work around
    }


    void setSelect(boolean isSelected) {
        maxProgressView.setVisibility(isSelected?View.VISIBLE:View.GONE);
    }
}

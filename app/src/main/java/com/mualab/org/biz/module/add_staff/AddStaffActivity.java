package com.mualab.org.biz.module.add_staff;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.helper.MySnackBar;
import com.mualab.org.biz.module.BaseActivity;
import com.mualab.org.biz.module.add_staff.fragments.AddStaffFragment;

public class AddStaffActivity extends BaseActivity implements View.OnClickListener{
    private ImageView ivHeaderBack;
    private TextView tvHeaderTitle;


    public void setBackButtonVisibility(int visibility){
        if(ivHeaderBack!=null)
            ivHeaderBack.setVisibility(visibility);
    }

    public void setTitle(String text){
        if(tvHeaderTitle!=null)
            tvHeaderTitle.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);
        initView();
    }

    private void initView() {
        ivHeaderBack = findViewById(R.id.ivHeaderBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.text_staff));

        ivHeaderBack.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        replaceFragment(new AddStaffFragment(), false);
        super.onStart();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivHeaderBack:
                onBackPressed();
                break;
        }
    }


    private boolean doubleBackToExitPressedOnce;
    private Runnable runnable;

    @Override
    public void onBackPressed() {
          /* Handle double click to finish module*/
        Handler handler = new Handler();
        FragmentManager fm = getSupportFragmentManager();
        int i = fm.getBackStackEntryCount();
        if (i > 0) {
            fm.popBackStack();
        } else if (!doubleBackToExitPressedOnce) {

            doubleBackToExitPressedOnce = true;
            //Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();
            MySnackBar.showSnackbar(this, findViewById(R.id.lyCoordinatorLayout), "Click again to exit");
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        } else {
            handler.removeCallbacks(runnable);
            super.onBackPressed();
        }
    }
}

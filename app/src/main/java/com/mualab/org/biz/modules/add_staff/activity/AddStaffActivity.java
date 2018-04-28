package com.mualab.org.biz.modules.add_staff.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.add_staff.fragments.ArtistStaffFragment;

public class AddStaffActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvHeaderTitle;


    /*   public void setBackButtonVisibility(int visibility){
           if(ivHeaderBack!=null)
               ivHeaderBack.setVisibility(visibility);
       }
   */
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
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.text_staff));
        ivHeaderBack.setVisibility(View.VISIBLE);
        ivHeaderBack.setOnClickListener(this);
        addFragment(new ArtistStaffFragment(), true,R.id.flStffContainer);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivHeaderBack:
                onBackPressed();
                break;
        }
    }

    /* frangment replace code */
    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_in,0,0);
            transaction.add(containerId, fragment, backStackName);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }

    }


    @Override
    public void onBackPressed() {
        // Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flStffContainer);
        FragmentManager fm = getSupportFragmentManager();
        int i = fm.getBackStackEntryCount();
        if (i > 1) {
            fm.popBackStack();
        }else  {
            finish();
            super.onBackPressed();
        }
    }

}

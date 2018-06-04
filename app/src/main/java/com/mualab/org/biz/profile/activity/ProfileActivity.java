package com.mualab.org.biz.profile.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mualab.org.biz.R;
import com.mualab.org.biz.util.StatusBarUtil;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
    }
}

package com.mualab.org.biz.activity.authentication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mualab.org.biz.R;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.helper.MyToast;

public class ChooseUserTypeActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user_type);
       // if(ScreenUtils.hasSoftKeys(getWindowManager(), this)) findViewById(R.id.nevSoftBar).setVisibility(View.VISIBLE);
        findViewById(R.id.btnIndependent).setOnClickListener(this);
        findViewById(R.id.btnBusiness).setOnClickListener(this);

        findViewById(R.id.tvCustomerApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appPackageName = "com.mualab.org.user";//getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    MyToast.getInstance(ChooseUserTypeActivity.this).showSmallCustomToast(getString(R.string.under_development));
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnIndependent:
                geNextActivity(Constants.BUSINESS);
                break;
            case R.id.btnBusiness:
                geNextActivity(Constants.INDEPENDENT);
                break;
        }
    }

    private void geNextActivity(String registrationType){
        Intent intent = new Intent(this,RegistrationActivity.class);
        intent.putExtra(Constants.registrationType, registrationType);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(intent);
        finish();
    }
}

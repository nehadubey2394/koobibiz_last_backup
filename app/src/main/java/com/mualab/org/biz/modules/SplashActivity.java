package com.mualab.org.biz.modules;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mualab.org.biz.modules.authentication.LoginActivity;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.session.Session;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splash);

        Session session = Mualab.getInstance().getSessionManager();
        if(session.isLoggedIn()){
            if(session.isBusinessProfileComplete()){
                startActivity(new Intent(SplashActivity.this, MainActivity.class));

            }else
                startActivity(new Intent(SplashActivity.this, NewBaseActivity.class));
        }else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }finish();
    }




}

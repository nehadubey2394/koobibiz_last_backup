package com.mualab.org.biz.fcm;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseInstanceIdService";

    @SuppressLint("LongLogTag")
    @Override
    public void onTokenRefresh() {

        String refreshDiviceToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "onTokenRefresh: "+refreshDiviceToken );

    }
}

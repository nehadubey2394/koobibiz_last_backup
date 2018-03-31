package com.mualab.org.biz.application;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.mualab.org.biz.BuildConfig;
import com.mualab.org.biz.db.AppDatabase;
import com.mualab.org.biz.model.Location;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;

/**
 * Created by dharmraj on 21/12/17.
 **/

public class Mualab extends Application {

    public static boolean IS_DEBUG_MODE = BuildConfig.DEBUG;
    public static final String TAG = Mualab.class.getSimpleName();
    public static final String authToken = "authToken";

    private static Mualab mInstance;
    public static Location currentLocation;

    private  DatabaseReference ref;
    private  Session session;
    private PreRegistrationSession bpSession;
    private RequestQueue mRequestQueue;

    private static final String DATABASE_NAME = "MualabDb";
    private static final String PREFERENCES = "Room.preferences";
    private static final String KEY_FORCE_UPDATE = "force_update";
    private AppDatabase database;


    public static Mualab getInstance() {
        return mInstance;
    }

    public static Mualab get() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mInstance.getSessionManager();
        FirebaseApp.initializeApp(this);

        // create database
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build();

        // ref = FirebaseDatabase.getInstance().getReference();
    }

    public AppDatabase getDB(){
        return database;
    }

    public boolean isForceUpdate(){
        return true;
    }

    public Session getSessionManager() {
        if (session == null)
            session = new Session(getApplicationContext());
        return session;
    }

    public PreRegistrationSession getBusinessProfileSession() {
        if (bpSession == null)
            bpSession = new PreRegistrationSession(getApplicationContext());
        return bpSession;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void cancelAllPendingRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //  MultiDex.install(this);
    }
}

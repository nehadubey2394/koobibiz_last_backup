package com.mualab.org.biz.data;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mualab.org.biz.data.local.db.AppDbHelper;
import com.mualab.org.biz.data.local.db.DbHelper;
import com.mualab.org.biz.data.local.prefs.AppPreferencesHelper;
import com.mualab.org.biz.data.local.prefs.PreferencesHelper;
import com.mualab.org.biz.data.remote.ApiHelper;
import com.mualab.org.biz.data.remote.AppApiHelper;


/**
 * Created by hemant
 * Date: 10/4/18.
 */


public final class AppDataManager implements DataManager {

    private static AppDataManager instance;
    public final Gson mGson;
    private final ApiHelper mApiHelper;
    private final DbHelper mDbHelper;
    private final PreferencesHelper mPreferencesHelper;

    private AppDataManager(Context context) {
        mDbHelper = AppDbHelper.getDbInstance(context);
        mPreferencesHelper = new AppPreferencesHelper(context);
        mApiHelper = AppApiHelper.getAppApiInstance();
        mGson = new GsonBuilder().create();
    }

    public synchronized static AppDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new AppDataManager(context);
        }
        return instance;
    }

    /* App Api's Info */
    @Override
    public void logout(Activity activity) {
        mPreferencesHelper.logout(activity);
    }

    @Override
    public Boolean isLoggedIn() {
        return null;
    }
}

package com.mualab.org.biz.data.local.prefs;

import android.app.Activity;
import android.content.Context;

/**
 * Created by hemant
 * Date: 10/4/18.
 */

public final class AppPreferencesHelper implements PreferencesHelper {

    //Todo merge this
  /*  private static final String APP_PREFERENCE = "AppPreference";
    private static final String APP_REM_PREFERENCE = "AppRemPreference";

    private static final String PREF_KEY_USER_LOGGED_IN_MODE = "PREF_KEY_USER_LOGGED_IN_MODE";
    private static final String PREF_KEY_APP_LANGUAGE = "PREF_KEY_APP_LANGUAGE";

    //Remember me
    private static final String PREF_KEY_APP_REM_EMAIL = "PREF_KEY_APP_REM_EMAIL";
    private static final String PREF_KEY_APP_REM_PWD = "PREF_KEY_APP_REM_PWD";

    //User info
    private static final String PREF_KEY_APP_USER_ID = "PREF_KEY_APP_USER_ID";
    private static final String PREF_KEY_APP_NAME = "PREF_KEY_APP_NAME";
    private static final String PREF_KEY_APP_EMAIL = "PREF_KEY_APP_EMAIL";
    private static final String PREF_KEY_APP_CONT_NUM = "PREF_KEY_APP_CONT_NUM";
    private static final String PREF_KEY_APP_LOCATION = "PREF_KEY_APP_LOCATION";
    private static final String PREF_KEY_APP_LAT = "PREF_KEY_APP_LAT";
    private static final String PREF_KEY_APP_LNG = "PREF_KEY_APP_LNG";
    private static final String PREF_KEY_APP_DEVICE_TYPE = "PREF_KEY_APP_DEVICE_TYPE";
    private static final String PREF_KEY_APP_DEVICE_TOKEN = "PREF_KEY_APP_DEVICE_TOKEN";
    private static final String PREF_KEY_APP_SOCIAL_ID = "PREF_KEY_APP_SOCIAL_ID";
    private static final String PREF_KEY_APP_SOCIAL_TYPE = "PREF_KEY_APP_SOCIAL_TYPE";
    private static final String PREF_KEY_APP_AUTH_TOKEN = "PREF_KEY_APP_AUTH_TOKEN";
    private static final String PREF_KEY_APP_STATUS = "PREF_KEY_APP_STATUS";
    private static final String PREF_KEY_APP_PROFILE_IMAGE = "PREF_KEY_APP_PROFILE_IMAGE";
    private static final String PREF_KEY_APP_SUBSCRI_ID = "PREF_KEY_APP_SUBSCRI_ID";
    private static final String PREF_KEY_APP_SUBSCRI_PLAN = "PREF_KEY_APP_SUBSCRI_PLAN";

    //Local Data Flag
    private static final String PREF_KEY_APP_LOCAL_DATA = "PREF_KEY_APP_LOCAL_DATA";

    //Sync status for online and offline
    private static final String PREF_KEY_APP_ONLINE_OFFLINE = "PREF_KEY_APP_ONLINE_OFFLINE";

    //Sync Timer of half an hour flag
    private static final String PREF_KEY_APP_SYNC_TIMER_FLAG = "PREF_KEY_APP_SYNC_TIMER_FLAG";

    private final SharedPreferences mPrefs;
    private final SharedPreferences mRemPrefs;*/

    public AppPreferencesHelper(Context context) {
        //mPrefs = context.getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE);
        //mRemPrefs = context.getSharedPreferences(APP_REM_PREFERENCE, Context.MODE_PRIVATE);
    }

    @Override
    public Boolean isLoggedIn() {
        return false;//mPrefs.getBoolean(PREF_KEY_USER_LOGGED_IN_MODE, false);
    }

    @Override
    public void logout(Activity activity) {

    }

}

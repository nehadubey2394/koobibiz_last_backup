package com.mualab.org.biz.data.local.prefs;


import android.app.Activity;

/**
 * Created by hemant
 * Date: 10/4/18.
 */

public interface PreferencesHelper {

    Boolean isLoggedIn();

    void logout(Activity activity);
}

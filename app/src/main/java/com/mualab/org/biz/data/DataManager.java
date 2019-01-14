package com.mualab.org.biz.data;


import android.app.Activity;

import com.mualab.org.biz.data.local.db.DbHelper;
import com.mualab.org.biz.data.local.prefs.PreferencesHelper;
import com.mualab.org.biz.data.remote.ApiHelper;


/**
 * Created by hemant
 * Date: 10/4/18.
 */

public interface DataManager extends DbHelper, PreferencesHelper, ApiHelper {

    @Override
    void logout(Activity activity);

    @Override
    Boolean isLoggedIn();
}

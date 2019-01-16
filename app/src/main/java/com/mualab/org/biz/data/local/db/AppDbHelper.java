package com.mualab.org.biz.data.local.db;

import android.content.Context;


/**
 * Created by hemant
 * Date: 10/4/18.
 */

public final class AppDbHelper implements DbHelper {

    private static AppDbHelper instance;

    private final AppDatabase mAppDatabase;

    private AppDbHelper(Context context) {
        this.mAppDatabase = AppDatabase.getDatabaseInstance(context);
    }

    public synchronized static DbHelper getDbInstance(Context context) {
        if (instance == null) {
            instance = new AppDbHelper(context);
        }
        return instance;
    }

}

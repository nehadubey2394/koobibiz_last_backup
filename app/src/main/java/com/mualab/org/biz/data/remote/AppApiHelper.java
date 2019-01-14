package com.mualab.org.biz.data.remote;

/**
 * Created by hemant
 * Date: 10/4/18.
 */

public final class AppApiHelper implements ApiHelper {

    private final static String TAG = AppApiHelper.class.getSimpleName();

    private static AppApiHelper instance;

    public synchronized static AppApiHelper getAppApiInstance() {
        if (instance == null) {
            instance = new AppApiHelper();
        }
        return instance;
    }

}

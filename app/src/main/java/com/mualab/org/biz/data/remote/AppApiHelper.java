package com.mualab.org.biz.data.remote;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;

import java.util.HashMap;

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

    @Override
    public ANRequest doGetArtistBookingHistory(HashMap<String, String> header, HashMap<String, String> params) {
        return AndroidNetworking.post(Webservices.BOOKING_HISTORY)
                .addHeaders(header)
                .addBodyParameter(params)
                .setTag(TAG)
                .setPriority(Priority.HIGH)
                .build();
    }

    @Override
    public ANRequest doGetBookingDetail(HashMap<String, String> header, HashMap<String, String> params) {
        return AndroidNetworking.post(Webservices.BOOKING_DETAIL)
                .addHeaders(header)
                .addBodyParameter(params)
                .setTag(TAG)
                .setPriority(Priority.HIGH)
                .build();
    }
}

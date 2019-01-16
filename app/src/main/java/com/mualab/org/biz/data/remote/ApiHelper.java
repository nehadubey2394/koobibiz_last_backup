package com.mualab.org.biz.data.remote;


import com.androidnetworking.common.ANRequest;

import java.util.HashMap;

/**
 * Created by hemant
 * Date: 10/4/18.
 */

public interface ApiHelper {

    ANRequest doGetArtistBookingHistory(HashMap<String, String> header, HashMap<String, String> params);
}

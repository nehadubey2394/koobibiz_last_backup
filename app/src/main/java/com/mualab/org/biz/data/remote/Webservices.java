package com.mualab.org.biz.data.remote;

import com.mualab.org.biz.task.API;

/**
 * Created by hemant.
 * Date: 30/8/18
 * Time: 2:30 PM
 */

final class Webservices {

    //public static String BASE_URL = "http://koobi.co.uk:5000/api/";
    //public static String BASE_URL = "http://koobi.co.uk:3000/api/"; //live
    //private static String BASE_URL = "http://koobi.co.uk:8042/api/"; //dev

    //Booking Module
    static String BOOKING_HISTORY = API.BASE_URL + "artistBookingHistory";
    static String BOOKING_DETAIL = API.BASE_URL + "bookingDetail";

    private Webservices() {
        // This class is not publicly instantiable
    }
}

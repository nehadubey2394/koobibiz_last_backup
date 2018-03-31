package com.mualab.org.biz.model.booking;

import java.io.Serializable;

/**
 * Created by Mindiii on 3/28/2018.
 */

public class BookingInfo implements Serializable{
    public UserDetail userDetail;
    public String _Id,bookingPrice,serviceId,subServiceId,artistServiceId,biBookingDate,startTime,endTime,staffId,staffName,staffImage,artistServiceName,bookingDate,bookingStatus;
}

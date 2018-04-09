package com.mualab.org.biz.activity.booking.listner;

import com.mualab.org.biz.model.booking.BookingInfo;
import com.mualab.org.biz.model.booking.Bookings;

public interface OnStaffChangeListener {

	void onStaffSelect(int position, BookingInfo bookingInfo);

}

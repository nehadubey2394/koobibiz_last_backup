package com.mualab.org.biz.modules.booking.listner;
;
import com.mualab.org.biz.model.booking.Bookings;

public interface PendingBookingListener {

	void onActionClick(int position, Bookings bookingInfo, String text);

}

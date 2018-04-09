package com.mualab.org.biz.activity.booking.listner;

import com.mualab.org.biz.model.booking.Bookings;
import com.mualab.org.biz.model.booking.Staff;

;import java.util.List;

public interface OnBookingListListener {

	void onItemClick(int position, List<Staff> staffList, String id);

}

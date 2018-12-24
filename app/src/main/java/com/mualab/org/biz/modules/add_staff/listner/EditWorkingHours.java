package com.mualab.org.biz.modules.add_staff.listner;

import com.mualab.org.biz.model.TimeSlot;

import java.util.List;

public interface EditWorkingHours {

        void onHorusChange(String JsonArray,List<TimeSlot> slotList);

}

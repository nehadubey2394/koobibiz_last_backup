package com.mualab.org.biz.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BusinessDay implements Serializable {

    public int id;
    public int dayId;
    public String dayName;
    public boolean isOpen;
    public boolean isExpand;

    public ArrayList<TimeSlot> slots = new ArrayList<>();
    public ArrayList<TimeSlot> tempSlots = new ArrayList<>();


    public void addTimeSlot(TimeSlot slot){
        if(slots==null) slots= new ArrayList<>();
        slots.add(slot);

        if(tempSlots==null) tempSlots= new ArrayList<>();
        tempSlots.add(slot);
    }

    public int getTimeSlotSize(){
        if(slots!=null) return slots.size();
        return 0;
    }

   /* public static class TimeSlot{
        public int id = 1;
        public String startTime = "10:00 AM";
        public String endTime = "7:00 PM";
        public String slotTime = "10:00 AM - 7:00 PM";
    }
*/

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("dayName", dayName);
        result.put("slots", slots);
        return result;
    }
}

package com.mualab.org.biz.model;
import android.arch.persistence.room.Ignore;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by dharmraj on 5/1/18.
 */


public class TimeSlot implements Serializable {

    @SerializedName("_id")
    public int id = 1;

    @SerializedName("day")
    public int dayId = 1;

    @SerializedName("startTime")
    public String startTime = "10:00 AM";

    @SerializedName("endTime")
    public String endTime = "07:00 PM";

    @Ignore
    public String edtStartTime = "10:00 AM";
    @Ignore
    public String edtEndTime = "07:00 PM";

    public String minStartTime = "10:00 AM";
    public String maxEndTime = "07:00 PM";


    public boolean isCancle = false;


    // public String edtStartTime;
    //public String edtEndTime;

    public String slotTime = "10:00 AM - 07:00 PM";

    @SerializedName("status")
    public int status = 1;

    @Ignore
    public int bizdayPosition;

    public TimeSlot(int dayId){
        this.dayId = dayId;
    }

    public TimeSlot(int dayId,int bizdayPosition ){
        this.dayId = dayId;
        this.bizdayPosition = bizdayPosition;
    }

}

package com.mualab.org.biz.model.add_staff;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BusinessDayForStaff implements Serializable {

    public int day;
    @SerializedName("startTime")
    public String startTime = "10:00 AM";
    @SerializedName("endTime")
    public String endTime = "07:00 PM";
}

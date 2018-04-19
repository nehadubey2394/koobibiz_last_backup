package com.mualab.org.biz.model.add_staff;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dharmraj on 4/1/18.
 **/

public class BusinessDayForStaff implements Serializable {

    public int dayId;
    @SerializedName("startTime")
    public String startTime = "10:00 AM";
    @SerializedName("endTime")
    public String endTime = "07:00 PM";

}

package com.mualab.org.biz.model.add_staff;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mindiii on 2/6/2018.
 */

public class Services implements Serializable{
    public String serviceId;
    public boolean isOutCall;
    public String serviceName;
    public ArrayList<SubServices> arrayList = new ArrayList<>();
    public boolean isExpand = false;
    public  boolean isSubItemChecked = false;
}

package com.mualab.org.biz.model.add_staff;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Mindiii on 2/6/2018.
 */

public class SubServices implements Serializable{
    public  String _id,subServiceName,serviceId,subServiceId;
    public boolean isOutCall2;
    public   ArrayList<ArtistServices>artistservices = new ArrayList<>();
}

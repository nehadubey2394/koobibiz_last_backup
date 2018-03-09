package com.mualab.org.biz.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by dharmraj on 20/1/18.
 */

public class ParseService {

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("serviceList")
    public List<Service> serviceList;

    @SerializedName("artistService")
    public List<SubCategory> subCategory;

}

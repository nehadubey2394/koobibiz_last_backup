package com.mualab.org.biz.model;

import java.io.Serializable;

/**
 * Created by dharmraj on 10/1/18.
 */

public class Address implements Serializable{
    int id;
    public String city;
    public String state;
    public String country;
    public String stAddress1;
    public String stAddress2;
    public String placeName;
    //public String fullAddress;
    public String postalCode;
    public String latitude;
    public String longitude;

}

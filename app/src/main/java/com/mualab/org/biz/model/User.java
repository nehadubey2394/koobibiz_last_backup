package com.mualab.org.biz.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by dharmraj on 21/12/17.
 */

public class User implements Serializable{

    @SerializedName("_id")
    public String id;

    public String fullName;
    public String firstName;
    public String lastName;
    public String userName;
    public String businessName;
    public String gender;
    public String dob;
    public String profileImage;
    public String password;
    public String userType;
    //public String userType;

    public String countryCode;
    public String contactNo;
    public String email;

    public String city;
    public String state;
    public String country;
    public String address;
    public String address2;
    public String postalCode;
    public String latitude;
    public String longitude;

    public boolean otpVerified;
    public String otp;
    public String mailVerified;

    public String isLive;
    public String status;
    public String chatId;
    public String fireBaseId;
    public String socialId;
    public String firebaseToken;

    public String authToken;
    public String deviceToken;
    public String deviceType;

    @SerializedName("isDocument")
    public int isProfileComplete;

    /*Business profile*/
    public String businessType;
    public String bio;
    public String radius;
    public String serviceType;
    public String businesspostalCode;
    public String inCallpreprationTime;
    public String outCallpreprationTime;
    public String certificateCount;

    public BusinessProfile getBusinessProfie(){
        BusinessProfile bsp = new BusinessProfile();
        bsp.address = getBusinessAddress();
        bsp.businessDays = null;

        if(!TextUtils.isEmpty(this.radius))
            bsp.radius = (int) Double.parseDouble(this.radius);
        else{
            bsp.radius = 1;
            this.radius= "1";
        }

        if(!TextUtils.isEmpty(this.serviceType))
            bsp.serviceType = Integer.parseInt(this.serviceType);
        else{
            bsp.serviceType = 1;
            this.serviceType= "1";
        }
      /*  if(this.serviceType!=null)
            bsp.serviceType = Integer.parseInt(this.serviceType);*/

        bsp.inCallpreprationTime = this.inCallpreprationTime;
        bsp.outCallpreprationTime = this.outCallpreprationTime;
        bsp.bio = this.bio;

        if(!TextUtils.isEmpty(this.certificateCount))
            bsp.serviceType = Integer.parseInt(this.certificateCount);
        else{
            bsp.certificateCount = 0;
            this.certificateCount= "0";
        }

        /*if(this.certificateCount!=null)
            bsp.certificateCount = Integer.parseInt(this.certificateCount);*/

        return bsp;
    }

    public Address getBusinessAddress(){
        Address a = new Address();
        a.placeName = this.businessName;
        a.stAddress1 = this.address;
        a.stAddress2 = this.address2;
        //a.fullAddress = this.address;
        a.city = this.city;
        a.state = this.state;
        a.country = this.country;
        a.postalCode = this.postalCode;
        a.latitude = this.latitude;
        a.longitude = this.longitude;
        return a;
    }

    public Address setAddress(Address a){
        this.businessName = a.placeName;
        this.address = a.stAddress1;
        this.address2 = a.stAddress2;
        this.city = a.city;
        this.state = a.state;
        this.country = a.country;
        this.postalCode = a.postalCode;
        this.latitude = a.latitude;
        this.longitude = a.longitude;
        return a;
    }
}

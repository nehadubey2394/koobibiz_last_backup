package com.mualab.org.biz.model.booking;

import com.mualab.org.biz.model.add_staff.ArtistServices;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Company implements Serializable{
    public String _id,job,mediaAccess,holiday,businessId,artistId,businessName,userName,profileImage,address;
    public List<BusinessDayForStaff> staffHoursList = new ArrayList<>();
    public List<ArtistServices> staffService = new ArrayList<>();
}

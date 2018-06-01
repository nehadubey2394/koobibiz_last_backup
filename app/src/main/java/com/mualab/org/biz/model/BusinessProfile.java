package com.mualab.org.biz.model;

import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmraj on 24/1/18.
 */

public class BusinessProfile {

    public boolean isBusinessHoursAdded;
    public int serviceType = 1; // inCall = 1, outCall=2, both = 3;
    public int radius = 1; // min = 1 mile and max = 100 mile
    public int bankStatus = 0; //

    public String inCallpreprationTime;
    public String outCallpreprationTime;
    public String bio;
    public int certificateCount;

    public int stepComplete; // stepComplete is current fragment index, totel step is no of fragment count

    public User user;
    public Address address;
    public List<BusinessDay> businessDays;
    public List<BusinessDay> edtStaffDays;
    public List<BusinessDayForStaff> dayForStaffs = new ArrayList<>();;
    public List<SubCategory> subCategories = new ArrayList<>();
}

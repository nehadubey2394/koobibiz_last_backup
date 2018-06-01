package com.mualab.org.biz.model.company_management;

import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CompanyDetail implements Serializable{
    public String _id,job,mediaAccess,holiday,businessId,artistId,businessName,userName,profileImage,address;
    public List<BusinessDayForStaff> staffHoursList = new ArrayList<>();
    public List<ComapnySelectedServices> staffService = new ArrayList<>();
    public List<BusinessDay>businessDays = new ArrayList<>();

}

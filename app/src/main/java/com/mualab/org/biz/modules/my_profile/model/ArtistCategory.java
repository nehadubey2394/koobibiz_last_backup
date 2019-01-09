package com.mualab.org.biz.modules.my_profile.model;

import com.mualab.org.biz.model.add_staff.SubServices;

import java.util.ArrayList;

public class ArtistCategory {
    public String serviceId;
    public boolean isOutCall,isSelect = false;
    public String serviceName;
    public ArrayList<SubServices> arrayList = new ArrayList<>();
}

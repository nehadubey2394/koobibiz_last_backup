package com.mualab.org.biz.model.add_staff;

import java.io.Serializable;

public class SelectedServices implements Serializable{
    public String _id,artistId,businessId,serviceId,subserviceId,artistServiceId,
            inCallPrice,outCallPrice,completionTime,title;
    public boolean isHold;
}

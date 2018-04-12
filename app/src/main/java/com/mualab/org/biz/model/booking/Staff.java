package com.mualab.org.biz.model.booking;

import java.io.Serializable;

/**
 * Created by Mindiii on 3/29/2018.
 */

public class Staff implements Serializable{
    /*{
"staffId":3,
"satffName":"gautam",
"staffImage":"http://koobi.co.uk:3000/uploads/profile/1522044944762.jpg",
"staffServiceId":[
"71"
],
"job":"Beginner"
},*/
    public String _id,staffId,staffName,staffImage,job;
    public  boolean isSelected;
}

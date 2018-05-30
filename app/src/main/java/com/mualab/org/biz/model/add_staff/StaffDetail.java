package com.mualab.org.biz.model.add_staff;

import com.mualab.org.biz.model.BusinessDay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StaffDetail implements Serializable {
    public String _id,job,mediaAccess,holiday,userName,profileImage,staffId,businessId;

    public List<BusinessDayForStaff>staffHoursList = new ArrayList<>();
    public List<BusinessDay>businessDays = new ArrayList<>();
    public List<BusinessDay>edtStaffDays = new ArrayList<>();

    public List<AddedStaffServices> staffServices = new ArrayList<>();


    /*{
"_id":1,
"job":"Beginner",
"mediaAccess":"Admin",
"holiday":"5",
"staffInfo":{
"userName":"Sneha",
"profileImage":"http://koobi.co.uk:3000/uploads/profile/1523265058990.jpg",
"staffId":12
},
"staffHours":[
{
"day":0,
"startTime":"10:00 AM",
"endTime":"03:00 PM"
},
],
"businessId":26,
"staffService":[
{
"_id":10,
"staffId":4,
"inCallPrice":"80",
"outCallPrice":"90",
"completionTime":"00:50",
"status":1,
"deleteStatus":1,
"crd":"2018-04-12T09:14:48.721Z",
"upd":"2018-04-12T09:14:48.721Z",
"artistId":12,
"serviceId":1,
"subserviceId":2,
"artistServiceId":39,
"businessId":29,
"__v":0
},
{
"_id":1,
"staffId":1,
"inCallPrice":"1111",
"outCallPrice":"2222",
"completionTime":"00:50",
"status":1,
"deleteStatus":1,
"crd":"2018-04-12T07:44:42.343Z",
"upd":"2018-04-12T07:44:42.343Z",
"artistId":12,
"serviceId":1,
"subserviceId":2,
"artistServiceId":34,
"businessId":26,
"__v":0,
"title":" Black Hair Color"
},
{
"_id":2,
"staffId":1,
"inCallPrice":"1110",
"outCallPrice":"2220",
"completionTime":"00:40",
"status":1,
"deleteStatus":1,
"crd":"2018-04-12T07:44:42.343Z",
"upd":"2018-04-12T07:44:42.343Z",
"artistId":12,
"serviceId":1,
"subserviceId":2,
"artistServiceId":35,
"businessId":26,
"__v":0,
"title":"Red color"
},
{
"_id":9,
"staffId":4,
"inCallPrice":"123",
"outCallPrice":"345",
"completionTime":"00:50",
"status":1,
"deleteStatus":1,
"crd":"2018-04-12T09:14:48.721Z",
"upd":"2018-04-12T09:14:48.721Z",
"artistId":12,
"serviceId":1,
"subserviceId":2,
"artistServiceId":38,
"businessId":29,
"__v":0
}
]
}*/
}

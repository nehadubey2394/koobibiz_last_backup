package com.mualab.org.biz.model.company_management;

import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CompanyDetail implements Serializable{
    public String _id,job,mediaAccess,holiday,businessId,artistId,businessName,
            userName,profileImage,address,status;
    public List<BusinessDayForStaff> staffHoursList = new ArrayList<>();
    public List<ComapnySelectedServices> staffService = new ArrayList<>();
    public List<BusinessDay>businessDays = new ArrayList<>();

    /*{"_id":2,
"job":"Moderate",
"mediaAccess":"",
"holiday":"2",
"staffHours":[
{
"bizdayPosition":0,
"day":0,
"endTime":"07:00 PM",
"startTime":"10:00 AM"
},
{
"bizdayPosition":0,
"day":1,
"endTime":"07:00 PM",
"startTime":"10:00 AM"
},
{
"bizdayPosition":0,
"day":2,
"endTime":"07:00 PM",
"startTime":"10:00 AM"
},
{
"bizdayPosition":0,
"day":3,
"endTime":"07:00 PM",
"startTime":"10:00 AM"
},
{
"bizdayPosition":0,
"day":4,
"endTime":"07:00 PM",
"startTime":"10:00 AM"
},
{
"bizdayPosition":0,
"day":5,
"endTime":"07:00 PM",
"startTime":"10:00 AM"
}
],
"status":0,
"businessId":46,
"artistId":19,
"businessType":[
{
"serviceName":"Makeup"
}
],
"staffService":[
{
"_id":3,
"staffId":2,
"inCallPrice":"0.0",
"outCallPrice":"400.0",
"completionTime":"00:50",
"status":1,
"deleteStatus":1,
"crd":"2018-12-22T12:12:30.005Z",
"upd":"2018-12-22T12:12:30.005Z",
"businessId":46,
"artistId":19,
"serviceId":2,
"subserviceId":2,
"artistServiceId":24,
"__v":0,
"title":"clean up"
},
{
"_id":4,
"staffId":2,
"inCallPrice":"120.0",
"outCallPrice":"0.0",
"completionTime":"02:00",
"status":1,
"deleteStatus":1,
"crd":"2018-12-22T12:12:30.005Z",
"upd":"2018-12-22T12:12:30.005Z",
"businessId":46,
"artistId":19,
"serviceId":2,
"subserviceId":1,
"artistServiceId":23,
"__v":0,
"title":"bleaching"
},
{
"_id":5,
"staffId":2,
"inCallPrice":"500.0",
"outCallPrice":"600.0",
"completionTime":"01:00",
"status":1,
"deleteStatus":1,
"crd":"2018-12-22T12:12:30.005Z",
"upd":"2018-12-22T12:12:30.005Z",
"businessId":46,
"artistId":19,
"serviceId":2,
"subserviceId":1,
"artistServiceId":22,
"__v":0,
"title":"facial"
}
],
"businessName":"Girls saloon",
"userName":"neha",
"profileImage":"http://koobi.co.uk:8042/uploads/profile/1545117648664.jpg",
"address":"MINDIII Systems Pvt. Ltd. 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension"}*/

}

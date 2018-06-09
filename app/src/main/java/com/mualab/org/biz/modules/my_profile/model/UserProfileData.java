package com.mualab.org.biz.modules.my_profile.model;

import java.io.Serializable;

public class UserProfileData implements Serializable {
    /*"userDetail":[
{
"_id":20,
"firstName":"Sneha",
"lastName":"dubey",
"userName":"Sneha",
"businessName":"Sneha Herbal Beauty parlour",
"businesspostalCode":"",
"buildingNumber":"",
"businessType":"business",
"profileImage":"http://koobi.co.uk:3000/uploads/profile/1528103051813.jpg",
"email":"neha.mindiii@gmail.com",
"gender":"null",
"dob":"null",
"address":"Sangam Nagar Main Road, Vasundhara, Sangam Nagar, Indore, Madhya Pradesh 452006, India",
"address2":"",
"countryCode":"+91",
"contactNo":"8959756198",
"userType":"artist",
"followersCount":"0",
"followingCount":"0",
"serviceCount":"8",
"certificateCount":"2",
"postCount":"0",
"reviewCount":"0",
"ratingCount":"0",
"bio":"",
"serviceType":3
}
]*/
    public String _id,firstName,lastName,userName,businessName,businesspostalCode,buildingNumber,businessType,
            profileImage,email,gender,dob,address,address2,countryCode,contactNo,userType,followersCount,
            followingCount,serviceCount,certificateCount,postCount,reviewCount,ratingCount,bio,serviceType,isCertificateVerify;
}

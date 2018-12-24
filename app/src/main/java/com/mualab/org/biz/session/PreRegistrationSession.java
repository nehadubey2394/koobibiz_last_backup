package com.mualab.org.biz.session;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.SubCategory;
import com.mualab.org.biz.model.add_staff.StaffDetail;

import java.util.ArrayList;
import java.util.List;

public class PreRegistrationSession {

    private static final String PREF_NAME = "businessProfile";
    private Context _context;
    private SharedPreferences mypref;
    private SharedPreferences.Editor editor;
    private int radius;
    private int serviceType;

    {
        radius = 5;
        serviceType = 1;
    }


    public PreRegistrationSession(Context context) {
        this._context = context;
        mypref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = mypref.edit();
        editor.apply();
    }

    public void createBusinessProfile(BusinessProfile businessProfile) {
        Gson gson = new Gson();
        String json = gson.toJson(businessProfile); // myObject - instance of MyObject
        editor.putString("businessProfile", json);
        updateServiceType(businessProfile.serviceType);
        updateAddress(businessProfile.address);
        updateRadius(businessProfile.radius);
        updateIncallPreprationTime(businessProfile.inCallpreprationTime);
        updateOutcallPreprationTime(businessProfile.outCallpreprationTime);
        editor.apply();
    }

    public BusinessProfile getBusinessProfile() {
        Gson gson = new Gson();
        String string = mypref.getString("businessProfile", "");
        if (!string.isEmpty()){
            BusinessProfile businessProfile = gson.fromJson(string, BusinessProfile.class);
            businessProfile.address = getAddress();
            return businessProfile;
            // return businessProfile;
        }
        else return new BusinessProfile();
    }

/*    public List<BusinessDay> getBusinessHours() {
        businessProfile = getBusinessProfile();
        if(businessProfile!=null && businessProfile.businessDays!=null){
            return businessProfile.businessDays;
        }
        else return null;
    }*/

    public void setBusinessHours(List<BusinessDay> businessHour) {
        Gson gson = new Gson();
        String json = gson.toJson(businessHour); // myObject - instance of MyObject
        editor.putString("businessHours", json);
        editor.apply();
    }

    public void setEditedStaffHours(List<BusinessDay> businessHour) {
        Gson gson = new Gson();
        String json = gson.toJson(businessHour); // myObject - instance of MyObject
        editor.putString("edtStafBusinessHours", json);
        editor.apply();
    }

    public BusinessDay getEditedStaffHours(){
        Gson gson = new Gson();
        String string = mypref.getString("edtStafBusinessHours", "");
        if (!string.isEmpty()){
            return gson.fromJson(string, BusinessDay.class);
        }else return null;
    }

    public void setStaffBusinessHours(StaffDetail staffDetail) {
        Gson gson = new Gson();
        String json = gson.toJson(staffDetail); // myObject - instance of MyObject
        editor.putString("staffBusinessHours", json);
        editor.apply();
    }

    public StaffDetail getStaffBusinessHours(){
        Gson gson = new Gson();
        String string = mypref.getString("staffBusinessHours", "");
        if (!string.isEmpty()){
            return gson.fromJson(string, StaffDetail.class);
        }else return null;
    }

   /* public void setBusinessHours(List<BusinessDay> businessHour) {
        BusinessProfile businessProfile = getBusinessProfile();
        businessProfile = businessProfile!=null?businessProfile : new BusinessProfile();
        if(businessProfile.businessDays!=null){
            businessProfile.businessDays.clear();
            businessProfile.businessDays.addAll(businessHour);
        }else businessProfile.businessDays = businessHour;
        createBusinessProfile(businessProfile);
    }*/

    public void addCategory(List<SubCategory> categories) {
        BusinessProfile businessProfile = getBusinessProfile();
        businessProfile = businessProfile!=null?businessProfile : new BusinessProfile();
        if(businessProfile.subCategories!=null){
            businessProfile.subCategories.clear();
            businessProfile.subCategories.addAll(categories);
        }else businessProfile.subCategories = categories;
        createBusinessProfile(businessProfile);
    }

    public void addCategory(SubCategory subCategory) {
        BusinessProfile businessProfile = getBusinessProfile();
        businessProfile = businessProfile!=null?businessProfile : new BusinessProfile();
        List<SubCategory> subCategories = businessProfile.subCategories;

        if(subCategories!=null && subCategories.size()>0){

            for(SubCategory tmp :subCategories){
                if(tmp.serviceId == subCategory.serviceId
                        && tmp.categoryId==subCategory.categoryId
                        && tmp.name.equals(tmp.categoryName)){
                    tmp = subCategory;
                    break;
                }
            }
        }else{
            businessProfile.subCategories = new ArrayList<>();
            businessProfile.subCategories.add(subCategory);
        }
        createBusinessProfile(businessProfile);
    }


    /* Get business profile models*/

    public int getStepIndex(){
        return mypref.getInt("stepIndex", 0);
    }

    public String getBusinessName(){
        return mypref.getString("businessName","");
    }

    public int getBankStatus(){
        return mypref.getInt("bankStatus", 0);
    }

    public int getRadius(){
        return mypref.getInt("radius", radius);
    }

    public int getServiceType(){
        return mypref.getInt("serviceType", serviceType);
    }

    public Address getAddress(){
        Gson gson = new Gson();
        String string = mypref.getString("businessAddress", "");
        if (!string.isEmpty()){
            return gson.fromJson(string, Address.class);
        }else return null;
    }

    public BusinessDay getBusinessHours(){
        Gson gson = new Gson();
        String string = mypref.getString("businessHours", "");
        if (!string.isEmpty()){
            return gson.fromJson(string, BusinessDay.class);
        }else return null;
    }

    public String getInCallPreprationTime(){
        return mypref.getString("incallPreprationTime", "HH:MM");
    }

    public String getOutCallPreprationTime(){
        return mypref.getString("outcallPreprationTime", "HH:MM");
    }

    /* update business profile*/

    public void updateBusinessName(String businessName){
        editor.putString("businessName", businessName);
        editor.apply();
    }


    public void updateRegStep(int stepIndex){
        editor.putInt("stepIndex", stepIndex);
        editor.apply();
    }

    public void updateRadius(int radius){
        editor.putInt("radius", radius);
        editor.apply();
    }

    public void updateServiceType(int serviceType){
        editor.putInt("serviceType", serviceType);
        editor.commit();
        editor.apply();
    }

    public void updateBankStatus(int serviceType){
        editor.putInt("bankStatus", serviceType);
        editor.apply();
    }


    public void updateAddress(Address businessAddress){
        Gson gson = new Gson();
        String json = gson.toJson(businessAddress); // myObject - instance of MyObject
        editor.putString("businessAddress", json);
        editor.apply();
    }

    public void updateBusinessHours(BusinessDay businessDay){
        Gson gson = new Gson();
        String json = gson.toJson(businessDay); // myObject - instance of MyObject
        editor.putString("businessDay", json);
        editor.apply();
    }

    public void updateIncallPreprationTime(String incallPreprationTime){
        editor.putString("incallPreprationTime", incallPreprationTime);
        editor.apply();
    }

    public void updateOutcallPreprationTime(String outcallPreprationTime){
        editor.putString("outcallPreprationTime", outcallPreprationTime);
        editor.apply();
    }

    /* clear business profile*/
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}

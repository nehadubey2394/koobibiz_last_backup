package com.mualab.org.biz.modules.profile_setup.modle;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.mualab.org.biz.modules.profile_setup.converter.BusinessTypeConverter;
import com.mualab.org.biz.modules.profile_setup.converter.CategoryConverters;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "businessType")
@TypeConverters({BusinessTypeConverter.class})
public class MyBusinessType implements Serializable {

    public MyBusinessType(){

    }

    @Ignore
    public String bookingCount,deleteStatus,status,artistId, __v;
    @Ignore
    public boolean isChecked = false;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "serviceId")
    @SerializedName("serviceId")
    public int serviceId;

    @ColumnInfo(name = "serviceName")
    @SerializedName("serviceName")
    public String serviceName;

/*
    @SerializedName("description")
    public String description;
*/

    @SerializedName("subService")
    @TypeConverters({CategoryConverters.class})
    @Ignore
    public List<AddedCategory> categorys;

    @Ignore
    public boolean isSelected;

    public MyBusinessType(int id, String title/*,String description*/){
        this.serviceId = id;
        this.serviceName = title;
        //   this.description = description;
    }

   /* public void addCategory(AddedCategory category){
        if(categorys==null)
            categorys = new ArrayList<>();
        categorys.add(category);
    }

    public AddedCategory getCategory(int categoryId){
        if(categorys!=null){

            for (AddedCategory category : categorys){
                if(Integer.parseInt(category.subServiceId)==categoryId){
                    return category;
                }
            }
        }
        return null;
    }*/


    /*"bookingCount":"0",
"status":0,
"deleteStatus":1,
"_id":25,
"serviceId":16,
"serviceName":"Pediure",
"artistId":20,
"__v":0*/
}

package com.mualab.org.biz.modules.profile.modle;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.mualab.org.biz.db.converter.ServiceConverter;
import com.mualab.org.biz.modules.profile.converter.CategoryConverters;
import com.mualab.org.biz.modules.profile.db_modle.Services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*@Entity(tableName = "categorys", foreignKeys = @ForeignKey(entity = MyBusinessType.class,
        parentColumns = "_id",
        childColumns = "serviceId",
        onDelete = CASCADE))*/

@Entity (tableName = "categorys", indices = {@Index(value = {"serviceId"})},
        foreignKeys = @ForeignKey(entity = MyBusinessType.class,
                parentColumns = "serviceId",
                childColumns = "serviceId",
                onDelete = ForeignKey.CASCADE))


@TypeConverters({CategoryConverters.class})
public class AddedCategory implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("_id")
    @ColumnInfo(name = "_id")
    public int _id;

    @ColumnInfo(name = "serviceId")
    @SerializedName("serviceId")
    public int serviceId;

    @ColumnInfo(name = "subServiceId")
    @SerializedName("subServiceId")
    public String subServiceId;

    @ColumnInfo(name = "subServiceName")
    @SerializedName("subServiceName")
    public String subServiceName;

    @SerializedName("service")
    @Ignore
    public List<Services> servicesList = new ArrayList<>();

    public AddedCategory(){

    }

    public AddedCategory(int id, String categoryName){
        this._id = id;
        this.subServiceName = categoryName;
        //this.description = description;
    }

/*    public void addService(Services services){
        if(servicesList==null)
            servicesList = new ArrayList<>();
        services.subserviceName = this.subServiceName;
        servicesList.add(services);
    }

    public Services getService(int subCategoryId){
        if(servicesList!=null){
            for (Services services : servicesList){
                if(services.id==subCategoryId){
                    return services;
                }
            }
        }
        return null;
    }

    public boolean deleteSubCategory(Services subCategory){
        return servicesList.remove(subCategory);
    }*/

    @Ignore
    public String bookingCount,deleteStatus, status,artistId, __v;
    @Ignore
    public boolean isChecked = false;

    /*"status":1,
"deleteStatus":1,
"_id":24,
"serviceId":3,
"subServiceId":4,
"subServiceName":"Nail",
"artistId":21,
"__v":0*/
}

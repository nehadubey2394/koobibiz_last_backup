package com.mualab.org.biz.modules.profile_setup.db_modle;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.mualab.org.biz.db.converter.ServiceConverter;
import com.mualab.org.biz.modules.profile_setup.modle.MyBusinessType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "service")
@TypeConverters({ServiceConverter.class})
/*@Entity(tableName = "service", foreignKeys = @ForeignKey(entity = AddedCategory.class,
        parentColumns = "_id",
        childColumns = "subserviceId",
        onDelete = CASCADE))*/

/*@Entity (tableName = "service", indices = {@Index(value = {"subserviceId"})},
        foreignKeys = @ForeignKey(entity = AddedCategory.class,
                parentColumns = "_id",
                childColumns = "subserviceId",
                onDelete = ForeignKey.CASCADE))*/

public class Services implements Serializable{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    public int id;

    @ColumnInfo(name = "serviceId")
    @SerializedName("serviceId")
    public int serviceId;

    @ColumnInfo(name = "bizTypeName")
    @SerializedName("bizTypeName")
    public String bizTypeName;

    @ColumnInfo(name = "serviceName")
    @SerializedName("serviceName")
    public String serviceName = "";

    @ColumnInfo(name = "subserviceId")
    @SerializedName("subserviceId")
    public int subserviceId;

    @ColumnInfo(name = "subserviceName")
    @SerializedName("subserviceName")
    public String subserviceName = "";

    @ColumnInfo(name = "description")
    @SerializedName("description")
    public String description = "";

    @ColumnInfo(name = "inCallPrice")
    @SerializedName("inCallPrice")
    public double inCallPrice = 0.00;

    @ColumnInfo(name = "outCallPrice")
    @SerializedName("outCallPrice")
    public double outCallPrice = 0.00;

    @ColumnInfo(name = "completionTime")
    @SerializedName("completionTime")
    public String completionTime = "HH:MM";

    @ColumnInfo(name = "preprationTime")
    @SerializedName("preprationTime")
    public String preprationTime = "HH:MM";

    @ColumnInfo(name = "bookingType")
    @SerializedName("bookingType")
    public String bookingType = "";


    //for add staff
    @Ignore
    @SerializedName("isSelected")
    public boolean isSelected;

    @Ignore
    @SerializedName("edtCompletionTime")
    public String edtCompletionTime = "HH:MM";

    @Ignore
    @SerializedName("edtInCallPrice")
    public double edtInCallPrice = 0.00;

    @Ignore
    @SerializedName("edtOutCallPrice")
    public double edtOutCallPrice = 0.00;

    @Ignore
    @SerializedName("edtBookingType")
    public String edtBookingType = "";

    @Ignore
    public String isInCallEdited,isOutCallEdited;


    @Ignore
    @SerializedName("businessType")
    public List<MyBusinessType> businessType = new ArrayList<>();

    /* @Ignore
     public List<MyBusinessType> bizypeList = new ArrayList<>();
     @Ignore
     public List<AddedCategory> categoryList = new ArrayList<>();
 */
    public Services(){

    }

    @Ignore
    public Services(int id, String subCategoryName, String description){
        this.id = id;
        this.subserviceName = subCategoryName;
        this.description = description;
    }
}

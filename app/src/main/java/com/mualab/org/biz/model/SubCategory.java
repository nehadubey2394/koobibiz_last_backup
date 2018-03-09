package com.mualab.org.biz.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by dharmraj on 18/1/18.
 **/

@Entity(tableName = "subcategorys")
/*foreignKeys = @ForeignKey(entity = Category.class,
        parentColumns = "id",
        childColumns = "categoryId",
        onDelete = CASCADE),
        indices=@Index(value="categoryId")*/
public class SubCategory implements Serializable{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("_id")
    public int id = -1;

    @SerializedName("serviceId")
    public int serviceId;

    @ColumnInfo(name = "categoryId")
    @SerializedName("subserviceId")
    public int categoryId;

    @SerializedName("title")
    public String name = "";

    @SerializedName("subserviceName")
    public String categoryName = "";

    @SerializedName("serviceName")
    public String serviceName = "";

    @SerializedName("description")
    public String description = "";

    @SerializedName("inCallPrice")
    public double inCallPrice = 0.00;

    @SerializedName("outCallPrice")
    public double outCallPrice = 0.00;

    @SerializedName("completionTime")
    public String completionTime = "HH:MM";

    @SerializedName("preprationTime")
    public String preprationTime = "HH:MM";

    public SubCategory(){

    }

    @Ignore
    public SubCategory(int id, String subCategoryName, String description){
        this.id = id;
        this.name = subCategoryName;
        this.description = description;
    }
}

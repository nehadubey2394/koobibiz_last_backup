package com.mualab.org.biz.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by dharmraj on 18/1/18.
 */

@Entity(tableName = "categorys", foreignKeys = @ForeignKey(entity = Service.class,
                parentColumns = "id",
                childColumns = "serviceId",
                onDelete = CASCADE))

public class Category {

    @PrimaryKey(autoGenerate = true)
    @SerializedName("_id")
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "serviceId")
    @SerializedName("serviceId")
    public int serviceId;

    @SerializedName("serviceName")
    public String serviceName;

    @SerializedName("title")
    public String name;

    @SerializedName("image")
    public String image;

    @SerializedName("description")
    public String description;

    @SerializedName("subCategory")
    @Ignore
    public List<SubCategory> subCategories = new ArrayList<>();

    public Category(){

    }

    public Category(int id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void addSubCategory(SubCategory subCategory){
        if(subCategories==null)
            subCategories = new ArrayList<>();
        subCategory.categoryName = this.name;
        subCategories.add(subCategory);
    }

    public SubCategory getSubCategory(int subCategoryId){
        if(subCategories!=null){
            for (SubCategory subCategory : subCategories){
                if(subCategory.id==subCategoryId){
                    return subCategory;
                }
            }
        }
        return null;
    }

    public boolean deleteSubCategory(SubCategory subCategory){
        return subCategories.remove(subCategory);
    }
}

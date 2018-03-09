package com.mualab.org.biz.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.mualab.org.biz.db.converter.ServiceConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmraj on 18/1/18.
 **/

@Entity(tableName = "service")
@TypeConverters({ServiceConverter.class})
public class Service {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("_id")
    public int id;

    @ColumnInfo(name = "name")
    @SerializedName("title")
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("subService")
    public List<Category> categorys;

    @Ignore
    public boolean isSelected;

    public Service(int id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void addCategory(Category category){
        if(categorys==null)
            categorys = new ArrayList<>();
        categorys.add(category);
    }

    public Category getCategory(int categoryId){
        if(categorys!=null){

            for (Category category : categorys){
                if(category.id==categoryId){
                    return category;
                }
            }
        }
        return null;
    }
}

package com.mualab.org.biz.db.join;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import com.mualab.org.biz.model.Category;
import com.mualab.org.biz.model.Service;
import com.mualab.org.biz.model.SubCategory;

/**
 * Created by dharmraj on 8/2/18.
 */

@Entity(tableName = "SubCategoryJoin",
        primaryKeys = { "serviceId", "categoryId" ,"_id"},
        foreignKeys = {
                @ForeignKey(entity = Service.class,
                        parentColumns = "id",
                        childColumns = "serviceId"),
                @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId"),
                @ForeignKey(entity = SubCategory.class,
                        parentColumns = "_id",
                        childColumns = "categoryId")
        })
@Dao
public class SubCategoryJoin {

    public final int serviceId;
    public final int categoryId;
    public final int id;

    public SubCategoryJoin(final int serviceId, final int categoryId, final int _id) {
        this.serviceId = serviceId;
        this.categoryId = categoryId;
        this.id = _id;
    }

}

package com.mualab.org.biz.db.join;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import com.mualab.org.biz.model.Category;
import com.mualab.org.biz.model.Service;

/**
 * Created by dharmraj on 30/1/18.
 */
@Entity(tableName = "serviceJoin",
        primaryKeys = { "serviceId", "categoryId" },
        foreignKeys = {
                @ForeignKey(entity = Service.class,
                        parentColumns = "id",
                        childColumns = "serviceId"),
                @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId")
        })
@Dao
public class ServiceJoin {

    public final int serviceId;
    public final int categoryId;

    public ServiceJoin(final int serviceId, final int categoryId) {
        this.serviceId = serviceId;
        this.categoryId = categoryId;
    }

}

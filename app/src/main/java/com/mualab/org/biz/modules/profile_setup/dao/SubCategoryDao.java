package com.mualab.org.biz.modules.profile_setup.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mualab.org.biz.model.Service;
import com.mualab.org.biz.model.SubCategory;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by dharmraj on 30/1/18.
 */

@Dao
public interface SubCategoryDao {

    @Query("SELECT * FROM subcategorys")
    List<Service> getAll();

    @Query("SELECT * FROM subcategorys WHERE name LIKE :name LIMIT 1")
    Service findByName(String name);

    @Insert(onConflict = REPLACE)
    void insertAll(List<SubCategory> subCategories);

    @Insert(onConflict = REPLACE)
    void insert(SubCategory subCategory);

    @Update
    void update(SubCategory category);

    @Delete
    void delete(SubCategory category);
}

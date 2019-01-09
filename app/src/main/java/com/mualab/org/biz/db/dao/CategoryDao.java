package com.mualab.org.biz.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mualab.org.biz.model.Category;
import com.mualab.org.biz.model.Service;
import com.mualab.org.biz.model.SubCategory;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by dharmraj on 30/1/18.
 */

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM categorys")
    List<Service> getAll();

    @Query("SELECT * FROM categor" +
            "ys WHERE subServiceName LIKE :name LIMIT 1")
    Service findByName(String name);

    @Insert(onConflict = REPLACE)
    void insertAll(List<Category> subCategories);

    /*@Insert(onConflict = REPLACE)
    @Query("SELECT * FROM categorys.service")
    void insertAllWithRelation(List<Category> subCategories);*/

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);
}

package com.mualab.org.biz.modules.profile.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mualab.org.biz.modules.profile.db_modle.Services;
import com.mualab.org.biz.modules.profile.modle.AddedCategory;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface AddedCategoryDao {

    @Query("SELECT * FROM categorys")
    List<AddedCategory> getAll();

    @Query("SELECT * FROM categorys WHERE subServiceName LIKE :subServiceName LIMIT 1")
    AddedCategory findByName(String subServiceName);

    @Insert(onConflict = REPLACE)
    void insertAll(List<AddedCategory> subCategories);

    /*@Insert(onConflict = REPLACE)
    @Query("SELECT * FROM categorys.service")
    void insertAllWithRelation(List<Category> subCategories);*/

    @Update
    void update(AddedCategory category);

    @Delete
    void delete(AddedCategory category);

    @Delete
    void deleteAll(List<AddedCategory> addedCategories);
}

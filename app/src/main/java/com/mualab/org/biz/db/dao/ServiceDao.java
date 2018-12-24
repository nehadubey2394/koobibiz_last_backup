package com.mualab.org.biz.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mualab.org.biz.model.Service;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by dharmraj on 30/1/18.
 */

@Dao
public interface ServiceDao {

    @Query("SELECT * FROM service")
    List<Service> getAll();

    @Query("SELECT * FROM service WHERE serviceName LIKE :name LIMIT 1")
    Service findByName(String name);

    @Query("SELECT * FROM service WHERE id=:id")
    Service findById(int id);


    @Insert(onConflict = REPLACE)
    void insertAll(List<Service> products);

    @Update
    void update(Service product);

    @Delete
    void delete(Service product);

    @Query("DELETE FROM service")
    void deleteAll();
}

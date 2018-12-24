package com.mualab.org.biz.modules.profile.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mualab.org.biz.modules.profile.db_modle.Services;
import com.mualab.org.biz.modules.profile.modle.MyBusinessType;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ServicesDao {

    @Query("SELECT * FROM service")
    List<Services> getAll();

    @Query("SELECT * FROM service WHERE serviceName LIKE :serviceName LIMIT 1")
    Services findByName(String serviceName);

    @Query("SELECT * FROM service WHERE id=:id")
    Services findById(int id);


    @Insert(onConflict = REPLACE)
    void insertAll(List<Services> services);

    @Update(onConflict = REPLACE)
    void update(Services services);

    @Delete
    void delete(Services services);
}

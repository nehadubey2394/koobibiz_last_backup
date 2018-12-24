package com.mualab.org.biz.modules.profile.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.mualab.org.biz.modules.profile.modle.MyBusinessType;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;


@Dao
public interface BizTypeDao {

    @Query("SELECT * FROM businessType")
    List<MyBusinessType> getAll();

    @Query("SELECT * FROM businessType WHERE serviceId LIKE :serviceName LIMIT 1")
    MyBusinessType findByName(String serviceName);

    @Query("SELECT * FROM businessType WHERE serviceId=:serviceId")
    MyBusinessType findById(int serviceId);

    //getting all 3 table's data
/*@Query("SELECT * FROM businessType AS businessType JOIN categorys AS " +
        "categorys ON categorys.serviceId=businessType.serviceId JOIN service " +
        "AS service ON service.subserviceId=categorys.subServiceId")*/


    @Insert(onConflict = REPLACE)
    void insertAll(List<MyBusinessType> products);

    @Update
    void update(MyBusinessType product);

    @Delete
    void delete(MyBusinessType product);

    @Delete
    void deleteAll(List<MyBusinessType> myBusinessTypes);
}

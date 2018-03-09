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
public interface ServiceJoinDao {

    @Insert
    void insert(ServiceJoinDao serviceJoinDao);

    /*@Query("SELECT * FROM service INNER JOIN serviceJoin ON
            ser.id=user_repo_join.userId WHERE
            user_repo_join.repoId=:repoId")
            List<User> getUsersForRepository(final int repoId);

    @Query("SELECT * FROM repo INNER JOIN user_repo_join ON
            repo.id=user_repo_join.repoId WHERE
            user_repo_join.userId=:userId")
            List<Repo> getRepositoriesForUsers(final int userId);*/
}

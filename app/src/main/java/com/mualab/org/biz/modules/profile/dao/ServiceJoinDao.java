package com.mualab.org.biz.modules.profile.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

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

package com.mualab.org.biz.data.local.db;

import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by hemant
 * Date: 10/4/18.
 */

/*@Database(entities = {ExpenseProductMapping.class},
        version = 1, exportSchema = false)*/
//@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase mAppDatabase;

    synchronized static AppDatabase getDatabaseInstance(Context context) {
        if (mAppDatabase == null) {
            //Todo merge this
            /*mAppDatabase = Room.databaseBuilder(context, AppDatabase.class, "AgrinvestDatabase")
                    .fallbackToDestructiveMigration()
                    .build();*/
        }

        return mAppDatabase;
    }

}

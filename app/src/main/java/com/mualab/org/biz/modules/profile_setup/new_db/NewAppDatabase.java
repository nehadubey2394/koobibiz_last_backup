package com.mualab.org.biz.modules.profile_setup.new_db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.mualab.org.biz.modules.profile_setup.dao.AddedCategoryDao;
import com.mualab.org.biz.modules.profile_setup.dao.BizTypeDao;
import com.mualab.org.biz.modules.profile_setup.dao.ServicesDao;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;
import com.mualab.org.biz.modules.profile_setup.modle.AddedCategory;
import com.mualab.org.biz.modules.profile_setup.modle.MyBusinessType;

@Database(entities = {MyBusinessType.class, AddedCategory.class, Services.class},
        version = 4, exportSchema = false
)
public abstract class NewAppDatabase extends RoomDatabase {

    public abstract ServicesDao serviceDao();

    public abstract AddedCategoryDao categoryDao();

    public abstract BizTypeDao businessTypeDao();


   /* public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //database.execSQL("ALTER TABLE product " + " ADD COLUMN price INTEGER");
            // enable flag to force update products
        }
    };*/

}
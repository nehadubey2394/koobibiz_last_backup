package com.mualab.org.biz.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;

import com.mualab.org.biz.db.dao.CategoryDao;
import com.mualab.org.biz.db.dao.ServiceDao;
import com.mualab.org.biz.db.dao.SubCategoryDao;
import com.mualab.org.biz.model.Category;
import com.mualab.org.biz.model.Service;
import com.mualab.org.biz.model.SubCategory;

/**
 * Created by dharmraj on 30/1/18.
 */

@Database(entities = {Service.class, Category.class, SubCategory.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ServiceDao serviceDao();

    public abstract CategoryDao categoryDao();

    public abstract SubCategoryDao subCategoryDao();




    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            //database.execSQL("ALTER TABLE product " + " ADD COLUMN price INTEGER");
            // enable flag to force update products
        }
    };

}
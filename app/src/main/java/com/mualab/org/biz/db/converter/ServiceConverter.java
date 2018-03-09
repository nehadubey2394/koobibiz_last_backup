package com.mualab.org.biz.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mualab.org.biz.model.Category;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmraj on 30/1/18.
 */

public class ServiceConverter {

    @TypeConverter
    public static List<Category> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Category>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayLisr(List<Category> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}

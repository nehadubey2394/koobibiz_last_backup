package com.mualab.org.biz.modules.profile_setup.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mualab.org.biz.modules.profile_setup.modle.AddedCategory;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CategoryConverters {

    @TypeConverter
    public static ArrayList<AddedCategory> fromString(String value) {
        Type listType = new TypeToken<ArrayList<AddedCategory>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayLisr(ArrayList<AddedCategory> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}

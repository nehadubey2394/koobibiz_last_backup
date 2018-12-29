package com.mualab.org.biz.modules.profile_setup.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ServicesConverters {

    @TypeConverter
    public static ArrayList<Services> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Services>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayLisr(ArrayList<Services> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}

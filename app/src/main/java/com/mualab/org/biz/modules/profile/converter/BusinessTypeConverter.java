package com.mualab.org.biz.modules.profile.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mualab.org.biz.modules.profile.modle.AddedCategory;
import com.mualab.org.biz.modules.profile.modle.MyBusinessType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dharmraj on 30/1/18.
 */

public class BusinessTypeConverter {

    @TypeConverter
    public static List<MyBusinessType> fromString(String value) {
        Type listType = new TypeToken<ArrayList<MyBusinessType>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayLisr(List<MyBusinessType> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}

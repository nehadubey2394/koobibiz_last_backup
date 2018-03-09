package com.mualab.org.biz.model.serializer;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mualab.org.biz.model.SubCategory;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * Created by dharmraj on 8/2/18.
 */

public class SubCategorySerializer implements Serializable, JsonSerializer<SubCategory> {
    @Override
    public JsonElement serialize(SubCategory obj, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jObj = (JsonObject)new GsonBuilder().create().toJsonTree(obj);
        jObj.remove("id");
        jObj.remove("_id");
        //jObj.remove("categoryName");
       // jObj.remove("serviceName");
        jObj.remove("preprationTime");
        return jObj;
    }
}

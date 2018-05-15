package com.mualab.org.biz.model.serializer;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mualab.org.biz.model.TimeSlot;

import java.io.Serializable;
import java.lang.reflect.Type;


public class EditTimeSlotSerializer implements Serializable, JsonSerializer<TimeSlot> {

    @Override
    public JsonElement serialize(TimeSlot obj, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jObj = (JsonObject)new GsonBuilder().create().toJsonTree(obj);
        jObj.remove("slotTime");
        jObj.remove("id");
        jObj.remove("_id");
        jObj.remove("status");
        return jObj;
    }
}

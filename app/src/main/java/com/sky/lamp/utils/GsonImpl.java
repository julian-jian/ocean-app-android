package com.sky.lamp.utils;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by dongqiang on 2016/10/25.
 */

public class GsonImpl extends Json {
    private Gson mGson = null;
    private GsonBuilder mBuilder = null;

    public GsonImpl() {
        mBuilder = new GsonBuilder();
        mBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY);
//        mBuilder.serializeNulls();
        mGson = mBuilder.create();
    }

    @Override
    public String toJson(Object src) {
        try {
            return mGson.toJson(src);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    @Override
    public <T> T toObject(String json, Class<T> claxx) {
        try {
            return mGson.fromJson(json, claxx);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public <T> T toObject(byte[] bytes, Class<T> claxx) {
        try {
            return mGson.fromJson(new String(bytes), claxx);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public <T> List<T> toList(final String json, Class<T> classType, Type type ) {
        String j = json.replace("\\", "");
        try {
            List<T> list = mGson.fromJson(j, type);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

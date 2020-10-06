package com.sky.lamp.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * LSP、自定义
 */
public class CommandLightMode implements Serializable {
    @SerializedName("deviceID")
    public String mDeviceID;
    @SerializedName("parameters")
    public List<LightItemMode> mParameters = new ArrayList<>();
    @SerializedName("userID")
    public String mUserID;
    public String modelName;
}

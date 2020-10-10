package com.sky.lamp.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jun on 2017/9/12.
 */
public class Device {

    @SerializedName("deviceID")
    private String mDeviceSN;
    @SerializedName("onlineStatus")
    private String mOnlineStatus;
    @SerializedName("activeStatus")
    private String mActiveStatus;
    @SerializedName("temperature")
    private String mTemperature;
    @SerializedName("streamURL")
    private String mStreamUrl;

    public String getDeviceSN() {
        return mDeviceSN;
    }

    public void setDeviceSN(String deviceSN) {
        mDeviceSN = deviceSN;
    }

    public String getOnlineStatus() {
        return mOnlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        mOnlineStatus = onlineStatus;
    }

    public String getActiceStatus() {
        return mActiveStatus;
    }

    public void setActiceStatus(String acticeStatus) {
        mActiveStatus = acticeStatus;
    }

    public String getTemperature() {
        return mTemperature;
    }

    public void setTemperature(String temperature) {
        mTemperature = temperature;
    }

    public String getStreamUrl() {
        return mStreamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        mStreamUrl = streamUrl;
    }
}

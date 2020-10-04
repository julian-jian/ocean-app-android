package com.sky.lamp.bean;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 保存到本地数据
 */
public class CommandLightMode implements Serializable {
    @SerializedName("command")
    private String mCommand;
    @SerializedName("deviceID")
    private String mDeviceID;
    @SerializedName("parameters")
    private List<LightMode> mParameters;
    @SerializedName("userID")
    private String mUserID;

    public String getDeviceID() {
        return this.mDeviceID;
    }

    public void setDeviceID(String deviceID) {
        this.mDeviceID = deviceID;
    }

    public String getUserID() {
        return this.mUserID;
    }

    public void setUserID(String userID) {
        this.mUserID = userID;
    }

    public String getCommand() {
        return this.mCommand;
    }

    public void setCommand(String command) {
        this.mCommand = command;
    }

    public List<LightMode> getParameters() {
        return this.mParameters;
    }

    public void setParameters(List<LightMode> parameters) {
        this.mParameters = parameters;
    }
}

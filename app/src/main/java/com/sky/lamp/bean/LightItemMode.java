package com.sky.lamp.bean;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class LightItemMode implements Serializable {
    @SerializedName("id")
    private int mIndex;
    @SerializedName("white")
    private int mLight1Level;
    @SerializedName("blue")
    private int mLight2Level;
    @SerializedName("yellow")
    private int mLight3Level;
    @SerializedName("purple")
    private int mLight4Level;
    @SerializedName("uv")
    private int mLight5Level;
    @SerializedName("red")
    private int mLight6Level;
    @SerializedName("green")
    private int mLight7Level;
    @SerializedName("modeName")
    private String mModeName;
    @SerializedName("startTime")
    private String mStartTime;
    @SerializedName("endTime")
    private String mStopTime;

    public int getIndex() {
        return this.mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    public String getModeName() {
        return this.mModeName;
    }

    public void setModeName(String mode) {
        this.mModeName = mode;
    }

    public String getStartTime() {
        return this.mStartTime;
    }

    public void setStartTime(String startTime) {
        this.mStartTime = startTime;
    }

    public String getStopTime() {
        return this.mStopTime;
    }

    public void setStopTime(String stopTime) {
        this.mStopTime = stopTime;
    }

    public int getLight1Level() {
        return this.mLight1Level;
    }

    public void setLight1Level(int light1Level) {
        this.mLight1Level = light1Level;
    }

    public int getLight2Level() {
        return this.mLight2Level;
    }

    public void setLight2Level(int light2Level) {
        this.mLight2Level = light2Level;
    }

    public int getLight3Level() {
        return this.mLight3Level;
    }

    public void setLight3Level(int light3Level) {
        this.mLight3Level = light3Level;
    }

    public int getLight4Level() {
        return this.mLight4Level;
    }

    public void setLight4Level(int light4Level) {
        this.mLight4Level = light4Level;
    }

    public int getLight5Level() {
        return this.mLight5Level;
    }

    public void setLight5Level(int light5Level) {
        this.mLight5Level = light5Level;
    }

    public int getLight6Level() {
        return this.mLight6Level;
    }

    public void setLight6Level(int light6Level) {
        this.mLight6Level = light6Level;
    }

    public int getLight7Level() {
        return this.mLight7Level;
    }

    public void setLight7Level(int light7Level) {
        this.mLight7Level = light7Level;
    }
}

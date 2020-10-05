package com.sky.lamp.bean;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class LightModeInfo implements Parcelable, Serializable {
    public static final Creator<LightModeInfo> CREATOR = new Creator<LightModeInfo>() {
        public LightModeInfo createFromParcel(Parcel source) {
            return new LightModeInfo(source);
        }

        public LightModeInfo[] newArray(int size) {
            return new LightModeInfo[size];
        }
    };
    private int mIndex;
    private int mLight1Level;
    private int mLight2Level;
    private int mLight3Level;
    private int mLight4Level;
    private int mLight5Level;
    private int mLight6Level;
    private int mLight7Level;
    private String mModeName;
    private String mStartTime;
    private String mStopTime;

    public LightModeInfo() {

    }

    protected LightModeInfo(Parcel in) {
        this.mIndex = in.readInt();
        this.mModeName = in.readString();
        this.mStartTime = in.readString();
        this.mStopTime = in.readString();
        this.mLight1Level = in.readInt();
        this.mLight2Level = in.readInt();
        this.mLight3Level = in.readInt();
        this.mLight4Level = in.readInt();
        this.mLight5Level = in.readInt();
        this.mLight6Level = in.readInt();
        this.mLight7Level = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mIndex);
        dest.writeString(this.mModeName);
        dest.writeString(this.mStartTime);
        dest.writeString(this.mStopTime);
        dest.writeInt(this.mLight1Level);
        dest.writeInt(this.mLight2Level);
        dest.writeInt(this.mLight3Level);
        dest.writeInt(this.mLight4Level);
        dest.writeInt(this.mLight5Level);
        dest.writeInt(this.mLight6Level);
        dest.writeInt(this.mLight7Level);
    }

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

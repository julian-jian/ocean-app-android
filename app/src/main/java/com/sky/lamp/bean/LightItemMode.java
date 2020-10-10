package com.sky.lamp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class LightItemMode  {
    @Id
    private Long id;
    public int index;
    public int light1Level = 50;
    public int light2Level = 50;
    public int light3Level = 50;
    public int light4Level = 50;
    public int light5Level = 50;
    public int light6Level = 50;
    public int light7Level = 50;
    public String modeName;//模式1
    public String startTime = "00:00";
    public String stopTime = "23:59";
    public long parent_id;
    @Generated(hash = 1201714956)
    public LightItemMode(Long id, int index, int light1Level, int light2Level,
            int light3Level, int light4Level, int light5Level, int light6Level,
            int light7Level, String modeName, String startTime, String stopTime,
            long parent_id) {
        this.id = id;
        this.index = index;
        this.light1Level = light1Level;
        this.light2Level = light2Level;
        this.light3Level = light3Level;
        this.light4Level = light4Level;
        this.light5Level = light5Level;
        this.light6Level = light6Level;
        this.light7Level = light7Level;
        this.modeName = modeName;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.parent_id = parent_id;
    }
    @Generated(hash = 1621573110)
    public LightItemMode() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getIndex() {
        return this.index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public int getLight1Level() {
        return this.light1Level;
    }
    public void setLight1Level(int light1Level) {
        this.light1Level = light1Level;
    }
    public int getLight2Level() {
        return this.light2Level;
    }
    public void setLight2Level(int light2Level) {
        this.light2Level = light2Level;
    }
    public int getLight3Level() {
        return this.light3Level;
    }
    public void setLight3Level(int light3Level) {
        this.light3Level = light3Level;
    }
    public int getLight4Level() {
        return this.light4Level;
    }
    public void setLight4Level(int light4Level) {
        this.light4Level = light4Level;
    }
    public int getLight5Level() {
        return this.light5Level;
    }
    public void setLight5Level(int light5Level) {
        this.light5Level = light5Level;
    }
    public int getLight6Level() {
        return this.light6Level;
    }
    public void setLight6Level(int light6Level) {
        this.light6Level = light6Level;
    }
    public int getLight7Level() {
        return this.light7Level;
    }
    public void setLight7Level(int light7Level) {
        this.light7Level = light7Level;
    }
    public String getModeName() {
        return this.modeName;
    }
    public void setModeName(String modeName) {
        this.modeName = modeName;
    }
    public String getStartTime() {
        return this.startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getStopTime() {
        return this.stopTime;
    }
    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }
    public long getParent_id() {
        return this.parent_id;
    }
    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }
}

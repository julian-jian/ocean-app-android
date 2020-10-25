package com.sky.lamp.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class BindDeviceBean {
    @Id(autoincrement = true)
    public Long id;
    public String deviceId;
    public String userId;
    public boolean isChecked;
    public boolean isDel;

    @Generated(hash = 1189619983)
    public BindDeviceBean(Long id, String deviceId, String userId,
            boolean isChecked, boolean isDel) {
        this.id = id;
        this.deviceId = deviceId;
        this.userId = userId;
        this.isChecked = isChecked;
        this.isDel = isDel;
    }
    @Generated(hash = 1361397984)
    public BindDeviceBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDeviceId() {
        return this.deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceSN() {
        return deviceId;
    }
    public boolean getIsChecked() {
        return this.isChecked;
    }
    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
    public boolean getIsDel() {
        return this.isDel;
    }
    public void setIsDel(boolean isDel) {
        this.isDel = isDel;
    }
}

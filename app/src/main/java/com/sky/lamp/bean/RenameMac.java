package com.sky.lamp.bean;

import java.util.Objects;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
@Entity
public class RenameMac {
    @Id(autoincrement = true)
    private Long id;
    public String mac;
    public String name;
    @Generated(hash = 1568625673)
    public RenameMac(Long id, String mac, String name) {
        this.id = id;
        this.mac = mac;
        this.name = name;
    }
    @Generated(hash = 1995003529)
    public RenameMac() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RenameMac renameMac = (RenameMac) o;
        return  Objects.equals(mac, renameMac.mac) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mac, name);
    }
}

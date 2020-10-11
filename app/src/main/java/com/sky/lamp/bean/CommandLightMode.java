package com.sky.lamp.bean;

import java.util.List;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import com.sky.lamp.dao.CommandLightModeDao;
import com.sky.lamp.dao.DaoSession;
import com.sky.lamp.dao.LightItemModeDao;

/**
 * LSP、自定义
 */
@Entity
public class CommandLightMode {
    @Id(autoincrement = true)
    public Long id;
    public String mDeviceID;
    @ToMany(referencedJoinProperty = "parent_id")
    public List<LightItemMode> mParameters;
    public String mUserID;
    public String modelName;
    public String t1;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 564579061)
    private transient CommandLightModeDao myDao;
    @Generated(hash = 1481888654)
    public CommandLightMode(Long id, String mDeviceID, String mUserID, String modelName, String t1) {
        this.id = id;
        this.mDeviceID = mDeviceID;
        this.mUserID = mUserID;
        this.modelName = modelName;
        this.t1 = t1;
    }
    @Generated(hash = 2008176647)
    public CommandLightMode() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMDeviceID() {
        return this.mDeviceID;
    }
    public void setMDeviceID(String mDeviceID) {
        this.mDeviceID = mDeviceID;
    }
    public String getMUserID() {
        return this.mUserID;
    }
    public void setMUserID(String mUserID) {
        this.mUserID = mUserID;
    }
    public String getModelName() {
        return this.modelName;
    }
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 2096569075)
    public List<LightItemMode> getMParameters() {
        if (mParameters == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LightItemModeDao targetDao = daoSession.getLightItemModeDao();
            List<LightItemMode> mParametersNew = targetDao
                    ._queryCommandLightMode_MParameters(id);
            synchronized (this) {
                if (mParameters == null) {
                    mParameters = mParametersNew;
                }
            }
        }
        return mParameters;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 172874207)
    public synchronized void resetMParameters() {
        mParameters = null;
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2082924777)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCommandLightModeDao() : null;
    }
    public String getT1() {
        return this.t1;
    }
    public void setT1(String t1) {
        this.t1 = t1;
    }
}

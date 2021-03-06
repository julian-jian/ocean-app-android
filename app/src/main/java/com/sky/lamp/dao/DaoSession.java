package com.sky.lamp.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.sky.lamp.bean.RenameMac;
import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.bean.BindDeviceBean;

import com.sky.lamp.dao.RenameMacDao;
import com.sky.lamp.dao.CommandLightModeDao;
import com.sky.lamp.dao.LightItemModeDao;
import com.sky.lamp.dao.BindDeviceBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig renameMacDaoConfig;
    private final DaoConfig commandLightModeDaoConfig;
    private final DaoConfig lightItemModeDaoConfig;
    private final DaoConfig bindDeviceBeanDaoConfig;

    private final RenameMacDao renameMacDao;
    private final CommandLightModeDao commandLightModeDao;
    private final LightItemModeDao lightItemModeDao;
    private final BindDeviceBeanDao bindDeviceBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        renameMacDaoConfig = daoConfigMap.get(RenameMacDao.class).clone();
        renameMacDaoConfig.initIdentityScope(type);

        commandLightModeDaoConfig = daoConfigMap.get(CommandLightModeDao.class).clone();
        commandLightModeDaoConfig.initIdentityScope(type);

        lightItemModeDaoConfig = daoConfigMap.get(LightItemModeDao.class).clone();
        lightItemModeDaoConfig.initIdentityScope(type);

        bindDeviceBeanDaoConfig = daoConfigMap.get(BindDeviceBeanDao.class).clone();
        bindDeviceBeanDaoConfig.initIdentityScope(type);

        renameMacDao = new RenameMacDao(renameMacDaoConfig, this);
        commandLightModeDao = new CommandLightModeDao(commandLightModeDaoConfig, this);
        lightItemModeDao = new LightItemModeDao(lightItemModeDaoConfig, this);
        bindDeviceBeanDao = new BindDeviceBeanDao(bindDeviceBeanDaoConfig, this);

        registerDao(RenameMac.class, renameMacDao);
        registerDao(CommandLightMode.class, commandLightModeDao);
        registerDao(LightItemMode.class, lightItemModeDao);
        registerDao(BindDeviceBean.class, bindDeviceBeanDao);
    }
    
    public void clear() {
        renameMacDaoConfig.clearIdentityScope();
        commandLightModeDaoConfig.clearIdentityScope();
        lightItemModeDaoConfig.clearIdentityScope();
        bindDeviceBeanDaoConfig.clearIdentityScope();
    }

    public RenameMacDao getRenameMacDao() {
        return renameMacDao;
    }

    public CommandLightModeDao getCommandLightModeDao() {
        return commandLightModeDao;
    }

    public LightItemModeDao getLightItemModeDao() {
        return lightItemModeDao;
    }

    public BindDeviceBeanDao getBindDeviceBeanDao() {
        return bindDeviceBeanDao;
    }

}

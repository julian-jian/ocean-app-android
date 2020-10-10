package com.sky.lamp.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.sky.lamp.bean.CommandLightMode;
import com.sky.lamp.bean.LightItemMode;

import com.sky.lamp.dao.CommandLightModeDao;
import com.sky.lamp.dao.LightItemModeDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig commandLightModeDaoConfig;
    private final DaoConfig lightItemModeDaoConfig;

    private final CommandLightModeDao commandLightModeDao;
    private final LightItemModeDao lightItemModeDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        commandLightModeDaoConfig = daoConfigMap.get(CommandLightModeDao.class).clone();
        commandLightModeDaoConfig.initIdentityScope(type);

        lightItemModeDaoConfig = daoConfigMap.get(LightItemModeDao.class).clone();
        lightItemModeDaoConfig.initIdentityScope(type);

        commandLightModeDao = new CommandLightModeDao(commandLightModeDaoConfig, this);
        lightItemModeDao = new LightItemModeDao(lightItemModeDaoConfig, this);

        registerDao(CommandLightMode.class, commandLightModeDao);
        registerDao(LightItemMode.class, lightItemModeDao);
    }
    
    public void clear() {
        commandLightModeDaoConfig.clearIdentityScope();
        lightItemModeDaoConfig.clearIdentityScope();
    }

    public CommandLightModeDao getCommandLightModeDao() {
        return commandLightModeDao;
    }

    public LightItemModeDao getLightItemModeDao() {
        return lightItemModeDao;
    }

}
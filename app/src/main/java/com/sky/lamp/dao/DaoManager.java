package com.sky.lamp.dao;

import android.annotation.SuppressLint;
import android.content.Context;

/**
 * Created by yechaoa on 2018/10/29.
 * Describe :
 */
public class DaoManager {

    private static final String DB_NAME = "order_db";
    private Context context;
    @SuppressLint("StaticFieldLeak")
    private volatile static DaoManager manager = new DaoManager();
    private static DaoMaster sDaoMaster;
    @SuppressLint("StaticFieldLeak")
    private static DaoMaster.DevOpenHelper sHelper;
    private static DaoSession sDaoSession;

    /**
     * 单例模式获得操作数据库对象
     */
    public static DaoManager getInstance() {
        return manager;
    }

    public void init(Context context) {
        this.context = context;
    }

    /**
     * 判断是否有存在数据库，如果没有则创建
     */
    private DaoMaster getDaoMaster() {
        if (sDaoMaster == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            sDaoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return sDaoMaster;
    }

    /**
     * 完成对数据库的添加、删除、修改、查询操作
     */
    public DaoSession getDaoSession() {
        if (sDaoSession == null) {
            if (sDaoMaster == null) {
                sDaoMaster = getDaoMaster();
            }
            sDaoSession = sDaoMaster.newSession();
        }
        return sDaoSession;
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public void closeConnection() {
        closeHelper();
        closeDaoSession();
    }

    private void closeHelper() {
        if (sHelper != null) {
            sHelper.close();
            sHelper = null;
        }
    }

    private void closeDaoSession() {
        if (sDaoSession != null) {
            sDaoSession.clear();
            sDaoSession = null;
        }
    }
}

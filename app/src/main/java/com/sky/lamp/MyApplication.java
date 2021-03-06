package com.sky.lamp;

import static com.sky.lamp.Constants.USERNAME;
import static com.sky.lamp.Constants.USER_ID;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sky.CrashHandler;
import com.sky.MyActivityLifecycleCallbacks;
import com.sky.lamp.bean.BindDeviceBean;
import com.sky.lamp.dao.BindDeviceBeanDao;
import com.sky.lamp.dao.DaoManager;
import com.sky.lamp.utils.RxSPUtilTool;
import com.vondear.rxtools.RxDeviceTool;
import com.vondear.rxtools.RxTool;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import app.socketlib.com.library.socket.MultiTcpManager;

//import android.support.multidex.MultiDex;

/**
 * Created by sky on 2017/12/19.
 */

public class MyApplication extends Application {

    private static MyApplication application;


    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
//        QbSdk.initX5Environment(this,null);
        RxTool.init(this);
        initImageLoader(this);
        DaoManager.getInstance().init(this);
        SocketManager.getInstance();
        MultiTcpManager.getInstance().init(this);
        new CrashHandler().init(this);
        registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks.getInstance());
    }

    /**
     * 初始化imageload
     * @param context
     */
    public void initImageLoader(Context context) {
        int screenWidth = RxDeviceTool.getScreenWidth(this);
        int screenHeight = RxDeviceTool.getScreenHeight(this);
        int maxImgWidth = screenWidth == 0 ? 720 : screenWidth;
        int maxImgHeight = screenHeight == 0 ? 720 : screenHeight;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).memoryCacheExtraOptions(maxImgWidth, maxImgHeight).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO) // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }
    /**
     * 获取Application
     *
     * @return
     */
    public static MyApplication getInstance()
    {
        return application;
    }

    public boolean isLogin() {
        String email = RxSPUtilTool.getString(getApplicationContext(), USERNAME);
        return !TextUtils.isEmpty(email);
    }

    public String getUserId() {
        String userId = RxSPUtilTool.getString(getApplicationContext(), USER_ID);
        return userId;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

    public List<BindDeviceBean> queryCurrentBindDevice() {
        String userId = RxSPUtilTool.getString(this, Constants.USER_ID);
        return DaoManager.getInstance().getDaoSession().getBindDeviceBeanDao().queryBuilder()
                .where(BindDeviceBeanDao.Properties.UserId.eq(userId)).list();
    }


}

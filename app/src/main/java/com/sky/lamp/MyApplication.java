package com.sky.lamp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sky.CrashHandler;
import com.vondear.rxtools.RxDeviceTool;
import com.vondear.rxtools.RxTool;

import android.app.Application;
import android.content.Context;

//import android.support.multidex.MultiDex;

/**
 * Created by sky on 2017/12/19.
 */

public class MyApplication extends Application {

    private static MyApplication application;
    public ExecutorService executorService = Executors.newSingleThreadExecutor();


    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
//        QbSdk.initX5Environment(this,null);
        RxTool.init(this);
        initImageLoader(this);
        new CrashHandler().init(this);
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }
}

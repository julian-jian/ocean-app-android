package com.sky.lamp.http;


import com.sky.lamp.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class AppService {
    //    https://06cb21c2.ngrok.io/AppSys/api?meth=login
    //测试地址
//    public static final String BASETESTURL = "http://117.48.214.179:8080/AppSys/";
    //    public static final String BASETESTURL = "http://xiaojinku.dzedai.com.cn:8080/AppSys/";
    public static final String BASETESTURL = "http://59.110.168.144/";
    //
    public static OkHttpClient okHttpClient;
    public static Retrofit retrofit;

    /*private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASETESTURL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();*/
    public static <T> T createApi(Class<T> clazz) {
        if (okHttpClient == null) {
            if (BuildConfig.LOG_DEBUG) {
                okHttpClient = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            } else {
                okHttpClient = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)).connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
            }
            retrofit = new Retrofit.Builder().baseUrl(BASETESTURL).addCallAdapterFactory(RxJavaCallAdapterFactory.create())//加入RX支持
                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        }

        return retrofit.create(clazz);
    }

    public static <T> T createStringApi(Class<T> clazz) {
        if (okHttpClient == null) {
            if (BuildConfig.LOG_DEBUG) {
                okHttpClient = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build();
            } else {
                okHttpClient = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)).build();
            }
            retrofit = new Retrofit.Builder().baseUrl(BASETESTURL).addCallAdapterFactory(RxJavaCallAdapterFactory.create())//加入RX支持
                    .client(okHttpClient).build();
        }
        return retrofit.create(clazz);
    }

    public Observable getObserver(Observable o) {
        return o.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }


}

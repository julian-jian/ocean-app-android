package com.sky.lamp.utils;

import com.sky.lamp.MyApplication;
import com.sky.lamp.R;
import com.vondear.rxtools.RxNetTool;
import com.vondear.rxtools.view.RxToast;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by sky on 2016/7/10.
 */
public abstract class MySubscriber<T> extends Subscriber<T> {

    @Override
    public void onError(Throwable error) {
        com.orhanobut.logger.Logger.e(error.getMessage());
        if (!RxNetTool.isNetworkAvailable(MyApplication.getInstance())) {
            RxToast.showToast("当前无网络，请检查");
        } else {
            if (error instanceof UnknownHostException) {
                RxToast.showToast(R.string.network_disable_please_check);
            } else if (error instanceof SocketTimeoutException) {
                RxToast.showToast("连接超时，请稍后再试");
            } else if (error instanceof HttpException) {
                RxToast.showToast(R.string.network_disable_please_check);
            } else if (error != null) {
                RxToast.showToast(R.string.server_error);
            }
        }
        error.printStackTrace();
    }
}

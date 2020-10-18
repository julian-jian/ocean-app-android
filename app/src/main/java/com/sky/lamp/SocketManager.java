package com.sky.lamp;

import static com.sky.lamp.utils.HexUtils.tenToHexByte;

import java.util.Calendar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.sky.lamp.utils.HexUtils;
import com.vondear.rxtools.view.RxToast;

import android.os.Handler;
import app.socketlib.com.library.ContentServiceHelper;
import app.socketlib.com.library.events.ConnectSuccessEvent;
import app.socketlib.com.library.socket.SocketConfig;
import app.socketlib.com.library.utils.Contants;

public class SocketManager {

    private static SocketManager INSTANCE;
    boolean mConnectSuccess;

    public SocketManager() {
        EventBus.getDefault().register(this);
    }

    public static SocketManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SocketManager();
        }
        return INSTANCE;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ConnectSuccessEvent event) {
        if (event.getConnectType() == Contants.CONNECT_SUCCESS_TYPE) {
            mConnectSuccess = true;
        } else {
            mConnectSuccess = false;
        }
    }

    public void bindSocket(String ip) {
        SocketConfig socketConfig =
                ContentServiceHelper.getConfig(MyApplication.getInstance(), ip);
        ContentServiceHelper.bindService(MyApplication.getInstance(), socketConfig);
    }

    public void unbindSocket() {
        ContentServiceHelper.unBindService(MyApplication.getInstance());
    }

    public boolean isConnect() {
        return mConnectSuccess;
    }


}

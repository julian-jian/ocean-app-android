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
            RxToast.showToast("建立连接成功");
            mConnectSuccess = true;
            sendSyncTimeCommand();
        } else {
            mConnectSuccess = false;
            RxToast.showToast("建立连接失败");
        }
    }

    public void bindSocket(String ip) {
        SocketConfig socketConfig =
                ContentServiceHelper.getConfig(MyApplication.getInstance(),ip);
        ContentServiceHelper.bindService(MyApplication.getInstance(), socketConfig);
    }

    public void unbindSocket() {
        ContentServiceHelper.unBindService(MyApplication.getInstance());
    }

    public boolean isConnect() {
        return mConnectSuccess;
    }

    private void sendSyncTimeCommand() {
        final byte[] temp = new byte[] {
                (byte) 0xaa, (byte) 0x09, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x55};
        Calendar calendar = Calendar.getInstance();
        // 应该不考虑时间
        temp[2] = tenToHexByte(calendar.get(Calendar.HOUR_OF_DAY));
        temp[3] = tenToHexByte(calendar.get(Calendar.MINUTE));
        // 检验位
        temp[14] = HexUtils.getVerifyCode(temp);
        System.out.println("sendCommand success " + HexUtils.bytes2Hex(temp));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ContentServiceHelper.sendClientMsg(temp);
            }
        }, 1000);
    }
}

package app.socketlib.com.library.socket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import android.content.Context;
import android.util.Log;
import app.socketlib.com.library.ContentServiceHelper;
import app.socketlib.com.library.events.ConnectClosedEvent;
import app.socketlib.com.library.utils.Contants;
import app.socketlib.com.library.utils.HexUtils;

public class MultiTcpManager {

    private static MultiTcpManager INSTANCE;
    private List<MultiTcpImpl> multiTcpList = new CopyOnWriteArrayList<>();
    private Context mContext;
    private List<String> mIpList;

    public MultiTcpManager() {
    }

    public static MultiTcpManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MultiTcpManager();
        }
        return INSTANCE;
    }

    public void init(Context context)    {
        mContext = context;
        EventBus.getDefault().register(this);
    }

    public void connect(List<String> ipList) {
        Log.w("SOCKET", "connect() called with: ipList = [" + ipList + "]");
        if (ipList == null) {
            return;
        }
        mIpList = ipList;
        List<SocketConfig> list = new ArrayList<>();
        for (String ip : ipList) {
            SocketConfig socketConfig =
                    ContentServiceHelper.getConfig(mContext, ip);
            list.add(socketConfig);
        }
        connectServer(list);
    }

    @Subscribe
    public void reconnect(ConnectClosedEvent connectClosedEvent) {
        Log.w("SOCKET", "reconnect");
        if (multiTcpList.size() > 0 && mIpList.size() > 0) {
            for (MultiTcpImpl multiTcp : multiTcpList) {
                multiTcp.disConnect();
            }
            connect(mIpList);
        }
    }

    public void connectServer(final List<SocketConfig> list) {
        multiTcpList.clear();
        for (final SocketConfig socketConfig : list) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MultiTcpImpl multiTcp =
                            new MultiTcpImpl(socketConfig, Contants.CONNECT_CLOSE_TYPE);
                    multiTcpList.add(multiTcp);
                    multiTcp.connnectToServer();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendSyncTimeCommand(multiTcp);
                }
            }).start();
        }
    }

    private void sendSyncTimeCommand(MultiTcpImpl multiTcp) {
        final byte[] temp = new byte[] {
                (byte) 0xaa, (byte) 0x09, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x55};
        Calendar calendar = Calendar.getInstance();
        // ?????????????????????
        temp[2] = HexUtils.tenToHexByte(calendar.get(Calendar.HOUR_OF_DAY));
        temp[3] = HexUtils.tenToHexByte(calendar.get(Calendar.MINUTE));
        // ?????????
        temp[14] = HexUtils.getVerifyCode(temp);
        multiTcp.send(temp);
        System.out.println("sendCommand success " + HexUtils.bytes2Hex(temp));
    }

    public void disConnect() {
        for (MultiTcpImpl multiTcp : multiTcpList) {
            multiTcp.disConnect();
        }
        multiTcpList.clear();
    }

    public void send(String msg) {
        for (MultiTcpImpl multiTcp : multiTcpList) {
            multiTcp.send(msg);
        }
    }

    public void send(byte[] msg) {
        for (MultiTcpImpl multiTcp : multiTcpList) {
            multiTcp.send(msg);
        }
    }

}

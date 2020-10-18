package app.socketlib.com.library.socket;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import app.socketlib.com.library.ContentServiceHelper;
import app.socketlib.com.library.utils.Contants;

public class MultiTcpManager {

    private static MultiTcpManager INSTANCE;
    private List<MultiTcpImpl> multiTcpList = new ArrayList<>();
    private Context mContext;

    public MultiTcpManager() {
    }

    public static MultiTcpManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MultiTcpManager();
        }
        return INSTANCE;
    }

    public void init(Context context) {
        mContext = context;
    }

    public void connect(List<String> ipList) {
        List<SocketConfig> list = new ArrayList<>();
        for (String ip : ipList) {
            SocketConfig socketConfig =
                    ContentServiceHelper.getConfig(mContext, ip);
            list.add(socketConfig);
        }
        connectServer(list);
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
                }
            }).start();
        }
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

package app.socketlib.com.library.events;

import java.util.HashMap;

/**
 * author：JianFeng
 * date：2017/8/18 16:13
 * description：socket连接成功,用于通知订阅重新订阅一次
 */
public class ConnectSuccessEvent {
    public String ip;
    public HashMap<String,String> ips = new HashMap<>();
    private int connectType;

    public ConnectSuccessEvent(int connectType) {
        this.connectType = connectType;
    }

    public ConnectSuccessEvent(int connectType, String ip) {
        this.connectType = connectType;
        this.ip = ip;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    public int getConnectType() {
        return connectType;
    }
}

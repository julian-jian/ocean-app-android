package app.socketlib.com.library.events;

/**
 * @author：JianFeng
 * @date：2017/10/31 17:14
 * @description：连接关闭的事件
 */
public class ConnectFailEvent {
    int colseType ;
    public String ip;

    public ConnectFailEvent() {
    }

    public ConnectFailEvent(String ip) {
        this.ip = ip;
    }

    public int getColseType() {
        return colseType;
    }

    public void setColseType(int colseType) {
        this.colseType = colseType;
    }
}

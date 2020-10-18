package app.socketlib.com.library.events;

/**
 * @author：JianFeng
 * @date：2017/10/31 17:14
 * @description：连接关闭的事件
 */
public class ConnectClosedEvent {
    public int colseType ;
    public String ip;
    public ConnectClosedEvent(int colseType) {
        this.colseType = colseType;
    }
    public ConnectClosedEvent(int colseType,String ip) {
        this.colseType = colseType;
        this.ip = ip;
    }

    public int getColseType() {
        return colseType;
    }

    public void setColseType(int colseType) {
        this.colseType = colseType;
    }
}

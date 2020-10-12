package app.socketlib.com.library;

import org.apache.mina.core.buffer.IoBuffer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import app.socketlib.com.library.socket.SocketConfig;
import app.socketlib.com.library.utils.Contants;
import app.socketlib.com.library.utils.SocketCommandCacheUtils;

/**
 * @author：JianFeng
 * @date：2017/10/31 15:29
 * @description：服务连接的管理类,服务绑定,解绑,数据发送等在此进行
 */
public class ContentServiceHelper {
    private static final String TAG = ContentServiceHelper.class.getSimpleName();
    private static ConnectServiceBinder socketBinder;//服务的binder


    private static ServiceConnection serviceConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            socketBinder = (ConnectServiceBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            socketBinder = null;
        }
    };


    //绑定服务
    public static boolean bindService(Context context, SocketConfig socketConfig) {
        Intent intent = new Intent(Contants.ACTION_SERVICE_CONTENT);
        intent.setPackage(context.getPackageName());
        Bundle bundle = new Bundle();
        bundle.putParcelable(Contants.STOCK_CONFIG_KEY, socketConfig);
        intent.putExtras(bundle);
        SocketCommandCacheUtils.getInstance().initCache(); //初始化command缓存
        return context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    //解绑服务
    public static void unBindService(Context context) {
        if (null != serviceConnection) {
            context.unbindService(serviceConnection);
            SocketCommandCacheUtils.getInstance().removeAllCache();//清除所有command缓存
        }
    }

    public static SocketConfig getConfig(Context context,String ip) {
        return new SocketConfig.Builder(context.getApplicationContext())
                .setIp(ip)//ip
                .setPort(61818)//端口
                .setReadBufferSize(1024)//readBuffer
                .setIdleTimeOut(30)//客户端空闲时间,客户端在超过此时间内不向服务器发送数据,则视为idle状态,则进入心跳状态
                .setTimeOutCheckInterval(60*10)//客户端连接超时时间,超过此时间则视为连接超时
                .setRequestInterval(10)//请求超时间隔时间
                //                .setHeartbeatRequest("(1,1)\r\n")//与服务端约定的发送过去的心跳包
                //                .setHeartbeatResponse("(10,10)\r\n") //与服务端约定的接收到的心跳包
                .builder();

    }

    /***
     * 发送客户端数据到service
     * @param msg
     */
    public static void sendClientMsg(String msg){
        sendMessage(socketBinder,msg);
    }

    public static void sendClientMsg(byte[] msg){
        sendMessage2(socketBinder, IoBuffer.wrap(msg));
    }

    private static void sendMessage2(ConnectServiceBinder connectBinder, Object msg) {
        if (null != connectBinder) {
            try {
                Log.d(TAG, "sendMessage " + msg);
                connectBinder.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //TODO 请先连接
//            SocketCommandCacheUtils.getInstance().addCache(msg);
        }
    }


    private static void sendMessage(ConnectServiceBinder connectBinder, String msg) {
        if (null != connectBinder) {
            try {
                Log.d(TAG, "sendMessage " + msg);
                connectBinder.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            SocketCommandCacheUtils.getInstance().addCache(msg);
        }
    }
}

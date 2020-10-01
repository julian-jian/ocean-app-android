package com.sky.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClientBiz {
    private String mServerIp = "192.168.4.2";//本机的地址，因为我是用模拟器的，本机地址，cmd工具中ipconfig即能看到
    private InetAddress mServerAddress;
    private int mServerPort = 61818;//端口号
    private DatagramSocket mSocket;

	//构造方法
    public UdpClientBiz(){
        try {
            mServerAddress = InetAddress.getByName(mServerIp);
            mSocket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	
    public interface OnMsgReturnedListener{
        void onMsgReturned(String msg);
        void onError(Exception ex);
    }

	//发送数据给服务端的方法 msg:发送的数据  OnMsgReturnedListener 接口是为了拿到收到的信息，因为你要发消息，肯定收消息的
    public void sendMsg(final String msg , final OnMsgReturnedListener listener){

		//因为是耗时操作，所以这边开了个子线程
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                	//下面一大堆和服务端的代码基本上一样，无非就是一个是从控制台输的数据，一个从页面输的
                    byte[] clientMsgBytes = msg.getBytes();
                    DatagramPacket clientPacket = new DatagramPacket(clientMsgBytes,
                            clientMsgBytes.length,
                            mServerAddress,
                            mServerPort);
                    mSocket.send(clientPacket);

                    byte[] buf = new byte[1024];
                    DatagramPacket serverMsgPacket = new DatagramPacket(buf, buf.length);
                    mSocket.receive(serverMsgPacket);
                    String serverMsg = new String(serverMsgPacket.getData(),0,serverMsgPacket.getLength());

                    listener.onMsgReturned(serverMsg);//收到的服务端消息

                } catch (Exception e) {//防止有错误，大家有错误不要老是去规避，要想办法将错误打印出来
                    listener.onError(e);
                }
            }
        }.start();
    }

	//释放一下资源也是极好的
    public void onDestroy(){
        if (mSocket != null) {
            mSocket.close();
        }

    }
}


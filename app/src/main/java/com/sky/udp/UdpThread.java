package com.sky.udp;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;

import com.sky.lamp.MainActivity;

/**
 * 后台监听 UDP 消息的子线程
 */
public class UdpThread implements Runnable {

    /**
     * TAG：日志标签
     * messageText：UDP 监听到的消息文本，每次不能超过 1024 字节
     * myHandler：用于给主线程发送消息的 Handler
     */
    private final String TAG = "Wmx Log::";
    private String messageText;
    public MainActivity.MyHandler myHandler;

    public UdpThread(MainActivity.MyHandler myHandler) {
        this.myHandler = myHandler;
    }

    @Override
    public void run() {
        Log.i("Wmx Logs:: ", "Udp 新线程开启..........." + Thread.currentThread().getName());
        DatagramSocket datagramSocket = null;
        /** 数据接收大小设置为 1024 字节，超出部分是接收不到的
         */
        byte[] buffer = new byte[1024];
        DatagramPacket datagramPacket;
        try {
            /**
             * InetSocketAddress(String hostname, int port)：网络套接字地址，同时指定监听的 ip 与 端口
             *      A valid port value is between 0 and 65535.
             * InetSocketAddress(int port)：网络套接字地址，指定监听的 端口，ip 默认为移动设备本机 ip
             * */
            InetSocketAddress socketAddress = new InetSocketAddress("192.168.4.1",61818);

            /**DatagramSocket(SocketAddress bindaddr)：根据绑定好的 SocketAddress 创建 UDP 数据包套接字
             * DatagramSocket(int port)：只指定 监听的端口时，IP 默认为移动设备本机 ip
             */
            datagramSocket = new DatagramSocket(socketAddress);
            datagramPacket = new DatagramPacket(buffer, buffer.length);

            /**循环监听*/
            while (true) {
                datagramSocket.receive(datagramPacket);
                /**读取数据
                 * 指定使用 UTF-8 编码，对于中文乱码问题，遵循对方发送时使用什么编码，则接收时也使用同样的编码的原则*/
                messageText = new String(datagramPacket.getData(), 0, datagramPacket.getLength(),
                        Charset.forName("UTF-8"));

                /**
                 * 可以创建一个新的 Message,但是推荐调用 handler 的 obtainMessage 方法获取 Message,
                 * 这个方法的作用是从系统的消息池中取出一个 Message,这样就可以避免 Message 创建和销毁带来的资源浪费。
                 *
                 */
                Message obtainMessage = myHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("messageText", messageText);
                /**为发送的消息设置数据*/
                obtainMessage.setData(bundle);
                /**发送消息*/
                myHandler.sendMessage(obtainMessage);
                Log.i("WMx Logs::",
                        " UDP 监听到消息>>>>>" + messageText + " >>> 线程：" + Thread.currentThread()
                                .getName() + ">>为主线程传输完毕...");
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (datagramSocket != null) {
                if (!datagramSocket.isConnected()) {
                    datagramSocket.disconnect();
                }
                if (!datagramSocket.isClosed()) {
                    datagramSocket.close();
                }
                /**即使抛异常了，也要再次监听*/
                new Thread(new UdpThread(myHandler)).start();
            }
        }
    }
}
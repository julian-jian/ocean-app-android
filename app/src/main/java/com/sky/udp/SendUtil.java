package com.sky.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.orhanobut.logger.Logger;

public class SendUtil {
    public static final String TAG = SendUtil.class.getSimpleName();
    public static void send(String ip, int port) {
        try {
            /*
             * 向服务器端发送数据
             */
            // 1.定义服务器的地址、端口号、数据
            // 注意 ip 要替换成你的
            InetAddress address = InetAddress.getByName(ip);
            byte[] b = new byte[16];
            b[0] = (byte) 0xAA;
            b[1] = (byte) 0x01;
            b[2] = (byte) 0x08;
            b[3] = (byte) 0x00;
            b[4] = (byte) 0x0C;
            b[5] = (byte) 0x00;
            b[6] = (byte) 0x32;
            b[7] = (byte) 0x32;
            b[8] = (byte) 0x32;
            b[9] = (byte) 0x32;
            b[10] = (byte) 0x32;
            b[11] = (byte) 0x32;
            b[12] = (byte) 0x32;
            b[13] = (byte) 0x32;
            b[14] = (byte) 0x4F;
            b[15] = (byte) 0x55;
            byte[] data = "00 0C 0032 32 32 32 32 32 32 32 4F55".getBytes();
            // 2.创建数据报，包含发送的数据信息
            DatagramPacket packet = new DatagramPacket(b, b.length, address, port);
            // 3.创建DatagramSocket对象
            DatagramSocket socket = new DatagramSocket();
            // 4.向服务器端发送数据报
            socket.send(packet);
            /*
             * 接收服务器端响应的数据
             */
            // 1.创建数据报，用于接收服务器端响应的数据
            byte[] data2 = new byte[10];
            DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
            // 2.接收服务器响应的数据
            socket.receive(packet2);
            // 3.读取数据
            String reply = new String(data2, 0, packet2.getLength());
            Logger.d(TAG, "chat() called with: ip = [" + ip + "], port = [" + port + "] "+reply);
            // 4.关闭资源
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

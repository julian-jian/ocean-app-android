package com.sky.lamp.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SendUdp extends Thread {
    private String dataToSend;
    private Handler mHandler;
    private String mIP;
    private DatagramSocket socket;

    public SendUdp(String ip, Handler handler) {
        this.mIP = ip;
        this.mHandler = handler;
    }

    public void run() {
        Message msg;
        String receiveContent = "";
        try {
            this.socket = new DatagramSocket();
            InetAddress server = InetAddress.getByName(this.mIP);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("--SEND UDP IpAddress--ip-->");
            stringBuilder.append(server.getHostAddress());
            Log.d("jun", stringBuilder.toString());
            String search = Common.UDP_SEARCH;
            this.socket.send(new DatagramPacket(search.getBytes(), search.length(), server, 8750));
            byte[] buffer = new byte[1024];
            DatagramPacket in = new DatagramPacket(buffer, buffer.length);
            this.socket.setSoTimeout(Common.SO_TIME_OUT);
            while (true) {
                this.socket.receive(in);
                receiveContent = new String(in.getData(), 0, in.getLength(), "UTF-8");
                stringBuilder = new StringBuilder();
                stringBuilder.append("--send udp receive-->");
                stringBuilder.append(receiveContent);
                Log.d("jun", stringBuilder.toString());
                msg = this.mHandler.obtainMessage();
                msg.what = 0;
                msg.obj = receiveContent;
                this.mHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = this.mHandler.obtainMessage();
            msg.what = 1;
            this.mHandler.sendMessage(msg);
        } finally {
            if (this.socket != null) {
                this.socket.close();
            }
        }
    }
}

package com.sky.lamp;

import org.junit.Test;

import com.sky.udp.SendUtil;

public class TestSend {
    @Test
    public void send() {
        SendUtil.send("192.168.4.1",61818);
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

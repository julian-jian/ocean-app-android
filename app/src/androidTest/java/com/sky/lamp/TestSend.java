package com.sky.lamp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Test;

import com.sky.udp.SendUtil;

public class TestSend {
    @Test
    public void send() {
                SendUtil.send("192.168.4.1",61818);
        List<String> list = new CopyOnWriteArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        List<String> list2 = new ArrayList<String>(list);
        list.remove(1);
//        System.out.println("TestSend.send " + list.size());
//        System.out.println("TestSend.send " + list2.size());

        for (String tmp : list) {
            System.out.println("TestSend.send " + tmp);
            list.remove(0);
        }

        //        try {
        //            Thread.sleep(1000*10);
        //        } catch (InterruptedException e) {
        //            e.printStackTrace();
        //        }
    }
}

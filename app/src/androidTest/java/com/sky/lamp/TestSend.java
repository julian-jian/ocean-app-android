package com.sky.lamp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Test;

import com.google.gson.Gson;
import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.bean.LightModelCache;
import com.sky.lamp.response.BindResponse;
import com.sky.lamp.response.GetBindDeviceResponse;
import com.sky.lamp.utils.RxSPUtilTool;

public class TestSend {
    @Test
    public void send() {
//                SendUtil.send("192.168.4.1",61818);
        List<String> list = new CopyOnWriteArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        List<String> list2 = new ArrayList<String>(list);
        list.remove(1);
        byte b = (byte) 0xFF;
        System.out.println("TestSend.send "+b);
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

    @Test
    public void testToByt() {
        byte[] bytes = HexStringToBinary("0xff");
        System.out.println("TestSend.testToByt");
    }
    private static String hexStr =  "0123456789ABCDEF";
    /**
     *
     * @param hexString
     * @return 将十六进制转换为字节数组
     */
    public static byte[] HexStringToBinary(String hexString){
        //hexString的长度对2取整，作为bytes的长度
        int len = hexString.length()/2;
        byte[] bytes = new byte[len];
        byte high = 0;//字节高四位
        byte low = 0;//字节低四位

        for(int i=0;i<len;i++){
            //右移四位得到高位
            high = (byte)((hexStr.indexOf(hexString.charAt(2*i)))<<4);
            low = (byte)hexStr.indexOf(hexString.charAt(2*i+1));
            bytes[i] = (byte) (high|low);//高地位做或运算
        }
        return bytes;
    }

    @Test
    public void testCache() {
    }
}

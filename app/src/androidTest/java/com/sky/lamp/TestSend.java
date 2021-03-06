package com.sky.lamp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.sky.lamp.bean.LightItemMode;
import com.sky.lamp.ui.fragment.ModelInfoSettingFragment;

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
    public void testTimeValid() {
        List<LightItemMode> list = new ArrayList<>();
        LightItemMode a = new LightItemMode();
        LightItemMode b = new LightItemMode();
        LightItemMode c = new LightItemMode();
        a.startTime = "01:00";
        a.stopTime = "02:00";

        b.startTime = "01:30";
        b.stopTime = "02:00";
        list.add(a);
        list.add(b);
        Assert.assertNotNull("",new ModelInfoSettingFragment().isTimeValid(list));

        list.clear();
        a.startTime = "02:00";
        a.stopTime = "03:00";
        b.startTime = "05:00";
        b.stopTime = "02:30";
        list.add(a);
        list.add(b);
        Assert.assertNotNull("",new ModelInfoSettingFragment().isTimeValid(list));


//        01:00 - 03:00  01:00 -02:00
        list.clear();
        a.startTime = "01:00";
        a.stopTime = "03:00";
        b.startTime = "01:00";
        b.stopTime = "02:00";
        list.add(a);
        list.add(b);
        Assert.assertNotNull("",new ModelInfoSettingFragment().isTimeValid(list));


        list.clear();
        a.startTime = "01:00";
        a.stopTime = "02:00";
        b.startTime = "01:00";
        b.stopTime = "02:00";
        list.add(a);
        list.add(b);
        Assert.assertNotNull("",new ModelInfoSettingFragment().isTimeValid(list));


        list.clear();
        a.startTime = "01:00";
        a.stopTime = "02:00";
        b.startTime = "03:00";
        b.stopTime = "03:00";
        list.add(a);
        list.add(b);
        Assert.assertNotNull("",new ModelInfoSettingFragment().isTimeValid(list));



        list.clear();
        a.startTime = "01:00";
        a.stopTime = "02:00";
        b.startTime = "03:00";
        b.stopTime = "02:00";
        list.add(a);
        list.add(b);
        Assert.assertNotNull("",new ModelInfoSettingFragment().isTimeValid(list));



        list.clear();
        a.startTime = "03:00";
        a.stopTime = "02:00";
        b.startTime = "04:00";
        b.stopTime = "01:00";
        list.add(a);
        list.add(b);
        Assert.assertNotNull("",new ModelInfoSettingFragment().isTimeValid(list));


        // 分成2段 第一段start - 23：59
        // 第二段00：00 - END
        // start  - 6 - 7 - 8 - 9 - 10 - 11 -12 - 13 - 14 - 15 - 16 -17 - 18 -19 -20 -21 -22 -23:59
        // -1
        // -2 - END

        list.clear();
        a.startTime = "05:00";
        a.stopTime = "03:00";
        list.add(a);
        b.startTime = "03:00";
        b.stopTime = "06:00";
        list.add(b);
        Assert.assertNotNull("",new ModelInfoSettingFragment().isTimeValid(list));



        list.clear();
        a.startTime = "05:00";
        a.stopTime = "03:00";
        list.add(a);

        b.startTime = "03:00";
        b.stopTime = "04:00";
        list.add(b);

        b.startTime = "04:00";
        b.stopTime = "05:02";
        list.add(c);
        Assert.assertNotNull("",new ModelInfoSettingFragment().isTimeValid(list));

        // 正常数据-------------------------------


        list.clear();
        a.startTime = "01:00";
        a.stopTime = "02:00";
        b.startTime = "03:00";
        b.stopTime = "04:00";
        list.add(a);
        list.add(b);
        Assert.assertNull("",new ModelInfoSettingFragment().isTimeValid(list));



        list.clear();
        a.startTime = "01:00";
        a.stopTime = "02:00";
        b.startTime = "03:00";
        b.stopTime = "01:00";
        list.add(a);
        list.add(b);
        Assert.assertNull("",new ModelInfoSettingFragment().isTimeValid(list));



        list.clear();
        a.startTime = "05:00";
        a.stopTime = "02:00";
        list.add(a);
        Assert.assertNull("",new ModelInfoSettingFragment().isTimeValid(list));



        list.clear();
        a.startTime = "05:00";
        a.stopTime = "03:00";
        list.add(a);
        b.startTime = "03:00";
        b.stopTime = "04:00";
        list.add(b);
        Assert.assertNull("",new ModelInfoSettingFragment().isTimeValid(list));




    }


}

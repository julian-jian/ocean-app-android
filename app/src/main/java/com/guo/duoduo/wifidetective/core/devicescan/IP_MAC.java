package com.guo.duoduo.wifidetective.core.devicescan;

import java.io.Serializable;

/**
 * Created by 郭攀峰 on 2015/10/25.
 */
public class IP_MAC implements Serializable
{
    public String mIp;
    public String mMac;
    public String mManufacture;
    public String mDeviceName;
    public boolean mSelect;

    public IP_MAC(String ip, String mac)
    {
        this.mIp = ip;
        this.mMac = mac;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        IP_MAC ip_mac = (IP_MAC) o;
        // 后面mac10位相同
        boolean boo =
                ip_mac.mMac.startsWith("8c:") &&
                mMac.toLowerCase().equals(ip_mac.mMac.replace("8c:","8e:"));
        return mMac.toLowerCase().equals(ip_mac.mMac.toLowerCase()) || mMac.toUpperCase().equals(ip_mac.mMac.toUpperCase())
                || boo;
    }

    @Override
    public int hashCode()
    {
        int result = mMac.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "IP_MAC{" + "mIp='" + mIp + '\'' + ", mMac='" + mMac + '\'' + '}';
    }
}

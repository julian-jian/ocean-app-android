package com.sky.lamp.response;

import java.util.List;

public class WifiResponse {
    public List<WifiResponse.DataBean> data;
    public static class DataBean {
        public String name;
        public String mac;
    }
}

package com.sky.lamp.response;

import java.util.List;

/**
 * Created by zhangfy on 2018/8/1.
 */

public class BannerResponse {

    /**
     * code : 200
     * data : [{"title":"轮播图-1","banner_url":"","banner_imgfile":"/uploadfile/backend/banner/2018/07/30/img_1532934221.jpg"},{"title":"轮播图-2","banner_url":"","banner_imgfile":"/uploadfile/backend/banner/2018/07/30/img_1532934319.jpg"},{"title":"轮播图-3","banner_url":"","banner_imgfile":"/uploadfile/backend/banner/2018/07/30/img_1532934444.jpg"}]
     */

    public int code;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * title : 轮播图-1
         * banner_url :
         * banner_imgfile : /uploadfile/backend/banner/2018/07/30/img_1532934221.jpg
         */

        public String title;
        public String banner_url;
        public String banner_imgfile;
    }
}

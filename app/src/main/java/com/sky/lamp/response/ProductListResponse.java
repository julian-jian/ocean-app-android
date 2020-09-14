package com.sky.lamp.response;

import java.util.List;

/**
 * Created by zhangfy on 2018/8/1.
 */

public class ProductListResponse {
    /**
     * code : 200
     * data : [{"id":"1","title":"产品-1","thumb_image":"/uploadfile/product/2018/07/30/img_1532943685.jpg"},{"id":"2","title":"产品-2","thumb_image":"/uploadfile/product/2018/07/30/img_1532945970.jpg"},{"id":"3","title":"产品-3","thumb_image":"/uploadfile/product/2018/07/30/img_1532946300.jpg"},{"id":"4","title":"产品-4","thumb_image":"/uploadfile/product/2018/07/30/img_1532946477.jpg"},{"id":"5","title":"产品-5","thumb_image":"/uploadfile/product/2018/07/30/img_1532946850.jpg"}]
     */

    public int code;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * id : 1
         * title : 产品-1
         * thumb_image : /uploadfile/product/2018/07/30/img_1532943685.jpg
         */

        public String id;
        public String title;
        public String model;
        public String thumb_image;
    }
}

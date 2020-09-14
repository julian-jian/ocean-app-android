package com.sky.lamp.response;

import java.util.List;

/**
 * Created by zhangfy on 2018/8/1.
 */

public class PowerListResponse {


    /**
     * code : 200
     * data : [{"cate_id":"1","cate_name":"220kV洲边站","cate_img":"/uploadfile/product/2018/08/02/img_1533193833.jpg"},{"cate_id":"2","cate_name":"110kV峣山站","cate_img":"/uploadfile/product/2018/08/02/img_1533194624.jpg"},{"cate_id":"3","cate_name":"110kV松岗站","cate_img":"/uploadfile/product/2018/08/02/img_1533194072.jpg"},{"cate_id":"4","cate_name":"110kV穆院站","cate_img":"/uploadfile/product/2018/08/02/img_1533194665.jpg"}]
     */

    public int code;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * cate_id : 1
         * cate_name : 220kV洲边站
         * cate_img : /uploadfile/product/2018/08/02/img_1533193833.jpg
         */

        public String cate_id;
        public String cate_name;
        public String cate_img;
    }
}

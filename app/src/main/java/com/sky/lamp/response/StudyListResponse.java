package com.sky.lamp.response;

import java.util.List;

/**
 * Created by zhangfy on 2018/8/1.
 */

public class StudyListResponse {

    /**
     * code : 200
     * data : [{"a_id":"10","a_title":"学习园地-1","thumb_image":"/uploadfile/backend/article/2018/07/30/img_1532948302.jpg"},{"a_id":"11","a_title":"学习园地-2","thumb_image":"/uploadfile/backend/article/2018/07/30/img_1532948889.jpg"},{"a_id":"9","a_title":"感悟自然--\u201c相约登顶羊台山\u201d","thumb_image":"/uploadfile/backend/article/2018/07/30/img_1532948721.jpg"}]
     */

    public int code;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * a_id : 10
         * a_title : 学习园地-1
         * thumb_image : /uploadfile/backend/article/2018/07/30/img_1532948302.jpg
         */

        public String a_id;
        public String a_title;
        public String thumb_image;
        public String date;
    }
}

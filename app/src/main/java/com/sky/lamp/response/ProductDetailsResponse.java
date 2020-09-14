package com.sky.lamp.response;

/**
 * Created by zhangfy on 2018/8/1.
 */

public class ProductDetailsResponse {

    /**
     * code : 200
     * data : {"id":"1","thumb_image":"/uploadfile/product/2018/08/07/img_1533635508.jpg","model":"GL314","manufactor":"苏州阿海珐220kV","transform":"8","operat_voltage":"110","close_time":"65±5(分相)；≤114（联动）","close_distinct":"≤3","branch_time":"≤40 (分相)；≤42（联动）","branch_distinct":"≤2","cb_time":"","loop_resistance":"44、48","speed_definition":"合前分后8ms","standard_stroke":"总150±4，超2","close_speed":"5.1-6 或3.1-4（三相联动）","branch_speed":"7.0-8或6.5-7.5（三相联动）","sensor_type":"","circuit_image":"/uploadfile/product/2018/07/30/img_1532943733.jpg","use_instrument":"","close_terminal":"正极101、负极102，端子-X01:1","branch_terminal":"端子-X01:137ABC","terminal_position":"","remarks":"","summary":"0","content":"0","cate_name":"220KV洲边站"}
     */

    public int code;
    /**
     * id : 1
     * thumb_image : /uploadfile/product/2018/08/07/img_1533635508.jpg
     * model : GL314
     * manufactor : 苏州阿海珐220kV
     * transform : 8
     * operat_voltage : 110
     * close_time : 65±5(分相)；≤114（联动）
     * close_distinct : ≤3
     * branch_time : ≤40 (分相)；≤42（联动）
     * branch_distinct : ≤2
     * cb_time :
     * loop_resistance : 44、48
     * speed_definition : 合前分后8ms
     * standard_stroke : 总150±4，超2
     * close_speed : 5.1-6 或3.1-4（三相联动）
     * branch_speed : 7.0-8或6.5-7.5（三相联动）
     * sensor_type :
     * circuit_image : /uploadfile/product/2018/07/30/img_1532943733.jpg
     * use_instrument :
     * close_terminal : 正极101、负极102，端子-X01:1
     * branch_terminal : 端子-X01:137ABC
     * terminal_position :
     * remarks :
     * summary : 0
     * content : 0
     * cate_name : 220KV洲边站
     */

    public DataBean data;

    public static class DataBean {
        public String id;
        public String thumb_image;
        public String model;
        public String manufactor;
        public String transform;
        public String operat_voltage;
        public String close_time;
        public String close_distinct;
        public String branch_time;
        public String branch_distinct;
        public String cb_time;
        public String loop_resistance;
        public String speed_definition;
        public String standard_stroke;
        public String close_speed;
        public String branch_speed;
        public String sensor_type;
        public String circuit_image;
        public String use_instrument;
        public String close_terminal;
        public String branch_terminal;
        public String terminal_position;
        public String remarks;
        public String summary;
        public String content;
        public String cate_name;
    }
}

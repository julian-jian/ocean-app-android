package com.sky.lamp.response;

public class BaseResponse {
    /**
     * status : 0
     * userID :
     * result :
     */

    public int status;
    public String userID;
    public String result;

    public static final int SUCCESS = 0;

    public boolean isSuccess() {
        return  status == SUCCESS;
    }

}

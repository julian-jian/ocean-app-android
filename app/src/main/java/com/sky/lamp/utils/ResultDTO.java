package com.sky.lamp.utils;

public class ResultDTO {

    private String statueMessage;
    private String sex;
    private String birthday;
    private int age;

    public String getStatueMessage() {
        return statueMessage;
    }

    public void setStatueMessage(String statueMessage) {
        this.statueMessage = statueMessage;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String toString() {
        String res = "";
        if (this.statueMessage != null) {
            res += this.statueMessage;
        } else {
            res += "sex:" + this.sex + ",birthday:" + this.birthday + ",age:" + this.age;
        }
        return res;
    }
}
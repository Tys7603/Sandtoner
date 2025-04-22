package com.wanyue.main.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

public class IntegralRecordBean {

    @SerializedName("mark")
    @JSONField(name = "mark")
    private String title;

    @SerializedName("pm")
    @JSONField(name = "pm")
    private int type;

    @SerializedName("number")
    @JSONField(name = "number")
    private String integral;

    private String total;


    private String add_time;
    private String result;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIntegral() {
        return integral;
    }

    public void setIntegral(String integral) {
        this.integral = integral;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }


    public  String getResult() {

        if(result==null){
            if(type==1){
                result=integral!=null?integral:total;
                result="+"+result;
            }else{
                result=integral!=null?integral:total;
                result="-"+result;
            }
        }
        return result;
    }
}

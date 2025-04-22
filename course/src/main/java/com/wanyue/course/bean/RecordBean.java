package com.wanyue.course.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

public class RecordBean  extends CourseBean{

    @SerializedName("add_time")
    @JSONField(name = "add_time")
    private String addTime;


    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
}

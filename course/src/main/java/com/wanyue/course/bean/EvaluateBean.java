package com.wanyue.course.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

public class EvaluateBean {

   @SerializedName("star")
   @JSONField(name="star")
   private int starCount;
    @SerializedName("avatar")
    @JSONField(name="avatar")
   private String userAvator;
    @SerializedName("nickname")
    @JSONField(name="nickname")
   private String userNickName;

    @SerializedName("uid")
    @JSONField(name="uid")
   private String userId;
   private String content;

    @SerializedName("add_time")
    @JSONField(name="add_time")
   private String studyRate;
    @SerializedName("addtime")
    @JSONField(name="addtime")
    private String addTime;
    private  String des;

    public int getStarCount() {

        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public String getUserAvator() {
        return userAvator;
    }
    public void setUserAvator(String userAvator) {
        this.userAvator = userAvator;
    }
    public String getUserNickName() {
        return userNickName;
    }
    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getStudyRate() {
        return studyRate;
    }
    public void setStudyRate(String studyRate) {
        this.studyRate = studyRate;
    }

    public String getAddTime() {
        return addTime;
    }
    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}

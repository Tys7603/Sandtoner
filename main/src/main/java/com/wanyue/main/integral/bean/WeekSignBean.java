package com.wanyue.main.integral.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;

public class WeekSignBean  implements MultiItemEntity {
    public static final int NORMAL=0;
    public static final int LINE=1;

    private int id;
    @JSONField(name = "day_txt")
    @SerializedName("day_txt")
    private String day;
    private boolean isSign;

    @JSONField(name = "integral")
    @SerializedName("integral")
    private String reward;

    private int type;
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
    public boolean isSign() {
        return isSign;
    }
    public void setSign(boolean sign) {
        isSign = sign;
    }

    public String getReward() {
        return reward;
    }
    public void setReward(String reward) {
        this.reward = reward;
    }
    @Override
    public int getItemType() {
        return type;
    }

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}

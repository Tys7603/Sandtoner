package com.wanyue.shop.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.bean.GoodsBean;
import java.util.List;

public class FoodGoodsBean {
    @SerializedName("day")
    @JSONField(name = "day")
    private String title;

    @SerializedName("list")
    @JSONField(name = "list")
    private List<GoodsBean>goodsList;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<GoodsBean> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<GoodsBean> goodsList) {
        this.goodsList = goodsList;
    }
}

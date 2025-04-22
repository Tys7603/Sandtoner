package com.wanyue.shop.store.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.shop.bean.StoreBean;
import java.util.List;

public class GoodsSelectStoreBean extends StoreBean {
    @SerializedName("list")
    @JSONField(name = "list")
    private List<GoodsBean> selectGoodsList;


    public List<GoodsBean> getSelectGoodsList() {
        return selectGoodsList;
    }
    public void setSelectGoodsList(List<GoodsBean> selectGoodsList) {
        this.selectGoodsList = selectGoodsList;
    }
}

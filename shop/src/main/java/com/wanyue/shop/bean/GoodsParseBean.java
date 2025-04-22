package com.wanyue.shop.bean;

import android.util.ArrayMap;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.bean.SimpleLiveBean;
import com.wanyue.common.bean.SpecsValueBean;
import com.wanyue.shop.evaluate.bean.EvaluateBean2;
import com.wanyue.video.bean.VideoBean;
import com.wanyue.video.bean.VideoSimpleBean;

import java.util.List;

/*解析接口字段*/
public class GoodsParseBean {

    @SerializedName("good_list")
    @JSONField(name ="good_list")
    private List<GoodsBean> goodsList; //为你推荐
    private List<SpecsBean> productAttr; //规格

    /*这块的数据类型是变化的,不能直接解析会出错,arrayMap保证map的顺序是有序的*/
    private ArrayMap<String, SpecsValueBean> productRealyValue;//规格对应的值;
    private String replyChance;
    private String replyCount;

    @SerializedName("storeInfo")
    @JSONField(name ="storeInfo")
    private StoreGoodsBean goodsInfo;
    private EvaluateBean2 reply;


    @SerializedName("shop_name")
    @JSONField(name ="shop_name")
    private String shopName;

    @SerializedName("shoptype")
    @JSONField(name ="shoptype")
    private int shopType;

    @SerializedName("mer_id")
    @JSONField(name ="mer_id")
    private int shopId;


    @SerializedName("shop_avatar")
    @JSONField(name ="shop_avatar")
    private String shopAvatar;

    @SerializedName("shop_score1")
    @JSONField(name ="shop_score1")
    private String shopScore1;

    @SerializedName("shop_score2")
    @JSONField(name ="shop_score2")
    private String shopScore2;

    @SerializedName("shop_score3")
    @JSONField(name ="shop_score3")
    private String shopScore3;

    private List<VideoBean>video;

    @SerializedName("live")
    @JSONField(name ="live")
    private List<SimpleLiveBean>liveList;


    public List<GoodsBean> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<GoodsBean> goodsList) {
        this.goodsList = goodsList;
    }

    public List<SpecsBean> getProductAttr() {
        return productAttr;
    }

    public void setProductAttr(List<SpecsBean> productAttr) {
        this.productAttr = productAttr;
    }


    public ArrayMap<String, SpecsValueBean> getProductRealyValue() {
        return productRealyValue;
    }

    public void setProductRealyValue(ArrayMap<String, SpecsValueBean> productRealyValue) {
        this.productRealyValue = productRealyValue;
    }

    public EvaluateBean2 getReply() {
        return reply;
    }

    public void setReply(EvaluateBean2 reply) {
        this.reply = reply;
    }

    public String getReplyChance() {
        return replyChance;
    }

    public void setReplyChance(String replyChance) {
        this.replyChance = replyChance;
    }

    public String getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(String replyCount) {
        this.replyCount = replyCount;
    }

    public StoreGoodsBean getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(StoreGoodsBean goodsInfo) {
        this.goodsInfo = goodsInfo;
    }


    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopAvatar() {
        return shopAvatar;
    }

    public void setShopAvatar(String shopAvatar) {
        this.shopAvatar = shopAvatar;
    }

    public String getShopScore1() {
        return shopScore1;
    }

    public void setShopScore1(String shopScore1) {
        this.shopScore1 = shopScore1;
    }

    public String getShopScore2() {
        return shopScore2;
    }

    public void setShopScore2(String shopScore2) {
        this.shopScore2 = shopScore2;
    }

    public String getShopScore3() {
        return shopScore3;
    }

    public void setShopScore3(String shopScore3) {
        this.shopScore3 = shopScore3;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public StoreBean getStoreData(){
         if(shopId<=0){
            return null;
         }
        StoreBean storeBean=new StoreBean();
        storeBean.setName(shopName);
        storeBean.setAvatar(shopAvatar);
        storeBean.setShopScore1(shopScore1);
        storeBean.setShopScore2(shopScore2);
        storeBean.setShopScore3(shopScore3);
        storeBean.setId(shopId+"");
        storeBean.setShoptype(shopType);
        return storeBean;
    }

    public int getShopType() {
        return shopType;
    }

    public void setShopType(int shopType) {
        this.shopType = shopType;
    }

    public List<SimpleLiveBean> getLiveList() {
        return liveList;
    }
    public void setLiveList(List<SimpleLiveBean> liveList) {
        this.liveList = liveList;
    }

    public List<VideoBean> getVideo() {
        return video;
    }

    public void setVideo(List<VideoBean> video) {
        this.video = video;
    }
}

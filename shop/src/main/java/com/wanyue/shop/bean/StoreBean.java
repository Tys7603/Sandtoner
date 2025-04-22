package com.wanyue.shop.bean;

import android.text.TextUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.bean.UserBean;

public class StoreBean {

    @SerializedName("shopid")
    @JSONField(name = "shopid")
    private String id;

    @SerializedName("nickname")
    @JSONField(name = "nickname")
    private String name;

    @SerializedName("avatar")
    @JSONField(name ="avatar")
    private String avatar;

    @SerializedName("isattrnt")
    @JSONField(name ="isattrnt")
    private int isFollow;

    @SerializedName("fans")
    @JSONField(name ="fans")
    private String fansNum;


    private String addr;

    @SerializedName("shop_score1")
    @JSONField(name ="shop_score1")
    private String shopScore1;

    @SerializedName("shop_score2")
    @JSONField(name ="shop_score2")
    private String shopScore2;

    @SerializedName("shop_score3")
    @JSONField(name ="shop_score3")
    private String shopScore3;


    @SerializedName("content_url")
    @JSONField(name ="content_url")
    private String contentUrl;
    private int shoptype;

    private String uid;


    public StoreBean(String id, int isFollow) {
        this.id = id;
        this.isFollow = isFollow;
    }
    public StoreBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getId() {
        if(TextUtils.isEmpty(id)){
            id=uid;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFansNum() {
        return fansNum;
    }

    public void setFansNum(String fansNum) {
        this.fansNum = fansNum;
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

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public UserBean format(){
        UserBean userBean=new UserBean();
        userBean.setId(id);
        userBean.setAvatar(avatar);
        userBean.setUserNiceName(name);
        return userBean;
    }

    public int getShoptype() {
        return shoptype;
    }

    public void setShoptype(int shoptype) {
        this.shoptype = shoptype;
    }
}

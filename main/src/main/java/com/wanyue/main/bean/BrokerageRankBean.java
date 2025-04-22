package com.wanyue.main.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

/**
 * The type Brokerage rank bean.
 */
public class BrokerageRankBean {
    private String uid;
    private String nickname;
    private String avatar;
    @SerializedName("brokerage_price")
    @JSONField(name = "brokerage_price")
    private String brokeragePrice;


    /**
     * Gets uid.
     *
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets uid.
     *
     * @param uid the uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Gets nickname.
     *
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets nickname.
     *
     * @param nickname the nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Gets avatar.
     *
     * @return the avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Sets avatar.
     *
     * @param avatar the avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Gets brokerage price.
     *
     * @return the brokerage price
     */
    public String getBrokeragePrice() {
        return brokeragePrice;
    }

    /**
     * Sets brokerage price.
     *
     * @param brokeragePrice the brokerage price
     */
    public void setBrokeragePrice(String brokeragePrice) {
        this.brokeragePrice = brokeragePrice;
    }
}

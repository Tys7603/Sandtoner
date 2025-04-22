package com.wanyue.live.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 2019/4/29.
 */
public class LiveGiftPrizePoolWinBean {

    private String mUid;
    private String mAvatar;
    private String mName;
    private String mCoin;

    /**
     * Gets uid.
     *
     * @return the uid
     */
    @JSONField(name = "uid")
    public String getUid() {
        return mUid;
    }

    /**
     * Sets uid.
     *
     * @param uid the uid
     */
    @JSONField(name = "uid")
    public void setUid(String uid) {
        mUid = uid;
    }

    /**
     * Gets avatar.
     *
     * @return the avatar
     */
    @JSONField(name = "uhead")
    public String getAvatar() {
        return mAvatar;
    }

    /**
     * Sets avatar.
     *
     * @param avatar the avatar
     */
    @JSONField(name = "uhead")
    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    @JSONField(name = "uname")
    public String getName() {
        return mName;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    @JSONField(name = "uname")
    public void setName(String name) {
        mName = name;
    }

    /**
     * Gets coin.
     *
     * @return the coin
     */
    @JSONField(name = "wincoin")
    public String getCoin() {
        return mCoin;
    }

    /**
     * Sets coin.
     *
     * @param coin the coin
     */
    @JSONField(name = "wincoin")
    public void setCoin(String coin) {
        mCoin = coin;
    }


}

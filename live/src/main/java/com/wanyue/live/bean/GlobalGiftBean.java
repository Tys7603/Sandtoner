package com.wanyue.live.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 全站礼物实体类
 */
public class GlobalGiftBean {

    private String mLiveUid;
    private String mLiveName;
    private String mUserName;
    private String mGiftName;

    /**
     * Gets live uid.
     *
     * @return the live uid
     */
    @JSONField(name = "liveuid")
    public String getLiveUid() {
        return mLiveUid;
    }

    /**
     * Sets live uid.
     *
     * @param liveUid the live uid
     */
    @JSONField(name = "liveuid")
    public void setLiveUid(String liveUid) {
        mLiveUid = liveUid;
    }

    /**
     * Gets live name.
     *
     * @return the live name
     */
    @JSONField(name = "livename")
    public String getLiveName() {
        return mLiveName;
    }

    /**
     * Sets live name.
     *
     * @param liveName the live name
     */
    @JSONField(name = "livename")
    public void setLiveName(String liveName) {
        mLiveName = liveName;
    }

    /**
     * Gets user name.
     *
     * @return the user name
     */
    @JSONField(name = "uname")
    public String getUserName() {
        return mUserName;
    }

    /**
     * Sets user name.
     *
     * @param userName the user name
     */
    @JSONField(name = "uname")
    public void setUserName(String userName) {
        mUserName = userName;
    }

    /**
     * Gets gift name.
     *
     * @return the gift name
     */
    @JSONField(name = "giftname")
    public String getGiftName() {
        return mGiftName;
    }

    /**
     * Sets gift name.
     *
     * @param giftName the gift name
     */
    @JSONField(name = "giftname")
    public void setGiftName(String giftName) {
        mGiftName = giftName;
    }
}

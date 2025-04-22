package com.wanyue.live.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 2018/11/21.
 */
public class RedPackResultBean {

    private String avatar;
    private String time;
    private String uid;
    private String userNiceName;
    private String winCoin;

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
     * Gets time.
     *
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(String time) {
        this.time = time;
    }

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
     * Gets user nice name.
     *
     * @return the user nice name
     */
    @JSONField(name = "user_nicename")
    public String getUserNiceName() {
        return userNiceName;
    }

    /**
     * Sets user nice name.
     *
     * @param userNiceName the user nice name
     */
    @JSONField(name = "user_nicename")
    public void setUserNiceName(String userNiceName) {
        this.userNiceName = userNiceName;
    }

    /**
     * Gets win coin.
     *
     * @return the win coin
     */
    @JSONField(name = "win")
    public String getWinCoin() {
        return winCoin;
    }

    /**
     * Sets win coin.
     *
     * @param winCoin the win coin
     */
    @JSONField(name = "win")
    public void setWinCoin(String winCoin) {
        this.winCoin = winCoin;
    }
}

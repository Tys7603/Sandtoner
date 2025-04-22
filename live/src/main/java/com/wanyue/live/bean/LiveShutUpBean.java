package com.wanyue.live.bean;

import com.wanyue.common.bean.UserBean;

/**
 * 2019/4/27.
 */
public class LiveShutUpBean extends UserBean {
    private String uid;

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
        this.id = uid;
    }
}

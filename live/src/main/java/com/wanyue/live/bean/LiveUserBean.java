package com.wanyue.live.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.wanyue.common.bean.UserBean;

/**
 * The type Live user bean.
 */
public class LiveUserBean extends UserBean {
    @JSONField(name = "action")
    private int authority;
    private int isshut;

    /**
     * Gets authority.
     *
     * @return the authority
     */
    public int getAuthority() {
        return authority;
    }

    /**
     * Sets authority.
     *
     * @param authority the authority
     */
    public void setAuthority(int authority) {
        this.authority = authority;
    }

    /**
     * Gets isshut.
     *
     * @return the isshut
     */
    public int getIsshut() {
        return isshut;
    }

    /**
     * Sets isshut.
     *
     * @param isshut the isshut
     */
    public void setIsshut(int isshut) {
        this.isshut = isshut;
    }
}

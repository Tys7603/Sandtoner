package com.wanyue.live.socket;

import com.alibaba.fastjson.JSONArray;

/**
 * 2017/8/22.
 * 接收socket的实体类
 */
public class SocketReceiveBean {
    private String retcode;
    private String retmsg;
    private JSONArray msg;

    /**
     * Gets retcode.
     *
     * @return the retcode
     */
    public String getRetcode() {
        return retcode;
    }

    /**
     * Sets retcode.
     *
     * @param retcode the retcode
     */
    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    /**
     * Gets retmsg.
     *
     * @return the retmsg
     */
    public String getRetmsg() {
        return retmsg;
    }

    /**
     * Sets retmsg.
     *
     * @param retmsg the retmsg
     */
    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    /**
     * Gets msg.
     *
     * @return the msg
     */
    public JSONArray getMsg() {
        return msg;
    }

    /**
     * Sets msg.
     *
     * @param msg the msg
     */
    public void setMsg(JSONArray msg) {
        this.msg = msg;
    }
}

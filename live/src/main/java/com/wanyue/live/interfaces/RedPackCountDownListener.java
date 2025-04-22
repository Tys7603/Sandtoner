package com.wanyue.live.interfaces;

/**
 * 2018/11/22.
 */
public interface RedPackCountDownListener {
    /**
     * On count down.
     *
     * @param lastTime the last time
     */
    void onCountDown(int lastTime);

    /**
     * Gets red pack id.
     *
     * @return the red pack id
     */
    int getRedPackId();
}

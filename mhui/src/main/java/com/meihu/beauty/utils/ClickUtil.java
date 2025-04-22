package com.meihu.beauty.utils;

/**
 * on 2018/9/29.
 */
public class ClickUtil {

    private static long sLastClickTime;

    /**
     * Can click boolean.
     *
     * @return the boolean
     */
    public static boolean canClick() {
        long curTime = System.currentTimeMillis();
        if (curTime - sLastClickTime < 500) {
            return false;
        }
        sLastClickTime = curTime;
        return true;
    }

}

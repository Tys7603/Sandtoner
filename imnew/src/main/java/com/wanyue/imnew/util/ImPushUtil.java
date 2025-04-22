package com.wanyue.imnew.util;

import android.content.Context;
import android.text.TextUtils;

import com.wanyue.common.CommonApplication;
import com.wanyue.common.Constants;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.SpUtil;

import cn.jpush.android.api.JPushInterface;


/**
 *  2017/8/3.
 * 极光推送相关
 */

public class ImPushUtil {

    public static final String TAG = "极光推送";
    private static ImPushUtil sInstance;
    private boolean mClickNotification;
    private int mNotificationType;

    private ImPushUtil() {

    }

    public static ImPushUtil getInstance() {
        if (sInstance == null) {
            synchronized (ImPushUtil.class) {
                if (sInstance == null) {
                    sInstance = new ImPushUtil();
                }
            }
        }
        return sInstance;
    }

    public void init(Context context) {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(context);
        L.e(TAG, "regID------>" + JPushInterface.getRegistrationID(context));
    }


    public void logout() {
        stopPush();
    }

    public void resumePush() {
        if (JPushInterface.isPushStopped(CommonApplication.sInstance)) {
            JPushInterface.resumePush(CommonApplication.sInstance);
        }
    }

    public void stopPush() {
        JPushInterface.stopPush(CommonApplication.sInstance);
    }


    public void  setAlias(Context context,String alias){
       JPushInterface.setAlias(context,1,alias);
    }


    public boolean isClickNotification() {
        return mClickNotification;
    }

    public void setClickNotification(boolean clickNotification) {
        mClickNotification = clickNotification;
    }

    public int getNotificationType() {
        return mNotificationType;
    }

    public void setNotificationType(int notificationType) {
        mNotificationType = notificationType;
    }

    /**
     * 获取极光推送 RegistrationID
     */
    public String getPushID() {
        String registerId=JPushInterface.getRegistrationID(CommonApplication.sInstance);
        if(!TextUtils.isEmpty(registerId)){
           SpUtil.getInstance().setStringValue(Constants.PUSH_ID,registerId);
        }
        return JPushInterface.getRegistrationID(CommonApplication.sInstance);
    }
}

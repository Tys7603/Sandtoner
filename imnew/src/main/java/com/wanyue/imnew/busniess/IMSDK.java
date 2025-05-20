package com.wanyue.imnew.busniess;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.imsdk.relationship.UserInfo;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.TUIKitImpl;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.config.GeneralConfig;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.DraftInfo;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.bean.UserBean;
import com.wanyue.common.utils.SpUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.imnew.busniess.helper.ConfigHelper;

import java.util.HashMap;

public class IMSDK {

    public static void init(Context context,int appid){
        //String url="https://img1.baidu.com/it/u=3419657024,694829991&fm=15&fmt=auto";
        GeneralConfig generalConfig=new GeneralConfig();
      //  generalConfig.setUserFaceUrl(url);
        TUIKit.getConfigs().setGeneralConfig(generalConfig);
        TUIKit.init(context, appid,TUIKit.getConfigs());

    }

    public static void login(){
        String sign = SpUtil.getInstance().getStringValue(SpUtil.TX_IM_USER_SIGN);
        String uid = CommonAppConfig.getUid();

        // Validate inputs
//        if (TextUtils.isEmpty(sign) || TextUtils.isEmpty(uid)) {
//            ToastUtil.show("IM login failed: Invalid login information");
//            return;
//        }

        Log.d("IMSDK", "Attempting IM login - uid: " + uid + ", attempt: " + currentRetryCount);

        // Cancel old timeout if exists
        if (loginTimeoutRunnable != null) {
            mainHandler.removeCallbacks(loginTimeoutRunnable);
        }

        // Set timeout for request
        loginTimeoutRunnable = () -> {
            if (currentRetryCount < MAX_RETRY_COUNT) {
                Log.e("IMSDK", "Login timeout after " + NETWORK_TIMEOUT_MS + "ms");
                handleRetry("Connection timeout, retrying...");
            }
        };
        mainHandler.postDelayed(loginTimeoutRunnable, NETWORK_TIMEOUT_MS);

        TUIKit.login(uid, sign, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                CommonAppConfig.setLoginIM(true);
                UserBean userBean=CommonAppConfig.getUserBean();
                IMSDK.editUserIcon(userBean.getAvatar(),userBean.getUserNiceName());
            }
            @Override
            public void onError(String module, int errCode, String errMsg) {
                // Cancel timeout on error
                if (loginTimeoutRunnable != null) {
                    mainHandler.removeCallbacks(loginTimeoutRunnable);
                }

                switch (errCode) {
                    case 6017: // Network error
                        handleRetry("Network connection error, retrying...");
                        break;
                    case 6205: // Invalid usersig
//                        ToastUtil.show("IM login failed: Invalid signature, please login again");
                        CommonAppConfig.setLoginIM(false);
                        break;
                    case 6206: // Usersig expired
//                        ToastUtil.show("IM login failed: Signature expired, please login again");
                        CommonAppConfig.setLoginIM(false);
                        break;
                    default:
//                        handleRetry("Login failed, retrying...");
                        break;
                }
            }
        });
    }

    private static void handleRetry(String message) {
        if (currentRetryCount < MAX_RETRY_COUNT) {
            currentRetryCount++;
            Log.d("IMSDK", message + " (Attempt " + currentRetryCount + " of " + MAX_RETRY_COUNT + ")");
            ToastUtil.show(message);

            // Only retry if network is available
            if (isNetworkConnected()) {
                mainHandler.postDelayed(() -> login(), RETRY_DELAY_MS);
            } else {
                ToastUtil.show("No network connection, will automatically retry when network is available");
            }
        } else {
            currentRetryCount = 0;
            CommonAppConfig.setLoginIM(false);
//            ToastUtil.show("Login failed after " + MAX_RETRY_COUNT + " attempts. Please try again later.");
        }
    }

    public static void editUserIcon(String url,String nickName){
        HashMap<String, Object> hashMap = new HashMap<>();
        // 头像，mIconUrl 就是您上传头像后的 URL，请参见 Demo 中的随机头像作为示例
        V2TIMUserFullInfo v2TIMUserFullInfo = new V2TIMUserFullInfo();
        // 头像
        if (!TextUtils.isEmpty(url)) {
            v2TIMUserFullInfo.setFaceUrl(url);
        }
        v2TIMUserFullInfo.setNickname(nickName);
        V2TIMManager.getInstance().setSelfInfo(v2TIMUserFullInfo, new V2TIMCallback() {
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public static void outLogin(){
        CommonAppConfig.setLoginIM(false);
        V2TIMManager.getInstance().logout(null);
    }
}

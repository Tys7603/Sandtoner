package com.wanyue.imnew.busniess;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.wanyue.common.CommonApplication;
import com.wanyue.common.bean.UserBean;
import com.wanyue.common.utils.SpUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.imnew.busniess.helper.ConfigHelper;

import java.util.HashMap;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;

public class IMSDK {

    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 1000; // 1 second delay between retries
    private static final long NETWORK_TIMEOUT_MS = 10000; // 10 seconds timeout
    private static int currentRetryCount = 0;
    private static boolean isNetworkAvailable = true;
    private static NetworkCallback networkCallback;
    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Runnable loginTimeoutRunnable;

    public static void init(Context context, int appid) {
        GeneralConfig generalConfig = new GeneralConfig();
        TUIKit.getConfigs().setGeneralConfig(generalConfig);
        TUIKit.init(context, appid, TUIKit.getConfigs());
        
        registerNetworkCallback(context);
    }

    private static void registerNetworkCallback(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return;

        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        NetworkRequest networkRequest = builder.build();

        networkCallback = new NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                isNetworkAvailable = true;
                Log.d("IMSDK", "Network is available");
                // Auto retry login if in waiting state
                if (currentRetryCount > 0) {
                    mainHandler.post(() -> login());
                }
            }

            @Override
            public void onLost(Network network) {
                isNetworkAvailable = false;
                Log.d("IMSDK", "Network is lost");
                ToastUtil.show("Network connection lost, please check your connection");
            }
        };

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    private static boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) CommonApplication.sInstance.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void login() {
        if (!isNetworkConnected()) {
            ToastUtil.show("No network connection, please check your connection");
            return;
        }

        String sign = SpUtil.getInstance().getStringValue(SpUtil.TX_IM_USER_SIGN);
        String uid = CommonAppConfig.getUid();
        
        // Validate inputs
        if (TextUtils.isEmpty(sign) || TextUtils.isEmpty(uid)) {
            ToastUtil.show("IM login failed: Invalid login information");
            return;
        }

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
                // Cancel timeout on success
                if (loginTimeoutRunnable != null) {
                    mainHandler.removeCallbacks(loginTimeoutRunnable);
                }
                
                currentRetryCount = 0;
                CommonAppConfig.setLoginIM(true);
                UserBean userBean = CommonAppConfig.getUserBean();
                if (userBean != null) {
                    IMSDK.editUserIcon(userBean.getAvatar(), userBean.getUserNiceName());
                }
                Log.d("IMSDK", "IM login successful");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                // Cancel timeout on error
                if (loginTimeoutRunnable != null) {
                    mainHandler.removeCallbacks(loginTimeoutRunnable);
                }

                Log.e("IMSDK", "IM login failed - module: " + module + ", code: " + errCode + ", msg: " + errMsg);
                
                switch (errCode) {
                    case 6017: // Network error
                        handleRetry("Network connection error, retrying...");
                        break;
                    case 6205: // Invalid usersig
                        ToastUtil.show("IM login failed: Invalid signature, please login again");
                        CommonAppConfig.setLoginIM(false);
                        break;
                    case 6206: // Usersig expired
                        ToastUtil.show("IM login failed: Signature expired, please login again");
                        CommonAppConfig.setLoginIM(false);
                        break;
                    default:
                        handleRetry("Login failed, retrying...");
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
            ToastUtil.show("Login failed after " + MAX_RETRY_COUNT + " attempts. Please try again later.");
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

    public static void release() {
        if (networkCallback != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) CommonApplication.sInstance.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            }
        }
        if (loginTimeoutRunnable != null) {
            mainHandler.removeCallbacks(loginTimeoutRunnable);
        }
    }
}

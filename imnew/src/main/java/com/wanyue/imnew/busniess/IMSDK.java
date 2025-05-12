package com.wanyue.imnew.busniess;

import android.content.Context;
import android.text.TextUtils;

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
        String uid=CommonAppConfig.getUid();
        TUIKit.login(uid, sign, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                CommonAppConfig.setLoginIM(true);
                UserBean userBean=CommonAppConfig.getUserBean();
                IMSDK.editUserIcon(userBean.getAvatar(),userBean.getUserNiceName());
            }
            @Override
            public void onError(String module, int errCode, String errMsg) {
                CommonAppConfig.setLoginIM(false);
                ToastUtil.show("IM Login failed：" + module + " errmsg: " + errMsg);
            }
        });
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

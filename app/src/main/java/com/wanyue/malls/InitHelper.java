package com.wanyue.malls;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import com.alibaba.android.arouter.launcher.ARouter;
import com.meihu.beautylibrary.MHSDK;
import com.mob.MobSDK;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.rtmp.TXLiveBase;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.CommonApplication;
import com.wanyue.common.bean.ConfigBean;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.DebugUtil;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.SystemUtil;
import com.wanyue.imnew.busniess.IMSDK;
import java.util.concurrent.TimeUnit;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class InitHelper {
   private static boolean mBeautyInited;
    public void startDelayInit(final Application context,int time){
        Observable.timer(time, TimeUnit.MILLISECONDS).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        delayInit(context);
                    }
            });
    }
    private  void delayInit(Application context){
        boolean isDebug= SystemUtil.isApkInDebug(context);
        DebugUtil.setDeBug(isDebug);
        L.setDeBug(isDebug);
        //初始化腾讯bugly
        CrashReport.initCrashReport(context);
        CrashReport.setAppVersion(context, CommonAppConfig.getVersion());
        //初始化ShareSdk
        MobSDK.init(context);
        //初始化极光推送

        //初始化 ARouter
        if (isDebug) {
            ARouter.openLog();
            ARouter.openDebug();
          //  LeakCanary.install(context);
            if (LeakCanary.isInAnalyzerProcess(context)) {
                return;
            }
        }
        RxFFmpegInvoke.getInstance().setDebug(isDebug);
        ARouter.init(context);
    }

    public static void startNowInit(Context context,String beautyKey){
        initBeautySdk(beautyKey,context);
        TXLiveBase.getInstance().setLicence(context,
                "http://license.vod2.myqcloud.com/license/v1/213775168ab4cebd4a43c4bbf31267d2/TXLiveSDK.licence",
                  "25ed6ca4ca33eaa3aecc905e959a66ea"
         );
    }

    private static  boolean isInitIM;
    public static void initIM(int appid){
        IMSDK.init(CommonApplication.sInstance,appid);
        isInitIM=true;
    }

    public static void initIM(){
        if(isInitIM){
            return;
        }
        ConfigBean configBean=CommonAppConfig.getConfig();
        if(configBean==null){
            return;
        }
        int appid=configBean.getTxSdkappid();
        IMSDK.init(CommonApplication.sInstance,appid);
    }

    /**
     * 初始化美狐
     */

    private static void initBeautySdk(String beautyKey, Context context) {
        CommonAppConfig.setBeautyKey(beautyKey);
        if (!TextUtils.isEmpty(beautyKey)) {
            if (!mBeautyInited) {
                 mBeautyInited = true;
                MHSDK.init(context, beautyKey);
                CommonAppConfig.setTiBeautyEnable(true);
                //根据后台配置设置美颜参数
            }
        } else {
            MHSDK.init(context, "");
            CommonAppConfig.setTiBeautyEnable(false);
        }
    }
    /*美狐是否初始化*/
    public static boolean isBeautyInited() {
        return mBeautyInited;
    }
}

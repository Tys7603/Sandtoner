package com.wanyue.malls;

import android.os.Build;
import android.util.Log;

import androidx.multidex.BuildConfig;

import com.wanyue.common.CommonApplication;
import com.wanyue.common.utils.ResourceUtil;

import co.paystack.android.PaystackSdk;

/**
 * The type Mail application.
 */
public class MailApplication extends CommonApplication {

    @Override
    public void onCreate() {
        super.onCreate();
		ELContext.setContext(this);
        InitHelper initHelper=new InitHelper();
        initHelper.startDelayInit(this,100);
        PaystackSdk.initialize(getApplicationContext());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ResourceUtil.clearAllDrawable();
    }
}

package com.wanyue.malls;

import com.wanyue.common.CommonApplication;
import com.wanyue.common.utils.ResourceUtil;

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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ResourceUtil.clearAllDrawable();
    }
}

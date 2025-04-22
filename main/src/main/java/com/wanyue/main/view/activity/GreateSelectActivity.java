package com.wanyue.main.view.activity;

import android.view.ViewGroup;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.main.R;
import com.wanyue.main.view.proxy.MainHomeGreateSelectViewProxy;

public class GreateSelectActivity extends BaseActivity {
    private ViewGroup mContainer;

    @Override
    public void init() {
        setTabTitle("今日优选");
        mContainer = (ViewGroup) findViewById(R.id.container);
        MainHomeGreateSelectViewProxy  selectViewProxy=new MainHomeGreateSelectViewProxy();
        getViewProxyMannger().addViewProxy(mContainer,selectViewProxy,selectViewProxy.getDefaultTag());
        selectViewProxy.initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_greate_select;
    }
}
package com.wanyue.shop.view.activty;

import android.view.ViewGroup;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.shop.R;
import com.wanyue.shop.store.view.proxy.StoreCouponListVIewProxy;

/**
 * The type Get coupon activity.
 */
public class GetCouponActivity extends BaseActivity {
    private ViewGroup mContainer;
    @Override
    public void init() {
        setTabTitle("Coupon Center");
        mContainer = (ViewGroup) findViewById(R.id.container);
        StoreCouponListVIewProxy storeCouponListVIewProxy = new StoreCouponListVIewProxy();
        getViewProxyMannger().addViewProxy(mContainer,storeCouponListVIewProxy,storeCouponListVIewProxy.getDefaultTag());
        storeCouponListVIewProxy.initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_get_coupon;
    }
}
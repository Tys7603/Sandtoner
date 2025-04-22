package com.wanyue.main.view.proxy;

import android.view.ViewGroup;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.main.R;
import com.wanyue.shop.model.ShopCartModel;
import com.wanyue.shop.view.view.ShopCartViewProxy;

public class MainShopCartViewProxy extends RxViewProxy {
    private ViewGroup mRootView;
    private ShopCartViewProxy mShopCartViewProxy;
    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        setDefaultStatusBarPadding();
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mShopCartViewProxy=new ShopCartViewProxy();
        getViewProxyMannger().addViewProxy(mRootView,mShopCartViewProxy,mShopCartViewProxy.getDefaultTag());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            mShopCartViewProxy.initData();
            ShopCartModel.requestShopcartCount();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_main_shop_cart;
    }
}

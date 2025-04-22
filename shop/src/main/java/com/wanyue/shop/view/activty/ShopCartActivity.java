package com.wanyue.shop.view.activty;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.shop.R;
import com.wanyue.shop.view.view.ShopCartViewProxy;

public class ShopCartActivity extends BaseActivity {
    private ViewGroup mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//键盘顶起布局
        super.onCreate(savedInstanceState);
    }

    @Override
    public void init() {
        setTabTitle(R.string.shop_cart);
        setTabBackGound(R.color.white);
        mContainer =findViewById(R.id.container);

        ShopCartViewProxy shopCartViewProxy=new ShopCartViewProxy();
        getViewProxyMannger().addViewProxy(mContainer,shopCartViewProxy,shopCartViewProxy.getDefaultTag());
        shopCartViewProxy.initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_shop_cart;
    }
}

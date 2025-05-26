package com.wanyue.shop.view.view;

import com.wanyue.shop.R;

/**
 * The type Goods handle no cart view proxy.
 */
public class GoodsHandleNoCartViewProxy extends GoodsHandleViewProxy {
    @Override
    public int getLayoutId() {
        return R.layout.view_goods_handle_no_cart;
    }

    @Override
    public void setButtonsEnabled(boolean enabled) {
        if (mBtnBuy != null) {
            mBtnBuy.setEnabled(enabled);
            mBtnBuy.setAlpha(enabled ? 1.0f : 0.5f);
        }
    }
}

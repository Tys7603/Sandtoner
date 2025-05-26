package com.wanyue.shop.view.view;

import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.shop.bean.StoreGoodsBean;

/**
 * The type Base goods detail bottom view proxy.
 */
public abstract class BaseGoodsDetailBottomViewProxy extends RxViewProxy {
    /**
     * The M store goods bean.
     */
    protected StoreGoodsBean mStoreGoodsBean;

    /**
     * Sets store goods bean.
     *
     * @param storeGoodsBean the store goods bean
     */
    public void setStoreGoodsBean(StoreGoodsBean storeGoodsBean) {
        mStoreGoodsBean = storeGoodsBean;
    }

    /**
     * Enable or disable the action buttons (add to cart, buy now)
     * @param enabled true to enable buttons, false to disable
     */
    public void setButtonsEnabled(boolean enabled) {
        // Default implementation does nothing
        // Subclasses should override this method to implement their own button enabling/disabling logic
    }
}

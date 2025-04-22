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
}

package com.wanyue.shop.store.view.proxy;

import android.view.ViewGroup;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.shop.R;
import com.wanyue.shop.R2;
import com.wanyue.shop.store.bean.ClassifyBean;
import com.wanyue.shop.store.view.activity.SearchStoreActivity;
import butterknife.OnClick;

/**
 * The type Base store child v iew proxy.
 */
public abstract class BaseStoreChildVIewProxy extends RxViewProxy {

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        setDefaultStatusBarPadding(R.id.v_tab);
    }

    /**
     * Click store search.
     */
    @OnClick(R2.id.btn_search)
    public void clickStoreSearch(){
        ClassifyBean classify=new ClassifyBean();
        classify.setStoreId(mStoreId);
        SearchStoreActivity.forward(getActivity(),classify);
    }

    /**
     * Back.
     */
    @OnClick(R2.id.btn_back)
    public void back(){
        getActivity().finish();
    }

    /**
     * The M store id.
     */
    protected String mStoreId;

    /**
     * Sets store id.
     *
     * @param storeId the store id
     */
    public void setStoreId(String storeId) {
        mStoreId = storeId;
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }
}

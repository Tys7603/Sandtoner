package com.wanyue.shop.store.view.proxy;

import android.view.View;
import android.view.ViewGroup;
import com.wanyue.common.server.observer.DialogObserver;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.CouponBean;
import com.wanyue.shop.components.adapter.CouponListAdapter;
import com.wanyue.shop.components.view.CouponListViewProxy;
import java.util.List;
import io.reactivex.Observable;

public class StoreCouponListVIewProxy extends CouponListViewProxy<CouponBean> implements CouponListViewProxy.OnCouponListner<CouponBean> {
    private String mStoreId;
    private boolean mCloseStore;
    @Override
    protected void initView(ViewGroup contentView) {
        CouponListAdapter adapter= new CouponListAdapter(null);
        adapter.setCloseToStore(mCloseStore);
        setCouponListAdapter(adapter);
        setClickListner(this);
        super.initView(contentView);
        setNoDataTip("No coupons available for use");
    }

    @Override
    public Observable<List<CouponBean>> getData(int p) {
        return getCoupon(p);
    }
    private Observable<List<CouponBean>> getCoupon(int p) {
      return ShopAPI.getCouponList(p,0,null,mStoreId);
    }

    @Override
    public void click(View view, final CouponBean couponBean, final int position) {
        String id=couponBean.getId();
        if(couponBean.isUse()){
            return;
        }
        ShopAPI.receiveCoupon(id).subscribe(new DialogObserver<Boolean>(getActivity()) {
            @Override
            public void onNextTo(Boolean aBoolean) {
                if(aBoolean){
                    ToastUtil.show("领取成功");
                    couponBean.setUse(true);
                    notifyItemChanged(position);
                }
            }
        });
    }

    public void setCloseStore(boolean closeStore) {
        mCloseStore = closeStore;
    }

    public void setStoreId(String storeId) {
        mStoreId = storeId;
    }
}

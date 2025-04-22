package com.wanyue.shop.components.view;

import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.bean.CouponBean;
import com.wanyue.shop.components.adapter.BaseCouponListAdapter;
import com.wanyue.shop.store.view.activity.StoreActivity;
import java.util.List;
import io.reactivex.Observable;

public abstract  class CouponListViewProxy<T extends CouponBean> extends RxViewProxy implements BaseQuickAdapter.OnItemChildClickListener {

    private RxRefreshView<T> mRefreshView;
    private BaseCouponListAdapter<T> mCouponListAdapter;
    private OnCouponListner<T> mListner;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mRefreshView = (RxRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setIconId(R.drawable.icon_empty_no_coupon);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(getActivity()));

        mCouponListAdapter.setOnItemChildClickListener(this);
        mRefreshView.setAdapter(mCouponListAdapter);
        mRefreshView.setDataListner(new RxRefreshView.DataListner<T>() {
            @Override
            public Observable<List<T>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<T> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            initData();
        }
    }

    public void initData() {
        if(mRefreshView!=null){
           mRefreshView.initData();
        }
    }
    public abstract Observable<List<T>> getData(int p);

    @Override
    public int getLayoutId() {
        return R.layout.view_single_refresh;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, final int position) {
        if(mCouponListAdapter==null|| !ClickUtil.canClick()){
            return;
        }
        int id=view.getId();
        final CouponBean couponBean= mCouponListAdapter.getItem(position);
        if(id==R.id.tv_store_title){
            toStore(couponBean);
        }


        if(mListner!=null){
           mListner.click(view,couponBean,position);
        }
    }

    private void toStore(CouponBean couponBean) {
        StoreActivity.forward(getActivity(),couponBean.getShopId());
    }


    public void notifyItemChanged(int position){
        if(mCouponListAdapter!=null){
           mCouponListAdapter.notifyItemChanged(position);
        }
    }


    public void setCouponListAdapter(BaseCouponListAdapter<T> couponListAdapter) {
        mCouponListAdapter = couponListAdapter;
    }

    public void setClickListner(OnCouponListner<T> listner) {
        mListner = listner;
    }

    public interface OnCouponListner<T>{
        public void click(View view,CouponBean couponBean,int position);
    }
}

package com.wanyue.shop.store.view.activity;

import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.shop.R;
import com.wanyue.shop.store.adapter.SelectStoreAdapter;
import com.wanyue.shop.store.api.StoreAPI;
import com.wanyue.shop.store.bean.GoodsSelectStoreBean;
import java.util.List;
import io.reactivex.Observable;

public class BusniessStoreActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener{

    private RxRefreshView<GoodsSelectStoreBean> mRefreshView;
    private SelectStoreAdapter mStoreAdapter;
    @Override
    public void init() {
        setTabTitle("Featured Shops");
        mRefreshView =findViewById(R.id.refreshView);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,1));

        mStoreAdapter=new SelectStoreAdapter(null);
        mStoreAdapter.setOnItemClickListener(this);
        mRefreshView.setAdapter(mStoreAdapter);
        mRefreshView.setDataListner(new RxRefreshView.DataListner<GoodsSelectStoreBean>() {
            @Override
            public Observable<List<GoodsSelectStoreBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<GoodsSelectStoreBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });
        mRefreshView.initData();
    }

    private Observable<List<GoodsSelectStoreBean>> getData(int p) {
        return StoreAPI.getSelectedStore(p).compose(this.<List<GoodsSelectStoreBean>>bindToLifecycle());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_busniess_store;
    }
    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        GoodsSelectStoreBean storeBean= mStoreAdapter.getItem(i);
        StoreActivity.forward(mContext,storeBean.getId());
    }
}
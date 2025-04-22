package com.wanyue.shop.store.view.activity;

import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.shop.R;
import com.wanyue.shop.bean.StoreBean;
import com.wanyue.shop.store.adapter.StoreAdapter;
import com.wanyue.shop.store.api.StoreAPI;
import java.util.List;
import io.reactivex.Observable;

/**
 * The type Store list activity.
 */
public class StoreListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    private RxRefreshView<StoreBean> mRefreshView;
    private StoreAdapter mStoreAdapter;

    @Override
    public void init() {
        setTabTitle("关注店铺");
        mRefreshView = (RxRefreshView) findViewById(R.id.refreshView);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,1));

        mStoreAdapter=new StoreAdapter(null);
        mStoreAdapter.setOnItemClickListener(this);
        mRefreshView.setAdapter(mStoreAdapter);
        mRefreshView.setDataListner(new RxRefreshView.DataListner<StoreBean>() {
            @Override
            public Observable<List<StoreBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<StoreBean> data) {

            }
            @Override
            public void error(Throwable e) {

            }
        });
        mRefreshView.initData();
    }

    private Observable<List<StoreBean>> getData(int p) {
        return StoreAPI.getFollowStore(p).compose(this.<List<StoreBean>>bindToLifecycle());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_store_list;
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        if(mStoreAdapter==null){
            return;
        }
        StoreBean storeBean=mStoreAdapter.getItem(i);
        StoreActivity.forward(mContext,storeBean.getId());
    }
}
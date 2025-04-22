package com.wanyue.common.proxy;

import android.view.ViewGroup;
import com.wanyue.common.R;
import com.wanyue.common.custom.refresh.RxRefreshView;
import java.util.List;
import io.reactivex.Observable;

public abstract class SingleRefreshViewProxy<T> extends RxViewProxy {
    protected RxRefreshView<T> mRefreshView;
    protected RxRefreshView.DataAdapter<T> mAdapter;
    private boolean mIsLoadByUserVisible; //是否在切换事件请求数据;
    private boolean mFirstVisibe=true;
    private boolean mOnlyShowFirst;//切换事件请求数据只在第一次发生;
    private String mNoDataTip;
    private int mPadding;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mRefreshView =  findViewById(R.id.refreshView);
        mRefreshView.setNoDataTip(mNoDataTip);
        mRefreshView.setPadding(mPadding,0,mPadding,0);

        mRefreshView.setAdapter(mAdapter);
        mRefreshView.setReclyViewSetting(getReclyViewSetting());
        mRefreshView.setDataListner(new RxRefreshView.DataListner<T>() {
            @Override
            public Observable<List<T>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<T> data) {
               SingleRefreshViewProxy.this.compeleteData(data);
            }
            @Override
            public void error(Throwable e) {
            }
        });
        if(mIsLoadByUserVisible){
            initData();
        }
    }

    public void compeleteData(List<T> data) {
    }

    public void initData() {
        if(mRefreshView!=null){
           mRefreshView.initData();
        }
    }

    public abstract Observable<List<T>> getData(int p);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser){
            return;
        }
        if(mOnlyShowFirst){
           if(mFirstVisibe){
              initData();
           }
        }else{
            initData();
        }
        mFirstVisibe=false;
    }


    public RxRefreshView.ReclyViewSetting getReclyViewSetting() {
        return RxRefreshView.ReclyViewSetting.createLinearSetting(getActivity());
    }

    public void setAdapter(RxRefreshView.DataAdapter<T> adapter) {
        mAdapter = adapter;
    }
    @Override
    public int getLayoutId() {
        return R.layout.view_single_refresh;
    }


    public void setNoDataTip(String noDataTip) {
        mNoDataTip = noDataTip;
    }

    public void setOnlyShowFirst(boolean onlyShowFirst) {
        mOnlyShowFirst = onlyShowFirst;
    }

    public void setLoadByUserVisible(boolean loadByUserVisible) {
        mIsLoadByUserVisible = loadByUserVisible;
    }

    public void setPadding(int padding) {
        mPadding = padding;
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return false;
    }
}

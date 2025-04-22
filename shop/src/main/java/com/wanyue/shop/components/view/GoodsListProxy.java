package com.wanyue.shop.components.view;

import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.shop.R;
import com.wanyue.shop.components.adapter.GoodsListAdapter;
import com.wanyue.shop.view.activty.GoodsDetailActivity;
import java.util.List;
import io.reactivex.Observable;


public abstract class GoodsListProxy extends RxViewProxy {
    private RxRefreshView<GoodsBean> mRefreshView;
    private GoodsListAdapter mGoodsListAdapter;
    private boolean mNeedUserLookRequest;


    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        filterContainerParm(contentView.getLayoutParams());
        mRefreshView = (RxRefreshView) findViewById(R.id.refreshView);

        mGoodsListAdapter=new GoodsListAdapter(null);
        mRefreshView.setAdapter(mGoodsListAdapter);

        mRefreshView.setReclyViewSetting( RxRefreshView.ReclyViewSetting.creatStaggeredGridSetting(getActivity(),2));
        mGoodsListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                GoodsBean goodsBean=mGoodsListAdapter.getItem(i);
                GoodsDetailActivity.forward(getActivity(),goodsBean.getId());
            }
        });
        mRefreshView.setAdapter(mGoodsListAdapter);
        mRefreshView.setDataListner(new RxRefreshView.DataListner<GoodsBean>() {
            @Override
            public Observable<List<GoodsBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<GoodsBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(mNeedUserLookRequest){
            initData();
        }
    }


    public void initData(){
        if(mRefreshView!=null){
          mRefreshView.initData();
        }
    }


    public void filterContainerParm(ViewGroup.LayoutParams layoutParams){

    }

    public void setNeedUserLookRequest(boolean needUserLookRequest) {
        mNeedUserLookRequest = needUserLookRequest;
    }

    public abstract Observable<List<GoodsBean>> getData(int p);

    @Override
    public int getLayoutId() {
        return R.layout.view_single_refresh;
    }

}

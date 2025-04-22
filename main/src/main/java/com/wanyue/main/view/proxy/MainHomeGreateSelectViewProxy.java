package com.wanyue.main.view.proxy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.utils.SystemUtil;
import com.wanyue.main.R;
import com.wanyue.main.adapter.GreateSelectAdapter;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.GreateSelectBean;
import com.wanyue.shop.view.activty.GoodsDetailActivity;
import com.wanyue.shop.view.activty.GreateGoodsSearchActivity;

import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class MainHomeGreateSelectViewProxy extends RxViewProxy implements BaseQuickAdapter.OnItemClickListener {
    private FrameLayout mVpTopContainer;
    private RxRefreshView<GoodsBean> mRefreshView;
    private GreateSelectAdapter mGreateSelectAdapter;
    private TextView mTvTip;

    @Override
    public int getLayoutId() {
        return R.layout.view_greate_select;
    }

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
       // setDefaultStatusBarPadding();
        mVpTopContainer = (FrameLayout) findViewById(R.id.vp_top_container);
        mRefreshView =  findViewById(R.id.refreshView);
        MainDefaultHeadViewProxy viewProxy=new MainDefaultHeadViewProxy() {
            @Override
            public void foward() {
                startActivity(GreateGoodsSearchActivity.class);
            }
        };

        viewProxy.putArgs("title",getString(R.string.search_goods_name));
        getViewProxyChildMannger().addViewProxy(mVpTopContainer,viewProxy,viewProxy.getDefaultTag());
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(getActivity(),0));
        mGreateSelectAdapter=new GreateSelectAdapter(null);
        mGreateSelectAdapter.setOnItemClickListener(this);
        LayoutInflater inflater=getViewProxyMannger().getLayoutInflater();
        if(inflater!=null){
          View view=inflater.inflate(R.layout.item_head_recly_greate_select,mContentView,false);
          mTvTip =  view.findViewById(R.id.tv_tip);
          mGreateSelectAdapter.addHeaderView(view);
        }

        mRefreshView.setAdapter(mGreateSelectAdapter);
        mRefreshView.setDataListner(new RxRefreshView.DataListner<GoodsBean>() {
            @Override
            public Observable<List<GoodsBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List data) {

            }
            @Override
            public void error(Throwable e) {

            }
        });
    }


    private Observable<List<GoodsBean>> getData(int p) {
        return MainAPI.getGreateSelectGoods(p).map(new Function<GreateSelectBean, List<GoodsBean>>() {
            @Override
            public List<GoodsBean> apply(GreateSelectBean greateSelectBean) throws Exception {
                SystemUtil.logCurrentThread();
                if(mTvTip!=null){
                   mTvTip.setText(greateSelectBean.getTips());
                }
                return greateSelectBean.getList();
            }
        });
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
         if(isVisibleToUser&&mIsFirstUserVisble){
             mIsFirstUserVisble=false;
             initData();
         }
    }

    public void initData() {
        if(mRefreshView!=null){
            mRefreshView.initData();
        }
    }

    /**
     * Callback method to be invoked when an item in this RecyclerView has
     * been clicked.
     *
     * @param adapter  the adpater
     * @param view     The itemView within the RecyclerView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if(mGreateSelectAdapter==null){
            return;
        }
        GoodsBean goodsBean=mGreateSelectAdapter.getItem(position);
        if(goodsBean==null){
            return;
        }
        GoodsDetailActivity.forward(getActivity(),goodsBean.getId());
    }
}

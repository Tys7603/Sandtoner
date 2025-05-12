package com.wanyue.main.view.proxy;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.activity.WebViewActivity;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.live.activity.LiveAudienceActivity;
import com.wanyue.live.bean.LiveBean;
import com.wanyue.live.presenter.LiveRoomCheckLivePresenter;
import com.wanyue.main.R;
import com.wanyue.main.adapter.LikeGoodsAdapter;
import com.wanyue.main.adapter.MainMoudleAdapter;
import com.wanyue.main.adapter.ReCommentAdapter;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.AdvBean;
import com.wanyue.main.bean.BannerBean;
import com.wanyue.main.bean.HomePageBean;
import com.wanyue.main.bean.ReCommentBean;

import com.wanyue.main.integral.view.activity.IntegralActivity;
import com.wanyue.main.view.activity.GreateSelectActivity;
import com.wanyue.main.view.activity.MainActivity;
import com.wanyue.main.view.activity.MySpreadActivity;
import com.wanyue.main.view.activity.SearchLiveActivity;
import com.wanyue.shop.store.view.activity.BusniessStoreActivity;


import com.wanyue.shop.view.activty.GetCouponActivity;
import com.wanyue.shop.view.activty.GoodsDetailActivity;
import com.wanyue.shop.view.activty.GoodsFootActivity;
import com.wanyue.shop.view.activty.GreateGoodsSearchActivity;
import com.wanyue.shop.view.activty.MyCollectGoodsActivity;
import com.wanyue.shop.view.activty.MyOrderActivity;
import com.wanyue.video.activity.AbsVideoPlayActivity;

import java.util.List;


public class MainHomePageViewProxy extends RxViewProxy implements LiveRoomCheckLivePresenter.ActionListener {

    private FrameLayout mVpTopContainer;
    private FrameLayout mVpBannerContainer;
    private BannerViewProxy<BannerBean> mBannerViewProxy;
    private boolean isFirstVisble = true;

    private SmartRefreshLayout mRefreshLayout;

    private RecyclerView mRcRecommend;
    private RecyclerView mRcGoods;
    private RecyclerView mRcMoudle;

    private ReCommentAdapter mReCommentAdapter;
    private LikeGoodsAdapter mLikeGoodsAdapter;
    private MainMoudleAdapter mMainMoudleAdapter;

    private ViewGroup mVpRecommendContainer;
    private  int mPage;

    private AdvViewProxy mAdvViewProxy;
    private FrameLayout mVpAdvContainer;

    @Override
    public int getLayoutId() {
        return R.layout.view_main_home_page;
    }

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        setDefaultStatusBarPadding(R.id.vp_top_container);
        mVpTopContainer = findViewById(R.id.vp_top_container);
        mVpBannerContainer =  findViewById(R.id.vp_banner_container);
        mVpAdvContainer = findViewById(R.id.vp_adv_container);
        mRcMoudle =  findViewById(R.id.rc_moudle);

        mMainMoudleAdapter=new MainMoudleAdapter(null,getActivity());
        mRcMoudle.setAdapter(mMainMoudleAdapter);
        int size=DpUtil.dp2px(5);
        ViewUtil.setViewOutlineProvider(mRcMoudle,size);

        RxRefreshView.ReclyViewSetting.createGridSetting(getActivity(), 2, 0, new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                if(mMainMoudleAdapter==null){
                    return 1;
                }
                int weight=mMainMoudleAdapter.getItem(i).getItemType()==2?2:1;
                return weight;
            }
        }).settingRecyclerView(mRcMoudle);
        initTopRecommend();

        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPage=1;
                initData();
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPage=mPage+1;
                loadMoreData();
            }
        });
        mRcGoods =  findViewById(R.id.rc_goods);
        mLikeGoodsAdapter=new LikeGoodsAdapter(null);
        mLikeGoodsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                if(mLikeGoodsAdapter==null){
                   return;
                }
                GoodsBean goodsBean=mLikeGoodsAdapter.getItem(i);
                GoodsDetailActivity.forward(getActivity(),goodsBean.getId());
            }
        });

        RxRefreshView.ReclyViewSetting.creatStaggeredGridSetting(getActivity(),2).settingRecyclerView(mRcGoods);
        mRcGoods.setAdapter(mLikeGoodsAdapter);
        mVpRecommendContainer =findViewById(R.id.vp_recommend_container);
        MainHomePageHeadViewproxy viewProxy = new MainHomePageHeadViewproxy() {
            @Override
            public void foward() {
                startActivity(GreateGoodsSearchActivity.class);
            }
        };
        viewProxy.putArgs("title", "Please enter product keywords");
        getViewProxyChildMannger().addViewProxy(mVpTopContainer, viewProxy, viewProxy.getDefaultTag());
    }

    private void loadMoreData() {
        mPage=mPage+1;
        MainAPI.getLikeGoods(mPage).compose(this.<List<GoodsBean>>bindToLifecycle())
                .subscribe(new DefaultObserver<List<GoodsBean>>() {
                    @Override
                    public void onNext(@NonNull List<GoodsBean> list) {
                        if(mRefreshLayout!=null){
                           mRefreshLayout.finishLoadMore();
                        }
                        if(!ListUtil.haveData(list)){
                            mPage=mPage-1;
                        }
                        if(mLikeGoodsAdapter!=null){
                           mLikeGoodsAdapter.appendData(list);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(mRefreshLayout!=null){
                           mRefreshLayout.finishRefresh();
                        }
                    }
                }) ;
    }

    private void setAdvToUI(AdvBean adv) {
        if(adv==null||adv.getId()==0){
            getViewProxyMannger().removeViewProxy(mAdvViewProxy);
            mAdvViewProxy=null;
        }else{
            if(mAdvViewProxy==null){
               mAdvViewProxy=new AdvViewProxy();
               getViewProxyMannger().addViewProxy(mVpAdvContainer,mAdvViewProxy,mAdvViewProxy.getDefaultTag());
            }
            mAdvViewProxy.setData(adv);
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser ) {
            isFirstVisble = false;
            initData();
        }
    }


    //推荐和活动
    private void initTopRecommend(){
        mRcRecommend =  findViewById(R.id.rc_recommend);
        RxRefreshView.ReclyViewSetting.createGridSetting(getActivity(),5).settingRecyclerView(mRcRecommend);
        mReCommentAdapter=new ReCommentAdapter(null);
        mRcRecommend.setAdapter(mReCommentAdapter);
        mReCommentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ReCommentAdapter reCommentAdapter= (ReCommentAdapter) adapter;
                ReCommentBean reCommentBean=reCommentAdapter.getItem(position);

                switch (reCommentBean.getId()){
                    case ReCommentAdapter.SIGN:
                        startActivity(IntegralActivity.class);
                        break;
                    case ReCommentAdapter.FOOT:
                        startActivity(GoodsFootActivity.class);
                        break;
                    case ReCommentAdapter.SELECTED_WANGPU:
                        startActivity(BusniessStoreActivity.class);
                        break;
                    case ReCommentAdapter.COUPON:
                        startActivity(GetCouponActivity.class);
                        break;
                    case ReCommentAdapter.GREAT_SELECT:
                        startActivity(GreateSelectActivity.class);
                        break;
                    case ReCommentAdapter.ORDER:
                        startActivity(MyOrderActivity.class);
                        break;
                    case ReCommentAdapter.SPREAD:
                        startActivity(MySpreadActivity.class);
                        break;
                    case ReCommentAdapter.COLECT:
                        startActivity(MyCollectGoodsActivity.class);
                        break;
                    case ReCommentAdapter.CLASSFIY:
                        ((MainActivity)getActivity()).setSelectClasfiy();

                        break;
                    case ReCommentAdapter.HELP:
                        String url= CommonAppConfig.HOST+"/appapi/help/list";
                        WebViewActivity.forward(getActivity(),url,false);
                        break;
                }
            }
        });
    }


    private void initData() {
        MainAPI.getIndex(mPage).compose(this.<HomePageBean>bindToLifecycle())
                .subscribe(new DefaultObserver<HomePageBean>() {
                    @Override
                    public void onNext(@NonNull HomePageBean homePageBean) {
                        if(mRefreshLayout!=null){
                           mRefreshLayout.finishRefresh();
                        }
                        List<BannerBean> list = homePageBean.getBannerList();
                        initBannerView(list);

                        initRecommendViewProxy(homePageBean);

                        AdvBean adv=homePageBean.getAdv();
                        setAdvToUI(adv);

                        if(mReCommentAdapter!=null){
                           mReCommentAdapter.setData(homePageBean.getClassList());
                        }


                        if(mMainMoudleAdapter!=null){
                           mMainMoudleAdapter.setNewData(homePageBean.getMoudleList());
                        }



                        List<GoodsBean>goodsList=homePageBean.getGoodsList();
                        if(mLikeGoodsAdapter!=null){
                           mLikeGoodsAdapter.setData(goodsList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if(mRefreshLayout!=null){
                            mRefreshLayout.finishRefresh();
                        }
                    }
                });
    }

    private RecommendViewProxy mRecommendViewProxy;
    private void initRecommendViewProxy(HomePageBean homePageBean) {
        if(mRecommendViewProxy==null){
           mRecommendViewProxy=new RecommendViewProxy();
           getViewProxyMannger().addViewProxy(mVpRecommendContainer,mRecommendViewProxy,mRecommendViewProxy.getDefaultTag());
        }
            mRecommendViewProxy.setData(homePageBean);

    }

    private void initBannerView(List<BannerBean> beanList) {
        if (!ListUtil.haveData(beanList)) {
            return;
        }
        if (mBannerViewProxy == null) {
            mBannerViewProxy = new BannerViewProxy<>();
            mBannerViewProxy.setData(beanList);
            getViewProxyChildMannger().addViewProxy(mVpBannerContainer, mBannerViewProxy, mBannerViewProxy.getDefaultTag());
        } else {
            mBannerViewProxy.update(beanList);
        }
    }

    @Override
    public void onLiveRoomChanged(LiveBean liveBean, String data) {
        LiveAudienceActivity.forward(getActivity(), liveBean, data);
    }

}

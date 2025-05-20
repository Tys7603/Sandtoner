package com.wanyue.main.view.proxy;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
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
import com.wanyue.shop.adapter.HotGoodsAdapter;
import com.wanyue.shop.store.view.activity.BusniessStoreActivity;


import com.wanyue.shop.view.activty.GetCouponActivity;
import com.wanyue.shop.view.activty.GoodsDetailActivity;
import com.wanyue.shop.view.activty.GoodsFootActivity;
import com.wanyue.shop.view.activty.GreateGoodsSearchActivity;
import com.wanyue.shop.view.activty.MyCollectGoodsActivity;
import com.wanyue.shop.view.activty.MyOrderActivity;
import com.wanyue.video.activity.AbsVideoPlayActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;


public class MainHomePageViewProxy extends RxViewProxy implements LiveRoomCheckLivePresenter.ActionListener {

    private FrameLayout mVpTopContainer;
    private FrameLayout mVpBannerContainer;
    private BannerViewProxy<BannerBean> mBannerViewProxy;
    private boolean isFirstVisble = true;

    private SmartRefreshLayout mRefreshLayout;

    private RecyclerView mRcRecommend;
    private RecyclerView mRcGoods;
    private RecyclerView mRcMoudle;
    private RecyclerView mRcTopProducts;

    private ReCommentAdapter mReCommentAdapter;
    private LikeGoodsAdapter mLikeGoodsAdapter;
    private MainMoudleAdapter mMainMoudleAdapter;
    private HotGoodsAdapter mHotGoodsAdapter;

    private ViewGroup mVpRecommendContainer;
    private  int mPage;

    private AdvViewProxy mAdvViewProxy;
    private FrameLayout mVpAdvContainer;

    private List<GoodsBean> mAllTopProducts = new ArrayList<>();

    private Handler mHandler = new Handler();

    private static final int BOUNDARY_THRESHOLD = 100;
    private static final float SCROLL_RESISTANCE = 0.5f;
    private static final int MAX_OVERSCROLL = 200;
    private static final int SCROLL_DURATION = 300;
    private static final int TOUCH_RETRY_DELAY = 100; // Add delay for touch retry
    private static final int MAX_TOUCH_RETRIES = 3; // Maximum number of touch retries
    private static final float VELOCITY_THRESHOLD = 0.7f; // Add minimal velocity threshold
    
    private OverScroller mScroller;
    private float mLastTouchY;
    private boolean mIsDragging;
    private int mTouchSlop;
    private int mActivePointerId;
    private float mInitialTouchY;
    private boolean mIsAtTop;
    private boolean mIsAtBottom;
    private long mLastScrollTime;
    private float mScrollVelocity;
    private int mTouchRetryCount = 0;
    private Handler mTouchHandler = new Handler();
    private boolean mIsTouchEnabled = true;
    private float mLastVelocity = 0f;
    private boolean mIsBoundaryLocked = false; // Add boundary lock state

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
        mRcTopProducts = findViewById(R.id.rc_top_products);
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
        RxRefreshView.ReclyViewSetting.createGridSetting(getActivity(),2).settingRecyclerView(mRcTopProducts);
        mHotGoodsAdapter = new HotGoodsAdapter(null);
        mRcTopProducts.setAdapter(mHotGoodsAdapter);
        mHotGoodsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsBean goodsBean = mHotGoodsAdapter.getItem(position);
                if(goodsBean != null) {
                    GoodsDetailActivity.forward(getActivity(), goodsBean.getId());
                }
            }
        });
        initTopRecommend();

        mRefreshLayout = findViewById(R.id.refreshLayout);
        
        // Initialize scroll handling
        mScroller = new OverScroller(getActivity(), new DecelerateInterpolator());
        mTouchSlop = ViewConfiguration.get(getActivity()).getScaledTouchSlop();
        
        configureRefreshLayout();
        
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

        // Configure SmartRefreshLayout with boundary handling
        configureRefreshLayout();
        
        // Setup touch handling for all RecyclerViews
        setupRecyclerViewTouchHandling();
    }

    private void configureRefreshLayout() {
        mRefreshLayout.setEnableOverScrollDrag(true);
        mRefreshLayout.setEnableOverScrollBounce(true);
        mRefreshLayout.setEnableNestedScroll(true);
        mRefreshLayout.setEnableScrollContentWhenRefreshed(true);
        mRefreshLayout.setEnableScrollContentWhenLoaded(true);
        
        // Set custom header with boundary handling
        mRefreshLayout.setRefreshHeader(new BoundaryAwareHeader(getActivity()));
        
        // Configure refresh thresholds
        mRefreshLayout.setHeaderHeight(80);
        mRefreshLayout.setHeaderTriggerRate(0.5f);
        mRefreshLayout.setFooterTriggerRate(0.5f);
        
        // Add refresh listener with boundary check
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!mIsAtTop) {
                    refreshLayout.finishRefresh();
                    return;
                }
                mPage = 1;
                initData();
            }
        });
        
        // Add load more listener with boundary check
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!mIsAtBottom) {
                    refreshLayout.finishLoadMore();
                    return;
                }
                mPage++;
                loadMoreData();
            }
        });
    }

    private void setupRecyclerViewTouchHandling() {
        RecyclerView[] recyclerViews = {mRcMoudle, mRcTopProducts, mRcGoods, mRcRecommend};
        for (RecyclerView recyclerView : recyclerViews) {
            if (recyclerView != null) {
                recyclerView.setNestedScrollingEnabled(true);
                
                // Add touch listener with retry mechanism
                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (!mIsTouchEnabled) {
                            return false;
                        }
                        
                        boolean handled = handleRecyclerViewTouch(recyclerView, event);
                        
                        // Handle touch failure
                        if (!handled && event.getAction() == MotionEvent.ACTION_DOWN) {
                            handleTouchFailure();
                        }
                        
                        return handled;
                    }
                });
                
                // Add scroll listener with enhanced boundary detection
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        updateBoundaryState(recyclerView);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            resetTouchState();
                        }
                    }
                    
                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        updateScrollVelocity(dy);
                        updateBoundaryState(recyclerView);
                        
                        // Reset touch retry count on successful scroll
                        if (Math.abs(dy) > 0) {
                            resetTouchState();
                        }
                    }
                });
            }
        }
    }
    
    private void handleTouchFailure() {
        if (mTouchRetryCount < MAX_TOUCH_RETRIES) {
            mTouchRetryCount++;
            mIsTouchEnabled = false;
            mIsBoundaryLocked = false; // Reset boundary lock on failure
            
            mTouchHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsTouchEnabled = true;
                    // Enhanced scroll position recovery
                    for (RecyclerView recyclerView : new RecyclerView[]{mRcMoudle, mRcTopProducts, mRcGoods, mRcRecommend}) {
                        if (recyclerView != null) {
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mIsAtTop) {
                                        recyclerView.scrollToPosition(0);
                                    } else if (mIsAtBottom) {
                                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                                    }
                                }
                            });
                        }
                    }
                }
            }, TOUCH_RETRY_DELAY);
        } else {
            // Enhanced reset on too many retries
            resetTouchState();
            mTouchRetryCount = 0;
            mIsBoundaryLocked = false;
            // Force scroll to nearest boundary
            for (RecyclerView recyclerView : new RecyclerView[]{mRcMoudle, mRcTopProducts, mRcGoods, mRcRecommend}) {
                if (recyclerView != null) {
                    int currentScroll = recyclerView.computeVerticalScrollOffset();
                    int maxScroll = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent();
                    if (currentScroll < maxScroll / 2) {
                        recyclerView.scrollToPosition(0);
                    } else {
                        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    }
                }
            }
        }
    }
    
    private void resetTouchState() {
        mTouchRetryCount = 0;
        mIsTouchEnabled = true;
        mIsDragging = false;
        mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    }
    
    private boolean handleRecyclerViewTouch(RecyclerView recyclerView, MotionEvent event) {
        // Add velocity check to prevent rapid scrolling
        if (Math.abs(mScrollVelocity) > MAX_OVERSCROLL * 2) {
            return false;
        }
        
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!mIsTouchEnabled) {
                    return false;
                }
                mActivePointerId = event.getPointerId(0);
                mInitialTouchY = event.getY();
                mLastTouchY = mInitialTouchY;
                mIsDragging = false;
                mScroller.forceFinished(true);
                mIsBoundaryLocked = false; // Reset boundary lock on new touch
                return false;
                
            case MotionEvent.ACTION_MOVE:
                if (!mIsTouchEnabled || mActivePointerId == MotionEvent.INVALID_POINTER_ID) {
                    return false;
                }
                
                int pointerIndex = event.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }
                
                float y = event.getY(pointerIndex);
                float deltaY = y - mLastTouchY;
                mLastTouchY = y;
                
                // Enhanced velocity-based resistance
                float velocityResistance = 1.0f - (Math.abs(mScrollVelocity) / (MAX_OVERSCROLL * 2));
                velocityResistance = Math.max(0.3f, velocityResistance); // Increase minimum resistance
                deltaY *= velocityResistance;
                
                if (!mIsDragging && Math.abs(deltaY) > mTouchSlop) {
                    mIsDragging = true;
                }
                
                if (mIsDragging) {
                    // Enhanced boundary resistance with lock
                    if ((mIsAtTop && deltaY > 0) || (mIsAtBottom && deltaY < 0)) {
                        if (!mIsBoundaryLocked) {
                            float boundaryResistance = SCROLL_RESISTANCE * (1.0f + Math.abs(mScrollVelocity) / MAX_OVERSCROLL);
                            deltaY *= Math.min(0.2f, boundaryResistance); // Increase boundary resistance
                            if (Math.abs(deltaY) < 1.0f) {
                                mIsBoundaryLocked = true; // Lock at boundary
                                return true;
                            }
                        } else {
                            return true; // Maintain lock
                        }
                    } else {
                        mIsBoundaryLocked = false;
                    }
                    
                    // Dynamic overscroll limit with velocity threshold
                    float maxOverscroll = MAX_OVERSCROLL * (1.0f - Math.abs(mScrollVelocity) / (MAX_OVERSCROLL * 2));
                    if (Math.abs(mScrollVelocity) > MAX_OVERSCROLL * VELOCITY_THRESHOLD) {
                        maxOverscroll *= 0.5f; // Reduce overscroll at high velocity
                    }
                    if (Math.abs(deltaY) > maxOverscroll) {
                        deltaY = deltaY > 0 ? maxOverscroll : -maxOverscroll;
                    }
                    
                    recyclerView.scrollBy(0, (int) deltaY);
                    return true;
                }
                break;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsDragging) {
                    // Enhanced boundary handling
                    if (mIsAtTop || mIsAtBottom) {
                        if (mIsBoundaryLocked) {
                            // Quick snap to boundary
                            recyclerView.scrollToPosition(mIsAtTop ? 0 : recyclerView.getAdapter().getItemCount() - 1);
                        } else {
                            smoothScrollToBoundary(recyclerView);
                        }
                    }
                }
                mIsDragging = false;
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                mIsBoundaryLocked = false;
                break;
                
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                break;
        }
        return false;
    }
    
    private void updateBoundaryState(RecyclerView recyclerView) {
        mIsAtTop = !recyclerView.canScrollVertically(-1);
        mIsAtBottom = !recyclerView.canScrollVertically(1);
        
        // Update refresh/load more state
        mRefreshLayout.setEnableRefresh(mIsAtTop);
        mRefreshLayout.setEnableLoadMore(mIsAtBottom);
    }
    
    private void updateScrollVelocity(int dy) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastScrollTime > 0) {
            mScrollVelocity = dy / (float)(currentTime - mLastScrollTime);
        }
        mLastScrollTime = currentTime;
    }
    
    private void smoothScrollToBoundary(RecyclerView recyclerView) {
        int currentScroll = recyclerView.computeVerticalScrollOffset();
        int maxScroll = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent();
        
        if (currentScroll < BOUNDARY_THRESHOLD) {
            // Scroll to top
            mScroller.startScroll(0, currentScroll, 0, -currentScroll, SCROLL_DURATION);
        } else if (currentScroll > maxScroll - BOUNDARY_THRESHOLD) {
            // Scroll to bottom
            mScroller.startScroll(0, currentScroll, 0, maxScroll - currentScroll, SCROLL_DURATION);
        }
        
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (mScroller.computeScrollOffset()) {
                    recyclerView.scrollTo(0, mScroller.getCurrY());
                    recyclerView.post(this);
                }
            }
        });
    }
    
    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastTouchY = ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }
    
    // Custom header with boundary awareness
    private static class BoundaryAwareHeader extends com.scwang.smartrefresh.layout.header.ClassicsHeader {
        public BoundaryAwareHeader(Context context) {
            super(context);
            setEnableLastTime(false);
            setDrawableSize(20);
            setDrawableMarginRight(20);
            setFinishDuration(300);
            setTextSizeTitle(14);
            setTextSizeTime(10);
            setAccentColor(0xffbbbbbb);
            setPrimaryColor(0xffffffff);
        }
        
        @Override
        public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
            // Add resistance when reaching boundaries
            if (percent > 1.0f) {
                percent = 1.0f + (percent - 1.0f) * SCROLL_RESISTANCE;
            }
            super.onMoving(isDragging, percent, offset, height, maxDragHeight);
        }
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
        // Call top products API first
        MainAPI.getTopProducts().compose(this.<List<GoodsBean>>bindToLifecycle())
                .subscribe(new DefaultObserver<List<GoodsBean>>() {
                    @Override
                    public void onNext(@NonNull List<GoodsBean> topProducts) {
                        if (topProducts != null && !topProducts.isEmpty()) {
                            mHotGoodsAdapter.setData(topProducts);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });

        // Get other homepage data
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

                        // Handle guess you like products
                        List<GoodsBean> goodsList = homePageBean.getGoodsList();
                        List<GoodsBean> guessYouLike = new ArrayList<>();
                        if (goodsList != null && !goodsList.isEmpty()) {
                            List<GoodsBean> tempList = new ArrayList<>(goodsList);
                            Collections.shuffle(tempList);
                            int guessCount = Math.min(10, tempList.size());
                            guessYouLike.addAll(tempList.subList(0, guessCount));
                        }
                        if(mLikeGoodsAdapter!=null){
                            mLikeGoodsAdapter.setData(guessYouLike);
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

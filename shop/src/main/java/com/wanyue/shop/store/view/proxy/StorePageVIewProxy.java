package com.wanyue.shop.store.view.proxy;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wanyue.common.activity.WebViewActivity;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.proxy.BaseViewProxy;
import com.wanyue.common.proxy.ViewProxyPageAdapter;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.R2;
import com.wanyue.shop.adapter.ShopNavigatorAadpter;
import com.wanyue.shop.bean.GoodsSearchArgs;
import com.wanyue.shop.bean.StoreBean;
import com.wanyue.shop.business.ShopEvent;
import com.wanyue.shop.components.view.GoodsListProxy;
import com.wanyue.shop.store.api.StoreAPI;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import io.reactivex.Observable;

public class StorePageVIewProxy extends BaseStoreChildVIewProxy {
    private RoundedImageView mImgStoreAvator;
    private TextView mTvStoreName;
    private TextView mTvTime;
    private ViewGroup mBtnFollow;
    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    protected StoreBean mStoreBean;
    private TextView mTvFollow;
    private TextView mTvStoreSelf;



    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mImgStoreAvator = (RoundedImageView) findViewById(R.id.img_store_avator);
        mTvStoreName = (TextView) findViewById(R.id.tv_store_name);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mBtnFollow =  findViewById(R.id.btn_follow);
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTvFollow = (TextView) findViewById(R.id.tv_follow);
        mTvStoreSelf = (TextView) findViewById(R.id.tv_store_self);

        String[] titleArray = getTitleArray();
        List<BaseViewProxy> viewProxiyList = new ArrayList<>();
        loadViewProxy(viewProxiyList);
        mViewPager.setOffscreenPageLimit(viewProxiyList.size());

        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        ShopNavigatorAadpter adapter = new ShopNavigatorAadpter(titleArray,getActivity(), mViewPager);
        commonNavigator.setAdapter(adapter);

        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
        ViewProxyPageAdapter pageAdapter = new ViewProxyPageAdapter(getViewProxyMannger(),viewProxiyList);
        pageAdapter.attachViewPager(mViewPager,0);


    }

    private void checkFollowButtonAnswer() {
        if(mStoreBean.getIsFollow()==1){
           mTvFollow.setText("Following");
           mTvFollow.setBackground(ResourceUtil.getDrawable(R.drawable.bg_color_gray1_radius_15,false));
        }else{
            mTvFollow.setText("Follow");
            mTvFollow.setBackground(ResourceUtil.getDrawable(R.drawable.bg_color_global_radius_15,false));
        }
    }


    private void loadViewProxy(List<BaseViewProxy> list) {
        GoodsListProxy goodsListProxy1=new GoodsListProxy() {
            @Override
            public void filterContainerParm(ViewGroup.LayoutParams layoutParams) {
                super.filterContainerParm(layoutParams);
            }
            @Override
            public Observable<List<GoodsBean>> getData(int p) {
                return getGoods(p,2);
            }
        };
        goodsListProxy1.setNeedUserLookRequest(true);
        GoodsListProxy goodsListProxy2=new GoodsListProxy() {
            @Override
            public Observable<List<GoodsBean>> getData(int p) {
                return getGoods(p,1);
            }
        };
        goodsListProxy2.setNeedUserLookRequest(true);
        StoreCouponListVIewProxy couponListViewProxy=new StoreCouponListVIewProxy();
        couponListViewProxy.setCloseStore(true);
        couponListViewProxy.setStoreId(mStoreId);
        list.add(goodsListProxy1);
        list.add(goodsListProxy2);
        list.add(couponListViewProxy);
    }

    private Observable<List<GoodsBean>> getGoods(int p, int i) {
        GoodsSearchArgs args=new GoodsSearchArgs();
        args.order=i;
        args.mer_id=mStoreId;
       return StoreAPI.getProductList(args,p).compose(this.<List<GoodsBean>>bindToLifecycle());
    }


    private String[] getTitleArray() {
        String[]titleArray={"Recommendations","New Products","Coupons"};
        return titleArray;
    }



    @OnClick(R2.id.btn_follow)
    public void toFollow(){
        if(mStoreBean==null){
            return;
        }
    String id=  mStoreBean.getId();
    final int isTargeFollow= mStoreBean.getIsFollow()==1?0:1;
     StoreAPI.attenStore(id,isTargeFollow).compose(this.<Boolean>bindToLifecycle())
      .subscribe(new DefaultObserver<Boolean>() {
          @Override
          public void onNext(@NonNull Boolean aBoolean) {
              if(aBoolean){
                  mStoreBean.setIsFollow(isTargeFollow);
                  LiveEventBus.get(ShopEvent.FOLLOW_STORE).post(mStoreBean);
              }
          }
      });

    }

    @Override
    public int getLayoutId() {
        return R.layout.view_store_page;
    }

    public void setStoreBean(StoreBean storeBean) {
        mStoreBean = storeBean;
        if(!isInit()){
            return;
        }
        mTvTime.setText("Followers: "+mStoreBean.getFansNum());
        mTvStoreName.setText(mStoreBean.getName());
        ImgLoader.display(getActivity(),mStoreBean.getAvatar(),mImgStoreAvator);
        checkFollowButtonAnswer();
        if(storeBean.getShoptype()==2){
          ViewUtil.setVisibility(mTvStoreSelf, View.VISIBLE);
        }else{
          ViewUtil.setVisibility(mTvStoreSelf, View.GONE);
        }
    }

    @OnClick(R2.id.img_bg)
    public void toStoreHome(){
        if(mStoreBean==null){
            return;
        }

        WebViewActivity.forward(getActivity(),mStoreBean.getContentUrl(),"Store Introduction");
    }


    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }
}

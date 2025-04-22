package com.wanyue.shop.view.activty;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.proxy.BaseViewProxy;
import com.wanyue.common.proxy.ViewProxyPageAdapter;
import com.wanyue.common.server.observer.DialogObserver;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.adapter.MyCouponAdapter;
import com.wanyue.shop.adapter.MyOverdueCouponAdapter;
import com.wanyue.shop.adapter.ShopNavigatorAadpter;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.CouponBean;
import com.wanyue.shop.bean.MyCouponBean;
import com.wanyue.shop.components.adapter.BaseCouponListAdapter;
import com.wanyue.shop.components.view.CouponListViewProxy;
import com.wanyue.shop.store.view.activity.StoreActivity;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;

/**
 * The type My coupon activity.
 */
public class MyCouponActivity extends BaseActivity {

    private MagicIndicator mIndicator;
    private ViewPager mViewPager;

    private CouponListViewProxy<MyCouponBean> mCouponListViewProxy1;
    private CouponListViewProxy<MyCouponBean> mCouponListViewProxy2;

    @Override
    public void init() {
        mIndicator = findViewById(R.id.indicator);
        mViewPager = findViewById(R.id.viewPager);

        String[] titleArray = getTitleArray();
        List<BaseViewProxy> viewProxiyList = new ArrayList<>();
        loadViewProxy(viewProxiyList);
        mViewPager.setOffscreenPageLimit(viewProxiyList.size());

        CommonNavigator commonNavigator = new CommonNavigator(this);
        ShopNavigatorAadpter adapter = new ShopNavigatorAadpter(titleArray,this, mViewPager);
        commonNavigator.setAdapter(adapter);
        commonNavigator.setAdjustMode(true);
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
        ViewProxyPageAdapter pageAdapter = new ViewProxyPageAdapter(getViewProxyMannger(),viewProxiyList);
        pageAdapter.attachViewPager(mViewPager,0);
    }

    private void loadViewProxy(List<BaseViewProxy> viewProxiyList) {
        mCouponListViewProxy1=new CouponListViewProxy() {
            @Override
            public Observable<List<MyCouponBean>> getData(int p) {
                return ShopAPI.getMyCoupon(p,1);
            }
        };
        mCouponListViewProxy1.setClickListner(new CouponListViewProxy.OnCouponListner<MyCouponBean>() {
            @Override
            public void click(View view, CouponBean couponBean, int position) {
                StoreActivity.forward(mContext,couponBean.getShopId());
            }
        });
       final  MyCouponAdapter couponAdapter= new MyCouponAdapter(null);
        couponAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                CouponBean couponBean=  couponAdapter.getItem(i);
                StoreActivity.forward(mContext,couponBean.getShopId());
            }
        });
        mCouponListViewProxy1.setCouponListAdapter(couponAdapter);

        mCouponListViewProxy2=new CouponListViewProxy() {
            @Override
            public Observable<List<MyCouponBean>> getData(int p) {
                return ShopAPI.getMyCoupon(p,3);
            }
        };

        MyOverdueCouponAdapter overdueCouponAdapter= new MyOverdueCouponAdapter(null);
        mCouponListViewProxy2.setCouponListAdapter(overdueCouponAdapter);
        mCouponListViewProxy2.setClickListner(new CouponListViewProxy.OnCouponListner<MyCouponBean>() {
            @Override
            public void click(View view, CouponBean couponBean, int position) {
                deleteCoupon(couponBean,position);
            }
        });

        viewProxiyList.add(mCouponListViewProxy1);
        viewProxiyList.add(mCouponListViewProxy2);
    }

    //删除优惠券
    private void deleteCoupon(CouponBean couponBean,final  int position) {
        ShopAPI.deleteCoupon(couponBean.getId()).subscribe( new DialogObserver<Boolean>(this) {
            @Override
            public void onNextTo(Boolean aBoolean) {
                if(aBoolean){
                    mCouponListViewProxy2.initData();
                }
            }
        });
    }

    private String[] getTitleArray() {
        String[] titleArray={"未使用","已过期"};
        return titleArray;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_coupon;
    }
}
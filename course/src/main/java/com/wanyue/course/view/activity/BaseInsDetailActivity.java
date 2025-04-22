package com.wanyue.course.view.activity;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.proxy.BaseViewProxy;
import com.wanyue.common.proxy.ViewProxyPageAdapter;
import com.wanyue.course.R2;
import com.wanyue.course.adapter.CourseNavigatorAadpter;
import com.wanyue.course.bean.ProjectBean;
import com.wanyue.course.busniess.CourseEvent;
import com.wanyue.course.busniess.IBuyer;
import com.wanyue.course.view.proxy.insbottom.BottomViewHelper;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class BaseInsDetailActivity<T extends ProjectBean> extends BaseActivity {

    @BindView(R2.id.vp_top_container)
    protected ViewGroup mVpTopContainer;

    @BindView(R2.id.indicator)
    protected MagicIndicator mIndicator;

    @BindView(R2.id.viewPager)
    protected ViewPager mViewPager;

    @BindView(R2.id.vp_bottom)
    protected ViewGroup mVpBottom;

    protected T mProjectBean;
    protected BottomViewHelper mBottomViewHelper;


    @Override
    public void init() {
        mProjectBean = (T) getIntent().getParcelableExtra(Constants.DATA);
        if (mProjectBean == null) {
            finish();
            return;
        }
        String[] titleArray = getTitleArray();
        List<BaseViewProxy> viewProxiyList = new ArrayList<>();
        loadViewProxy(viewProxiyList);
        mViewPager.setOffscreenPageLimit(viewProxiyList.size());
        ViewProxyPageAdapter pageAdapter = new ViewProxyPageAdapter(getViewProxyMannger(), viewProxiyList);
        pageAdapter.attachViewPager(mViewPager);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        CourseNavigatorAadpter adapter = new CourseNavigatorAadpter(titleArray, this, mViewPager);
        commonNavigator.setAdapter(adapter);
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);

        LiveEventBus.get(CourseEvent.BUY_EVENT).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(@Nullable Object object) {
                refreshData();
            }
        });
        setDataToUI(mProjectBean);
    }

    protected void setDataToUI(T projectBean) {

    }

    public void refreshData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    protected boolean needCheckVip() {
        return mProjectBean.getIsvip() == 1;
    }

    /*查看观看权限*/
    public boolean checkWatchPermisson() {
        if (mProjectBean == null) {
            return false;
        }
        initBottmViewHelper();
        return mBottomViewHelper.setProjectBean(mProjectBean);
    }


    protected void initBottmViewHelper() {
        if (mBottomViewHelper == null) {
            mBottomViewHelper = new BottomViewHelper(mVpBottom, getViewProxyMannger());
            mBottomViewHelper.setListner(new IBuyer.Listner() {
                @Override
                public void onSuccess() {
                    refreshData();
                }
                @Override
                public void onCancle() {
                }
                @Override
                public void onError(int code) {
                }
            });
        }
    }

    /*初始化代理类*/
    public abstract void loadViewProxy(List<BaseViewProxy> viewProxiyList);

    /*初始化标题类*/
    public abstract String[] getTitleArray();


    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }
}

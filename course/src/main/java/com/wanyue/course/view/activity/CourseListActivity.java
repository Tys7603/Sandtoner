package com.wanyue.course.view.activity;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.adapter.BaseNavigatorAdapter;
import com.wanyue.common.bean.CatBean;
import com.wanyue.common.proxy.BaseViewProxy;
import com.wanyue.common.proxy.ViewProxyPageAdapter;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.course.R;
import com.wanyue.course.adapter.CourseNavigatorAadpter;
import com.wanyue.course.api.CourseAPI;
import com.wanyue.course.bean.ProjectBean;
import com.wanyue.course.view.proxy.ProjectListProxy;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;

public class CourseListActivity extends BaseActivity {
    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    private BaseNavigatorAdapter mIndicatorAdapter;

    @Override
    public void init() {
        setTabTitle("Selected Content");
        mIndicator = findViewById(R.id.indicator);
        mViewPager =findViewById(R.id.viewPager);
        getCat();
    }

    private void getCat() {
        CourseAPI.getCat().compose(this.<List<CatBean>>bindToLifecycle()).subscribe(new DefaultObserver<List<CatBean>>() {
            @Override
            public void onNext(@NonNull List<CatBean> catBeans) {
                int size= ListUtil.size(catBeans);

                CommonNavigator commonNavigator = new CommonNavigator(CourseListActivity.this);
                mIndicatorAdapter = new BaseNavigatorAdapter(catBeans,CourseListActivity.this, mViewPager);
                mIndicatorAdapter.setEableScale(false);
                //commonNavigator.setAdjustMode(true);
                commonNavigator.setAdapter(mIndicatorAdapter);

                mIndicator.setNavigator(commonNavigator);
                ViewPagerHelper.bind(mIndicator, mViewPager);
                ViewProxyPageAdapter pageAdapter = new ViewProxyPageAdapter(getViewProxyMannger(),getViewProxy(catBeans));
                pageAdapter.attachViewPager(mViewPager,0);
            }
        });
    }

    private List<? extends BaseViewProxy> getViewProxy(List<CatBean> catList) {
        List<BaseViewProxy> viewProxyList=new ArrayList<>();
        for(final CatBean catBean:catList){
            ProjectListProxy projectListProxy=new ProjectListProxy() {
                @Override
                public Observable<List<ProjectBean>> getData(int p) {
                    return CourseAPI.get(catBean.getId(),p);
                }
                @Override
                public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                }
            };
            viewProxyList.add(projectListProxy);
        }
        return viewProxyList;

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_course_list;
    }
}
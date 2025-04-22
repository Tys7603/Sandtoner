package com.wanyue.video.activity;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.adapter.BaseNavigatorAdapter;
import com.wanyue.common.bean.CatBean;
import com.wanyue.common.proxy.BaseViewProxy;
import com.wanyue.common.proxy.ViewProxyPageAdapter;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.video.R;
import com.wanyue.video.api.VideoAPI;
import com.wanyue.video.bean.VideoBean;
import com.wanyue.video.components.view.VideoListProxy;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;

public class VideoListActivity extends BaseActivity {

    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    private BaseNavigatorAdapter mIndicatorAdapter;

    @Override
    public void init() {
        setTabTitle("视频");
        mIndicator = findViewById(R.id.indicator);
        mViewPager =findViewById(R.id.viewPager);
        getCat();
    }

    private void getCat() {
        VideoAPI.getCat(1).compose(this.<List<CatBean>>bindToLifecycle()).subscribe(new DefaultObserver<List<CatBean>>() {
            @Override
            public void onNext(@NonNull List<CatBean> catBeans) {
                CommonNavigator commonNavigator = new CommonNavigator(VideoListActivity.this);
                mIndicatorAdapter = new BaseNavigatorAdapter(catBeans,VideoListActivity.this, mViewPager);
                mIndicatorAdapter.setEableScale(false);
                //commonNavigator.setAdjustMode(true);
                commonNavigator.setAdapter(mIndicatorAdapter);

                mIndicator.setNavigator(commonNavigator);
                ViewPagerHelper.bind(mIndicator, mViewPager);
                ViewProxyPageAdapter pageAdapter = new ViewProxyPageAdapter(getViewProxyMannger(),getViewProxy(catBeans));
                pageAdapter.attachViewPager(mViewPager,1);
            }
        });
    }

    private List<? extends BaseViewProxy> getViewProxy(List<CatBean> catList) {
        List<BaseViewProxy> viewProxyList=new ArrayList<>();
        for(final CatBean catBean:catList){
            VideoListProxy projectListProxy=new VideoListProxy() {
                @Override
                public Observable<List<VideoBean>> getData(int p) {
                   return VideoAPI.getVideoList(catBean.getId(),p);
                }
                @Override
                public String getKey() {
                    return "videolist&"+catBean.getId();
                }
            };
            projectListProxy.setLoadByUserVisible(true);
            viewProxyList.add(projectListProxy);
        }
        return viewProxyList;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_list;
    }
}
package com.wanyue.main.find.view.activity;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.adapter.BaseNavigatorAdapter;
import com.wanyue.common.bean.LiveClassBean;
import com.wanyue.common.proxy.BaseViewProxy;
import com.wanyue.common.proxy.ViewProxyPageAdapter;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.live.R;
import com.wanyue.live.bean.LiveBean;
import com.wanyue.live.http.LiveHttpUtil;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.FeatureBean;
import com.wanyue.main.find.api.FindAPI;
import com.wanyue.main.find.bean.FindBean;
import com.wanyue.main.find.bean.FindLiveBean;
import com.wanyue.main.find.view.proxy.NormalFindListProxy;
import com.wanyue.main.view.proxy.HomeLiveViewProxy;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class LiveListActivity extends BaseActivity {

    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    private BaseNavigatorAdapter mIndicatorAdapter;


    @Override
    public void init() {
        setTabTitle("Live Stream");
        mIndicator = findViewById(com.wanyue.video.R.id.indicator);
        mViewPager =findViewById(com.wanyue.video.R.id.viewPager);
        getCatData();
    }


    private void getCatData() {
        LiveHttpUtil.getLiveClassList().compose(this.<List<LiveClassBean>>bindToLifecycle())
        .subscribe(new DefaultObserver<List<LiveClassBean>>() {
            @Override
            public void onNext(@NonNull List<LiveClassBean> list) {
                LiveClassBean liveClassBean=new LiveClassBean();
                liveClassBean.setId(LiveClassBean.FOLLOW);
                liveClassBean.setName(getString(com.wanyue.main.R.string.follow));
                list.add(0,liveClassBean);

                liveClassBean=new LiveClassBean();
                liveClassBean.setId(LiveClassBean.FEATURED);
                liveClassBean.setName(getString(com.wanyue.main.R.string.featured));
                list.add(1,liveClassBean);

                CommonNavigator commonNavigator = new CommonNavigator(LiveListActivity.this);
                mIndicatorAdapter = new BaseNavigatorAdapter(list,LiveListActivity.this, mViewPager);
                mIndicatorAdapter.setEableScale(false);
                //commonNavigator.setAdjustMode(true);
                commonNavigator.setAdapter(mIndicatorAdapter);

                mIndicator.setNavigator(commonNavigator);
                ViewPagerHelper.bind(mIndicator, mViewPager);
                ViewProxyPageAdapter pageAdapter = new ViewProxyPageAdapter(getViewProxyMannger(),getViewProxy(list));
                pageAdapter.attachViewPager(mViewPager,1);
            }
        });
    }

    private List<? extends BaseViewProxy> getViewProxy(List<LiveClassBean> list) {
        List<BaseViewProxy> viewProxyList=new ArrayList<>();
        NormalFindListProxy normalFindListProxy=null;

        for(final LiveClassBean catBean:list){
              int id=  catBean.getId();
            if(id==LiveClassBean.FEATURED){
                normalFindListProxy=new NormalFindListProxy() {
                    @Override
                    public Observable<List<FindBean>> getData(int p) {
                        return FindAPI.getFeatured(p);
                    }
                };
            }else if(id==LiveClassBean.FOLLOW){
                normalFindListProxy=new NormalFindListProxy() {
                    @Override
                    public Observable<List<FindBean>> getData(int p) {
                        return FindAPI.getLiveListByFollow(p);
                    }
                };
            }else{
                normalFindListProxy=new NormalFindListProxy(){
                    public Observable<List<FindBean>> getData(int p) {
                        return FindAPI.getLiveListByClass(catBean.getId()+"",p).compose(this.<List<FindBean>>bindToLifecycle());
                    }
                };
            }

            normalFindListProxy.setIconId(R.mipmap.icon_default_no_data);
            normalFindListProxy.setLevel(1);
            normalFindListProxy.setTitle("No live stream available");
            viewProxyList.add(normalFindListProxy);
        }
        return viewProxyList;
    }



    @Override
    public int getLayoutId() {
        return R.layout.activity_live_list;
    }
}
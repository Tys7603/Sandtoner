package com.wanyue.main.view.proxy;

import android.graphics.Color;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;
import com.wanyue.common.proxy.BaseViewProxy;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.proxy.ViewProxyPageAdapter;
import com.wanyue.main.R;
import com.wanyue.main.find.adapter.MainFindIndicatorAdapter;
import com.wanyue.main.find.api.FindAPI;
import com.wanyue.main.find.bean.FindBean;
import com.wanyue.main.find.view.proxy.NormalFindListProxy;
import com.wanyue.main.find.view.proxy.VideoFindListProxy;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

public class MainHomeFindViewProxy extends RxViewProxy {

    private ViewGroup mVpTab;
    private MagicIndicator mIndicator;
    private ViewPager mViewPager;

    private MainFindIndicatorAdapter mIndicatorAdapter;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        setDefaultStatusBarPadding();
        mVpTab = findViewById(R.id.vp_tab);
        mIndicator =  findViewById(R.id.indicator);
        mViewPager =  findViewById(R.id.viewPager);

        String[] titleArray={"关注","上新","种草","直播","视频"};

        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        mIndicatorAdapter = new MainFindIndicatorAdapter(titleArray, getActivity(), mViewPager);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mIndicatorAdapter);

        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
        ViewProxyPageAdapter pageAdapter = new ViewProxyPageAdapter(getViewProxyMannger(),getViewProxy());
        pageAdapter.attachViewPager(mViewPager,0);
    }

    private List<? extends BaseViewProxy> getViewProxy() {
        List<RxViewProxy>list=new ArrayList<>();
        NormalFindListProxy find1=new NormalFindListProxy();



        NormalFindListProxy find2=new NormalFindListProxy();
        find2.setType(FindBean.TYPE_NEW);



        NormalFindListProxy find3=new NormalFindListProxy();
        find3.setType(FindBean.TYPE_RECOMMEND);
        NormalFindListProxy find4=new NormalFindListProxy(){
            @Override
            public Observable<List<FindBean>> getData(int p) {
                return FindAPI.getFindLiveList(p);
            }
        };
        
        find4.setType(FindBean.TYPE_LIVE);
        VideoFindListProxy find5=new VideoFindListProxy();
        list.add(find1);
        list.add(find2);
        list.add(find3);
        list.add(find4);
        list.add(find5);

        find1.setLevel(1);
        find1.setTitle("暂无数据，去其他频道看看吧～");
        find1.setBackgroundColor(Color.WHITE);

        find2.setLevel(1);
        find2.setTitle("暂无数据，去其他频道看看吧～");
        find2.setBackgroundColor(Color.WHITE);

        find3.setLevel(1);
        find3.setTitle("暂无数据，去其他频道看看吧～");
        find3.setBackgroundColor(Color.WHITE);

        find4.setLevel(1);
        find4.setTitle("暂无数据，去其他频道看看吧～");
        find4.setBackgroundColor(Color.WHITE);

        return list;
    }


    @Override
    public int getLayoutId() {
        return R.layout.view_main_find;
    }
}

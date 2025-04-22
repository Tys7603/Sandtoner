package com.wanyue.main.view.activity;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.proxy.BaseViewProxy;
import com.wanyue.common.proxy.ViewProxyPageAdapter;
import com.wanyue.main.R;
import com.wanyue.main.adapter.BrokerageIndicatorAdapter;
import com.wanyue.main.view.proxy.BrokerageRankListViewProxy;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import java.util.ArrayList;
import java.util.List;

public class BrokerageRankActivity extends BaseActivity {
    private TextView mTvSelfRank;
    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    @Override
    public void init() {
        setTabTitle(getString(R.string.spread_tip6));
        mTvSelfRank = (TextView) findViewById(R.id.tv_self_rank);
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);


        List<BaseViewProxy> viewProxyList=initLiveViewList();
        ViewProxyPageAdapter pageAdapter = new ViewProxyPageAdapter(getViewProxyMannger(), viewProxyList);
        pageAdapter.attachViewPager(mViewPager,0);
        mViewPager.setOffscreenPageLimit(viewProxyList.size());
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        String[]titleArray={getString(R.string.week_rank),getString(R.string.month_rank)};
        BrokerageIndicatorAdapter brokerageIndicatorAdapter=new BrokerageIndicatorAdapter(titleArray,this,mViewPager);
        commonNavigator.setAdapter(brokerageIndicatorAdapter);
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
    }


    private List<BaseViewProxy> initLiveViewList() {
        List<BaseViewProxy>viewProxyList=new ArrayList<>(2);
        BrokerageRankListViewProxy viewProxy1=new BrokerageRankListViewProxy() {
            @Override
            public void positionNotify(int position) {
                setSelfRankPosition(position);
            }
        };
        viewProxy1.setType("week");
        BrokerageRankListViewProxy viewProxy2=new BrokerageRankListViewProxy() {
            @Override
            public void positionNotify(int position) {
                setSelfRankPosition(position);
            }
        };
        viewProxy1.setType("month");
        viewProxyList.add(viewProxy1);
        viewProxyList.add(viewProxy2);
        return viewProxyList;
    }

    private void setSelfRankPosition(int position) {
        if(position==0){
            mTvSelfRank.setText(R.string.spread_tip17);
        }else{
            String positionString=Integer.toString(position);
            String title=getString(R.string.spread_tip18,positionString);
            int startIndex=title.indexOf(positionString);
            SpannableStringBuilder style = new SpannableStringBuilder(title);
            style.setSpan(new AbsoluteSizeSpan(25, true), startIndex, startIndex+positionString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            mTvSelfRank.setText(style);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_brokerage_rank;
    }

}

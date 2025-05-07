package com.wanyue.main.store.view.activity;

import android.view.View;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.http.ParseSingleHttpCallback;
import com.wanyue.common.proxy.ViewProxyPageAdapter;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.main.R;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.business.MainEvent;
import com.wanyue.main.store.adapter.ConsignmentManngerIndicatorAdapter;
import com.wanyue.main.store.bean.ConsignMentGoodsBean;
import com.wanyue.main.store.view.proxy.GoodsManngerViewProxy;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;

public class ConsignmentManngerActivity extends BaseActivity {

    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    private List<GoodsManngerViewProxy> mViewProxyList;
    private ConsignmentManngerIndicatorAdapter mIndicatorAdapter;

    @Override
    public void init() {
        setTabTitle("Product management");
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        List<String>titleArray= ListUtil.asList("在售","已下架");
        initViewProxy();
        mIndicatorAdapter=new ConsignmentManngerIndicatorAdapter(titleArray,this,mViewPager);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mIndicatorAdapter);
        mIndicator.setNavigator(commonNavigator);
        ViewProxyPageAdapter pageAdapter = new ViewProxyPageAdapter(getViewProxyMannger(), mViewProxyList);
        pageAdapter.attachViewPager(mViewPager);
        ViewPagerHelper.bind(mIndicator, mViewPager);
        getNum();
        LiveEventBus.get(MainEvent.ADD_GOODS,String.class).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                notifyShifListNum();
            }
        });

        LiveEventBus.get(MainEvent.MOBIFY_GOODS).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object object) {
                getNum();
            }
        });
    }
    private void getNum() {
        notifyShifListNum();
        notifyShifListNoNum();
    }

    /*更新*/
    private void notifyShifListNoNum() {
        MainAPI.getNoShifListNum(new ParseSingleHttpCallback<Integer>("nums") {
            @Override
            public void onSuccess(Integer data) {
                if(mIndicatorAdapter!=null){
                   mIndicatorAdapter.notifyTitle(StringUtil.contact("已下架","\t",data),1);
                }
            }
        });
    }

    /*更新*/
    private void notifyShifListNum() {
        MainAPI.getShifListNum(new ParseSingleHttpCallback<Integer>("nums") {
            @Override
            public void onSuccess(Integer data) {
                if(mIndicatorAdapter!=null){
                   mIndicatorAdapter.notifyTitle(StringUtil.contact("在售","\t",data),0);
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainAPI.cancle(MainAPI.SHOP_SHELF_LIST_NO);
        MainAPI.cancle(MainAPI.SHOP_SHELF_NUM);
    }

    private void initViewProxy() {
        mViewProxyList=new ArrayList<>(2);
        GoodsManngerViewProxy viewProxy=new GoodsManngerViewProxy() {
            @Override
            public Observable<List<ConsignMentGoodsBean>> getData(int p) {
                notifyShifListNum();
                return MainAPI.getShelfList(null,p);
            }
        };

        viewProxy.setSale(true);
        GoodsManngerViewProxy viewProxy2=new GoodsManngerViewProxy() {
            @Override
            public Observable<List<ConsignMentGoodsBean>> getData(int p) {
                notifyShifListNoNum();
                return MainAPI.getNoShelfList(null,p);
            }
        };
        viewProxy2.setSale(false);
        mViewProxyList.add(viewProxy);
        mViewProxyList.add(viewProxy2);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_consignment_mannger;
    }

    public void addGoods(View view) {
        if(!ClickUtil.canClick()){
            return;
        }
        startActivity(StoreGoodsAddActivity.class);
    }
}

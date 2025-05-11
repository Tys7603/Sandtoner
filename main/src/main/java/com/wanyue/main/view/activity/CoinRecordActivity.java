package com.wanyue.main.view.activity;


import android.content.Context;
import android.content.Intent;
import androidx.viewpager.widget.ViewPager;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.proxy.ViewProxyPageAdapter;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.main.R;
import com.wanyue.main.adapter.CoinRecordIndicatorAdapter;
import com.wanyue.main.view.proxy.CoinRecordListViewProxy;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import java.util.List;

public class CoinRecordActivity extends BaseActivity {

    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    private CoinRecordIndicatorAdapter mCoinRecordAdapter;
    private int mPostion;

    @Override
    public void init() {
        setTabTitle("Consumption record");
        mPostion=getIntent().getIntExtra(Constants.POSITION,0);
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        String[]titleArray={"All","Consumption","Recharge"};
        mCoinRecordAdapter=new CoinRecordIndicatorAdapter(titleArray,this,mViewPager);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mCoinRecordAdapter);
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
        List<CoinRecordListViewProxy>recordListViewProxyList=initViewProxy();
        ViewProxyPageAdapter pageAdapter = new ViewProxyPageAdapter(getViewProxyMannger(),recordListViewProxyList);
        pageAdapter.attachViewPager(mViewPager,mPostion);
    }

    private List<CoinRecordListViewProxy> initViewProxy() {
        CoinRecordListViewProxy recordListViewProxy1=new CoinRecordListViewProxy();
        CoinRecordListViewProxy recordListViewProxy2=new CoinRecordListViewProxy();
        CoinRecordListViewProxy recordListViewProxy3=new CoinRecordListViewProxy();
        recordListViewProxy1.setType(0);
        recordListViewProxy2.setType(1);
        recordListViewProxy3.setType(2);
        return ListUtil.asList(recordListViewProxy1,recordListViewProxy2,recordListViewProxy3);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_coin_record;
    }

    public  static void forward(Context context,int postion){
        Intent intent=new Intent(context,CoinRecordActivity.class);
        intent.putExtra(Constants.POSITION,postion);
        context.startActivity(intent);

    }
}

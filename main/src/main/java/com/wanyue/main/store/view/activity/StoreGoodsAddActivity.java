package com.wanyue.main.store.view.activity;

import android.app.Dialog;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.http.ParseArrayHttpCallBack;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.proxy.ViewProxyPageAdapter;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.main.R;
import com.wanyue.main.adapter.MainNavigatorAadpter;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.ClassifyBean;
import com.wanyue.main.store.view.proxy.GoodsManngerAddViewProxy;
import com.wanyue.shop.view.view.SearchViewProxy;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Store goods add activity.
 */
public class StoreGoodsAddActivity extends BaseActivity {
    private ViewGroup mVpSearchContainer;
    private MagicIndicator mIndicator;
    private ViewPager mViewPager;
    private List<GoodsManngerAddViewProxy>mViewProxyList;

    @Override
    public void init() {
        setTabTitle("Add product");
        mVpSearchContainer = (ViewGroup) findViewById(R.id.vp_search_container);
        mIndicator = (MagicIndicator) findViewById(R.id.indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        SearchViewProxy searchViewProxy=new SearchViewProxy();
        searchViewProxy.setHint("Search for products");
        searchViewProxy.setEnableAutoSearch(true);
        searchViewProxy.setSeacherListner(new SearchViewProxy.SeacherListner() {
            @Override
            public void search(String keyward) {
                if(!ListUtil.haveData(mViewProxyList)){
                   return;
                }
                for(GoodsManngerAddViewProxy viewProxy:mViewProxyList){
                    viewProxy.setKeyword(keyward);
                }
                int currentItem=mViewPager.getCurrentItem();
                GoodsManngerAddViewProxy goodsManngerAddViewProxy=ListUtil.safeGetData(mViewProxyList,currentItem);
                if(goodsManngerAddViewProxy!=null){
                   goodsManngerAddViewProxy.initData();
                }
            }
        });
        getViewProxyMannger().addViewProxy(mVpSearchContainer,searchViewProxy,searchViewProxy.getDefaultTag());
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        if(mViewProxyList==null){
           MainAPI.getCategory(new ParseArrayHttpCallBack<ClassifyBean>() {
                @Override
                public void onSuccess(int code, String msg, List<ClassifyBean> info) {
                  if(isSuccess(code)&& ListUtil.haveData(info)){
                     setData(info);
                  }
                }
               @Override
               public boolean showLoadingDialog() {
                   return true;
               }
               @Override
               public Dialog createLoadingDialog() {
                   return DialogUitl.loadingDialog(mContext);
               }
           }
          );
        }
    }

    private void setData(List<ClassifyBean> info) {
        ClassifyBean classifyBean1=new ClassifyBean();
        classifyBean1.setName(getString(R.string.featured2));
        classifyBean1.setId("0");
        info.add(0,classifyBean1);
        mViewProxyList=new ArrayList<>();
        for(ClassifyBean classifyBean:info){
          GoodsManngerAddViewProxy viewProxy=new GoodsManngerAddViewProxy();
          viewProxy.setCid(classifyBean.getId());
            mViewProxyList.add(viewProxy);
        }
        ViewProxyPageAdapter pageAdapter = new ViewProxyPageAdapter(getViewProxyMannger(),mViewProxyList);
        pageAdapter.attachViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(mViewProxyList.size());
        CommonNavigator commonNavigator = new CommonNavigator(this);
        MainNavigatorAadpter mainNavigatorAadpter=new MainNavigatorAadpter(info,this,mViewPager);
        mainNavigatorAadpter.setEableScale(false);
        commonNavigator.setAdapter(mainNavigatorAadpter);
        mIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mIndicator, mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainAPI.cancle(MainAPI.CATEGORY);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_store_goods_add;
    }
}

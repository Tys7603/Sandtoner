package com.wanyue.main.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.bean.SimpleLiveBean;
import com.wanyue.common.business.acmannger.ActivityMannger;
import com.wanyue.common.custom.MyViewPager;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.proxy.ViewProxyPageAdapter;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.ProcessResultUtil;
import com.wanyue.common.utils.RouteUtil;
import com.wanyue.common.utils.SpUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.imnew.busniess.IMSDK;
import com.wanyue.live.activity.LiveAnchorActivity;
import com.wanyue.live.activity.LiveAudienceActivity;
import com.wanyue.live.bean.LiveBean;
import com.wanyue.live.http.LiveShopAPI;
import com.wanyue.live.presenter.LiveRoomCheckLivePresenter;
import com.wanyue.main.R;
import com.wanyue.main.apply.activity.ApplyStoreActivity;
import com.wanyue.main.view.proxy.MainHomeClassifyVIewProxy;
import com.wanyue.main.view.proxy.MainHomeFindViewProxy;
import com.wanyue.main.view.proxy.MainHomePageViewProxy;
import com.wanyue.main.view.proxy.MainHomeUserViewProxy;
import com.wanyue.main.view.proxy.MainShopCartViewProxy;
import com.wanyue.shop.view.activty.GoodsDetailActivity;
import com.yzq.zxinglibrary.common.Constant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route(path = RouteUtil.PATH_MAIN)
public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, LiveRoomCheckLivePresenter.ActionListener {
    private MyViewPager mViewPager;
    private BottomNavigationView mBottomView;

    private MainHomePageViewProxy mMainHomePageViewProxy;
    private MainShopCartViewProxy mShopCartViewProxy;
    private MainHomeClassifyVIewProxy mMainHomeClassifyVIewProxy;
    private MainHomeUserViewProxy mMainHomeUserViewProxy;
    private MainHomeFindViewProxy mFindViewProxy;
    private ProcessResultUtil mProcessResultUtil;
    private LiveRoomCheckLivePresenter mLiveCheckLivePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void init() {
        LiveEventBus.get("look_live", SimpleLiveBean.class).observe(this, new Observer<SimpleLiveBean>() {
            @Override
            public void onChanged(SimpleLiveBean simpleLiveBean) {
                LiveBean liveBean=new LiveBean();
                liveBean.setPull(simpleLiveBean.getPull());
                liveBean.setStream(simpleLiveBean.getStream());
                liveBean.setUid(simpleLiveBean.getUid());

                if(mLiveCheckLivePresenter==null){
                   mLiveCheckLivePresenter=new LiveRoomCheckLivePresenter(mContext,MainActivity.this);
                }
                   mLiveCheckLivePresenter.checkLive(liveBean);
            }
        });

        ActivityMannger.getInstance().setBaseActivity(this);
        mViewPager =  findViewById(R.id.viewPager);
        mBottomView = findViewById(R.id.navigation);
        mBottomView.setOnNavigationItemSelectedListener(this);
        mBottomView.setElevation(6);

        mMainHomePageViewProxy = new MainHomePageViewProxy();
        mShopCartViewProxy = new MainShopCartViewProxy();
        mFindViewProxy= new MainHomeFindViewProxy();
        mMainHomeClassifyVIewProxy = new MainHomeClassifyVIewProxy();
        mMainHomeUserViewProxy = new MainHomeUserViewProxy();

        List<RxViewProxy> viewProxyList = Arrays.asList(
                mMainHomePageViewProxy,
                mMainHomeClassifyVIewProxy,
                mFindViewProxy,
                mShopCartViewProxy,
                mMainHomeUserViewProxy);
        mViewPager.setOffscreenPageLimit(viewProxyList.size());
        ViewProxyPageAdapter viewProxyPageAdapter = new ViewProxyPageAdapter(getViewProxyMannger(), viewProxyList);
        viewProxyPageAdapter.attachViewPager(mViewPager);
        mProcessResultUtil = new ProcessResultUtil(this);
        IMSDK.login();
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    private void checkPermissions(Runnable runnable) {
        if(mProcessResultUtil==null){
            return;
        }
        mProcessResultUtil.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
        }, runnable);

    }


    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        checkPermissions(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void releaseActivty() {
        super.releaseActivty();
        ActivityMannger.getInstance().releaseBaseActivity(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.main_home) {
                setPageIndex(0);
            } else if (id == R.id.main_classfiy) {
                setPageIndex(1);
            } else if (id == R.id.main_find) {
                setPageIndex(2);
            } else if (id == R.id.main_shopcart) {
                setPageIndex(3);
        }   else if (id == R.id.main_user) {
                setPageIndex(4);
            }
        return true;
    }


   public void clickOpenLive(View view){
        openLive(null);
   }

   public void openLive(String id){



       if(CommonAppConfig.getUserBean().getIsshop()==1){
           Activity activity= (LiveAudienceActivity) ActivityMannger.getInstance().getFirstClassTypeActivieActivity(LiveAudienceActivity.class);
           if(activity!=null){
              ToastUtil.show("请关闭直播间悬浮窗");
              return;
           }
           LiveAnchorActivity.forward(this,id);
       }else{
           openAuthDialog();
       }
   }

    private void openAuthDialog() {
        DialogUitl.Builder builder = new DialogUitl.Builder(this);
        builder.setTitle("开通店铺")
                .setContent("你未认证开通店铺，无法进行直播")
                .setConfrimString("前往认证")
                .setCancelable(true)
                .setClickCallback(new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                       startActivity(ApplyStoreActivity.class);
                    }
                })
                .build()
                .show();
    }

    private long mLastClickBackTime;
    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if (curTime - mLastClickBackTime > 2000) {
            mLastClickBackTime = curTime;
            ToastUtil.show(R.string.main_click_next_exit);
            return;
        }
        LiveAudienceActivity activity= (LiveAudienceActivity) ActivityMannger.getInstance().getFirstClassTypeActivieActivity(LiveAudienceActivity.class);
        if(activity!=null){
           activity.onBackAndFinish();
        }

        finish();
    }

    @Override
    public void finish() {
        super.finish();
        ActivityMannger.getInstance().releaseBaseActivity(this);
    }


    public void setSelectClasfiy() {
        if(mBottomView!=null){
           mBottomView.setSelectedItemId(R.id.main_classfiy);
        }
    }

    public void setPageIndex(int i) {
        if(mViewPager!=null){
           mViewPager.setCurrentItem(i,false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1111) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                checkLivekey(content);

            }
        }
    }

    private void checkLivekey(final String content) {
        LiveShopAPI.liveKey(content).subscribe(new DefaultObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean isAllow) {
                if(isAllow){
                    openLive(content);
                }
            }
        });

    }

    @Override
    public void onLiveRoomChanged(LiveBean liveBean, String data) {
        LiveAudienceActivity.forward(MainActivity.this,liveBean,data);
    }
}

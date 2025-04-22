package com.wanyue.shop.store.view.activity;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.tencent.imsdk.conversation.Conversation;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.CheckImageView;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ViewUtil;

import com.wanyue.imnew.view.chat.ChatActivity;
import com.wanyue.shop.R;
import com.wanyue.shop.R2;
import com.wanyue.shop.bean.StoreBean;
import com.wanyue.shop.business.ShopEvent;
import com.wanyue.shop.store.api.StoreAPI;
import com.wanyue.shop.store.view.proxy.StoreClassfiyViewProxy;
import com.wanyue.shop.store.view.proxy.StorePageVIewProxy;
import butterknife.OnClick;

public class StoreActivity extends BaseActivity {
    private ViewGroup mBtnSearch;
    private TextView mEtSearch;
    private ViewGroup mVpContentContainer;
    private ViewGroup mBtn1;
    private CheckImageView mImg1;
    private TextView mTv1;
    private ViewGroup mBtn2;
    private CheckImageView mImg2;
    private TextView mTv2;
    private ViewGroup mBtn3;
    private ImageView mImg3;
    private TextView mTv3;

    private StorePageVIewProxy mStorePageVIewProxy;
    private StoreClassfiyViewProxy mStoreClassfiyViewProxy;
    private String mId;
    private StoreBean mStoreBean;

    @Override
    public void init() {

        mBtnSearch = findViewById(R.id.btn_search);
        mEtSearch = (TextView) findViewById(R.id.et_search);
        mVpContentContainer =  findViewById(R.id.vp_content_container);
        mBtn1 = (ViewGroup) findViewById(R.id.btn_1);
        mImg1 = (CheckImageView) findViewById(R.id.img_1);
        mTv1 = (TextView) findViewById(R.id.tv_1);
        mBtn2 = (ViewGroup) findViewById(R.id.btn_2);
        mImg2 = (CheckImageView) findViewById(R.id.img_2);
        mTv2 = (TextView) findViewById(R.id.tv_2);
        mBtn3 = (ViewGroup) findViewById(R.id.btn_3);
        mImg3 = (ImageView) findViewById(R.id.img_3);
        mTv3 = (TextView) findViewById(R.id.tv_3);


        mId=getIntent().getStringExtra(Constants.KEY_ID);
        LiveEventBus.get(ShopEvent.FOLLOW_STORE,StoreBean.class).observe(this, new Observer<StoreBean>() {
            @Override
            public void onChanged(StoreBean storeBean) {
                if(storeBean==null){
                    return;
                }
                if(StringUtil.equals(storeBean.getId(),mId)){
                    getData();
                }
            }
        });
        getData();
    }


    private void getData() {
        StoreAPI.getStoreDetail(mId).compose(this.<StoreBean>bindToLifecycle())
         .subscribe(new DefaultObserver<StoreBean>() {
             @Override
             public void onNext(@NonNull StoreBean storeBean) {
                 mStoreBean=storeBean;
                 selectButton1();
                 if(mStorePageVIewProxy!=null){
                    mStorePageVIewProxy.setStoreBean(storeBean);
                 }
             }
         });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_store;
    }

    @OnClick(R2.id.btn_1)
    public void selectButton1(){

        mImg1.setChecked(true);
        mTv1.setTextColor(ResourceUtil.getColor(R.color.global));

        mImg2.setChecked(false);
        mTv2.setTextColor(ResourceUtil.getColor(R.color.gray1));

        if(mStorePageVIewProxy==null){
           mStorePageVIewProxy=new StorePageVIewProxy();
           mStorePageVIewProxy.setStoreId(mId);
           getViewProxyMannger().addViewProxy(mVpContentContainer,mStorePageVIewProxy,mStorePageVIewProxy.getDefaultTag());
        }else{
            ViewUtil.setVisibility(mStorePageVIewProxy.getContentView(), ViewGroup.VISIBLE);
        }
        if(mStoreClassfiyViewProxy!=null){
           ViewUtil.setVisibility(mStoreClassfiyViewProxy.getContentView(), ViewGroup.INVISIBLE);
        }
    }

    @OnClick(R2.id.btn_2)
    public void selectButton2(){
        mImg1.setChecked(false);
        mTv1.setTextColor(ResourceUtil.getColor(R.color.gray1));
        mImg2.setChecked(true);
        mTv2.setTextColor(ResourceUtil.getColor(R.color.global));

        if(mStoreClassfiyViewProxy==null){
           mStoreClassfiyViewProxy=new StoreClassfiyViewProxy();
           mStoreClassfiyViewProxy.setStoreId(mId);
           getViewProxyMannger().addViewProxy(mVpContentContainer,mStoreClassfiyViewProxy,mStoreClassfiyViewProxy.getDefaultTag());
        }else{
           ViewUtil.setVisibility(mStoreClassfiyViewProxy.getContentView(), ViewGroup.VISIBLE);
        }
        if(mStorePageVIewProxy!=null){
           ViewUtil.setVisibility(mStorePageVIewProxy.getContentView(), ViewGroup.INVISIBLE);
        }
    }

    @OnClick(R2.id.btn_3)
    public void selectButton3(){
        if(mStoreBean==null){
            return;
        }
        ChatActivity.forward(this,mStoreBean.getId(),mStoreBean.getName());
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }

    public static void forward(Context context,String id){
        Intent intent=new Intent(context,StoreActivity.class);
        intent.putExtra(Constants.KEY_ID,id);
        context.startActivity(intent);

    }

}

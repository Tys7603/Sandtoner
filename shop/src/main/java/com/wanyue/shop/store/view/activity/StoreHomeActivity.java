package com.wanyue.shop.store.view.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.activity.WebViewActivity;
import com.wanyue.common.custom.UIFactory;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.bean.StoreBean;

public class StoreHomeActivity extends BaseActivity {

    private ImageView mImgStoreAvator;
    private TextView mTvStoreName;
    private TextView mTvFans;
    private TextView mTvAddr;
    private ViewGroup mVpWebContainer;
    private WebView mWebView;

    private TextView mTvAddContent;



    @Override
    public void init() {
        setTabTitle("店铺介绍");
        mImgStoreAvator = findViewById(R.id.img_store_avator);
        mTvStoreName = (TextView) findViewById(R.id.tv_store_name);
        mTvFans = (TextView) findViewById(R.id.tv_fans);
        mTvAddr = (TextView) findViewById(R.id.tv_addr);
        mVpWebContainer = (ViewGroup) findViewById(R.id.vp_web_container);
        mTvAddContent = (TextView) findViewById(R.id.tv_add_content);

        String json=getIntent().getStringExtra(Constants.DATA);

        StoreBean storeBean=JSON.parseObject(json,StoreBean.class);
        mTvFans.setText("粉丝："+storeBean.getFansNum());
        mTvStoreName.setText(storeBean.getName());
        ImgLoader.display(this,storeBean.getAvatar(),mImgStoreAvator);
        mTvAddr.setText(storeBean.getAddr());

        String url=storeBean.getContentUrl();
        mWebView = ViewUtil.createWebView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, DpUtil.dp2px(230));
        mVpWebContainer.addView(mWebView, params);
        mWebView.loadUrl(url);
        if(TextUtils.isEmpty(url)){
            ViewUtil.setVisibility(mTvAddContent, View.VISIBLE);
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_store_home;
    }


    public static void  forward(Context context, StoreBean storeBean){
        String json= JSON.toJSONString(storeBean);
        Intent intent=new Intent(context,StoreHomeActivity.class);
        intent.putExtra(Constants.DATA,json);
        context.startActivity(intent);
    }

}
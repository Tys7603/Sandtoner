package com.wanyue.course.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.course.R;
import com.wanyue.course.bean.ContentBean;
import com.wanyue.course.busniess.CourseConstants;
import com.wanyue.course.busniess.UIFactory;
import com.wanyue.course.video.AbsLockPlayerViewProxy;
import com.wanyue.course.video.TxAudioPlayViewProxy;
import com.wanyue.course.video.TxVideoPlayViewProxy;

public class ContentDetailActivity extends BaseActivity {

    private ViewGroup mVpTopContainer;
    private ViewGroup mContainer;
    private TextView mTvTitle;
    private TextView mTvDes;
    private WebView mWebView;
    private ViewGroup mVpTab;
    private ContentBean mContentBean;
    private TextView mTvTime;
    private AbsLockPlayerViewProxy mAbsLockPlayerViewProxy;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public void init() {
        Intent intent=getIntent();
        mContentBean=intent.getParcelableExtra("content");
        if(mContentBean==null){
            finish();
            return;
        }
        setTabTitle(getString(R.string.content_detail));
        mVpTopContainer = findViewById(R.id.vp_top_container);
        mContainer =  findViewById(R.id.container);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvDes = (TextView) findViewById(R.id.tv_des);
        mWebView = (WebView) findViewById(R.id.webView);
        ViewUtil.settingWebView(mWebView);
        mVpTab = (FrameLayout) findViewById(R.id.vp_tab);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        changeUIState(false,0);
        setUIData();
        loadLink(mContentBean.getContentUrl());
    }


    private void changeUIState(boolean isTrip,int tripSecond) {
        int type=mContentBean.getContentType();
        switch (type){
            case CourseConstants.CONTENT_IMAGE_TEXT: //图文形式
                changeToImageText();
                break;
            case CourseConstants.CONTENT_VIDEO: //视频形式
                changeToVideo(isTrip,tripSecond);
                break;
            case CourseConstants.CONTENT_AUDIO: //音频形式
                changeToAudio(isTrip,tripSecond);
                break;
            default:
                break;
        }
    }


    private void setUIData() {
        mTvTitle.setText(mContentBean.getTitle());
        mTvDes.setText(mContentBean.getIntroduce());
        String addTime=mContentBean.getAddTime();
        if(TextUtils.isEmpty(addTime)){
            mTvTime.setVisibility(View.GONE);
        }else{
            mTvTime.setText(addTime);
            mTvTime.setVisibility(View.VISIBLE);
        }
    }

    //加载链接
    private void loadLink(final String link) {
        mWebView.loadUrl(link);
    }


    /*切换到音频模式*/
    private void changeToAudio(boolean isTrip,int tripSecond) {
        String url=mContentBean.getUrl();
        mAbsLockPlayerViewProxy=new TxAudioPlayViewProxy();
        mAbsLockPlayerViewProxy.setShowLocked(isTrip,tripSecond);
        getViewProxyMannger().addViewProxy(mVpTopContainer,mAbsLockPlayerViewProxy,mAbsLockPlayerViewProxy.getDefaultTag());
        mAbsLockPlayerViewProxy.startPlay(url,mContentBean.getThumb());
        mAbsLockPlayerViewProxy.addTintView(null,1);
    }

    /*切换到视频模式*/
    private void changeToVideo(boolean isTrip,int tripSecond) {
        LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) mVpTopContainer.getLayoutParams();
        layoutParams.height= DpUtil.dp2px(200);
        mVpTopContainer.requestLayout();
        String url=mContentBean.getUrl();
        TxVideoPlayViewProxy videoPlayerViewProxy=new TxVideoPlayViewProxy();
        videoPlayerViewProxy.setShowTitle(true);
        videoPlayerViewProxy.putArgs(Constants.KEY_TITLE,mContentBean.getTitle());
        videoPlayerViewProxy.setShowLocked(isTrip,tripSecond);
        getViewProxyMannger().addViewProxy(mVpTopContainer,videoPlayerViewProxy,videoPlayerViewProxy.getDefaultTag());
        videoPlayerViewProxy.startPlay(url,mContentBean.getThumb());
        videoPlayerViewProxy.addTintView(null,1);
        mAbsLockPlayerViewProxy=videoPlayerViewProxy;
    }


    /*购买相关内容*/
    private void buy() {

    }

    private void changeToImageText() {
        mVpTopContainer.setVisibility(View.GONE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_content_detail;
    }

    /*正常打开*/
    public static void forward(Context context,ContentBean contentBean){
        Intent intent = new Intent(context,ContentDetailActivity.class);
        intent.putExtra("content",contentBean);
        context.startActivity(intent);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) mVpTopContainer.getLayoutParams();
            layoutParams.height= DpUtil.dp2px(200);
            mVpTopContainer.requestLayout();
            mVpTab.setVisibility(View.VISIBLE);
            showStatusNavigationBar();
        }else{
            LinearLayout.LayoutParams layoutParams= (LinearLayout.LayoutParams) mVpTopContainer.getLayoutParams();
            layoutParams.height=FrameLayout.LayoutParams.MATCH_PARENT;
            mVpTopContainer.requestLayout();
            mVpTab.setVisibility(View.GONE);
            hideStatusNavigationBar();
        }
    }

    //*隐藏导航栏*//
    private void hideStatusNavigationBar(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) ;//隐藏状态栏
    }

    //*显示导航栏*//
    public void showStatusNavigationBar(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN) ;//显示状态栏
    }


    @Override
    protected void onStop() {
        super.onStop();
        L.e("contentActivify==onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
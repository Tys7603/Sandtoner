package com.wanyue.main.view.proxy;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.tencent.qcloud.tim.uikit.TUIKitImpl;
import com.tencent.qcloud.tim.uikit.base.IMEventListener;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatManagerKit;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.custom.DrawableTextView;
import com.wanyue.common.proxy.Arg;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.imnew.view.conversation.ConversationActivity;
import com.wanyue.main.R;
import com.yzq.zxinglibrary.android.CaptureActivity;

import io.reactivex.Observable;

//首页标题栏目
public abstract class  MainHomePageHeadViewproxy extends RxViewProxy implements View.OnClickListener {
    private FrameLayout mBtnSearch;
    private TextView mEtSearch;
    private DrawableTextView mBtnScan;
    private DrawableTextView mBtnMessage;
    private TextView mTvRedPoint;

    private boolean mIsResume;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mBtnSearch = (FrameLayout) findViewById(R.id.btn_search);
        mEtSearch = (TextView) findViewById(R.id.et_search);
        mBtnScan = (DrawableTextView) findViewById(R.id.btn_scan);
        mBtnMessage = (DrawableTextView) findViewById(R.id.btn_message);
        mTvRedPoint = (TextView) findViewById(R.id.tv_red_point);

        String title = Arg.getParmString(this, "title");
        mEtSearch.setHint(title);
        mEtSearch.setEnabled(false);
        mEtSearch.setClickable(false);
        mEtSearch.setFocusableInTouchMode(false);
        mBtnScan.setOnClickListener(this);
        mBtnMessage.setEnabled(true);
        mBtnMessage.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);


        TUIKitImpl.addIMEventListener(new IMEventListener() {
            @Override
            public void onNewMessage(V2TIMMessage v2TIMMessage) {
                super.onNewMessage(v2TIMMessage);
                L.e("v2TIMMessage=="+v2TIMMessage);
                if(!StringUtil.equals(v2TIMMessage.getSender(), CommonAppConfig.getUid())&&mIsResume){
                    getUnReadCount();
                };
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mIsResume=false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsResume=true;
        getUnReadCount();
    }

    private void getUnReadCount() {
        V2TIMManager.getConversationManager().getTotalUnreadMessageCount(new V2TIMValueCallback<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                if(aLong>0){
                    ViewUtil.setTextAndViewsible(mTvRedPoint,aLong+"");
                }else{
                    ViewUtil.setVisibility(mTvRedPoint,View.INVISIBLE);
                }
            }
            @Override
            public void onError(int i, String s) {
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_main_home_page_head;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_scan) {
            Intent intent = new Intent(getActivity(), CaptureActivity.class);
            startActivityForResult(intent, 1111);
        } else if (id == R.id.btn_message) {
            startActivity(ConversationActivity.class);
        }else if(id ==R.id.btn_search){
            foward();
        }
    }



    public abstract void foward();
}

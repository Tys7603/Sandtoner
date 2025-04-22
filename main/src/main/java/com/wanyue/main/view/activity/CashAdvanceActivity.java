package com.wanyue.main.view.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.CheckImageView;
import com.wanyue.common.server.observer.DialogObserver;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.common.utils.DebugUtil;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.main.R;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.CommissionBankBean;
import com.wanyue.main.view.proxy.adavance.AdavanceViewProxy;
import com.wanyue.main.view.proxy.adavance.BankAdvanceViewProxy;
import com.wanyue.main.view.proxy.adavance.WxAdavanceViewProxy;
import com.wanyue.main.view.proxy.adavance.ZfbAdvanceViewProxy;
import java.util.List;

/*提现记录*/
public class CashAdvanceActivity extends BaseActivity {
    public static final String CASH_SUCCESS ="cash_success" ;
    private CommissionBankBean mCommissionBankBean;
    private List<AdavanceViewProxy> mViewProxyList;
    private FrameLayout mVpPannelContainer;
    private TextView mTvUsedMoney;
    private int mPosition;

    private ViewGroup mBtnWx;
    private ViewGroup mBtnZfb;
    private ViewGroup mBtnBank;


    private CheckImageView mImgCheckBank;
    private CheckImageView mImgCheckWx;
    private CheckImageView mImgCheckZfb;

    private ConstraintSet mOpenSet;
    private ConstraintSet mCloseSet;

    private ConstraintLayout mBankGroup;
    private ConstraintLayout mWxGroup;
    private ConstraintLayout mZfbGroup;

    @Override
    public void init() {
        setTabTitle(R.string.wallet_7);
        mVpPannelContainer = (FrameLayout)findViewById(R.id.vp_pannel_container);
        mTvUsedMoney = (TextView) findViewById(R.id.tv_used_money);

        mBtnBank = (ViewGroup) findViewById(R.id.btn_bank);
        mBtnWx =  findViewById(R.id.btn_wx);
        mBtnZfb = (ViewGroup) findViewById(R.id.btn_zfb);

        mBankGroup= (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.item_cash_advance_top,mBtnBank,false);
        mWxGroup= (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.item_cash_advance_top,mBtnWx,false);
        mZfbGroup= (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.item_cash_advance_top,mBtnZfb,false);

        mBtnBank.addView(mBankGroup);
        mBtnWx.addView(mWxGroup);
        mBtnZfb.addView(mZfbGroup);


        mOpenSet=new ConstraintSet();
        mOpenSet.clone(mBankGroup);
        mOpenSet.constrainHeight(R.id.line,DpUtil.dp2px(20));

        mCloseSet=new ConstraintSet();
        mCloseSet.clone(mBankGroup);

        mImgCheckBank = (CheckImageView) mBankGroup.findViewById(R.id.img_check);
        mImgCheckWx = (CheckImageView) mWxGroup.findViewById(R.id.img_check);
        mImgCheckZfb = (CheckImageView) mZfbGroup.findViewById(R.id.img_check);

        setUITopItemData(mBankGroup,mImgCheckBank,R.drawable.icon_advance_bank_default,R.drawable.icon_advance_bank_select,"银行卡");
        setUITopItemData(mWxGroup,mImgCheckWx,R.drawable.icon_advance_wx_default,R.drawable.icon_advance_wx_select,"微信");
        setUITopItemData(mZfbGroup,mImgCheckZfb,R.drawable.icon_advance_zfb_default,R.drawable.icon_advance_zfb_select,"支付宝");
    }

    private void setUITopItemData(ViewGroup group, CheckImageView imgCheck, int defaultId, int selectId, String title) {
        TextView tvTip=group.findViewById(R.id.tv_tip);
        tvTip.setText(title);
        imgCheck.addImageResouce(defaultId,selectId);
    }


    private void initViewPannerProxy() {
        if(mViewProxyList==null){
           mViewProxyList= ListUtil.asList(new BankAdvanceViewProxy(),new WxAdavanceViewProxy(),new ZfbAdvanceViewProxy());
        }
    }
    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        MainAPI.getCommissionBankMessage().
                compose(this.<CommissionBankBean>bindUntilOnDestoryEvent()).subscribe(new DialogObserver<CommissionBankBean>(this) {
            @Override
            public void onNextTo(CommissionBankBean commissionBankBean) {
                setData(commissionBankBean);
            }
        });
    }

    private void setData(CommissionBankBean commissionBankBean) {
        mCommissionBankBean=commissionBankBean;
        mTvUsedMoney.setText(getString(R.string.used_money,commissionBankBean.getCommissionCount()));
        initViewPannerProxy();
        selectBank(null);
    }

    private void setSelect(int position) {
        mPosition=position;
       int size=mViewProxyList.size();
       for(int i=0;i<size;i++){
         AdavanceViewProxy adavanceViewProxy=mViewProxyList.get(i);
         if(adavanceViewProxy==null){
            DebugUtil.sendException("AdavanceViewProxy 不能null");
            return;
         }
         if(i==position){
             if(!adavanceViewProxy.isAdd()){
                getViewProxyMannger().addViewProxy(mVpPannelContainer,adavanceViewProxy,adavanceViewProxy.getDefaultTag());
                adavanceViewProxy.setData(mCommissionBankBean);
             }else{
                 adavanceViewProxy.showToParent();
             }
         }else{
             if(adavanceViewProxy.isAdd()){
                adavanceViewProxy.goneToParent();
             }
         }
       }
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_cash_advance;
    }


    /*提现*/
    public void advance(View view) {
        if(!ClickUtil.canClick()){
            return;
        }
        AdavanceViewProxy adavanceViewProxy=ListUtil.safeGetData(mViewProxyList,mPosition);
        if(adavanceViewProxy==null){
            DebugUtil.sendException("mViewProxyList=="+mViewProxyList+"&&mPosition=="+mPosition);
            return;
        }
        boolean isFullCondition=adavanceViewProxy.localCheckCommitMessage();
        if(isFullCondition){
            MainAPI.commissionBankExtract(adavanceViewProxy.getCommitBundle()).compose(this.<Boolean>bindUntilOnDestoryEvent()).subscribe(new DialogObserver<Boolean>(this) {
                @Override
                public void onNextTo(Boolean aBoolean) {
                        if(aBoolean){
                          LiveEventBus.get(CASH_SUCCESS).post(aBoolean);
                          finish();
                        }
                }
            });
        }
    }
    private ConstraintLayout mSelectGoup;
    public void selectBank(View view) {
        setSelect(0);
        if(mSelectGoup==null){
        }else if(mSelectGoup==mZfbGroup){
            TransitionManager.beginDelayedTransition(mZfbGroup);
            mCloseSet.applyTo(mZfbGroup);
        }else if(mSelectGoup==mWxGroup){
            TransitionManager.beginDelayedTransition(mWxGroup);
            mCloseSet.applyTo(mWxGroup);
        }else if(mSelectGoup==mBankGroup){
            return;
        }
        mSelectGoup=mBankGroup;
        TransitionManager.beginDelayedTransition(mBtnBank);
        mOpenSet.applyTo(mBankGroup);


        mImgCheckBank.setChecked(true);
        mImgCheckWx.setChecked(false);
        mImgCheckZfb.setChecked(false);
    }
    public void selectWx(View view) {
        setSelect(1);
        if(mSelectGoup==null){
        }else if(mSelectGoup==mBankGroup){
            TransitionManager.beginDelayedTransition(mBankGroup);
            mCloseSet.applyTo(mBankGroup);
        }else if(mSelectGoup==mZfbGroup){
            TransitionManager.beginDelayedTransition(mZfbGroup);
            mCloseSet.applyTo(mZfbGroup);
        }else if(mSelectGoup==mWxGroup){
            return;
        }
        mSelectGoup=mWxGroup;
        TransitionManager.beginDelayedTransition(mWxGroup);
        mOpenSet.applyTo(mWxGroup);

        mImgCheckBank.setChecked(false);
        mImgCheckWx.setChecked(true);
        mImgCheckZfb.setChecked(false);
    }


    public void selectZfb(View view) {
        setSelect(2);
        if(mSelectGoup==null){
        }else if(mSelectGoup==mBankGroup){
            TransitionManager.beginDelayedTransition(mBankGroup);
            mCloseSet.applyTo(mBankGroup);
        }else if(mSelectGoup==mWxGroup){
            TransitionManager.beginDelayedTransition(mWxGroup);
            mCloseSet.applyTo(mWxGroup);
        }else if(mSelectGoup==mZfbGroup){
            return;
        }
        mSelectGoup=mZfbGroup;
        TransitionManager.beginDelayedTransition(mZfbGroup);
        mOpenSet.applyTo(mZfbGroup);

        mImgCheckBank.setChecked(false);
        mImgCheckWx.setChecked(false);
        mImgCheckZfb.setChecked(true);
    }
}

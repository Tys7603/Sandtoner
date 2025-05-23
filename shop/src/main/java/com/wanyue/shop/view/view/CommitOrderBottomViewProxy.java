package com.wanyue.shop.view.view;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.wanyue.common.Constants;
import com.wanyue.common.bean.UserBean;
import com.wanyue.common.custom.CheckImageView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.bean.OrderConfirmBean;

public abstract class CommitOrderBottomViewProxy extends RxViewProxy implements View.OnClickListener, CheckImageView.OnCheckClickListner {
    private TextView mTvFreight;
    private CheckImageView mBtnSelectPoint;
    private TextView mTagTvTip;
    private ViewGroup mBtnWx;
    private TextView mTvPpPay;
    private ViewGroup mBtnCoin;
    private ViewGroup mBtnPayStack;
    private TextView mTvCoinPay;
    private TextView mTvPayStack;

    private TextView mTvCode;
    private TextView mTvUseCoin;

    private String mPayType;
    private int mIsSelectCode;

    private OrderConfirmBean mOrderConfirmBean;

    @Override
    public int getLayoutId() {
        return R.layout.commit_order_bottom;
    }
    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mTvFreight = findViewById(R.id.tv_freight);
        mBtnSelectPoint = findViewById(R.id.btn_select_point);
        mTagTvTip = findViewById(R.id.tag_tv_tip);
        mBtnWx = findViewById(R.id.btn_wx);
        mBtnPayStack = findViewById(R.id.btn_pay_stack);
        mTvPayStack = findViewById(R.id.tv_pp_pay_stack);
        mTvPpPay = findViewById(R.id.tv_pp_pay);
        mBtnCoin = findViewById(R.id.btn_coin);
        mTvCoinPay = findViewById(R.id.tv_coin_pay);
        mTvCode = (TextView) findViewById(R.id.tv_code);
        mTvUseCoin = (TextView) findViewById(R.id.tv_use_coin);
        setSelect(Constants.PAY_TYPE_PP);
        mBtnSelectPoint.setChecked(mIsSelectCode==1);
        mBtnWx.setOnClickListener(this);
        mBtnCoin.setOnClickListener(this);
        mBtnPayStack.setOnClickListener(this);
        mBtnSelectPoint.setCheckClickListner(this);
    }

    private void setSelect(String payTypeCoin) {
        if(StringUtil.equals(mPayType,payTypeCoin)){
            return;
        }
        int selectColor=ResourceUtil.getColor(getActivity(),R.color.red);
        int normalColor=ResourceUtil.getColor(getActivity(),R.color.textColor);
        Drawable selectBg=ResourceUtil.getDrawable(R.drawable.bound_color_red_radius_3,true);
        Drawable normalBg=ResourceUtil.getDrawable(R.drawable.bound_color_grayee_radius_3,true);
        mPayType=payTypeCoin;

        if(mOrderConfirmBean!=null){
           mOrderConfirmBean.setPayType(mPayType);
        }

        switch (payTypeCoin) {
            case Constants.PAY_TYPE_COIN:
                mTvPpPay.setTextColor(normalColor);
                mTvCoinPay.setTextColor(selectColor);
                mBtnWx.setBackground(normalBg);
                mBtnCoin.setBackground(selectBg);
                mBtnPayStack.setBackground(normalBg);
                mTvPayStack.setTextColor(normalColor);
                break;
            case Constants.PAY_TYPE_PP:
                mTvCoinPay.setTextColor(normalColor);
                mTvPpPay.setTextColor(selectColor);
                mBtnWx.setBackground(selectBg);
                mBtnCoin.setBackground(normalBg);
                mBtnPayStack.setBackground(normalBg);
                mTvPayStack.setTextColor(normalColor);
                break;
            case Constants.PAY_TYPE_PSTACK:
                mTvCoinPay.setTextColor(normalColor);
                mTvPpPay.setTextColor(normalColor);
                mBtnWx.setBackground(normalBg);
                mBtnCoin.setBackground(normalBg);
                mBtnPayStack.setBackground(selectBg);
                mTvPayStack.setTextColor(selectColor);
                break;
        }
        selectChange();
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_wx){
          setSelect(Constants.PAY_TYPE_PP);
        }else if(id==R.id.btn_coin){
          setSelect(Constants.PAY_TYPE_COIN);
        } else if (id == R.id.btn_pay_stack) {
            setSelect(Constants.PAY_TYPE_PSTACK);
        }
    }
    @Override
    public void onCheckClick(CheckImageView view, boolean isChecked) {
        mIsSelectCode=isChecked?1:0;
        mOrderConfirmBean.setIsUseCode(mIsSelectCode);
        selectChange();
    }
    public String getPayType() {
        return mPayType;
    }
    public int getIsSelectCode() {
        return mIsSelectCode;
    }

    public void setData(OrderConfirmBean orderConfirmBean) {
        if(orderConfirmBean==null){
            return;
        }
        mOrderConfirmBean=orderConfirmBean;
        mOrderConfirmBean.setPayType(mPayType);
        mOrderConfirmBean.setIsUseCode(mIsSelectCode);
        UserBean userBean=mOrderConfirmBean.getUserInfo();
        if(userBean!=null){
            if(mTvCode!=null){
                mTvCode.setText(userBean.getIntegral());
            }
            if(mTvUseCoin!=null){
               mTvUseCoin.setText(getString(R.string.use_coin, StringUtil.getFormatPrice(userBean.getBalance())));
            }
        }
    }

    public abstract void selectChange();
}

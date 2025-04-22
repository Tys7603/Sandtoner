package com.wanyue.main.store.view.activity;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.DrawableTextView;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.main.R;
import com.wanyue.main.api.MainAPI;
import com.wanyue.shop.business.ShopState;

/**
 * The type My consignment activity.
 */
public class MyConsignmentActivity extends BaseActivity implements View.OnClickListener {
    private TextView mBtnOrderToday;
    private TextView mBtnOrderTotal;
    private TextView mBtnGoodsSale;
    private TextView mBtnGoodsUnsaled;
    private TextView mTvMoneyToday;
    private TextView mTvTotalToday;
    private TextView mTvSettlementedPrice;
    private TextView mTvNosettlementedPrice;

    @Override
    public void init() {
        setTabTitle("我的代销");
        mBtnOrderToday = (TextView) findViewById(R.id.btn_order_today);
        mBtnOrderTotal = (TextView) findViewById(R.id.btn_order_total);
        mBtnGoodsSale = (TextView) findViewById(R.id.btn_goods_sale);
        mBtnGoodsUnsaled = (TextView) findViewById(R.id.btn_goods_unsaled);
        mTvMoneyToday = (TextView) findViewById(R.id.tv_money_today);
        mTvTotalToday = (TextView) findViewById(R.id.tv_total_today);
        mTvSettlementedPrice = (TextView) findViewById(R.id.tv_settlemented_price);
        mTvNosettlementedPrice = (TextView) findViewById(R.id.tv_nosettlemented_price);
        findViewById(R.id.btn_order_detail).setOnClickListener(this);
        findViewById(R.id.btn_goods_detail).setOnClickListener(this);
        findViewById(R.id.btn_profit_detail).setOnClickListener(this);
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        getData();
    }

    private void getData() {
        MainAPI.getConsignment().compose(this.<JSONObject>bindToLifecycle()).subscribe(new DefaultObserver<JSONObject>() {
            @Override
            public void onNext(JSONObject jsonObject) {
                setData(jsonObject);
            }
        });
    }

    private void setData(JSONObject jsonObject) {
        mBtnGoodsSale.setText(getBuildString(R.string.goods_tip_17,jsonObject.getIntValue("onnums")));
        mBtnGoodsUnsaled.setText(getBuildString(R.string.goods_un_saled,jsonObject.getIntValue("unnums")));
        mBtnOrderToday.setText(getBuildString(R.string.order_today,jsonObject.getIntValue("orders_t")));
        mBtnOrderTotal.setText(getBuildString(R.string.order_total2,jsonObject.getIntValue("orders_all")));

        mTvMoneyToday.setText(jsonObject.getString("bring_t"));
        mTvTotalToday.setText(jsonObject.getString("bring_all"));
        mTvSettlementedPrice.setText(jsonObject.getString("bring_ok"));
        mTvNosettlementedPrice.setText(jsonObject.getString("bring_no"));
    }


    private SpannableStringBuilder getBuildString(int resId, int count) {
        String string=getString(resId);
        SpannableStringBuilder style = new SpannableStringBuilder(string);
        style.append("\n");
        int startIndex=style.length();
        String countString=Integer.toString(count);
        style.append(countString);
        style.setSpan(new AbsoluteSizeSpan(20, true), startIndex, startIndex+countString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        style.setSpan(new ForegroundColorSpan(Color.BLACK), startIndex, startIndex+countString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return style;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_consignment;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_order_detail){
            StoreOrderListActivity.forward(this, ShopState.ORDER_CONSIGNMENT);
        }else if(id==R.id.btn_profit_detail){
            ProfitActivity.forward(this,ProfitActivity.TYPE_CONSIGNMENT);
        }else if(id==R.id.btn_goods_detail){
            startActivity(ConsignmentManngerActivity.class);
        }
    }
}

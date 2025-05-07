package com.wanyue.shop.view.view;

import static com.tencent.bugly.Bugly.applicationContext;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.paypal.android.corepayments.CoreConfig;
import com.paypal.android.corepayments.Environment;
import com.paypal.android.corepayments.PayPalSDKError;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutClient;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutFundingSource;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutListener;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutRequest;
import com.paypal.android.paypalwebpayments.PayPalWebCheckoutResult;
import com.readystatesoftware.chuck.internal.ui.MainActivity;
import com.wanyue.common.custom.CheckImageView;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.shop.BuildConfig;
import com.wanyue.shop.R;
import com.wanyue.shop.adapter.ShopCartAdapter;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.ShopCartBean;
import com.wanyue.shop.bean.ShopcartParseBean;
import com.wanyue.shop.business.ShopState;
import com.wanyue.shop.model.ShopCartModel;
import com.wanyue.shop.view.activty.CommitOrderActivity;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class ShopCartViewProxy extends RxViewProxy implements View.OnClickListener, ShopCartAdapter.DeleteInvaildListner{

    private Button paymentButtonContainer;
    private Button btnPayStack;
    private String clientID = BuildConfig.CLIENT_ID;
    private String secretID = BuildConfig.SECRET_ID;
    private String returnUrl = "com.example.paypal://paypalpay";
    private String accessToken = "";
    private String uniqueId = "";
    private String orderid = "";

    private static final int STATE_NORMAL=1; //普通状态
    private static final int STATE_EDIT=2;  //编辑状态
    private int mState=STATE_NORMAL;

    private TextView mBtnMannger;
    private RxRefreshView<MultiItemEntity> mRefreshView;
    private ViewGroup mVpBottom;
    private CheckImageView mCheckTotalImage;
    private ViewGroup mVpToolManger;
    private TextView mBtnCollect;
    private TextView mBtnDelete;
    private ViewGroup mVpToolBuy;
    private TextView mTvTotalPrice;
    private TextView mBtnCommit;
    private TextView mTvGoodsNum;
    private TextView mTvTotalNum;

    private ShopCartAdapter mShopCartAdapter;
    private ShopCartModel mShopCartModel;
    private ShopcartParseBean mShopcartParseBean;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initView(ViewGroup viewGroup) {
        mShopCartModel= ViewModelProviders.of(getActivity()).get(ShopCartModel.class);
        mBtnMannger = (TextView) findViewById(R.id.btn_mannger);
        mTvGoodsNum = (TextView) findViewById(R.id.tv_goods_num);
        mRefreshView = (RxRefreshView) findViewById(R.id.refreshView);
        mVpBottom =  findViewById(R.id.vp_bottom);
        mCheckTotalImage = (CheckImageView) findViewById(R.id.check_total_image);
        mVpToolManger =  findViewById(R.id.vp_tool_manger);
        mBtnCollect = (TextView) findViewById(R.id.btn_collect);
        mBtnDelete = (TextView) findViewById(R.id.btn_delete);
        mVpToolBuy =  findViewById(R.id.vp_tool_buy);
        mTvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
        mBtnCommit = (TextView) findViewById(R.id.btn_commit);
        mTvTotalNum = (TextView) findViewById(R.id.tv_total_num);
        mRefreshView.setLoadMoreEnable(false);
        mRefreshView.setHasFixedSize(true);
        HotGoodsEmptyViewProxy hotGoodsEmptyViewProxy=new HotGoodsEmptyViewProxy();
        hotGoodsEmptyViewProxy.setEmptyText("No products available, go add something");
        mRefreshView.setEmptyViewProxy(getViewProxyMannger(),hotGoodsEmptyViewProxy);
        paymentButtonContainer = findViewById(R.id.payment_button_container);
        btnPayStack = findViewById(R.id.btn_pay_stack);
        mShopCartAdapter=new ShopCartAdapter(null,getViewProxyMannger(),getActivity(),mShopCartModel);
        mRefreshView.setAdapter(mShopCartAdapter);
        mRefreshView.setRefreshEnable(false);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(getActivity(),0));
        mRefreshView.setDataListner(new RxRefreshView.DataListner<MultiItemEntity>() {
            @Override
            public Observable<List<MultiItemEntity>> loadData(int p) {
                return getData();
            }
            @Override
            public void compelete(List<MultiItemEntity> data) {
                mShopCartAdapter.expandAll();
                if(ListUtil.haveData(data)){
                    ViewUtil.setVisibility(mVpBottom, View.VISIBLE);
                }else{
                    ViewUtil.setVisibility(mVpBottom,View.GONE);
                }
                notifyAllSelectButton();
            }
            @Override
            public void error(Throwable e) {
            }
        });
        freshStateUI();
        int shopCartNum=ShopCartModel.getShopCartNum();
        String goodsNum=Integer.toString(shopCartNum);
        mTvGoodsNum.setText(goodsNum);
        mTvTotalNum.setText(getString(R.string.all_select,goodsNum));
        mBtnMannger.setOnClickListener(this);
        mBtnCollect.setOnClickListener(this);
        mBtnDelete.setOnClickListener(this);
        mCheckTotalImage.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);
        initShopCartDataObserver();

        paymentButtonContainer.setVisibility(View.GONE);
        fetchAccessToken();

        paymentButtonContainer.setOnClickListener(v -> startOrder());
        btnPayStack.setOnClickListener(v -> startOrderPayStack());
    }

    private void startOrderPayStack() {
        Card card = new Card("4084084084084081", 1, 30, "408");
        if (card.isValid()) {
            Charge charge = new Charge();
            charge.setCard(card);
            charge.setAmount(5000 * 100);
            charge.setCurrency("ZAR");
            charge.setEmail("user@example.com");
            charge.setReference("ChargedFromJava_" + System.currentTimeMillis());

            PaystackSdk.chargeCard((Activity) getParentLayoutGroup().getContext(), charge, new Paystack.TransactionCallback() {
                @Override
                public void onSuccess(Transaction transaction) {
                    Toast.makeText(getParentLayoutGroup().getContext(), "Payment success", Toast.LENGTH_LONG).show();
                }

                @Override
                public void beforeValidate(Transaction transaction) {
                }

                @Override
                public void onError(Throwable error, Transaction transaction) {
                    Log.e("longnx", "onError: " + error.getMessage());
                    Toast.makeText(getParentLayoutGroup().getContext(), "Payment error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void fetchAccessToken() {
        String authString = clientID + ":" + secretID;
        String encodedAuthString = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);

        AndroidNetworking.post("https://api-m.sandbox.paypal.com/v1/oauth2/token")
                .addHeaders("Authorization", "Basic " + encodedAuthString)
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addBodyParameter("grant_type", "client_credentials")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            accessToken = response.getString("access_token");
                            paymentButtonContainer.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.d("Paypal", error.getErrorBody());
                    }
                });
    }

    private void startOrder() {
        uniqueId = UUID.randomUUID().toString();

        try {
            JSONObject orderRequestJson = new JSONObject();
            orderRequestJson.put("intent", "CAPTURE");

            JSONArray purchaseUnits = new JSONArray();
            JSONObject unit = new JSONObject();
            unit.put("reference_id", uniqueId);

            JSONObject amount = new JSONObject();
            amount.put("currency_code", "USD");
            amount.put("value", "5.00");

            unit.put("amount", amount);
            purchaseUnits.put(unit);
            orderRequestJson.put("purchase_units", purchaseUnits);

            JSONObject experienceContext = new JSONObject();
            experienceContext.put("payment_method_preference", "IMMEDIATE_PAYMENT_REQUIRED");
            experienceContext.put("brand_name", "SH Developer");
            experienceContext.put("locale", "en-US");
            experienceContext.put("landing_page", "LOGIN");
            experienceContext.put("shipping_preference", "NO_SHIPPING");
            experienceContext.put("user_action", "PAY_NOW");
            experienceContext.put("return_url", returnUrl);
            experienceContext.put("cancel_url", "https://example.com/cancelUrl");

            JSONObject paypal = new JSONObject();
            paypal.put("experience_context", experienceContext);

            JSONObject paymentSource = new JSONObject();
            paymentSource.put("paypal", paypal);
            orderRequestJson.put("payment_source", paymentSource);

            AndroidNetworking.post("https://api-m.sandbox.paypal.com/v2/checkout/orders")
                    .addHeaders("Authorization", "Bearer " + accessToken)
                    .addHeaders("Content-Type", "application/json")
                    .addHeaders("PayPal-Request-Id", uniqueId)
                    .addJSONObjectBody(orderRequestJson)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                handlerOrderID(response.getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.d("Paypal", "Order Error : " + error.getMessage() + " || " + error.getErrorBody() + " || " + error.getResponse());
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handlerOrderID(String orderID) {
        CoreConfig config = new CoreConfig(clientID, Environment.SANDBOX);
        PayPalWebCheckoutClient payPalWebCheckoutClient = new PayPalWebCheckoutClient((FragmentActivity) getParentLayoutGroup().getContext(), config, returnUrl);

        payPalWebCheckoutClient.setListener(new PayPalWebCheckoutListener() {
            @Override
            public void onPayPalWebSuccess(PayPalWebCheckoutResult result) {
                Log.d("longnx", "onPayPalWebSuccess: " + result);
            }

            @Override
            public void onPayPalWebFailure(PayPalSDKError error) {
                Log.d("longnx", "onPayPalWebFailure: ");
            }

            @Override
            public void onPayPalWebCanceled() {
                Log.d("longnx", "onPayPalWebCanceled: ");
            }
        });

        orderid = orderID;
        PayPalWebCheckoutRequest payPalWebCheckoutRequest = new PayPalWebCheckoutRequest(orderID, PayPalWebCheckoutFundingSource.PAYPAL);
        payPalWebCheckoutClient.start(payPalWebCheckoutRequest);
    }

    private void captureOrder(String orderID) {
        AndroidNetworking.post("https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderID + "/capture")
                .addHeaders("Authorization", "Bearer " + accessToken)
                .addHeaders("Content-Type", "application/json")
                .addJSONObjectBody(new JSONObject()) // Empty body
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getParentLayoutGroup().getContext(), "Payment Successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("Paypal", "Capture Error : " + error.getErrorDetail());
                    }
                });
    }

    public void onActivityNewIntent(Intent intent) {
        if (intent != null && intent.getData() != null) {
            String opType = intent.getData().getQueryParameter("opType");
            if ("payment".equals(opType)) {
                captureOrder(orderid);
            } else if ("cancel".equals(opType)) {
                Toast.makeText(getActivity(), "Payment Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*查看所有商品是否是选中状态*/
    private void notifyAllSelectButton() {
        if(mShopCartModel!=null){
            mCheckTotalImage.setChecked(mShopCartModel.isAllSelect());
        }
    }


    private void initShopCartDataObserver() {
        /*观察价格变化*/
        mShopCartModel.getPriceData().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(@NotNull String price) {
                mTvTotalPrice.setText(price);
            }
        });

        /*观察购物车数量变化*/
        ShopCartModel.obserShopCartNum(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                String numString=integer.toString();
                mTvGoodsNum.setText(numString);
                mTvTotalNum.setText(getString(R.string.all_select,numString));
            }
        });

        //观察是有选中的状态
        mShopCartModel.getCheckLiveData().observe(getActivity(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                mBtnCommit.setEnabled(aBoolean);
            }
        });


        mShopCartModel.getNotifyItemChangeLiveData().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                notifyAllSelectButton();
                if(integer==ShopCartModel.NOTIFY_DEFAULT){
                    if(mRefreshView!=null){
                        mRefreshView.initData();
                    }
                }else if(integer==ShopCartModel.NOTIFY_ALL){
                    if(mShopCartAdapter!=null){
                        mShopCartAdapter.notifyDataSetChanged();
                        if(mShopCartModel!=null){
                            mShopCartModel.measureTotalPrice();
                        }
                    }
                }else if(integer==ShopCartModel.NOTIFY_LOCK_SCROLL){
                    if(mRefreshView!=null){
                        mRefreshView.setCanScroll(false);
                    }
                }else if(integer==ShopCartModel.NOTIFY_UNLOCK_SCROLL){
                    if(mRefreshView!=null){
                        mRefreshView.setCanScroll(true);
                    }
                }
            }
        });
    }


    private void freshStateUI() {
        if(mState==STATE_NORMAL){
            mBtnMannger.setText(R.string.mannger);
            mVpToolBuy.setVisibility(View.VISIBLE);
            mVpToolManger.setVisibility(View.GONE);
        }else{
            mBtnMannger.setText(R.string.cancel);
            mVpToolBuy.setVisibility(View.GONE);
            mVpToolManger.setVisibility(View.VISIBLE);
        }
    }

    /*设置状态*/
    private void judgeState() {
        if(mState==STATE_NORMAL){
            mState=STATE_EDIT;
            mShopCartModel.setEditMode(true);
        }else if(mState==STATE_EDIT){
            mState=STATE_NORMAL;
            mShopCartModel.setEditMode(false);
        }
        freshStateUI();
    }


    /*网络请求*/
    private Observable<List<MultiItemEntity>> getData() {
        return ShopAPI.getShopCartData().map(new Function<ShopcartParseBean, List<MultiItemEntity>>() {
            @Override
            public List<MultiItemEntity> apply(ShopcartParseBean shopcartParseBean) throws Exception {
                mShopcartParseBean=shopcartParseBean;
                List<MultiItemEntity>list=ShopCartModel.transFormListData(shopcartParseBean);
                if(mShopCartModel!=null){
                    mShopCartModel.setShopCartData(shopcartParseBean.getValid());
                }
                return list;
            }
        }).compose(this.<List<MultiItemEntity>>bindUntilOnDestoryEvent());
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(mRefreshView!=null){
                mRefreshView.initData();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ShopAPI.cancle(ShopAPI.SHOP_CART_LIST);
    }
    @Override
    public void onClick(View v) {
        if(!ClickUtil.canClick()){
            return;
        }
        int id=v.getId();
        if(id==R.id.btn_mannger){
            judgeState();
        }else if(id==R.id.btn_collect){
            collect();
        }else if(id==R.id.btn_delete){
            deleteGoods();
        }else if(id==R.id.check_total_image){
            judgeAllSelect();
        }else if(id==R.id.btn_commit){
            commit();
        }
    }

    private void commit() {
        if(mShopCartModel==null){
            return;
        }

        String[] selectId=mShopCartModel.getAllSelectCartId();
        if(selectId==null||selectId.length<=0){
            ToastUtil.show(getString(R.string.select_goods_tip));
            return;
        }
        String idArray= StringUtil.splitJoint(selectId);
        CommitOrderActivity.forward(getActivity(),idArray);

    }

    private void judgeAllSelect() {
        if(mCheckTotalImage==null){
            return;
        }
        final boolean isTargetCheck=!mCheckTotalImage.isChecked();
        mShopCartModel.setAllSelected(isTargetCheck);
        mCheckTotalImage.setChecked(isTargetCheck);
    }

    /*删除商品*/
    private void deleteGoods() {
        final String[]allSelectId=mShopCartModel.getAllSelectCartId();
        if(allSelectId==null||allSelectId.length<=0) {
            ToastUtil.show(R.string.select_goods_tip);
            return;
        }
        DialogUitl.showSimpleDialog(getActivity(), "是否要删除商品?", new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if(mShopCartModel!=null){
                    mShopCartModel.deleteGoodsArray(allSelectId, ShopCartViewProxy.this);
                }
            }
        });
    }

    /*批量收藏商品*/
    private void collect() {
        String[] allSelectId=getAllSelectGoodsId();
        if(allSelectId==null||allSelectId.length<=0) {
            ToastUtil.show(getString(R.string.select_goods_tip));
            return;
        }
        ShopAPI.batchCollect(allSelectId, ShopState.PRODUCT_DEFAULT).
                compose(this.<Boolean>bindUntilOnDestoryEvent())
                .subscribe(new DefaultObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        if(aBoolean){
                            ToastUtil.show(R.string.collect_succ);
                        }
                    }
                });
    }

    private String[] getAllSelectGoodsId() {
        if(mShopCartModel==null){
            return null;
        }
        String[] allSelectId=mShopCartModel.getAllSelectGoodsId();
        return allSelectId;
    }

    @Override
    public void deleteInvaild() {
        if(mShopcartParseBean==null||mShopCartModel==null){
            return;
        }
        List<ShopCartBean>list=mShopcartParseBean.getInvalid();
        if(!ListUtil.haveData(list)){
            return;
        }
        int size=list.size();
        String[]allId=new String[size];
        for(int i=0;i<size;i++){
            ShopCartBean shopCartBean= list.get(i);
            allId[i]=shopCartBean.getId();
        }
        mShopCartModel.deleteGoodsArray(allId,this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_shop_cart;
    }

    public void initData() {
        if(mRefreshView!=null){
            mRefreshView.initData();
        }
    }
}

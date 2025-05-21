package com.wanyue.main.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.Constants;
import com.wanyue.common.HtmlConfig;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.activity.WebViewActivity;
import com.wanyue.common.api.CommonAPI;
import com.wanyue.common.bean.CoinBean;
import com.wanyue.common.bean.CoinPayBean;
import com.wanyue.common.custom.ItemDecoration;
import com.wanyue.common.event.CoinChangeEvent;
import com.wanyue.common.http.CommonHttpConsts;
import com.wanyue.common.http.CommonHttpUtil;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.interfaces.OnItemClickListener;
import com.wanyue.common.pay.PayCallback;
import com.wanyue.common.pay.PayPresenter;
import com.wanyue.common.utils.RouteUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.main.R;
import com.wanyue.main.adapter.CoinAdapter;
import com.wanyue.main.adapter.CoinPayAdapter;
import com.wanyue.main.api.MainAPI;
import com.wanyue.shop.view.activty.OrderDeatailActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的钻石
 */
@Route(path = RouteUtil.PATH_COIN)
public class MyDiamondsActivity extends BaseActivity implements OnItemClickListener<CoinBean>, View.OnClickListener {

    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView mPayRecyclerView;
    private CoinAdapter mAdapter;
    private CoinPayAdapter mPayAdapter;
    private TextView mBalance;
    private long mBalanceValue;
    private boolean mFirstLoad = true;
    private PayPresenter mPayPresenter;
    private String mCoinName;
    /*private TextView mTip1;
    private TextView mTip2;
    private TextView mCoin2;*/
    private String mChargeH5Url;
    private View mHeadView;
    private ImageView mIvPaystackSelected;
    private ImageView mIvPaypalSelected;
    private View mBtnPaystack;
    private View mBtnPaypal;
    private int mSelectedPaymentMethod = -1; // -1: none, 0: paystack, 1: paypal
    private boolean mShowBackHomeDialog = false;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, MyDiamondsActivity.class));
    }

    @Override
    public void init() {
        setTabTitle(WordUtil.getString(R.string.wallet, CommonAppConfig.getCoinName()));
        /*mTip1 = findViewById(R.id.tip_1);
        mTip2 = findViewById(R.id.tip_2);
        mCoin2 = findViewById(R.id.coin_2);*/
        mRefreshLayout = findViewById(R.id.refreshLayout);
        mRefreshLayout.setColorSchemeResources(R.color.global);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        mCoinName = CommonAppConfig.getCoinName();
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 3;
                }
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 5, 20);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);

        TextView coinNameTextView = findViewById(R.id.coin_name);
        coinNameTextView.setText(WordUtil.getString(R.string.wallet_coin_name, mCoinName));
        mBalance = findViewById(R.id.coin);

        mAdapter = new CoinAdapter(mContext, mCoinName);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setContactView(findViewById(R.id.top));
        mRecyclerView.setAdapter(mAdapter);
        findViewById(R.id.btn_tip).setOnClickListener(this);
        findViewById(R.id.btn_charge).setOnClickListener(this);
        View headView = mAdapter.getHeadView();
        mPayRecyclerView = headView.findViewById(R.id.pay_recyclerView);
        
        // Initialize payment method views
        mBtnPaystack = headView.findViewById(R.id.btn_pay_stack);
        mBtnPaypal = headView.findViewById(R.id.btn_wx);
        mIvPaystackSelected = headView.findViewById(R.id.iv_paystack_selected);
        mIvPaypalSelected = headView.findViewById(R.id.iv_paypal_selected);

        // Set click listeners for payment methods
        mBtnPaystack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPaymentMethod(0);
            }
        });

        mBtnPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPaymentMethod(1);
            }
        });

        ItemDecoration decoration2 = new ItemDecoration(mContext, 0x00000000, 14, 10);
        decoration2.setOnlySetItemOffsetsButNoDraw(true);
        mPayRecyclerView.addItemDecoration(decoration2);
        mPayRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false));
        mPayAdapter = new CoinPayAdapter(mContext);
        mPayRecyclerView.setAdapter(mPayAdapter);
        mPayPresenter = new PayPresenter(this);
        mPayPresenter.setServiceNameAli(Constants.PAY_BUY_COIN_ALI);
        mPayPresenter.setServiceNameWx(Constants.PAY_BUY_COIN_WX);
        mPayPresenter.setAliCallbackUrl(HtmlConfig.ALI_PAY_COIN_URL);
        mPayPresenter.setPayCallback(new PayCallback() {
            @Override
            public void onSuccess() {
                if (mPayPresenter != null) {
                    mPayPresenter.checkPayResult();
                }
            }

            @Override
            public void onFailed(int error) {

            }
        });
        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String trxref = data.getQueryParameter("trxref");
            String reference = data.getQueryParameter("reference");
            String token = data.getQueryParameter("token");

            if (trxref != null && reference != null) {
                verifyPayment(trxref, reference);
            } else {
                verifyPaymentPayPal(token);
            }
        }
    }

    private void verifyPayment(String trxref, String reference) {
        AndroidNetworking.get("http://system.sandtoner.com/api/paystack/verify")
                .addQueryParameter("trxref", trxref)
                .addQueryParameter("reference", reference)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            if (status == 200) {
                                org.json.JSONObject data = response.getJSONObject("data");
                                String resultStatus = data.getString("status");
                                if ("success".equalsIgnoreCase(resultStatus)) {
                                    Toast.makeText(MyDiamondsActivity.this, "Payment successful", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MyDiamondsActivity.this, "Payment failed: " + msg, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(MyDiamondsActivity.this, "Error: " + msg, Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MyDiamondsActivity.this, "Data processing error", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(MyDiamondsActivity.this, "Network error: " + anError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void verifyPaymentPayPal(String token) {
        AndroidNetworking.get("http://system.sandtoner.com/api/paypal/verify")
                .addQueryParameter("token", token)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {
                        try {
                            int status = response.getInt("status");
                            String msg = response.getString("msg");

                            if (status == 200) {
                                org.json.JSONObject data = response.getJSONObject("data");
                                String resultStatus = data.getString("status");
                                if ("success".equalsIgnoreCase(resultStatus)) {
                                    Toast.makeText(MyDiamondsActivity.this, "Payment successful", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MyDiamondsActivity.this, "Payment failed: " + msg, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(MyDiamondsActivity.this, "Error: " + msg, Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MyDiamondsActivity.this, "Data processing error", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(MyDiamondsActivity.this, "Network error: " + anError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_diamonds;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mShowBackHomeDialog) {
            mShowBackHomeDialog = false;
            showBackHomeDialog();
            return;
        }
        if (mFirstLoad) {
            mFirstLoad = false;
            loadData();
        }
    }

    private void loadData() {
        CommonAPI.getBalance(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (isSuccess(code) && info.length > 0) {
                    try {
                        android.util.Log.e("PAYMENT", "Raw response: " + info[0]);
                        JSONObject obj = JSON.parseObject(info[0]);
                        android.util.Log.e("PAYMENT", "Parsed obj: " + (obj != null ? obj.toJSONString() : "null"));
                        String coin = obj.getString("coin");
                        mBalanceValue = Long.parseLong(coin);
                        mBalance.setText(coin);
                        //mTip1.setText(obj.getString("tip_t"));
                        //mTip2.setText(obj.getString("tip_d"));
                        //mCoin2.setText(obj.getString("score"));

                        List<CoinBean> list = JSON.parseArray(obj.getString("list"), CoinBean.class);
                        if (mAdapter != null) {
                            mAdapter.setList(list);
                        }
                        mPayPresenter.setBalanceValue(mBalanceValue);
                        JSONObject objConfig = obj.getJSONObject("config");
                        mPayPresenter.setAliPartner(objConfig.getString("aliapp_partner"));
                        mPayPresenter.setAliSellerId(objConfig.getString("aliapp_seller_id"));
                        mPayPresenter.setAliPrivateKey(objConfig.getString("aliapp_key_android"));
                        mPayPresenter.setWxAppID(objConfig.getString("wx_appid"));
                        mPayPresenter.setServiceNameAli("getorderbyali");
                        mPayPresenter.setServiceNameWx("getorderbywx");
                        List<CoinPayBean> payList = new ArrayList<>();
                        if (objConfig.getIntValue("aliapp_switch") == 1) {
                            CoinPayBean coinPayBean = new CoinPayBean();
                            coinPayBean.setChecked(true);
                            coinPayBean.setName(WordUtil.getString(R.string.alipay));
                            coinPayBean.setId(Constants.PAY_TYPE_ALI);
                            coinPayBean.setIcon(R.mipmap.icon_cash_ali);
                            payList.add(coinPayBean);
                        }
                        if (objConfig.getIntValue("wx_switch") == 1) {
                            CoinPayBean coinPayBean = new CoinPayBean();
                            coinPayBean.setChecked(payList.size() <= 0);
                            coinPayBean.setName(WordUtil.getString(R.string.wxpay));
                            coinPayBean.setId(Constants.PAY_TYPE_WX);
                            coinPayBean.setIcon(R.mipmap.icon_cash_wx);
                            payList.add(coinPayBean);
                        }
                        if (mPayAdapter != null) {
                            mPayAdapter.setList(payList);
                        }
                        mChargeH5Url = obj.getString("h5url");
                    } catch (Exception e) {
                        android.util.Log.e("PAYMENT", "Error processing balance response", e);
                        Toast.makeText(mContext, "Error processing balance: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        showPaymentErrorDialog("Error processing balance: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                showPaymentErrorDialog(msg);
            }

            @Override
            public void onFinish() {
                if (mRefreshLayout != null) {
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void selectPaymentMethod(int method) {
        if (mSelectedPaymentMethod == method) {
            return;
        }

        // Hide all selection indicators
        mIvPaystackSelected.setVisibility(View.GONE);
        mIvPaypalSelected.setVisibility(View.GONE);

        // Reset all backgrounds to unselected
        mBtnPaystack.setSelected(false);
        mBtnPaypal.setSelected(false);

        // Show selected indicator and set selected background
        switch (method) {
            case 0:
                mIvPaystackSelected.setVisibility(View.VISIBLE);
                mBtnPaystack.setSelected(true);
                break;
            case 1:
                mIvPaypalSelected.setVisibility(View.VISIBLE);
                mBtnPaypal.setSelected(true);
                break;
        }

        mSelectedPaymentMethod = method;
    }

    @Override
    public void onItemClick(CoinBean bean, int position) {
        if (mPayPresenter == null) {
            return;
        }
        if (mSelectedPaymentMethod == -1) {
            ToastUtil.show(R.string.wallet_tip_5);
            return;
        }

        String money = bean.getMoney();
        String goodsName = StringUtil.contact(bean.getCoin(), mCoinName);
        String orderParams = StringUtil.contact(
                "&uid=", CommonAppConfig.getUid(),
                "&money=", money,
                "&ruleid=", bean.getId(),
                "&coin=", bean.getCoin(),
                "&type=", "1");

        switch (mSelectedPaymentMethod) {
            case 0: // PayStack
                mPayPresenter.pay("paystack", money, goodsName, orderParams);
                break;
            case 1: // PayPal
                mPayPresenter.pay("paypal", money, goodsName, orderParams);
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCoinChangeEvent(CoinChangeEvent e) {
        if (mBalance != null) {
            mBalance.setText(e.getCoin());
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_tip) {
            if (!TextUtils.isEmpty(mChargeH5Url)) {
                WebViewActivity.forward(mContext, mChargeH5Url);
            }
        } else if (i == R.id.btn_charge) {
            // Gọi API tạo order thanh toán
            if (mSelectedPaymentMethod == -1) {
                ToastUtil.show(R.string.wallet_tip_5);
                return;
            }
            CoinBean bean = mAdapter != null ? mAdapter.getCoinBean() : null;
            if (bean == null) {
                ToastUtil.show(R.string.wallet_tip_6);
                return;
            }
            String paytype = null;
            switch (mSelectedPaymentMethod) {
                case 0:
                    paytype = "4"; // paystack
                    break;
                case 1:
                    paytype = "3"; // paypal
                    break;
            }
            android.util.Log.e("PAYMENT", "Call API createSandtonerOrder with: paytype=" + paytype + ", money=" + bean.getMoney() + ", coin=" + bean.getCoin() + ", ruleid=" + bean.getId() + ", uid=" + CommonAppConfig.getUid() + ", token=" + CommonAppConfig.getToken() + ", order_date=order_date=recharge");
            MainAPI.createSandtonerOrder(
                CommonAppConfig.getUid(),
                CommonAppConfig.getToken(),
                bean.getMoney(),
                bean.getId(),
                bean.getCoin(),
                paytype,
                "order_date=recharge",
                new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 200 && info != null && info.length > 0) {
                            try {
                                android.util.Log.e("PAYMENT", "Raw response: " + info[0]);
                                JSONObject obj = JSON.parseObject(info[0]);
                                android.util.Log.e("PAYMENT", "Parsed obj: " + (obj != null ? obj.toJSONString() : "null"));
                                JSONObject data = obj.getJSONObject("data");
                                if (data == null) {
                                    data = obj;
                                }
                                android.util.Log.e("PAYMENT", "Parsed data: " + (data != null ? data.toJSONString() : "null"));
                                if (data == null) {
                                    Toast.makeText(mContext, "No data in response", Toast.LENGTH_LONG).show();
                                    android.util.Log.e("PAYMENT", "data is null! obj: " + (obj != null ? obj.toJSONString() : "null") + ", info[0]: " + info[0]);
                                    return;
                                }

                                String status = data.getString("status");
                                if (TextUtils.isEmpty(status)) {
                                    Toast.makeText(mContext, "No payment status returned", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                JSONObject result = data.getJSONObject("result");
                                if (result == null) {
                                    Toast.makeText(mContext, "No result data returned", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                String orderNo = result.getString("orderno");
                                if (TextUtils.isEmpty(orderNo)) {
                                    Toast.makeText(mContext, "No order number returned", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                
                                if ("PAYSTACK".equalsIgnoreCase(status)) {
                                    Object jsConfigObj = result.get("jsConfig");
                                    if (jsConfigObj == null) {
                                        android.util.Log.e("PAYSTACK", "jsConfig is null");
                                        Toast.makeText(mContext, "Order " + orderNo + " created successfully, but payment config is missing!", Toast.LENGTH_LONG).show();
                                        showPaymentErrorDialog("No Paystack payment config returned!");
                                        return;
                                    }

                                    if (jsConfigObj instanceof JSONObject) {
                                        JSONObject jsConfig = (JSONObject) jsConfigObj;
                                        JSONObject payData = jsConfig.getJSONObject("pay_data");
                                        if (payData != null && payData.containsKey("authorization_url")) {
                                            String url = payData.getString("authorization_url");
                                            if (!TextUtils.isEmpty(url)) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                mContext.startActivity(intent);
                                                Toast.makeText(mContext, "Order " + orderNo + " created successfully, please complete payment!", Toast.LENGTH_LONG).show();
                                                mShowBackHomeDialog = true;
                                            } else {
                                                android.util.Log.e("PAYSTACK", "authorization_url is empty: " + payData.toJSONString());
                                                Toast.makeText(mContext, "Order " + orderNo + " created successfully, but no payment URL returned!", Toast.LENGTH_LONG).show();
                                                showPaymentErrorDialog("No Paystack payment URL returned!");
                                            }
                                        } else {
                                            android.util.Log.e("PAYSTACK", "payData is null or missing authorization_url: " + (payData != null ? payData.toJSONString() : "payData is null"));
                                            Toast.makeText(mContext, "Order " + orderNo + " created successfully, but payment data is missing!", Toast.LENGTH_LONG).show();
                                            showPaymentErrorDialog("No Paystack payment data returned!");
                                        }
                                    } else if (jsConfigObj instanceof com.alibaba.fastjson.JSONArray) {
                                        android.util.Log.e("PAYSTACK", "jsConfig is JSONArray (empty): " + jsConfigObj.toString());
                                        Toast.makeText(mContext, "Order " + orderNo + " created successfully, but payment config is empty!", Toast.LENGTH_LONG).show();
                                        showPaymentErrorDialog("No Paystack payment config returned (empty array)!");
                                    } else {
                                        android.util.Log.e("PAYSTACK", "jsConfig is not JSONObject or JSONArray: " + jsConfigObj.toString());
                                        Toast.makeText(mContext, "Order " + orderNo + " created successfully, but payment config is invalid!", Toast.LENGTH_LONG).show();
                                        showPaymentErrorDialog("Invalid Paystack payment config format!");
                                    }
                                } else if ("PAYPAL".equalsIgnoreCase(status)) {
                                    Object jsConfigObj = result.get("jsConfig");
                                    if (jsConfigObj instanceof JSONObject) {
                                        JSONObject jsConfig = (JSONObject) jsConfigObj;
                                        JSONObject payData = jsConfig.getJSONObject("pay_data");
                                        if (payData != null && payData.containsKey("link_url")) {
                                            String url = payData.getString("link_url");
                                            if (!TextUtils.isEmpty(url)) {
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                mContext.startActivity(intent);
                                                Toast.makeText(mContext, "Order " + orderNo + " created successfully, please complete payment!", Toast.LENGTH_LONG).show();
                                                mShowBackHomeDialog = true;
                                            } else {
                                                android.util.Log.e("PAYPAL", "link_url is empty or null: " + payData.toJSONString());
                                                Toast.makeText(mContext, "Order " + orderNo + " created successfully, but no payment URL returned!", Toast.LENGTH_LONG).show();
                                                showPaymentErrorDialog("No PayPal payment URL returned!");
                                            }
                                        } else {
                                            android.util.Log.e("PAYPAL", "payData is null or missing link_url: " + (payData != null ? payData.toJSONString() : "payData is null"));
                                            Toast.makeText(mContext, "Order " + orderNo + " created successfully, but payment data is missing!", Toast.LENGTH_LONG).show();
                                            showPaymentErrorDialog("No PayPal payment data returned!");
                                        }
                                    } else {
                                        android.util.Log.e("PAYPAL", "jsConfig is not JSONObject: " + (jsConfigObj != null ? jsConfigObj.toString() : "jsConfig is null"));
                                        Toast.makeText(mContext, "Order " + orderNo + " created successfully, but payment config is missing!", Toast.LENGTH_LONG).show();
                                        showPaymentErrorDialog("No PayPal payment config returned!");
                                    }
                                } else {
                                    Toast.makeText(mContext, "Unknown payment status: " + status, Toast.LENGTH_LONG).show();
                                    showPaymentErrorDialog("Unknown payment status: " + status);
                                }
                            } catch (Exception e) {
                                android.util.Log.e("PAYMENT", "Error processing payment response", e);
                                Toast.makeText(mContext, "Error processing payment: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                showPaymentErrorDialog("Error processing payment: " + e.getMessage());
                            }
                        } else {
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            showPaymentErrorDialog(msg);
                        }
                    }
                    @Override
                    public void onError(int code, String msg) {
                        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                        showPaymentErrorDialog(msg);
                    }
                }
            );
        }
    }

    private void showPaymentErrorDialog(String message) {
        new AlertDialog.Builder(this)
            .setTitle("Payment Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show();
    }

    private void showBackHomeDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Payment")
            .setMessage("Have you completed payment? Back to home page?")
            .setPositiveButton("Back homepage", (dialog, which) -> {
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(homeIntent);
                finish();
            })
            .setNegativeButton("Stay here", null)
            .show();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_BALANCE);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_ALI_ORDER);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_WX_ORDER);
        if (mRefreshLayout != null) {
            mRefreshLayout.setOnRefreshListener(null);
        }
        mRefreshLayout = null;
        if (mPayPresenter != null) {
            mPayPresenter.release();
        }
        mPayPresenter = null;
        super.onDestroy();
    }


}

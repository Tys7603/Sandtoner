package com.wanyue.shop.view.pop;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.alibaba.fastjson.JSONObject;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.Constants;
import com.wanyue.common.bean.UserBean;
import com.wanyue.common.business.UserModel;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.http.ParseSingleHttpCallback;
import com.wanyue.common.pay.PayCallback;
import com.wanyue.common.pay.PayPresenter;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.SpUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.OrderConfirmBean;
import com.wanyue.shop.model.OrderModel;
import com.wanyue.shop.view.activty.CommitOrderActivity;
import com.wanyue.shop.view.activty.PaymentWebViewActivity;

import org.json.JSONException;


public class PayOrderPopView extends BaseBottomPopView implements View.OnClickListener {
    private ImageView mBtnClose;
    private ViewGroup mBtnWx;
    private ViewGroup mBtn_pp;
    private ViewGroup mBtnCoin;
    private TextView mTvCoinMoney;
    private String mId;

    private PayPresenter mPayPresenter;

    @Override
    protected void init() {
        super.init();
        mBtnClose = findViewById(R.id.btn_close);
        mBtnWx =  findViewById(R.id.btn_wx);
        mBtn_pp =  findViewById(R.id.btn_pp);
        mBtnCoin = findViewById(R.id.btn_coin);
        mTvCoinMoney =  findViewById(R.id.tv_coin_money);
        mBtnClose.setOnClickListener(this);
        mBtn_pp.setOnClickListener(this);
        mBtnWx.setOnClickListener(this);
        mBtnCoin.setOnClickListener(this);

        UserBean userBean= CommonAppConfig.getUserBean();
        if(userBean!=null){
         String price= StringUtil.getFormatPrice(userBean.getBalance());
         mTvCoinMoney.setText(price);
        }

        //reOrder();
    }

    public PayOrderPopView(@NonNull Context context) {
        super(context);
    }
    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_pay_order;
    }
    @Override
    public void onClick(View v) {
        if(!ClickUtil.canClick()){
            return;
        }
        int id=v.getId();
        if(id==R.id.btn_close){

            dismiss();

        }else if(id==R.id.btn_wx){

            reOrder(false);

        }else if(id==R.id.btn_pp){

            reOrder(true);

        }else if(id==R.id.btn_coin){

            payForCoin();

        }
    }

    public void setId(String id) {
        mId = id;
    }


    private void initPayPrestner() {
        if(mPayPresenter==null){
           mPayPresenter=new PayPresenter((Activity) getContext());
        }
    }

    private OrderConfirmBean mOrderConfirmBean;
    private void reOrder(Boolean paypal){
        Log.d("SSS", "reOrder mId: " + mId.toString());
        ShopAPI.againOrder(mId, new ParseSingleHttpCallback<String>("cateId") {
            @Override
            public void onSuccess(String data) {
                Log.d("SSS", "reOrder data: " + data);

                ShopAPI.orderConfirm(data, new ParseHttpCallback<JSONObject>() {
                    @Override
                    public void onSuccess(int code, String msg, JSONObject info) {
                        if(isSuccess(code)&&info!=null){

                            //String json=info.toJSONString();
                            mOrderConfirmBean = info.toJavaObject(OrderConfirmBean.class);
                            Log.d("SSS", "orderConfirm info: " + info);

                            if(paypal){
                                cancelPreOrder();
                                createOrderPaypal();
                                //payByPaypal();
                            }else {
                                cancelPreOrder();
                                //payByPayStack();
                                createOrderPayStack();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Handle error case
                        if (e != null) {
                            Log.d("SSS", "reOrder onError: " + e);
                            ToastUtil.show(e.getMessage());
                        }
                    }
                });

            }
        });

    }

    private void cancelPreOrder(){
        ShopAPI.cancleOrder(mId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                //ToastUtil.show(msg);
                if(isSuccess(code)){
                    Log.d("SSS", "cancleOrder isSuccess");
                }
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }
    public void createOrderPaypal() {
        String token = SpUtil.getInstance().getStringValue(SpUtil.TOKEN);
        String addressId= String.valueOf(mOrderConfirmBean.getAddrId());
        String key= mOrderConfirmBean.getOrderKey();

        AndroidNetworking.upload("https://system.sandtoner.com/api/order/create/" + key)
                .addHeaders("Authori-zation", "Bearer " + token)
                .addMultipartParameter("payType", "paypal")
                .addMultipartParameter("addressId", addressId)
                .setTag("createOrderPaypal")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {
                        Log.d("SSS", "Success: " + response.toString());
                        try {
                            String status = response.getString("status");
                            String msg = response.getString("msg");

                            if (status.equals("200")) {
                                dismiss();
                                org.json.JSONObject data = response.getJSONObject("data");
                                org.json.JSONObject result = data.getJSONObject("result");
                                org.json.JSONObject payConfig = result.getJSONObject("payConfig");
                                org.json.JSONObject pay_data = payConfig.getJSONObject("pay_data");
                                String link = pay_data.getString("link_url");
                                String orderId = result.getString("orderId");
                                Intent intent = new Intent(getContext(), PaymentWebViewActivity.class);
                                intent.putExtra("payment_url", link);
                                intent.putExtra("orderId", orderId);
                                ((Activity) getContext()).startActivityForResult(intent, 0);
//                                callSuccPay(orderId);
                            } else {
                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Data processing errors have occurred", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("SSS", "Error: " + error.getErrorDetail());

                    }
                });
    }

    public void createOrderPayStack() {
        String token = SpUtil.getInstance().getStringValue(SpUtil.TOKEN);
        String addressId= String.valueOf(mOrderConfirmBean.getAddrId());
        String key= mOrderConfirmBean.getOrderKey();
        String email= "user@gmail.com";


        AndroidNetworking.upload("https://system.sandtoner.com/api/order/create/" + key)
                .addHeaders("Authori-zation", "Bearer " + token)
                .addMultipartParameter("payType", "paystack")  // e.g., "paystack"
                .addMultipartParameter("addressId", addressId)
                .addMultipartParameter("email", email)
                .setTag("createOrderPaystack")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {
                        Log.d("SSS", "Success: " + response.toString());
                        try {
                            String status = response.getString("status");
                            String msg = response.getString("msg");

                            if (status.equals("200")) {
                                dismiss();
                                org.json.JSONObject data = response.getJSONObject("data");
                                org.json.JSONObject result = data.getJSONObject("result");
                                org.json.JSONObject payConfig = result.getJSONObject("payConfig");
                                String orderId = result.getString("orderId");
                                org.json.JSONObject pay_data = payConfig.getJSONObject("pay_data");
                                String link = pay_data.getString("authorization_url");
                                Intent intent = new Intent(getContext(), PaymentWebViewActivity.class);
                                intent.putExtra("payment_url", link);
                                intent.putExtra("orderId", orderId);
                                ((Activity) getContext()).startActivityForResult(intent, 0);
//                                callSuccPay(orderId);
                            } else {
                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Data processing errors have occurred", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("SSS", "Error: " + error.getErrorDetail());

                    }
                });
    }

    private void payByPaypal() {
        if(mId==null){
            dismiss();
            return;
        }


        ShopAPI.orderPay(mId, Constants.PAY_TYPE_PP, new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                if(isSuccess(code)){
                    Log.d("SSS", "orderPay info: " + info.toString());
                    try {
                        JSONObject result = info.getJSONObject("result");
                        String orderId = result.getString("order_id");

                        JSONObject payConfig = result.getJSONObject("payConfig");
                        JSONObject payData = payConfig.getJSONObject("pay_data");
                        String link = payData.getString("link_url");

                        Intent intent = new Intent(getContext(), PaymentWebViewActivity.class);
                        intent.putExtra("payment_url", link);
                        intent.putExtra("orderId", orderId);
                        ((Activity) getContext()).startActivityForResult(intent, 0);
                    } catch (Exception e) {
                        Log.e("DEBUG_INFO", "Lỗi khi parse JSON: " + e.getMessage());
                        e.printStackTrace();
                    }

                }

            }
            @Override
            public boolean showLoadingDialog() {
                return true;
            }
            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(getContext());
            }

            @Override
            public void onError(Throwable e) {
                // Handle error case for WeChat payment
                DialogUitl.dismissDialog(createLoadingDialog());
            }
        });
    }

    private void payByPayStack() {
        if(mId==null){
            dismiss();
            return;
        }

        ShopAPI.orderPay(mId, Constants.PAY_TYPE_PSTACK, new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                if(isSuccess(code)){

                    try {
                        JSONObject result = info.getJSONObject("result");
                        String orderId = result.getString("order_id");

                        JSONObject payConfig = result.getJSONObject("payConfig");
                        JSONObject payData = payConfig.getJSONObject("pay_data");
                        String link = payData.getString("link_url");

                        Intent intent = new Intent(getContext(), PaymentWebViewActivity.class);
                        intent.putExtra("payment_url", link);
                        intent.putExtra("orderId", orderId);
                        ((Activity) getContext()).startActivityForResult(intent, 0);
                    } catch (Exception e) {
                        Log.e("DEBUG_INFO", "Lỗi khi parse JSON: " + e.getMessage());
                        e.printStackTrace();
                    }

                }

            }
            @Override
            public boolean showLoadingDialog() {
                return true;
            }
            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(getContext());
            }

            @Override
            public void onError(Throwable e) {
                // Handle error case for WeChat payment
                DialogUitl.dismissDialog(createLoadingDialog());
            }
        });
    }

    private void tuneWx(JSONObject info) {
        initPayPrestner();
        mPayPresenter.setPayCallback(new PayCallback() {
            @Override
            public void onSuccess() {
                buySucc();
            }
            @Override
            public void onFailed(int errorCode) {
            }
        });
        try {
            JSONObject wxConfig=info.getJSONObject("result").getJSONObject("jsConfig");
            if(wxConfig!=null){
                mPayPresenter.setWxAppID(wxConfig.getString("appid"));
                mPayPresenter.wxPay2(wxConfig);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void payForCoin() {
        if(mId==null){
            dismiss();
            return;
        }
        ShopAPI.orderPay(mId, Constants.PAY_TYPE_COIN, new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                if(isSuccess(code)){
                    buySucc();
                }
            }
            @Override
            public boolean showLoadingDialog() {
                return true;
            }
            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(getContext());
            }

            @Override
            public void onError(Throwable e) {
                // Handle error case for Coin payment
                DialogUitl.dismissDialog(createLoadingDialog());
            }
        });
    }

    private void buySucc() {
        OrderModel.sendOrderChangeEvent(mId);
        UserModel.refreshBalance();
        dismiss();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        ShopAPI.cancle(ShopAPI.ORDER_PAY);
    }
}

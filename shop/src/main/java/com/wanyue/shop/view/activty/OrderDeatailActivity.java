package com.wanyue.shop.view.activty;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import com.alibaba.fastjson.JSONObject;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.activity.WebViewActivity;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.http.ParseSingleHttpCallback;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.SpUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.imnew.view.chat.ChatActivity;
import com.wanyue.shop.R;
import com.wanyue.shop.adapter.BuyerOrderChildAdapter;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.OrderBean;
import com.wanyue.shop.bean.OrderStatus;
import com.wanyue.shop.bean.ShopCartBean;
import com.wanyue.shop.bean.StoreGoodsBean;
import com.wanyue.shop.business.ShopState;
import com.wanyue.shop.evaluate.activity.PublishEvaluateActivity;
import com.wanyue.shop.model.OrderModel;
import com.wanyue.shop.view.pop.PayOrderPopView;
import com.wanyue.shop.view.view.order.AbsOderDetailBottomViewProxy;
import com.wanyue.shop.view.view.order.OrderDetailProgressViewProxy;
import com.wanyue.shop.view.widet.ViewGroupLayoutBaseAdapter;
import com.wanyue.shop.view.widet.linear.PoolLinearListView;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

import java.util.List;

public class OrderDeatailActivity extends BaseActivity implements View.OnClickListener {
    private String mOrderId;
    private OrderBean mOrderBean;
    private ImageView mImgStatusGif;
    private TextView mTvOrderTip;
    private TextView mTvTime;
    private ViewGroup mVpProgressContainer;
    private TextView mTvNamePhone;
    private TextView mTvAddress;
    private TextView mTvGoodsNum;
    private PoolLinearListView mListView;
    private TextView mTvOrderId;
    private TextView mBtnCopy;
    private TextView mTvOrderCreateTime;
    private TextView mTvPayType;
    private TextView mTvPayStyle;
    private TextView mTvOrderPrice;
    private TextView mTvYunfei;
    private TextView mTvTotalPrice;
    
    private ViewGroup mVpLogistics;
    private TextView mTvDelivery;
    private TextView mTvLogisticsCompany;
    private TextView mTvExpressNo;
    private ScrollView mScrollView;


    private OrderDetailProgressViewProxy mOrderDetailProgressViewProxy;
    private BuyerOrderChildAdapter mBuyerOrderChildAdapter;
    private AbsOderDetailBottomViewProxy mAbsOderDetailBottomViewProxy;

    private FrameLayout mVpBottom;
    private boolean mNoEvaluate;

    private int mStatus;
    @Override
    public void init() {
        setTabTitle(R.string.order_detail);
        mOrderId=getIntent().getStringExtra(Constants.KEY_ID);
        mNoEvaluate=getIntent().getBooleanExtra(Constants.KEY_EVALUATE,false);
        mImgStatusGif = (ImageView) findViewById(R.id.img_status_gif);
        mTvOrderTip = (TextView) findViewById(R.id.tv_order_tip);
        mTvTime =  findViewById(R.id.tv_time);
        mVpProgressContainer =  findViewById(R.id.vp_progress_container);
        mTvNamePhone = (TextView) findViewById(R.id.tv_name_phone);
        mTvAddress = (TextView) findViewById(R.id.tv_address);
        mTvGoodsNum = (TextView) findViewById(R.id.tv_goods_num);
        mListView = (PoolLinearListView) findViewById(R.id.listView);
        mTvOrderId = (TextView) findViewById(R.id.tv_order_id);
        mBtnCopy = (TextView) findViewById(R.id.btn_copy);
        mTvOrderCreateTime = (TextView) findViewById(R.id.tv_order_create_time);
        mTvPayType = (TextView) findViewById(R.id.tv_pay_type);
        mTvPayStyle = (TextView) findViewById(R.id.tv_pay_style);
        mTvOrderPrice = (TextView) findViewById(R.id.tv_order_price);
        mTvYunfei = (TextView) findViewById(R.id.tv_yunfei);
        mTvTotalPrice = (TextView) findViewById(R.id.tv_total_price);
        mVpBottom = (FrameLayout) findViewById(R.id.vp_bottom);

        mVpLogistics = (ViewGroup) findViewById(R.id.vp_logistics);
        mTvDelivery = (TextView) findViewById(R.id.tv_delivery);
        mTvLogisticsCompany = (TextView) findViewById(R.id.tv_logistics_company);
        mTvExpressNo = (TextView) findViewById(R.id.tv_express_no);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);

        mOrderDetailProgressViewProxy=new OrderDetailProgressViewProxy();
        getViewProxyMannger().addViewProxy(mVpProgressContainer,mOrderDetailProgressViewProxy,mOrderDetailProgressViewProxy.getDefaultTag());
        mBtnCopy.setOnClickListener(this);
        findViewById(R.id.btn_custom_service).setOnClickListener(this);
        OrderModel.watchOrderChangeEvent(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(StringUtil.equals(s,mOrderId)){
                    if(!isPaying)finish();
                    Log.d("SSS" , "onChanged");
                }
            }
        });
        mStatus=getIntent().getIntExtra(Constants.KEY_STATUS,0);
        if(mStatus!=ShopState.ORDER_BUY_SELF){
            FrameLayout.LayoutParams params= (FrameLayout.LayoutParams) mScrollView.getLayoutParams();
            params.bottomMargin=0;
        }
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_order_deatail;
    }
    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        ShopAPI.getOrderDetail(mOrderId, mStatus,new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                if(isSuccess(code)){
                   L.e(info.toJSONString());
                    mOrderBean=info.toJavaObject(OrderBean.class);
                    setOrderDataUI();
                }
            }

            @Override
            public void onError(Throwable e) {
                // Handle error case
                if (e != null) {
                    ToastUtil.show(e.getMessage());
                }
            }
        });
    }

    private void setOrderDataUI() {
        if(mOrderBean==null){
            return;
        }

        int orderType=-1;
        OrderStatus orderStatus=mOrderBean.getOrderStatus();
        if(orderStatus!=null){
           orderType=orderStatus.getType();
           if(mOrderDetailProgressViewProxy!=null){
              mOrderDetailProgressViewProxy.setStatus(orderType);
           }
           addBottomView(orderType);

            if(orderStatus.getType()== ShopState.ORDER_STATE_WAIT_PAY){
                mTvPayType.setText(R.string.no_pay);
            }else{
                mTvPayType.setText(R.string.payed);
            }
            mTvOrderTip.setText(orderStatus.getMsg());
            mTvPayStyle.setText(orderStatus.getPayType());
        }
        boolean isEvaluate=orderType==ShopState.ORDER_STATE_WAIT_EVALUATE&&!mNoEvaluate;//是否显示评论

        if(mOrderBean.getStatus()!=0){
            setLogisticsMessage(mOrderBean);
        }

        List<ShopCartBean> array=mOrderBean.getCartInfo();
        if(mBuyerOrderChildAdapter==null){
           mBuyerOrderChildAdapter=new BuyerOrderChildAdapter(array);
           mBuyerOrderChildAdapter.setOpenEvaluate(isEvaluate);
           mBuyerOrderChildAdapter.setOnItemChildClickListener(new ViewGroupLayoutBaseAdapter.OnItemChildClickListener<ShopCartBean>() {
                @Override
                public void onItemClick(int position, ShopCartBean shopCartBean, View view) {
                   PublishEvaluateActivity.forward(mContext,shopCartBean,mOrderId);
                }
            });
           mListView.setAdapter(mBuyerOrderChildAdapter);
        }else{
            mBuyerOrderChildAdapter.setData(array);
        }
        mTvNamePhone.setText(mOrderBean.getNameAndPhone());
        mTvAddress.setText(mOrderBean.getAddress());
        ImgLoader.display(this,mOrderBean.getStatusPic(),mImgStatusGif);
        mTvTime.setText(mOrderBean.getAddTime());
        mTvOrderId.setText(mOrderBean.getOrderId());
        mTvGoodsNum.setText(getString(R.string.total_num_jian2,Integer.toString(mOrderBean.getTotalNum())));
        mTvOrderCreateTime.setText(mOrderBean.getAddTime());
        mTvTotalPrice.setText(StringUtil.getPrice(mOrderBean.getPayPrice()));
        mTvOrderPrice.setText(StringUtil.getPrice(mOrderBean.getTotalPrice()));
        mTvYunfei.setText(StringUtil.getPrice(mOrderBean.getPayPostage()));
    }

    private void addBottomView(int orderType) {
        if(mAbsOderDetailBottomViewProxy==null&&mStatus==ShopState.ORDER_BUY_SELF){
            AbsOderDetailBottomViewProxy bottomViewProxy=AbsOderDetailBottomViewProxy.create(orderType);
            if(bottomViewProxy!=null){

                bottomViewProxy.setOnPaymentResultListener(new AbsOderDetailBottomViewProxy.OnPaymentResultListener() {
                    @Override
                    public void onSuccess(Boolean isPaypal, String addressId, String key) {
                        commitPayMethod(isPaypal, addressId, key);
                    }

                });
                mAbsOderDetailBottomViewProxy=bottomViewProxy;

                getViewProxyMannger().addViewProxy(mVpBottom,bottomViewProxy,bottomViewProxy.getDefaultTag());
                bottomViewProxy.setOrderBean(mOrderBean);
            }
        }else{
            mVpBottom.setVisibility(View.GONE);
        }
    }

    private Boolean isPaying= false;
    private void commitPayMethod(Boolean isPaypal, String addressId, String key){
        String token = SpUtil.getInstance().getStringValue(SpUtil.TOKEN);

        Log.d("SSS","commitPayMethod");
        isPaying= true;
        if (isPaypal) {
            createOrderPaypal(addressId, key, token);
        } else {
            String email = "user@gmail.com";
            createOrderPayStack(addressId, email, key, token);
        }
    }

    public void createOrderPaypal(String addressId, String key, String token) {

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

                        try {
                            String status = response.getString("status");
                            String msg = response.getString("msg");

                            if (status.equals("200")) {
                                org.json.JSONObject data = response.getJSONObject("data");
                                org.json.JSONObject result = data.getJSONObject("result");
                                org.json.JSONObject payConfig = result.getJSONObject("payConfig");

                                org.json.JSONObject pay_data = payConfig.getJSONObject("pay_data");
                                String link = pay_data.getString("link_url");

                                String orderId = result.getString("orderId");

                                Intent intent = new Intent(OrderDeatailActivity.this, PaymentWebViewActivity.class);
                                intent.putExtra("payment_url", link);
                                intent.putExtra("orderId", orderId);
                                startActivityForResult(intent, 0);
//                                callSuccPay(orderId);
                            } else {
                                Log.d("SSS", "msg: " + msg);
                                Toast.makeText(OrderDeatailActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(OrderDeatailActivity.this, "Data processing errors have occurred", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("API_ERROR", "Error: " + error.getErrorDetail());
                    }
                });
    }

    public void createOrderPayStack(String addressId, String email, String key, String token) {
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
                        Log.d("API_RESPONSE", "Success: " + response.toString());
                        try {
                            String status = response.getString("status");
                            String msg = response.getString("msg");

                            if (status.equals("200")) {
                                org.json.JSONObject data = response.getJSONObject("data");
                                org.json.JSONObject result = data.getJSONObject("result");
                                org.json.JSONObject payConfig = result.getJSONObject("payConfig");
                                String orderId = result.getString("orderId");
                                org.json.JSONObject pay_data = payConfig.getJSONObject("pay_data");
                                String link = pay_data.getString("authorization_url");


                                Intent intent = new Intent(OrderDeatailActivity.this, PaymentWebViewActivity.class);
                                intent.putExtra("payment_url", link);
                                intent.putExtra("orderId", orderId);
                                startActivityForResult(intent, 0);
//                                callSuccPay(orderId);
                            } else {
                                Log.d("SSS", "msg: " + msg);

                                Toast.makeText(OrderDeatailActivity.this, msg, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(OrderDeatailActivity.this, "Data processing errors have occurred", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("API_ERROR", "Error: " + error.getErrorDetail());

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("SSS" , "onActivityResult");

        if (requestCode == 0 && resultCode == RESULT_OK) {
            String trxref = data.getStringExtra("trxref");
            String reference = data.getStringExtra("reference");
            String token = data.getStringExtra("token");

            if (token.equals("")) {
                verifyPayment(trxref, reference);
            } else {
                verifyPaymentPayPal(token, reference);
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
                                callSuccPay(resultStatus.equals("SUCCESS") ? trxref : "");
                            } else {
                                Toast.makeText(OrderDeatailActivity.this, "Error: " + msg, Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(OrderDeatailActivity.this, "Data processing error", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(OrderDeatailActivity.this, "Network error: " + anError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void verifyPaymentPayPal(String token, String reference) {
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
                                callSuccPay(resultStatus.equals("SUCCESS") ? reference : "");
                            } else {
                                Toast.makeText(OrderDeatailActivity.this, "Error: " + msg, Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(OrderDeatailActivity.this, "Data processing error", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(OrderDeatailActivity.this, "Network error: " + anError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void callSuccPay(String orderId) {

        OrderPayResultActivity.forward(
                OrderDeatailActivity.this,
                orderId,
                !TextUtils.isEmpty(null));
        finish();
    }



    private void setLogisticsMessage(OrderBean orderBean) {
        mVpLogistics.setVisibility(View.VISIBLE);
        mTvDelivery.setText(orderBean.getDeliveryTypeName());
        mTvLogisticsCompany.setText(orderBean.getDeliveryName());
        mTvExpressNo.setText(orderBean.getDeliveryId());
    }

    public static void forward(Context context,int status, String orderId){
        forward(context,status,orderId,false);
    }

    public static void forward(Context context,int status, String orderId,boolean noEvaluate){
        Intent intent=new Intent(context,OrderDeatailActivity.class);
        intent.putExtra(Constants.KEY_ID,orderId);
        intent.putExtra(Constants.KEY_STATUS,status);
        intent.putExtra(Constants.KEY_EVALUATE,noEvaluate);
        context.startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShopAPI.cancle(ShopAPI.ORDER_DETAIL);
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_copy){
            if(mOrderBean!=null){
               copy(mOrderBean.getOrderId());
            }
        }else if(id==R.id.btn_custom_service){
            openService();
        }
    }

    private String mServiceLink;
    private void openService() {
        String storeId=mOrderBean.getStoreId();
        ChatActivity.forward(this,storeId,mOrderBean.getShopName());
       /* if(TextUtils.isEmpty(mServiceLink)){
            ShopAPI.getShopService(storeId, new ParseSingleHttpCallback<String>("service_url") {
                @Override
                public void onSuccess(String data) {
                    mServiceLink=data;
                    WebViewActivity.forward(OrderDeatailActivity.this,data);
                }
            });
        }else{
            WebViewActivity.forward(this,mServiceLink);
        }*/
    }


    /**
     * 复制到剪贴板
     */

    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(getString(com.wanyue.common.R.string.copy_success));
    }

}

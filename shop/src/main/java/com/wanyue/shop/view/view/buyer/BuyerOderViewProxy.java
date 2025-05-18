package com.wanyue.shop.view.view.buyer;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONObject;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.wanyue.common.adapter.base.BaseMutiRecyclerAdapter;
import com.wanyue.common.bean.SpecsValueBean;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.http.BaseHttpCallBack;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.http.ParseSingleHttpCallback;
import com.wanyue.common.proxy.BaseViewProxy;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.server.observer.DialogObserver;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.common.utils.DebugUtil;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.SpUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.adapter.BuyerOrderAdaper;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.OrderBean;
import com.wanyue.shop.bean.OrderConfirmBean;
import com.wanyue.shop.bean.ShopCartBean;
import com.wanyue.shop.business.ShopState;
import com.wanyue.shop.model.OrderModel;
import com.wanyue.shop.model.ShopCartModel;
import com.wanyue.shop.view.activty.CommitOrderActivity;
import com.wanyue.shop.view.activty.GoodsDetailActivity;
import com.wanyue.shop.view.activty.OrderDeatailActivity;
import com.wanyue.shop.evaluate.activity.PublishEvaluateActivity;
import com.wanyue.shop.view.activty.OrderPayResultActivity;
import com.wanyue.shop.view.activty.PaymentWebViewActivity;
import com.wanyue.shop.view.pop.PayOrderPopView;
import com.wanyue.shop.view.view.SpecsSelectViewProxy;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

import java.util.List;
import io.reactivex.Observable;

public abstract class BuyerOderViewProxy extends RxViewProxy implements BaseQuickAdapter.OnItemClickListener, BaseMutiRecyclerAdapter.OnItemChildClickListener2<OrderBean> {
    private RxRefreshView<OrderBean> mRefreshView;
    private BuyerOrderAdaper mAdapter;
    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyText("No order information available");
        mAdapter=new BuyerOrderAdaper(null);
        mAdapter.setOnItemClickListener(this);
        mRefreshView.setAdapter(mAdapter);
        mRefreshView.setDataListner(new RxRefreshView.DataListner<OrderBean>() {
            @Override
            public Observable<List<OrderBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<OrderBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });
        mAdapter.setOnItemChildClickListener2(this);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(getActivity(),10));
        OrderModel.watchOrderChangeEvent(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String id) {
                checkNeedRefresh(id);
            }
        });
    }

    //判断是否需要刷新订单
    private void checkNeedRefresh(String id) {
        if(mAdapter==null){
            return;
        }
        List<OrderBean>list= mAdapter.getArray();
        if(!ListUtil.haveData(list))     {
            return;
        }
        for(OrderBean orderBean:list)     {
            if(StringUtil.equals(orderBean.getOrderId(),id)){
                if(mRefreshView!=null)   {
                    mRefreshView.initData();
                }
                return;
            }
        }
    }
    public abstract Observable<List<OrderBean>> getData(int p);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            autoRefresh();
        }
    }

    private void autoRefresh() {

        if(mRefreshView!=null){
           mRefreshView.initData();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_single_refresh;
    }
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
           if(mAdapter==null){
               DebugUtil.sendException("onItemClick=="+null);
               return;
           }
          OrderBean orderBean=mAdapter.getItem(position);

            Log.d("SSS", new Gson().toJson(orderBean));
          OrderDeatailActivity.forward(getActivity(), ShopState.ORDER_BUY_SELF,orderBean.getOrderId());
    }

    @Override
    public void onItemClick(int position, OrderBean orderBean, View view) {
            int id=view.getId();
            if(id==R.id.btn_cancel){
              openCancleOrderDialog(orderBean.getOrderId(),position);
            }else if(id==R.id.btn_buy){
                buyOrder(orderBean.getOrderId(),position);
            }else if(id==R.id.btn_evaluate){
             toEvaluate(orderBean);
            }else if(id==R.id.btn_buy_again){
               againOrder(orderBean);
            }else if(id==R.id.btn_delete){
              openDeleteConfrimDialog(orderBean);
            }
    }


    private void openCancleOrderDialog(final String orderId, final int position) {
        DialogUitl.showSimpleDialog(getActivity(), "Cancel the order ?", new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                cancleOrder(orderId,position, true);
            }
        });
    }


    private void openDeleteConfrimDialog(final OrderBean orderBean) {
        DialogUitl.showSimpleDialog(getActivity(), "Do you want to delete the order?", new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                deleteOrder(orderBean);
            }
        });
    }


    /*删除订单*/
    private void deleteOrder(OrderBean orderBean) {
       final  String orderId=orderBean.getOrderId();
        ShopAPI.deleteOrder(orderId).compose(this.<Boolean>bindToLifecycle())
                .subscribe(new DialogObserver<Boolean>(getActivity()) {
                    @Override
                    public void onNextTo(Boolean aBoolean) {
                        if(aBoolean){
                          OrderModel.sendOrderChangeEvent(orderId);
                        }
                    }
                });
    }



    protected  void againOrder(OrderBean orderBean){
        ShopAPI.againOrder(orderBean.getOrderId(), new ParseSingleHttpCallback<String>("cateId") {
            @Override
            public void onSuccess(String data) {

                CommitOrderActivity.forward(getActivity(),data);
            }
        });
    }

    private void cancleOrder(String id,final int position, Boolean isNeedConfirm) {
        ShopAPI.cancleOrder(id, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(isNeedConfirm)ToastUtil.show(msg);

                if(isSuccess(code)){
                    if(mAdapter!=null){
                        mAdapter.remove(position);

                        if(!isNeedConfirm) autoRefresh();
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }

    private void toEvaluate(OrderBean orderBean) {
        OrderDeatailActivity.forward(getActivity(),ShopState.ORDER_BUY_SELF,orderBean.getOrderId());
    }


    private PayOrderPopView mPayOrderPopView;
    private void buyOrder(String id, int position) {
        if(mPayOrderPopView!=null&&mPayOrderPopView.isShow()){
            return;
        }

        getProductInfo(id , position);

    }



    //Note: Pay Method
    //CourseDetailActivity not yet apply those changes
    private OrderConfirmBean mOrderConfirmBean;
    private String productId;
    private int productCount;
    private void getProductInfo(String mId, int position){
        ShopAPI.getOrderDetail(mId, ShopState.ORDER_BUY_SELF,new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                if(isSuccess(code)){

                    OrderBean orderBean=info.toJavaObject(OrderBean.class);
                    ShopCartBean shopCartBean= orderBean.getCartInfo().get(0);
                    productId= shopCartBean.getProductId();
                    productCount= orderBean.getTotalNum();

                    requestAddShopCart(mId, position);
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
    private void requestAddShopCart(String mId, int position) {
        ShopAPI.addShopCart(
                productId, productCount, null, null, null, null, 1,new ParseHttpCallback<JSONObject>() {
                    @Override
                    public void onSuccess(int code, String msg, JSONObject info) {
                        if(isSuccess(code)&&info!=null){
                            String orderIdArray=info.getString("cartId");
                            //Log.d("SSS", "requestAddShopCart orderIdArray: " + orderIdArray);

                            ShopAPI.orderConfirm(orderIdArray, new ParseHttpCallback<JSONObject>() {
                                @Override
                                public void onSuccess(int code, String msg, JSONObject info) {
                                    if(isSuccess(code)&&info!=null){

                                        String json=info.toJSONString();
                                        //Log.d("SSS", "onSuccess: " + json);

                                        mOrderConfirmBean = info.toJavaObject(OrderConfirmBean.class);
                                        mOrderConfirmBean.setLiveUid(null);

                                        autoRefresh();

                                        //start chosse pay method
                                        openDialogPayMethodChoice(mId, position);

                                    } else {
                                        Log.d("SSS", "onSuccess null");
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    // Handle error case
                                    if (e != null) {
                                        ToastUtil.show(e.getMessage());
                                        Log.d("SSS", "onError: " + e.getMessage());
                                    }
                                }
                            });

                        }else{
                            ToastUtil.show(msg);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            ToastUtil.show(e.getMessage());
                        }
                    }
                }
        );
    }

    private void openDialogPayMethodChoice(String mId, int position){
        if(mPayOrderPopView!=null&&mPayOrderPopView.isShow()){
            return;
        }

        mPayOrderPopView=new PayOrderPopView(getActivity()){
            @Override
            protected void onDismiss() {
                super.onDismiss();
                mPayOrderPopView=null;
            }
        };
        mPayOrderPopView.setId(mId);
        mPayOrderPopView.setOnPaymentResultListener(new PayOrderPopView.OnPaymentResultListener() {
            @Override
            public void onSuccess(Boolean isPaypal) {
                // Xử lý khi thanh toán thành công
                String addressId = String.valueOf(mOrderConfirmBean.getAddrId());
                String key= mOrderConfirmBean.getOrderKey();
                commitPayMethod(isPaypal, addressId, key);
                cancleOrder(mId,position, false);

            }

        });
        new XPopup.Builder(getActivity())
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asCustom(mPayOrderPopView)
                .show();
    }

    private void commitPayMethod(Boolean isPaypal, String addressId, String key){
        String token = SpUtil.getInstance().getStringValue(SpUtil.TOKEN);

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

                                Intent intent = new Intent(getActivity(), PaymentWebViewActivity.class);
                                intent.putExtra("payment_url", link);
                                intent.putExtra("orderId", orderId);
                                startActivityForResult(intent, 0);
//                                callSuccPay(orderId);
                            } else {
                                Log.d("SSS", "msg: " + msg);
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Data processing errors have occurred", Toast.LENGTH_SHORT).show();
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


                                Intent intent = new Intent(getActivity(), PaymentWebViewActivity.class);
                                intent.putExtra("payment_url", link);
                                intent.putExtra("orderId", orderId);
                                startActivityForResult(intent, 0);
//                                callSuccPay(orderId);
                            } else {
                                Log.d("SSS", "msg: " + msg);

                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Data processing errors have occurred", Toast.LENGTH_SHORT).show();
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
        autoRefresh();

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
                                Toast.makeText(getActivity(), "Error: " + msg, Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Data processing error", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getActivity(), "Network error: " + anError.getMessage(), Toast.LENGTH_LONG).show();
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
                                Toast.makeText(getActivity(), "Error: " + msg, Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Data processing error", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getActivity(), "Network error: " + anError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void callSuccPay(String orderId) {

        autoRefresh();

        OrderPayResultActivity.forward(
                getActivity(),
                orderId,
                !TextUtils.isEmpty(null));
        //finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        autoRefresh();

    }
}

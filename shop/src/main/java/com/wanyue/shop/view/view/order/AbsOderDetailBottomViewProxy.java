package com.wanyue.shop.view.view.order;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.alibaba.fastjson.JSONObject;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.lxj.xpopup.XPopup;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.http.ParseSingleHttpCallback;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.server.observer.DialogObserver;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.SpUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.OrderBean;
import com.wanyue.shop.bean.OrderConfirmBean;
import com.wanyue.shop.bean.ShopCartBean;
import com.wanyue.shop.business.ShopState;
import com.wanyue.shop.model.OrderModel;
import com.wanyue.shop.view.activty.CommitOrderActivity;
import com.wanyue.shop.view.activty.OrderPayResultActivity;
import com.wanyue.shop.view.activty.OrderRefundActivity;
import com.wanyue.shop.view.activty.PaymentWebViewActivity;
import com.wanyue.shop.view.activty.ViewLogisticsActivity;
import com.wanyue.shop.view.pop.GetCodePop;
import com.wanyue.shop.view.pop.PayOrderPopView;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

/**
 * The type Abs oder detail bottom view proxy.
 */
public abstract class AbsOderDetailBottomViewProxy extends RxViewProxy implements View.OnClickListener {
    private OrderBean mOrderBean;
    private PayOrderPopView mPayOrderPopView;

    /**
     * Sets order bean.
     *
     * @param orderBean the order bean
     */
    public void setOrderBean(OrderBean orderBean) {
       mOrderBean = orderBean;
    }

    /**
     * Create abs oder detail bottom view proxy.
     *
     * @param state the state
     * @return the abs oder detail bottom view proxy
     */
    public static AbsOderDetailBottomViewProxy create(int state) {
        switch (state) {
            case ShopState.ORDER_STATE_WAIT_PAY:
                return new WaitPayODBottomViewProxy();
            case ShopState.ORDER_STATE_WAIT_DELIVERED:
                return new WaitDeliveredOdBottomViewProxy();
            case ShopState.ORDER_STATE_WAIT_RECEIVE:
                return new WaitReceiveOdViewProxy();
            case ShopState.ORDER_STATE_WAIT_EVALUATE:
                return new WaitEvaluateODViewProxy();
            case ShopState.ORDER_STATE_COMPELETE:
                return new CompeleteODViewProxy();
            default:
                return null;
        }
    }
    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        setOnClickListner(R.id.btn_cancel,this);
        setOnClickListner(R.id.btn_buy,this);

        setOnClickListner(R.id.btn_apply_refund,this);
        setOnClickListner(R.id.btn_check_wuliu,this);
        setOnClickListner(R.id.btn_buy_again,this);
        setOnClickListner(R.id.btn_delete,this);
        setOnClickListner(R.id.btn_take_goods,this);

    }

    /**
     * To refund.
     */
    public void toRefund(){
        OrderRefundActivity.forward(getActivity(),mOrderBean);
    }


    @Override
    public void onClick(View v) {
            if(!ClickUtil.canClick()||mOrderBean==null){
                return;
            }

            int id=v.getId();
            if(id==R.id.btn_apply_refund){
                toRefund();
            }else if(id==R.id.btn_check_wuliu){
                toViewLogistics();
            }else if(id==R.id.btn_buy_again){
                againOrder();
            }else if(id==R.id.btn_delete){
              openDeleteConfrimDialog(mOrderBean);
            }else if(id==R.id.btn_cancel){
                openCancleOrderDialog(mOrderBean);
            }else if(id==R.id.btn_buy){
                buy(mOrderBean.getOrderId());
            }else if(id==R.id.btn_take_goods){
                openTakeGoodsDialog(mOrderBean.getOrderId());
            }
    }


    private void openTakeGoodsDialog(final String orderId) {
        DialogUitl.showSimpleDialog(getActivity(), "To protect your rights, please confirm receipt after receiving the goods.",new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                takeGoods(orderId);
            }
        });
    }

    private void takeGoods(final String orderId) {
        ShopAPI.orderTake(orderId, new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                if(isSuccess(code)&&info!=null){
                    int gain_integral= info.getIntValue("gain_integral");
                    if(gain_integral==0){
                        ToastUtil.show(msg);
                        OrderModel.sendOrderChangeEvent(orderId);
                    }else{
                        openGetPopDialog(gain_integral);
                    }
                }else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onError(Throwable e) {
                // Handle error case for taking goods
                if (e != null) {
                    ToastUtil.show(e.getMessage());
                }
                DialogUitl.dismissDialog(createLoadingDialog());
            }
        });
    }

    private void openGetPopDialog(int gain_integral) {
        Activity activity=getActivity();
        GetCodePop getCodePop=new GetCodePop(activity);
        getCodePop.setCodeTip(Integer.toString(gain_integral));
        getCodePop.setOrderId(mOrderBean.getOrderId());
        new XPopup.Builder(activity)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asCustom(getCodePop)
                .show();
    }


    private void openCancleOrderDialog(final OrderBean orderBean) {
        DialogUitl.showSimpleDialog(getActivity(), "Cancel the order ?", new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                cancleOrder(orderBean.getOrderId(), true);
            }
        });
    }

    private void cancleOrder(final String id , Boolean isNeedConfirm) {
        ShopAPI.cancleOrder(id, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if(isNeedConfirm)ToastUtil.show(msg);
                if(isSuccess(code)){
                    OrderModel.sendOrderChangeEvent(id);
                }
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }

    private void openDeleteConfrimDialog(final OrderBean orderBean) {
        DialogUitl.showSimpleDialog(getActivity(), "Delete order ?", new DialogUitl.SimpleCallback() {
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
                           ToastUtil.show("Deleted successfully");
                           OrderModel.sendOrderChangeEvent(orderId);
                        }
                    }
                });
    }

    /**
     * Again order.
     */
    protected  void againOrder(){
        ShopAPI.againOrder(mOrderBean.getOrderId(), new ParseSingleHttpCallback<String>("cateId") {
            @Override
            public void onSuccess(String data) {
                CommitOrderActivity.forward(getActivity(),data);
            }
        });
    }

    private void toViewLogistics() {
       ViewLogisticsActivity.forward(getActivity(),mOrderBean.getOrderId());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        ShopAPI.cancle(ShopAPI.ORDER_AGAIN);
        ShopAPI.cancle(ShopAPI.ORDER_TAKE);
    }


    private void buy(String id) {
        if(mPayOrderPopView!=null&&mPayOrderPopView.isShow()){
            return;
        }

        getProductInfo(id);
    }

    //Note: Pay Method
    //CourseDetailActivity not yet apply those changes
    private OrderConfirmBean mOrderConfirmBean;
    private String productId;
    private int productCount;
    private void getProductInfo(String mId){
        Log.d("SSS", "getProductInfo");
        ShopAPI.getOrderDetail(mId, ShopState.ORDER_BUY_SELF,new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                if(isSuccess(code)){

                    OrderBean orderBean=info.toJavaObject(OrderBean.class);
                    ShopCartBean shopCartBean= orderBean.getCartInfo().get(0);
                    productId= shopCartBean.getProductId();
                    productCount= orderBean.getTotalNum();

                    requestAddShopCart(mId);
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
    private void requestAddShopCart(String mId) {
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

                                        //start chosse pay method
                                        openDialogPayMethodChoice(mId);

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

    private void openDialogPayMethodChoice(String mId){
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
                cancleOrder(mId , false);

            }

        });
        new XPopup.Builder(getActivity())
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asCustom(mPayOrderPopView)
                .show();
    }

    private void commitPayMethod(Boolean isPaypal, String addressId, String key){
        if (mListener != null) {
            mListener.onSuccess(isPaypal, addressId, key);
        }
    }

    //callback
    public interface OnPaymentResultListener {
        void onSuccess(Boolean isPaypal, String addressId, String key);
    }

    private AbsOderDetailBottomViewProxy.OnPaymentResultListener mListener;

    public void setOnPaymentResultListener(AbsOderDetailBottomViewProxy.OnPaymentResultListener listener) {
        this.mListener = listener;
    }
}

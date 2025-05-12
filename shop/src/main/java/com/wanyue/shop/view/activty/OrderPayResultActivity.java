package com.wanyue.shop.view.activty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.business.acmannger.ActivityMannger;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.common.utils.RouteUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.OrderBean;
import com.wanyue.shop.bean.OrderStatus;
import com.wanyue.shop.business.ShopState;

/**
 * The type Order pay result activity.
 */
public class OrderPayResultActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvOrderId;
    private TextView mTvOrderCreateTime;
    private TextView mTvPayStyle;
    private TextView mTvOrderPrice;
    private TextView mTvOrderFailed;
    private TextView mBtnPayAgain;
    private ImageView mImgState;
    private TextView mTvPayState;
    private ViewGroup mVpOrderFailed;


    private  boolean isSucc;

    private TextView mBtnBackMain;
    private boolean mIsFromLiveRoom;


    @Override
    public void init() {
        mTvOrderId = findViewById(R.id.tv_order_id);
        mTvOrderCreateTime = findViewById(R.id.tv_order_create_time);
        mTvPayStyle = findViewById(R.id.tv_pay_style);
        mTvOrderPrice = findViewById(R.id.tv_order_price);
        mVpOrderFailed = findViewById(R.id.vp_order_failed);
        mTvOrderFailed = findViewById(R.id.tv_order_failed);
        mBtnPayAgain = findViewById(R.id.btn_pay_again);
        mImgState = findViewById(R.id.img_state);
        mTvPayState = findViewById(R.id.tv_pay_state);
        mBtnBackMain = findViewById(R.id.btn_back_main);

        mBtnBackMain.setOnClickListener(this);
        findViewById(R.id.btn_pay_again).setOnClickListener(this);
        mIsFromLiveRoom=getIntent().getBooleanExtra(Constants.FROM,false);
        if(mIsFromLiveRoom){
            mBtnBackMain.setText("Return to live broadcast room");
        }else{
            mBtnBackMain.setText("Return to home page");
        }
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        getOrderDetail();
    }
    private  String mOrderId;
    private void getOrderDetail() {
        mOrderId=getIntent().getStringExtra(Constants.KEY_ID);
        ShopAPI.getOrderDetail(mOrderId, ShopState.ORDER_BUY_SELF,new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject jsonObject) {
                if(isSuccess(code)&&jsonObject!=null){
                    String json=jsonObject.toJSONString();
                    OrderBean orderBean=jsonObject.toJavaObject(OrderBean.class);
                    checkOrderPayResult(orderBean.getOrderStatus());
                    setOrderDataUI(orderBean);
                  return;
              }else{
                    finish();
                }
            }

            @Override
            public void onError(Throwable e) {
                // Handle error case for order detail
                if (e != null) {
                    ToastUtil.show(e.getMessage());
                }
                finish();
            }
        });
    }

    private void checkOrderPayResult(OrderStatus orderStatus) {
        isSucc = orderStatus != null;
        isPaySucc(isSucc);
    }

    private void setOrderDataUI(OrderBean mOrderBean) {
        if(mOrderBean==null){
            return;
        }
        OrderStatus orderStatus=mOrderBean.getOrderStatus();
        checkOrderPayResult(orderStatus);
        if(orderStatus!=null){
           mTvPayStyle.setText(orderStatus.getPayType());
        }
        mTvOrderId.setText(mOrderBean.getOrderId());
        mTvOrderCreateTime.setText(mOrderBean.getAddTime());
        mTvOrderPrice.setText(StringUtil.getPrice(mOrderBean.getPayPrice()));
    }


    private void isPaySucc(boolean isSucc) {
        if(isSucc){
            ImgLoader.display(this,R.drawable.icon_pay_succ,mImgState);
            mVpOrderFailed.setVisibility(View.GONE);
            mTvPayState.setText("Order payment successful");
            setTabTitle("Payment successful");
            mBtnPayAgain.setText("View order");
        }else{
            ImgLoader.display(this,R.drawable.icon_pay_fail,mImgState);
            mVpOrderFailed.setVisibility(View.VISIBLE);
            mTvOrderFailed.setText(getIntent().getStringExtra(Constants.KEY_TINT));
            mTvPayState.setText("Order payment failed");
            setTabTitle("Payment failed");
            mBtnPayAgain.setText("Repay");
        }
    }


    /**
     * Forward.
     *
     * @param context        the context
     * @param orderId        the order id
     * @param isFromLiveRoom the is from live room
     */
    public static void forward(Context context, String orderId,boolean isFromLiveRoom){
        forward(context,orderId,isFromLiveRoom,null);
    }

    /**
     * Forward.
     *
     * @param context        the context
     * @param orderId        the order id
     * @param isFromLiveRoom the is from live room
     * @param errorString    the error string
     */
    public static void forward(Context context, String orderId,boolean isFromLiveRoom,String errorString){
        Intent intent=new Intent(context,OrderPayResultActivity.class);
        intent.putExtra(Constants.KEY_ID,orderId);
        intent.putExtra(Constants.FROM,isFromLiveRoom);
        intent.putExtra(Constants.KEY_TINT,errorString);
        context.startActivity(intent);
    }



    @Override
    public int getLayoutId() {
        return R.layout.activity_order_pay_result;
    }

    @Override
    public void onClick(View v) {
        if(!ClickUtil.canClick()){
            return;
        }
        int id=v.getId();
        if(id==R.id.btn_pay_again){
         if(isSucc) {
             OrderDeatailActivity.forward(this, ShopState.ORDER_BUY_SELF,mOrderId);
         }else{
             startActivity(MyOrderActivity.class);
         }
         finish();
        }else if(id==R.id.btn_back_main){
         backToMain();
        }
    }

    private void backToMain() {
        Activity activity=ActivityMannger.getInstance().getTargetActivty();
        if(!mIsFromLiveRoom){
           RouteUtil.forwardMain(this);
        }else{
            ActivityMannger.getInstance().removeTopActivity(ActivityMannger.getInstance().getActivityLinkedHashSet(),activity);
        }
    }
}

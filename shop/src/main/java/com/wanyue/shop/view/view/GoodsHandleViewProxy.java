package com.wanyue.shop.view.view;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.alibaba.fastjson.JSONObject;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.activity.WebViewActivity;
import com.wanyue.common.custom.CheckImageView;
import com.wanyue.common.http.BaseHttpCallBack;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.http.ParseSingleHttpCallback;
import com.wanyue.common.proxy.BaseViewProxy;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.imnew.view.chat.ChatActivity;
import com.wanyue.shop.R;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.GoodsParseBean;
import com.wanyue.common.bean.SpecsValueBean;
import com.wanyue.shop.bean.StoreGoodsBean;
import com.wanyue.shop.model.GoodDetailModel;
import com.wanyue.shop.model.ShopCartModel;
import com.wanyue.shop.view.activty.CommitOrderActivity;
import com.wanyue.shop.view.activty.GoodsDetailActivity;
import com.wanyue.shop.view.activty.MyOrderActivity;
import com.wanyue.shop.view.activty.RefundOrderDetailActivity;
import com.wanyue.shop.view.activty.ShopCartActivity;

/**
 * The type Goods handle view proxy.
 */
/*底部操作框*/
public class GoodsHandleViewProxy extends BaseGoodsDetailBottomViewProxy implements View.OnClickListener {
    private ImageView mImgCustomerService;
    private TextView mTvCustomerService;
    private CheckImageView mImgCollect;
    private TextView mTvCollect;
    private ImageView mImgShopCart;
    private TextView mTvShopCart;
    private TextView mTvRedPoint;
    private TextView mBtnAddShop;
    private TextView mBtnBuy;
    private ShopCartModel mShopCartModel;
    private GoodDetailModel mGoodDetailModel;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mImgCustomerService =  findViewById(R.id.img_customer_service);
        mTvCustomerService =  findViewById(R.id.tv__customer_service);
        mImgCollect =  findViewById(R.id.img_collect);
        mTvCollect =  findViewById(R.id.tv_collect);
        mImgShopCart =  findViewById(R.id.img_shop_cart);
        mTvShopCart = findViewById(R.id.tv_shop_cart);
        mTvRedPoint =  findViewById(R.id.tv_red_point);
        mBtnAddShop = findViewById(R.id.btn_add_shop);
        mBtnBuy =  findViewById(R.id.btn_buy);
        FragmentActivity activity=getActivity();
        mShopCartModel= ViewModelProviders.of(activity).get(ShopCartModel.class);
        ViewUtil.setTextAndViewsibleByNumber(mTvRedPoint,ShopCartModel.getShopCartNum());
        ShopCartModel.obserShopCartNum(activity, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                ViewUtil.setTextAndViewsibleByNumber(mTvRedPoint,integer);
            }
        });
        mImgCollect.setOnClickListener(this);
        mTvCollect.setOnClickListener(this);

        mImgShopCart.setOnClickListener(this);
        mTvShopCart.setOnClickListener(this);

        if(mBtnAddShop!=null){
           mBtnAddShop.setOnClickListener(this);
        }
        mBtnBuy.setOnClickListener(this);

        mTvCustomerService.setOnClickListener(this);
        mImgCustomerService.setOnClickListener(this);

        ShopCartModel.requestShopcartCount();
    }

    @Override
    public void onStart() {
        super.onStart();
        getGoodDetailModel();
    }

    private void getGoodDetailModel() {
        if(mGoodDetailModel==null){
           mGoodDetailModel=ViewModelProviders.of(getActivity()).get(GoodDetailModel.class);;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_goods_handle;
    }

    @Override
    public void onClick(View v) {
        if(!ClickUtil.canClick()){
            return;
        }
        int id=v.getId();
        if(id==R.id.img_collect||id==R.id.tv_collect){
            if (CommonAppConfig.isLogin()) collect(); else ToastUtil.show("Please log in");
        }else if(id==R.id.img_shop_cart||id==R.id.tv_shop_cart){
            if (CommonAppConfig.isLogin()) toShopCart(); else ToastUtil.show("Please log in");
        }else if(id==R.id.btn_add_shop){
            addShopCart();
        }else if(id==R.id.btn_buy){
            addShopCartAndBuy();
        }else if(id==R.id.tv__customer_service||id==R.id.img_customer_service){
            if (CommonAppConfig.isLogin()) openService(); else ToastUtil.show("Please log in");
        }
    }



    private String mServiceLink;
    private void openService() {
        if(mStoreGoodsBean==null){
            return;
        }
        int storeId=mStoreGoodsBean.getStoreId();
        ChatActivity.forward(getActivity(),storeId+"",mStoreGoodsBean.getStoreName());
    }

    private boolean checkInventory() {
        if (mStoreGoodsBean == null) {
            return false;
        }
        int stock = mStoreGoodsBean.getStock();
        if (stock <= 0) {
            ToastUtil.show(getString(R.string.goods_tip_40)); // "In Stock" message
            return false;
        }
        return true;
    }

    //加入购物车
    private void addShopCart() {
        if (!checkInventory()) {
            return;
        }
        BaseViewProxy baseViewProxy=getViewProxyMannger().getViewProxyByTag(SpecsSelectViewProxy.class.getSimpleName());
        if(baseViewProxy==null){
            ((GoodsDetailActivity)getActivity()).showSpecsSelectWindow();
           return;
        }
       requestAddShopCart(0, new HttpCallback() {
           @Override
           public void onSuccess(int code, String msg, String[] info) {
               if(isSuccess(code)){
                  ShopCartModel.requestShopcartCount();
                  ToastUtil.show("Add to cart successfully");
               }else{
                   ToastUtil.show(msg);
               }
           }

           @Override
           public void onError(int code, String msg) {

           }
       });
    }
    //立即购买
    private void addShopCartAndBuy() {
        if (!checkInventory()) {
            return;
        }
        BaseViewProxy baseViewProxy=getViewProxyMannger().getViewProxyByTag(SpecsSelectViewProxy.class.getSimpleName());
        if(baseViewProxy==null){
            ((GoodsDetailActivity)getActivity()).showSpecsSelectWindow();
            return;
        }
        requestAddShopCart(1, new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                if(isSuccess(code)&&info!=null){
                    String data=info.getString("cartId");
                    String liveUid=mGoodDetailModel!=null?mGoodDetailModel.getLiveUid():null;
                    CommitOrderActivity.forward(getActivity(),data,liveUid);
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
        });
    }

    private void requestAddShopCart(int type, BaseHttpCallBack baseHttpCallBack) {
        if(mStoreGoodsBean==null){
           return;
        }
        getGoodDetailModel();
        String specId=null;
        SpecsValueBean specsValueBean=mGoodDetailModel.getSelectSpecValue();
        if(specsValueBean!=null){
           specId=specsValueBean.getId();
        }
        int goodsNum=mGoodDetailModel.getSelectNum();
        ShopAPI.addShopCart(
                mStoreGoodsBean.getId(), goodsNum, specId, null, null, null, type,baseHttpCallBack
              );
    }

    private void toShopCart() {
        startActivity(ShopCartActivity.class);
    }

    @Override
    public void setStoreGoodsBean(StoreGoodsBean storeGoodsBean) {
        mStoreGoodsBean = storeGoodsBean;
        if(mStoreGoodsBean!=null){
          setIsCollect(mStoreGoodsBean.isUserCollect());
        }
    }

    /*设置是否显示隐藏*/
    private void setIsCollect(boolean isCollect){
        if(mImgCollect!=null){
           mImgCollect.setChecked(isCollect);
        }
    }

    /*动态判断是否收藏*/
    private void collect(){
       GoodsParseBean goodsParseBean= GoodDetailModel.getGoodsParse(getActivity());
       if(goodsParseBean==null||goodsParseBean.getGoodsInfo()==null){
           return;
       }
        final StoreGoodsBean storeGoodsBean= goodsParseBean.getGoodsInfo();
        final boolean targeCollectStatus=!storeGoodsBean.isUserCollect();
        ShopAPI.judgeCollect(
                targeCollectStatus,
                storeGoodsBean.getIsSeckill(),
                storeGoodsBean.getId()).compose(this.<Boolean>bindToLifecycle()).subscribe(new DefaultObserver<Boolean>() {
            @Override
            public void onNext(Boolean aBoolean) {
                    if(aBoolean){
                       storeGoodsBean.setUserCollect(targeCollectStatus);
                       if(mImgCollect!=null){
                         mImgCollect.setChecked(targeCollectStatus);
                       }
                    }
            }
        });
    }
}

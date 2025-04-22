package com.wanyue.shop.view.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.server.RequestFactory;
import com.wanyue.common.server.observer.DialogObserver;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.adapter.GetCouponAdapter;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.CouponBean;
import java.util.List;
import io.reactivex.Observable;

public class CouponGetPopView extends BaseBottomPopView implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener {
    private ImageView mBtnClose;
    private RxRefreshView<CouponBean> mRefreshView;
    private GetCouponAdapter mGetCouponAdapter;
    private String mGoodsId;

    public CouponGetPopView(@NonNull Context context) {
        super(context);
    }
    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_coupon_get;
    }

    @Override
    protected void init() {
        super.init();
        mBtnClose =  findViewById(R.id.btn_close);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setIconId(R.drawable.icon_empty_no_coupon);
        mBtnClose.setOnClickListener(this);
        mGetCouponAdapter=new GetCouponAdapter(null);
        mGetCouponAdapter.setOnItemChildClickListener(this);
        mRefreshView.setAdapter(mGetCouponAdapter);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(getContext(),10));
        mRefreshView.setDataListner(new RxRefreshView.DataListner<CouponBean>() {
            @Override
            public Observable<List<CouponBean>> loadData(int p) {
                return getData(p);
            }

            @Override
            public void compelete(List<CouponBean> data) {

            }
            @Override
            public void error(Throwable e) {
            }
        });
        if(mRefreshView!=null){
           mRefreshView.initData();
        }
    }

    private Observable<List<CouponBean>> getData(int p) {
        return ShopAPI.getCouponList(p,1,mGoodsId,null);
    }

    public void setGoodsId(String goodsId) {
        mGoodsId = goodsId;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        RequestFactory.getRequestManager().cancle(ShopAPI.GET_COUPON_LIST);
        RequestFactory.getRequestManager().cancle(ShopAPI.RECEIVE_COUPON);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        if(mGetCouponAdapter==null|| !ClickUtil.canClick()){
            return;
        }
       final CouponBean couponBean= mGetCouponAdapter.getItem(position);
       String id=couponBean.getId();
       if(couponBean.isUse()){
           return;
       }
       ShopAPI.receiveCoupon(id).subscribe(new DialogObserver<Boolean>(getContext()) {
           @Override
           public void onNextTo(Boolean aBoolean) {
               if(aBoolean){
                   ToastUtil.show("领取成功");
                   couponBean.setUse(true);
                   mGetCouponAdapter.notifyItemChanged(position);
               }
           }
       });

    }
}

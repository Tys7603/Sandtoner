package com.wanyue.shop.view.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.server.RequestFactory;
import com.wanyue.common.utils.DebugUtil;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.adapter.ChooseCouponAdapter;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.ChooseCouponBean;
import com.wanyue.shop.bean.CouponBean;
import com.wanyue.shop.bean.ShopCartStoreBean;
import java.util.List;
import io.reactivex.Observable;

public class ChooseCouponPopView extends BaseBottomPopView implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {

    private ImageView mBtnClose;
    private RxRefreshView<ChooseCouponBean> mRefreshView;
    private ChooseCouponAdapter mChooseCouponAdapter;
    private ShopCartStoreBean mShopCartStoreBean;
    private String mSelectId;
    private OnChooseListner mOnChooseListner;

    public ChooseCouponPopView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_coupon_choose;
    }

    @Override
    protected void init() {
        super.init();
        mBtnClose =  findViewById(R.id.btn_close);
        mRefreshView = findViewById(R.id.refreshView);
        mBtnClose.setOnClickListener(this);
        mChooseCouponAdapter=new ChooseCouponAdapter(null);
        mChooseCouponAdapter.setOnItemClickListener(this);
        mRefreshView.setAdapter(mChooseCouponAdapter);
        mRefreshView.setIconId(R.drawable.icon_empty_no_coupon);

        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(getContext(),10));
        mRefreshView.setDataListner(new RxRefreshView.DataListner<ChooseCouponBean>() {
            @Override
            public Observable<List<ChooseCouponBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<ChooseCouponBean> data) {

            }
            @Override
            public void before(List<ChooseCouponBean> data) {
               if(ListUtil.haveData(data)){
                   for(ChooseCouponBean temp:data){
                       if(StringUtil.equals(temp.getCid(),mSelectId)){
                           temp.setSelect(true);
                           break;
                       }
                   }
               }/*else{
                   DebugUtil.sendException("data不能为null");
               }*/
            }
            @Override
            public void error(Throwable e) {
            }
        });
        if(mRefreshView!=null){
            mRefreshView.initData();
        }
        findViewById(R.id.btn_no_use).setOnClickListener(this);
    }

    private Observable<List<ChooseCouponBean>> getData(int p) {
        String id=mShopCartStoreBean==null?null:mShopCartStoreBean.getId();
        String cartId=mShopCartStoreBean==null?null:mShopCartStoreBean.getCartIds();
        double totalPrice=0;
        if(mShopCartStoreBean!=null&&mShopCartStoreBean.getPriceGroup()!=null){
            totalPrice=mShopCartStoreBean.getPriceGroup().getTotalPrice();
        }
        return ShopAPI.getOrderCouponList(totalPrice,cartId,id);
    }

    public void setShopCartStoreBean(ShopCartStoreBean shopCartStoreBean) {
        mShopCartStoreBean = shopCartStoreBean;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_close){
            dismiss();
        }else if(id==R.id.btn_no_use){
            clearChoose();
        }
    }

    private void clearChoose() {
        if(mChooseCouponAdapter!=null){
           mChooseCouponAdapter.clearSelect();
        }
        dismiss();
    }

    public void setSelectId(String selectId) {
        mSelectId = selectId;
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        if(mOnChooseListner!=null&&mChooseCouponAdapter!=null){
           mOnChooseListner.choose(mChooseCouponAdapter.getSelectCouponBean());
        }
        RequestFactory.getRequestManager().cancle(ShopAPI.ORDER_COUPON_LIST);
    }

    public void setOnChooseListner(OnChooseListner onChooseListner) {
        mOnChooseListner = onChooseListner;
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if(mChooseCouponAdapter==null){
            return;
        }
        mChooseCouponAdapter.select(position);
    }

    public interface OnChooseListner{
        public void choose(CouponBean couponBean);
    }
}

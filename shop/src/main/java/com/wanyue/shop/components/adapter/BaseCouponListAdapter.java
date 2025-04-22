package com.wanyue.shop.components.adapter;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.bean.CouponBean;
import java.util.List;

public abstract class BaseCouponListAdapter<T extends CouponBean> extends BaseRecyclerAdapter<T, BaseReclyViewHolder> {
    private boolean mCloseToStore;

    public BaseCouponListAdapter(List<T> data) {
        super(data);
    }
    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, T item) {
        helper.setText(R.id.tv_coupon_price, StringUtil.getPrice(item.getCouponPrice()));
        String minPrice=StringUtil.getPrice(item.getMinPrice());
        helper.setText(R.id.tv_user_price, WordUtil.getString(R.string.user_price_min,minPrice));
        helper.setText(R.id.tv_coupon_name,item.getTitle());
        String endTime=item.getEndTime();
        helper.setText(R.id.tv_end_time, endTime);

        TextView tvCouponType= helper.getView(R.id.tv_coupon_type);
        if(tvCouponType==null){

        }else if(StringUtil.equals(item.getType(),"0")){
            tvCouponType.setText("通用卷");
        }else if(StringUtil.equals(item.getType(),"1")){
            tvCouponType.setText("品类券");
        }else if(StringUtil.equals(item.getType(),"2")){
            tvCouponType.setText("商品卷");
        }

        TextView tvStoreSelf=helper.getView(R.id.tv_store_self);
        View imgArrow=helper.getView(R.id.img_arrow);
        if(item.getShoptype()==2){
            ViewUtil.setVisibility(tvStoreSelf, View.VISIBLE);

        }else{
            ViewUtil.setVisibility(tvStoreSelf, View.GONE);
        }

        TextView tvStoreTitle=helper.getView(R.id.tv_store_title);
        if(tvStoreTitle!=null){
           helper.setText(R.id.tv_store_title, item.getShopName());
           if(!mCloseToStore){
              helper.addOnClickListener(R.id.tv_store_title);
           }else{
              ViewUtil.setVisibility(imgArrow,View.INVISIBLE);
           }
        }
           helper.addOnClickListener(R.id.btn_coupon);
    }


    public void setCloseToStore(boolean closeToStore) {
        mCloseToStore = closeToStore;
    }
}

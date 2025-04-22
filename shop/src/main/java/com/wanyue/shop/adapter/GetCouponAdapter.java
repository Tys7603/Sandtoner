package com.wanyue.shop.adapter;

import android.view.View;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;

import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.utils.ResourceUtil;

import com.wanyue.shop.R;
import com.wanyue.shop.bean.CouponBean;
import com.wanyue.shop.components.adapter.BaseCouponListAdapter;

import java.util.List;

/**
 * The type Get coupon adapter.
 */
public class GetCouponAdapter extends BaseCouponListAdapter<CouponBean> {
    /**
     * Instantiates a new Get coupon adapter.
     *
     * @param data the data
     */
    public GetCouponAdapter(List<CouponBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_relcy_get_coupon;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, CouponBean item) {
        super.convert(helper,item);
        View bgView= helper.getView(R.id.img_head_bg);
        CheckedTextView btnGetCoupon= helper.getView(R.id.btn_coupon);
        boolean isGet=item.isUse();
        btnGetCoupon.setChecked(isGet);
        helper.setImageResouceId(R.drawable.bg_coupon_vaild,R.id.img_head_bg);
        if(isGet){
            btnGetCoupon.setText("已领取");
        }else{
            btnGetCoupon.setText("立即领取");
        }

    }
}

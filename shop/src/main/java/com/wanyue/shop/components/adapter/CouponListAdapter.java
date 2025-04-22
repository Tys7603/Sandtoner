package com.wanyue.shop.components.adapter;

import android.view.View;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.bean.CouponBean;
import java.util.List;

public class CouponListAdapter extends BaseCouponListAdapter<CouponBean> {
    public CouponListAdapter(List<CouponBean> data) {
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

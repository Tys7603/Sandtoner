package com.wanyue.shop.adapter;

import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.bean.MyCouponBean;
import com.wanyue.shop.components.adapter.BaseCouponListAdapter;
import java.util.List;

/**
 * The type My overdue coupon adapter.
 */
public class MyOverdueCouponAdapter extends BaseCouponListAdapter<MyCouponBean> {

    /**
     * Instantiates a new My overdue coupon adapter.
     *
     * @param data the data
     */
    public MyOverdueCouponAdapter(List<MyCouponBean> data) {
        super(data);
    }


    protected void convert(@NonNull BaseReclyViewHolder helper, MyCouponBean item){
        super.convert(helper, item);
        CheckedTextView btnGetCoupon= helper.getView(R.id.btn_coupon);
        btnGetCoupon.setChecked(true);
        btnGetCoupon.setText("删除");
        helper.setImageResouceId(R.drawable.bg_coupon_invaild,R.id.img_head_bg);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_my_invalid_coupon;
    }
}

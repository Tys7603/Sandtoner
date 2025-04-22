package com.wanyue.shop.adapter;

import android.view.View;
import android.widget.CheckedTextView;
import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.bean.MyCouponBean;
import com.wanyue.shop.components.adapter.BaseCouponListAdapter;
import java.util.List;

public class MyCouponAdapter extends BaseCouponListAdapter<MyCouponBean> {

    public MyCouponAdapter(List<MyCouponBean> data) {
        super(data);
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, MyCouponBean item) {
        super.convert(helper, item);
        boolean isGet=item.isUse();
        CheckedTextView btnGetCoupon= helper.getView(R.id.btn_coupon);
        View bgView= helper.getView(R.id.img_head_bg);

        btnGetCoupon.setChecked(isGet);
        btnGetCoupon.setText("立即使用");

        helper.setImageResouceId(R.drawable.bg_coupon_vaild,R.id.img_head_bg);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_my_coupon;
    }
}

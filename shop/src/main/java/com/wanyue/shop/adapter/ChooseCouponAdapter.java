package com.wanyue.shop.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.custom.CheckImageView;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.bean.ChooseCouponBean;
import com.wanyue.shop.components.adapter.BaseCouponListAdapter;
import java.util.List;

public class ChooseCouponAdapter extends BaseCouponListAdapter<ChooseCouponBean> {
    private ChooseCouponBean mSelectCouponBean;

    public ChooseCouponAdapter(List<ChooseCouponBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.ite_recly_user_coupon;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, ChooseCouponBean item) {
        super.convert(helper,item);
        int position=helper.getLayoutPosition();
        item.setPosition(position);
        CheckImageView imgSelectCoupon= helper.getView(R.id.img_select_coupon);
        helper.setImageResouceId(R.drawable.bg_coupon_head,R.id.img_head_bg);




       boolean isSelect=item.isSelect();
        if(isSelect){
           mSelectCouponBean=item;
        }
        imgSelectCoupon.setChecked(isSelect);
    }

    public void select(int position) {
        ChooseCouponBean chooseCouponBean=getItem(position);
        if(mSelectCouponBean==null){
           mSelectCouponBean=chooseCouponBean;
           chooseCouponBean.setSelect(true);
            notifyItemChanged(chooseCouponBean.getPosition());
        }else if(mSelectCouponBean!=chooseCouponBean){
            mSelectCouponBean.setSelect(false);
            notifyItemChanged(mSelectCouponBean.getPosition());
            chooseCouponBean.setSelect(true);
            notifyItemChanged(chooseCouponBean.getPosition());
            mSelectCouponBean=chooseCouponBean;
        }
    }

    public ChooseCouponBean getSelectCouponBean() {
        return mSelectCouponBean;
    }

    public void clearSelect() {
        if(mSelectCouponBean!=null){
           mSelectCouponBean.setSelect(false);
           notifyItemChanged(mSelectCouponBean.getPosition());
        }
        mSelectCouponBean=null;
    }
}

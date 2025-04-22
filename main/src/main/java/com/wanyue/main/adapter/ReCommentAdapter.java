package com.wanyue.main.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseViewHolder;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.main.R;
import com.wanyue.main.bean.ReCommentBean;

import java.util.List;

public class ReCommentAdapter extends BaseRecyclerAdapter<ReCommentBean, BaseReclyViewHolder> {
    public final static int SIGN=99;//积分签到
    public final static int COUPON=100;//优惠券
    public final static int FOOT=101;//足迹
    public final static int SPREAD=102;//推广
    public final static int ORDER=158;//我的订单
    public final static int COLECT=105;//我的收藏
    public final static int GREAT_SELECT=159;//今日优选
    public final static int SELECTED_WANGPU=160;//精选旺铺
    public final static int HELP=190;//精选旺铺
    public final static int CLASSFIY=192;//精选旺铺


    public ReCommentAdapter(List<ReCommentBean> data) {
        super(data);
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, ReCommentBean item) {
        helper.setImageUrl(item.getThumb(),R.id.img_thumb);
        helper.setText(R.id.text,item.getName());
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_main_recommend;
    }


}

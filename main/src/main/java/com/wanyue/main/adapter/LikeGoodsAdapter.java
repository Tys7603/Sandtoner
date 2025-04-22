package com.wanyue.main.adapter;

import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.main.R;
import java.util.List;

public class LikeGoodsAdapter extends BaseRecyclerAdapter<GoodsBean, BaseReclyViewHolder> {

    public LikeGoodsAdapter(List<GoodsBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_like_goods;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, GoodsBean item) {
        helper.setText(com.wanyue.shop.R.id.tv_title,item.getName());
        helper.setText(com.wanyue.shop.R.id.tv_price,item.getUnitPrice());
        helper.setImageUrl(item.getThumb(), com.wanyue.shop.R.id.img_goods_cover);
        helper.setText(R.id.tv_sale, WordUtil.getString(R.string.saled_num,item.getSales()));

    }
}

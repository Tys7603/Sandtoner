package com.wanyue.shop.adapter;

import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.shop.R;
import java.util.List;

public class HotGoodsAdapter extends BaseRecyclerAdapter<GoodsBean, BaseReclyViewHolder> {
    public HotGoodsAdapter(List<GoodsBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_hot_goods;
    }


    @Override
    protected void convert(BaseReclyViewHolder helper, GoodsBean item) {
        helper.setText(R.id.tv_title,item.getName());
        helper.setText(R.id.tv_price,item.getUnitPrice());
        helper.setImageUrl(item.getThumb(),R.id.img_goods_cover);
    }
}

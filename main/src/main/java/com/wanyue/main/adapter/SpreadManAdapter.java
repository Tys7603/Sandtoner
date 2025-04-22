package com.wanyue.main.adapter;

import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.main.R;
import com.wanyue.main.bean.SpreadManBean;
import java.util.List;

public class SpreadManAdapter extends BaseRecyclerAdapter<SpreadManBean, BaseReclyViewHolder> {
    public SpreadManAdapter(List<SpreadManBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_spread_man;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, SpreadManBean item) {
        helper.setImageUrl(item.getAvatar(),R.id.img_avator);
        helper.setText(R.id.tv_name,item.getNickname());
        helper.setText(R.id.tv_add_time, WordUtil.getString(R.string.add_time_tip,item.getTime()));
        helper.setText(R.id.tv_result,item.getResult());

    }


}

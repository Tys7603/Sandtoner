package com.wanyue.main.adapter;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.main.R;
import com.wanyue.main.bean.SpreadManRankBean;
import java.util.List;

public class SpreadManRankAdapter extends BaseRecyclerAdapter<SpreadManRankBean, BaseReclyViewHolder> {
    public SpreadManRankAdapter(List<SpreadManRankBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_spread_man_rank;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, SpreadManRankBean item) {
        int position=helper.getLayoutPosition()+3;
        helper.setImageUrl(item.getAvatar(),R.id.img_avator);
        helper.setText(R.id.tv_name,item.getNickname());
        helper.setText(R.id.tv_count,item.getCount()+"Agents");
        helper.setText(R.id.tv_rank_num,Integer.toString(position));
        ImageView imageView=helper.getView(R.id.img_rank_num);
        TextView tvRankNum=helper.getView(R.id.tv_rank_num);
        tvRankNum.setText(Integer.toString(position));
    }
}

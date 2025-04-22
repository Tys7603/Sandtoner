package com.wanyue.shop.adapter;

import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.shop.R;
import com.wanyue.video.bean.VideoBean;
import java.util.List;

/**
 * The type Goods video recommend adapter.
 */
public class GoodsVideoRecommendAdapter extends BaseRecyclerAdapter<VideoBean, BaseReclyViewHolder> {

    /**
     * Instantiates a new Goods video recommend adapter.
     *
     * @param data the data
     */
    public GoodsVideoRecommendAdapter(List<VideoBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_goods_video_recommend;
    }


    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, VideoBean videoBean) {
        if(videoBean!=null){
            helper.setText(R.id.tv_video_title,videoBean.getTitle());
            helper.setImageUrl(videoBean.getThumb(),R.id.img_avator);
        }
    }
}

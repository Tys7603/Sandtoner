package com.wanyue.main.adapter;

import androidx.annotation.NonNull;

import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.main.R;
import com.wanyue.video.bean.VideoBean;
import java.util.List;

public class HomePageVIdeoAdapter extends BaseRecyclerAdapter<VideoBean, BaseReclyViewHolder> {

    public HomePageVIdeoAdapter(List<VideoBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_relcy_home_page_video;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder heler, VideoBean videoBean) {
        heler.setImageUrl(videoBean.getThumb(),R.id.img_thumb);
        heler.setText(R.id.tv_play_num,videoBean.getViewNum());
    }
}

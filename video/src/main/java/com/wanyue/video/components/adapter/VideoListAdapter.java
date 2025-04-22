package com.wanyue.video.components.adapter;

import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.video.R;
import com.wanyue.video.bean.VideoBean;
import java.util.List;

public class VideoListAdapter extends BaseRecyclerAdapter<VideoBean, BaseReclyViewHolder> {

    public VideoListAdapter(List<VideoBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_relcy_video_list;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, VideoBean videoBean) {
        helper.setText(R.id.tv_video_title,videoBean.getTitle());
        helper.setImageUrl(videoBean.getThumb(),R.id.img_thumb);
        helper.setImageUrl(videoBean.getAvatar(),R.id.img_avatar);
        helper.setText(R.id.tv_nick_name,videoBean.getNickname());
        helper.setText(R.id.tv_video_play,videoBean.getViewNum()+"");

    }
}

package com.wanyue.main.find.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.main.R;
import com.wanyue.video.bean.VideoBean;
import java.util.List;

public class FindVideoAdapter extends BaseRecyclerAdapter<VideoBean, BaseReclyViewHolder> {

    public FindVideoAdapter(List<VideoBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_find_video;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, VideoBean videoBean) {
        ViewGroup vpAvatar=helper.getView(R.id.vp_avatar);
        ViewUtil.setViewOutlineProvider(vpAvatar, DpUtil.dp2px(5));
        if(videoBean!=null){
            helper.setText(R.id.tv_video_title,videoBean.getTitle());
            helper.setImageUrl(videoBean.getThumb(),R.id.img_avator);
        }
        GoodsBean goodsBean= ListUtil.safeGetData(videoBean.getGoods(),0);
        ViewGroup viewGroup=helper.getView(R.id.vp_goods_container);
        if(goodsBean!=null){
            ViewUtil.setVisibility(viewGroup, View.VISIBLE);
            FindGoodsAdapter.convertGoods(helper,goodsBean);
        }else{
            ViewUtil.setVisibility(viewGroup, View.GONE);
        }
        helper.setOnChildClickListner(R.id.vp_goods_container,mOnClickListener);
    }
}

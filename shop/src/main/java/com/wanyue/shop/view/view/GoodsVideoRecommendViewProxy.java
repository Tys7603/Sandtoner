package com.wanyue.shop.view.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.shop.R;
import com.wanyue.shop.adapter.GoodsVideoRecommendAdapter;
import com.wanyue.shop.view.activty.GoodsVideoActivity;
import com.wanyue.video.activity.VideoPlayActivity;
import com.wanyue.video.bean.VideoBean;
import com.wanyue.video.utils.VideoStorge;
import java.util.List;

public class GoodsVideoRecommendViewProxy extends RxViewProxy implements View.OnClickListener {

    private TextView mBtnLookAll;
    private RecyclerView mReclyView;
    private GoodsVideoRecommendAdapter mGoodVideoAdapter;

    private List<VideoBean>mVideoList;
    private String mGoodsId;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mBtnLookAll = findViewById(R.id.btn_look_all);
        mReclyView =findViewById(R.id.reclyView);
        mBtnLookAll.setOnClickListener(this);

        mGoodVideoAdapter=new GoodsVideoRecommendAdapter(mVideoList);
        mGoodVideoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                VideoStorge.getInstance().put(getKey(),mVideoList);
                VideoPlayActivity.forward(getActivity(),i,getKey(),1);
            }
        });
        mReclyView.setAdapter(mGoodVideoAdapter);

        RxRefreshView.ReclyViewSetting.createGridSetting(getActivity(),3)
                .settingRecyclerView(mReclyView);
    }

    private String getKey() {
        return "goodsid="+mGoodsId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_goods_video_recommend;
    }

    public void setVideoList(List<VideoBean> videoList) {
        mVideoList = videoList;
    }

    public String getGoodsId() {
        return mGoodsId;
    }
    public void setGoodsId(String goodsId) {
        mGoodsId = goodsId;
    }

    @Override
    public void onClick(View v) {
        GoodsVideoActivity.forward(getActivity(),mGoodsId);
    }
}

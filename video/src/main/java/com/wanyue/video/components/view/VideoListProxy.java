package com.wanyue.video.components.view;

import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.proxy.SingleRefreshViewProxy;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.video.R;
import com.wanyue.video.activity.VideoPlayActivity;
import com.wanyue.video.bean.VideoBean;
import com.wanyue.video.components.adapter.VideoListAdapter;
import com.wanyue.video.utils.VideoStorge;
import java.util.List;
import io.reactivex.Observable;

public abstract class VideoListProxy extends SingleRefreshViewProxy<VideoBean> implements BaseQuickAdapter.OnItemClickListener {
    private VideoListAdapter mVideoListAdapter;
    private Integer mLevel;
    private String mNodata;
    private int mIconid;
    @Override

    protected void initView(ViewGroup contentView) {
        mVideoListAdapter= new VideoListAdapter(null);
        setPadding(DpUtil.dp2px(10));
        setAdapter(mVideoListAdapter);
        super.initView(contentView);
        if(mLevel==null){
           mRefreshView.setEmptyLevel(1);
           mRefreshView.setIconId(R.mipmap.icon_default_no_data);
           mRefreshView.setNoDataTip("暂无视频");
        }else{
            mRefreshView.setEmptyLevel(mLevel);
            mRefreshView.setIconId(mIconid);
            mRefreshView.setNoDataTip(mNodata);
        }
        mVideoListAdapter.setOnItemClickListener(this);
    }



    public void setEmptyData(int level,String nodataTip,int iconId){
        mLevel=level;
        mNodata=nodataTip;
        mIconid=iconId;
    }

    @Override
    public void compeleteData(List<VideoBean> data) {
        super.compeleteData(data);
        VideoStorge.getInstance().put(getKey(),data);
    }

    public RxRefreshView.ReclyViewSetting getReclyViewSetting() {
       return RxRefreshView.ReclyViewSetting.createGridSetting(getActivity(),2,5);
    }

    @Override
    public abstract Observable<List<VideoBean>> getData(int p);
    public abstract String getKey();

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        VideoPlayActivity.forward(getActivity(),i,getKey(),1);
    }

}

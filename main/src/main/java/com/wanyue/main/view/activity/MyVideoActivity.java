package com.wanyue.main.view.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.main.R;
import com.wanyue.video.activity.VideoRecordActivity;
import com.wanyue.video.api.VideoAPI;
import com.wanyue.video.bean.VideoBean;
import com.wanyue.video.components.view.VideoListProxy;
import java.util.List;
import io.reactivex.Observable;

public class MyVideoActivity extends BaseActivity implements View.OnClickListener {
    private ViewGroup mContainer;
    private TextView mTvRightTitle;


    @Override
    public void init() {
        setTabTitle("My Video");
        mTvRightTitle = (TextView) findViewById(R.id.tv_right_title);
        mTvRightTitle.setText("Release");
        mTvRightTitle.setOnClickListener(this);

        mContainer =  findViewById(R.id.container);
        VideoListProxy videoListProxy=new VideoListProxy() {
            @Override
            public Observable<List<VideoBean>> getData(int p) {
                return VideoAPI.getMyVideoList(p);
            }
            @Override
            public String getKey() {
                return "videolist";
            }
        };

        videoListProxy.setEmptyData(0,"There is no work yet. Hurry up and publish your first work.",R.mipmap.icon_no_data_big);
        getViewProxyMannger().addViewProxy(mContainer,videoListProxy,videoListProxy.getDefaultTag());
        videoListProxy.initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_video;
    }

    @Override
    public void onClick(View v) {
        startActivity(VideoRecordActivity.class);
    }
}
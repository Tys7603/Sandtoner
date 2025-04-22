package com.wanyue.video.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.lifecycle.Observer;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wanyue.common.Constants;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.video.R;
import com.wanyue.video.bean.VideoBean;
import com.wanyue.video.utils.VideoStorge;
import com.wanyue.video.views.VideoScrollViewHolder;
import java.util.ArrayList;
import java.util.List;

public class VideoPlayActivity extends AbsVideoPlayActivity  {

    public static void forward(Context context, int position, String videoKey, int page) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(Constants.VIDEO_POSITION, position);
        intent.putExtra(Constants.VIDEO_KEY, videoKey);
        intent.putExtra(Constants.VIDEO_PAGE, page);
        context.startActivity(intent);
    }

    public static void forwardSingle(Context context, VideoBean videoBean) {
        if (videoBean == null) {
            return;
        }
        List<VideoBean> list = new ArrayList<>();
        list.add(videoBean);
        VideoStorge.getInstance().put(Constants.VIDEO_SINGLE, list);
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(Constants.VIDEO_POSITION, 0);
        intent.putExtra(Constants.VIDEO_KEY, Constants.VIDEO_SINGLE);
        intent.putExtra(Constants.VIDEO_PAGE, 1);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_play;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    @Override
    protected void main() {
        super.main();
        Intent intent = getIntent();
        mVideoKey = intent.getStringExtra(Constants.VIDEO_KEY);
        if (TextUtils.isEmpty(mVideoKey)) {
            return;
        }
        int position = intent.getIntExtra(Constants.VIDEO_POSITION, 0);
        int page = intent.getIntExtra(Constants.VIDEO_PAGE, 1);
        mVideoScrollViewHolder = new VideoScrollViewHolder(mContext, (ViewGroup) findViewById(R.id.container), position, mVideoKey, page);
        mVideoScrollViewHolder.addToParent();
        mVideoScrollViewHolder.subscribeActivityLifeCycle();

        LiveEventBus.get("video_isattent",VideoBean.class).observe(this, new Observer<VideoBean>() {
            @Override
            public void onChanged(VideoBean videoBean) {
              String uid=  videoBean.getUid();
              int attent=videoBean.getAttent();
              List<VideoBean>list=VideoStorge.getInstance().get(mVideoKey);
              int size= ListUtil.size(list);
              for(int i=0;i<size;i++){
                  VideoBean item=list.get(i);
                  if(StringUtil.equals(item.getUid(),uid)){
                     item.setAttent(attent);
                  }
              }
            }
        });

    }

    @Override
    public void onBackPressed() {
        release();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
        L.e("VideoPlayActivity------->onDestroy");
    }


}

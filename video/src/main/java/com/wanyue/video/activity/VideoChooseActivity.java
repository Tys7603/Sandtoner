package com.wanyue.video.activity;

import android.content.Intent;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.wanyue.common.Constants;
import com.wanyue.common.activity.AbsActivity;
import com.wanyue.video.R;
import com.wanyue.video.adapter.VideoChooseAdapter;
import com.wanyue.video.bean.VideoChooseBean;
import com.wanyue.common.custom.ItemDecoration;
import com.wanyue.common.interfaces.CommonCallback;
import com.wanyue.common.interfaces.OnItemClickListener;
import com.wanyue.video.utils.VideoLocalUtil;
import com.wanyue.common.utils.WordUtil;

import java.util.List;

/**
 * Created by cxf on 2018/12/10.
 * 选择本地视频
 */

public class VideoChooseActivity extends AbsActivity implements OnItemClickListener<VideoChooseBean> {

    private long mMaxDuration;
    private RecyclerView mRecyclerView;
    private View mNoData;
    private VideoLocalUtil mVideoLocalUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_choose;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.video_local));
        mMaxDuration = getIntent().getLongExtra(Constants.VIDEO_DURATION, 15000);
        mNoData = findViewById(R.id.no_data);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        ItemDecoration decoration = new ItemDecoration(mContext, 0x00000000, 1, 1);
        decoration.setOnlySetItemOffsetsButNoDraw(true);
        mRecyclerView.addItemDecoration(decoration);
        mVideoLocalUtil = new VideoLocalUtil();
        mVideoLocalUtil.getLocalVideoList(new CommonCallback<List<VideoChooseBean>>() {
            @Override
            public void callback(List<VideoChooseBean> videoList) {
                if (videoList == null || videoList.size() == 0) {
                    if (mNoData != null && mNoData.getVisibility() != View.VISIBLE) {
                        mNoData.setVisibility(View.VISIBLE);
                    }
                    return;
                }
                if (mRecyclerView != null) {
                    VideoChooseAdapter adapter = new VideoChooseAdapter(mContext, videoList);
                    adapter.setOnItemClickListener(VideoChooseActivity.this);
                    mRecyclerView.setAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onItemClick(VideoChooseBean bean, int position) {

        Intent intent = new Intent();
        intent.putExtra(Constants.VIDEO_PATH, bean.getVideoPath());
        intent.putExtra(Constants.VIDEO_DURATION, bean.getDuration());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mVideoLocalUtil != null) {
            mVideoLocalUtil.release();
        }
        mVideoLocalUtil = null;
        mRecyclerView = null;
        mNoData = null;
        super.onDestroy();
    }
}

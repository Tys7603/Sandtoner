package com.wanyue.video.activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.bean.CatBean;
import com.wanyue.common.interfaces.OnItemClickListener;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.video.R;
import com.wanyue.video.adapter.VideoClassifyAdapter;
import com.wanyue.video.api.VideoAPI;
import java.util.List;

public class VideoClassifyActivity extends BaseActivity implements OnItemClickListener<CatBean> {


    private RecyclerView mRecyclerView;
    private String mCheckId;


    @Override
    public void init() {
        setTabTitle("Video Categories");
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mCheckId = getIntent().getStringExtra(Constants.CLASS_ID);
        getVideoCLassify();
    }

    private void getVideoCLassify() {
        VideoAPI.getCat(0).compose(this.<List<CatBean>>bindToLifecycle()).subscribe(new DefaultObserver<List<CatBean>>() {
            @Override
            public void onNext(@NonNull List<CatBean> list) {
                int size= ListUtil.size(list);
                for(int i=0;i<size;i++){
                    CatBean bean = list.get(i);
                    if (StringUtil.equals(bean.getId(),mCheckId)) {
                        bean.setChecked(true);
                    } else {
                        bean.setChecked(false);
                    }
                }
                VideoClassifyAdapter adapter = new VideoClassifyAdapter(mContext, list);
                adapter.setOnItemClickListener(VideoClassifyActivity.this);
                mRecyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_classify;
    }

    @Override
    public void onItemClick(CatBean bean, int position) {
        Intent intent = new Intent();
        intent.putExtra(Constants.CLASS_ID, bean.getId());
        intent.putExtra(Constants.CLASS_NAME, bean.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
}
package com.wanyue.main.view.proxy;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.Constants;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.common.utils.SpannableStringUtils;
import com.wanyue.course.bean.CourseBean;
import com.wanyue.course.view.activity.CourseDetailActivity;
import com.wanyue.course.view.activity.CourseListActivity;
import com.wanyue.course.view.proxy.ins.CourseIntroduceViewProxy;
import com.wanyue.live.bean.LiveBean;
import com.wanyue.main.R;
import com.wanyue.main.R2;
import com.wanyue.main.adapter.HomePageCourseAdapter;
import com.wanyue.main.adapter.HomePageVIdeoAdapter;
import com.wanyue.main.bean.HomePageBean;
import com.wanyue.main.find.view.activity.LiveListActivity;
import com.wanyue.video.activity.VideoListActivity;
import com.wanyue.video.activity.VideoPlayActivity;
import com.wanyue.video.components.view.VideoListProxy;
import com.wanyue.video.utils.VideoStorge;

import java.util.Arrays;
import java.util.List;
import butterknife.OnClick;

public class RecommendViewProxy  extends RxViewProxy {
    private TextView mTvLiveTitle;
    private TextView mTvVideoTitle;
    private RecyclerView mRcVideo;
    private TextView mTvCourseTitle;
    private RecyclerView mRcCourse;
    private BannerForAduioProxy mBannerForAduioProxy;
    private FrameLayout mVpLiveContainer;

    private  HomePageVIdeoAdapter mHomePageVIdeoAdapter;
    private  HomePageCourseAdapter mHomePageCourseAdapter;


    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mTvLiveTitle = (TextView) findViewById(R.id.tv_live_title);
        mTvVideoTitle = (TextView) findViewById(R.id.tv_video_title);
        mRcVideo = (RecyclerView) findViewById(R.id.rc_tv_video_title);
        mTvCourseTitle = (TextView) findViewById(R.id.tv_course_title);
        mRcCourse = (RecyclerView) findViewById(R.id.rc_course);
        mVpLiveContainer = (FrameLayout) findViewById(R.id.vp_live_container);

        CharSequence text1=SpannableStringUtils.getBuilder("推荐").append("直播 ").setForegroundColor(ResourceUtil.getColor(R.color.global)).create();
        mTvLiveTitle.setText(text1);

        CharSequence text2=SpannableStringUtils.getBuilder("精彩").append("视频 ").setForegroundColor(ResourceUtil.getColor(R.color.global)).create();
        mTvVideoTitle.setText(text2);

        CharSequence text3=SpannableStringUtils.getBuilder("精选").append("内容 ").setForegroundColor(ResourceUtil.getColor(R.color.global)).create();
        mTvCourseTitle.setText(text3);


        mHomePageVIdeoAdapter=new HomePageVIdeoAdapter(null);
        RxRefreshView.ReclyViewSetting.createHorizonSetting(getActivity(),5).settingRecyclerView(mRcVideo);
        mRcVideo.setAdapter(mHomePageVIdeoAdapter);
        mHomePageVIdeoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                VideoStorge.getInstance().put("home_index", mHomePageVIdeoAdapter.getArray());
                VideoPlayActivity.forward(getActivity(),i,"home_index",1);
            }
        });


        mHomePageCourseAdapter=new HomePageCourseAdapter(null);
        mHomePageCourseAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                CourseBean courseBean=mHomePageCourseAdapter.getItem(i);
                Intent intent=new Intent();
                intent.setClass(getActivity(), CourseDetailActivity.class);
                intent.putExtra(Constants.DATA,courseBean);
                getActivity().startActivity(intent);
            }
        });
        RxRefreshView.ReclyViewSetting.createHorizonSetting(getActivity(),5).settingRecyclerView(mRcCourse);
        mRcCourse.setAdapter(mHomePageCourseAdapter);
    }
    public void setData(HomePageBean homePageBean){
        initRecommendLive(homePageBean.getRecommendLiveList());

        if(mHomePageCourseAdapter!=null){
           mHomePageCourseAdapter.setData(homePageBean.getCourseBeanList());
        }
        if(mHomePageVIdeoAdapter!=null){
           mHomePageVIdeoAdapter.setData(homePageBean.getRecommendVideoList());
        }
    }


    //推荐直播哦
    private void initRecommendLive( List<LiveBean> recommendList){
        if (!ListUtil.haveData(recommendList)) {
            return;
        }
        if (mBannerForAduioProxy==null) {
            mBannerForAduioProxy = new BannerForAduioProxy();
            mBannerForAduioProxy.setData(recommendList);
            getViewProxyChildMannger().addViewProxy(mVpLiveContainer, mBannerForAduioProxy, mBannerForAduioProxy.getDefaultTag());
        }else {
            mBannerForAduioProxy.update(recommendList);
        }
    }

    @OnClick(R2.id.btn_course)
    public void toCourse(){
        startActivity(CourseListActivity.class);
    }

    @OnClick(R2.id.btn_to_video)
    public void toVideo(){
        startActivity(VideoListActivity.class);
    }

    @OnClick(R2.id.btn_to_live)
    public void toLive(){
        startActivity(LiveListActivity.class);
    }



    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_recommend_live_and_other;
    }
}

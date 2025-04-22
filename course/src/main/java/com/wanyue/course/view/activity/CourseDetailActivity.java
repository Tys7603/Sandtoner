package com.wanyue.course.view.activity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.wanyue.common.Constants;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.proxy.BaseViewProxy;
import com.wanyue.course.R;
import com.wanyue.course.bean.CourseBean;
import com.wanyue.course.evaluate.view.proxy.EvaluateListViewProxy;
import com.wanyue.course.view.proxy.CatalogueViewProxy;
import com.wanyue.course.view.proxy.ins.BaseInsViewProxy;
import com.wanyue.course.view.proxy.ins.CourseIntroduceViewProxy;
import java.util.List;

public class CourseDetailActivity extends BaseInsDetailActivity<CourseBean>implements BaseInsViewProxy.RefreshListner<CourseBean>  {

    private ImageView mImgThumb;
    private EvaluateListViewProxy mEvaluateListViewProxy;
    private CatalogueViewProxy mCatalogueViewProxy;
    private CourseIntroduceViewProxy mLiveIntroduceViewProxy;

    @Override
    public void init() {
        super.init();
        setTabTitle("内容详情");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_course_detail;
    }

    @Override
    protected void setDataToUI(CourseBean projectBean) {
        super.setDataToUI(projectBean);
        initThumb();
        ImgLoader.display(this, projectBean.getThumb(), mImgThumb);
    }

    private void initThumb() {
        if(mImgThumb==null){
            mImgThumb=new ImageView(this);
            mImgThumb.setTransitionName("image");
            FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            mImgThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mVpTopContainer.addView(mImgThumb,layoutParams);
        }
    }


    @Override
    public void refreshData() {
        super.refreshData();
        if(mLiveIntroduceViewProxy!=null){
            mLiveIntroduceViewProxy.getData();
        }
    }

    @Override
    public void loadViewProxy(List<BaseViewProxy> viewProxiyList) {
        mLiveIntroduceViewProxy=new CourseIntroduceViewProxy();
        mEvaluateListViewProxy=new EvaluateListViewProxy();
        if(mProjectBean!=null){
            mLiveIntroduceViewProxy.putProjectId(mProjectBean.getId());
            mLiveIntroduceViewProxy.setRefreshListner(this);
            mEvaluateListViewProxy.putArgs(Constants.KEY_ID,mProjectBean.getId());
        }
        mCatalogueViewProxy=new CatalogueViewProxy();
        viewProxiyList.add(mLiveIntroduceViewProxy);
        viewProxiyList.add(mCatalogueViewProxy);
        viewProxiyList.add(mEvaluateListViewProxy);
    }

    @Override
    public String[] getTitleArray() {
        String[] titleArray = {getString(R.string.introduction), getString(R.string.catalog), getString(R.string.evaluation)};
        return titleArray;
    }

    @Override
    public boolean checkWatchPermisson() {
        boolean isBuy= super.checkWatchPermisson();
        if(isBuy){
            mVpBottom.setVisibility(View.GONE);
        }
        super.checkWatchPermisson();
        return isBuy;
    }

    @Override
    public void refreshData(CourseBean data) {
        ImgLoader.display(this, data.getThumb(), mImgThumb);
        if(mEvaluateListViewProxy!=null){
            mEvaluateListViewProxy.setTotalData(data.getStar());
            mEvaluateListViewProxy.setProjectBean(data);
        }
        if(mCatalogueViewProxy!=null){
            mCatalogueViewProxy.setCourseBean(data);
        }
        mProjectBean=data;
        checkWatchPermisson();
    }
}
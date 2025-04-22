package com.wanyue.course.view.proxy.ins;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.wanyue.common.Constants;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.course.R;
import com.wanyue.course.R2;
import com.wanyue.course.api.CourseAPI;
import com.wanyue.course.bean.ProjectBean;
import com.wanyue.course.busniess.UIFactory;
import butterknife.BindView;
import butterknife.OnClick;

public abstract class BaseInsViewProxy<T extends ProjectBean> extends RxViewProxy {
    @BindView(R2.id.tv_title)
    protected TextView mTvTitle;
    @BindView(R2.id.tv_price)
    protected TextView mTvPrice;
    @BindView(R2.id.tv_study_count)
    protected TextView mTvStudyCount;
    @BindView(R2.id.tv_project_introduce)
    protected TextView mTvProjectIntroduce;
    @BindView(R2.id.tv_center_tag)
    protected TextView mTvCenterTag;
    @BindView(R2.id.tv_first_title)
    protected TextView mTvFirstTitle;
    @BindView(R2.id.vp_container)
    protected ViewGroup mVpContainer;
    @BindView(R2.id.vip_container)
    protected FrameLayout mVipContainer;

    @BindView(R2.id.group_user_container)
    protected ViewGroup mGroupWorkUserContainer;

    protected Class<T>cs;
    private RefreshListner<T>mRefreshListner;
    protected T mData;



    public BaseInsViewProxy() {
        this.cs =initClass();
    }

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        String projectId= (String) getArgMap().get(Constants.KEY_ID);
        getData(projectId);
    }
    private void getData(String projectId) {
        if(cs==null){
            return;
        }
        CourseAPI.getProjectDetail(projectId,cs).compose(this.<T>bindUntilOnDestoryEvent()).subscribe(new DefaultObserver<T>() {
            @Override
            public void onNext(@NonNull T t) {
                mVpContainer.setVisibility(View.VISIBLE);
                mData=t;
                if(mRefreshListner!=null){
                    mRefreshListner.refreshData(t);
                }
                setData(t);
            }
        });

    }


    public void getData() {
        String projectId= (String) getArgMap().get(Constants.KEY_ID);
        getData(projectId);
    }

    protected void setData(T data) {
        mVpContainer.setVisibility(View.VISIBLE);
        if (data != null) {
            setTitle(data.getTitle());
            mTvPrice.setText(data.getHandlePrice());
            mTvPrice.setTextColor(UIFactory.getPriceViewColor(data.getPaytype()));
            mTvStudyCount.setText(getString(R.string.study_person_count, data.getStudyCount()));
            mTvProjectIntroduce.setText(data.getIntroduce());
        }
    }


    public void setTitle(String title) {
        mTvTitle.setText(title);
    }

    public void  putProjectId(String id){
        getArgMap().put(Constants.KEY_ID,id);
    }

    public abstract Class<T> initClass();

    public void setRefreshListner(RefreshListner<T> refreshListner) {
        mRefreshListner = refreshListner;
    }
    public interface RefreshListner<T>{
        public void refreshData(T data);
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }
}

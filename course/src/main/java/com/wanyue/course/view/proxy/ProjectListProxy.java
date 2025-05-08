package com.wanyue.course.view.proxy;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.Constants;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.course.R;
import com.wanyue.course.adapter.ProjectListAdapter;
import com.wanyue.course.bean.ProjectBean;
import com.wanyue.course.R2;
import com.wanyue.course.view.activity.CourseDetailActivity;

import java.util.List;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/*基础列表*/
public abstract class ProjectListProxy extends RxViewProxy implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {
    @BindView(R2.id.refreshView)
    RxRefreshView<ProjectBean> mRefreshView;
    private ProjectListAdapter mProjectListAdapter;
    private boolean mIsFromTeacher;
    private String mEmptyHint;

    private boolean mNeedWatchGradleChange=true;
    @Override
    public int getLayoutId() {
        return R.layout.view_single_refresh;
    }
    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);

        mProjectListAdapter=new ProjectListAdapter(null);
        mRefreshView.setAdapter(mProjectListAdapter);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(getActivity(),0));
        mRefreshView.setDataListner(new RxRefreshView.DataListner<ProjectBean>() {
            @Override
            public Observable<List<ProjectBean>> loadData(int p) {
                return valueGetData(p);
            }
            @Override
            public void compelete(List<ProjectBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });

        mRefreshView.setEmptyLevel(1);
        if(!TextUtils.isEmpty(mEmptyHint)){
           mRefreshView.setNoDataTip(mEmptyHint);
        }else{
            mRefreshView.setNoDataTip("No content available");
        }

        mProjectListAdapter.setOnItemClickListener(this);
        mProjectListAdapter.setOnItemChildClickListener(this);

    }

    /*是否需要观察学级变化*/
    public void setNeedWatchGradleChange(boolean needWatchGradleChange) {
        mNeedWatchGradleChange = needWatchGradleChange;
    }

    private Observable<List<ProjectBean>> valueGetData(int p) {
      return getData(p);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
         if(isVisibleToUser&&mRefreshView!=null){
             initData();
         }
    }

    public void setEmptyHint(String emptyHint) {
        mEmptyHint = emptyHint;
    }

    public void initData(){
        if(mRefreshView!=null){
          mRefreshView.initData();
        }
    }
    public abstract Observable<List<ProjectBean>> getData(int p);

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setFromTeacher(boolean fromTeacher) {
        mIsFromTeacher = fromTeacher;
    }
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ProjectBean projectBean=mProjectListAdapter.getItem(position);
        Intent intent=new Intent();
        intent.putExtra(Constants.FROM,mIsFromTeacher);
        intent.setClass(getActivity(), CourseDetailActivity.class);
        intent.putExtra(Constants.DATA,projectBean);
        getActivity().startActivity(intent);
    }




    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }
}

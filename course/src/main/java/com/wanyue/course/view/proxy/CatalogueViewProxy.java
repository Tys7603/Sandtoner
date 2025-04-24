package com.wanyue.course.view.proxy;


import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.http.ParseSingleHttpCallback;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.utils.ProcessResultUtil;
import com.wanyue.common.utils.RouteUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.course.R;
import com.wanyue.course.adapter.CourseLevelAdapter;
import com.wanyue.course.api.CourseAPI;
import com.wanyue.course.bean.ContentBean;
import com.wanyue.course.bean.CourseBean;
import com.wanyue.course.bean.CourseLevel1Bean;
import com.wanyue.course.busniess.CourseConstants;
import com.wanyue.course.view.activity.ContentDetailActivity;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/*课程目录列表*/
public class CatalogueViewProxy extends RxViewProxy {
    private RxRefreshView<MultiItemEntity> mRefreshView;
    private CourseBean mCourseBean;
    private CourseLevelAdapter mCourseLevelAdapter;
    private String mLectureId;
    private boolean mIsNeedRefresh;
    private ProcessResultUtil mProcessResultUtil;

    @Override
    public int getLayoutId() {
        return R.layout.view_catalogue;
    }
    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mRefreshView =  findViewById(R.id.reclyView);
        mProcessResultUtil = new ProcessResultUtil(getActivity());
        mCourseLevelAdapter=new CourseLevelAdapter(null);
        mRefreshView.setAdapter(mCourseLevelAdapter);
        mRefreshView.setIconId(R.mipmap.icon_default_no_data);
        mRefreshView.setEmptyLevel(1);
        mRefreshView.setNoDataTip(getString(R.string.no_lesson));
        mRefreshView.setLoadMoreEnable(false);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(getActivity(),1));
        mRefreshView.setDataListner(new RxRefreshView.DataListner<MultiItemEntity>() {
            @Override
            public Observable<List<MultiItemEntity>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<MultiItemEntity> data) {
                if(mCourseLevelAdapter!=null){
                   mCourseLevelAdapter.resumeExpand();
                }
            }
            @Override
            public void error(Throwable e) {
                e.printStackTrace();
            }
        });

        mCourseLevelAdapter.setOnChildItemClickLisnter(new CourseLevelAdapter.OnChildItemClickLisnter() {
            @Override
            public void onItemClick(CourseLevel1Bean courseLevel1Bean, int position) {
                 itemClick(courseLevel1Bean,position);
            }
        });
    }

    private void itemClick(CourseLevel1Bean courseLevel1Bean, int position) {
       if(courseLevel1Bean.getIsenter()==0){
           return;
       }

       int type=courseLevel1Bean.getType();
       int status=courseLevel1Bean.getStatus();
       if(!CommonAppConfig.isLogin()){
          RouteUtil.forwardLogin();
          return;
       }
        switch (type){
           case CourseConstants.LESSON_TYPE_COTENT_IMG_TEXT:
               ContentBean contentBean=courseLevel1Bean.parseContentBean(mCourseBean);
               contentBean.setContentType(CourseConstants.CONTENT_IMAGE_TEXT);
               fowardContent(contentBean,status);
               break;
           case CourseConstants.LESSON_TYPE_COTENT_VIDEO:
               contentBean=courseLevel1Bean.parseContentBean(mCourseBean);
               contentBean.setContentType(CourseConstants.CONTENT_VIDEO);
               fowardContent(contentBean,status);
               break;
           case CourseConstants.LESSON_TYPE_COTENT_AUDIO:
               contentBean=courseLevel1Bean.parseContentBean(mCourseBean);
               contentBean.setContentType(CourseConstants.CONTENT_AUDIO);
               fowardContent(contentBean,status);
               break;
           default:
              break;
       }
        mIsNeedRefresh=true;
    }


    private void fowardContent(final ContentBean contentBean, int status) {
        CourseAPI.getLessonDetail(contentBean.getLessionId(), new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                CourseLevel1Bean level=info.toJavaObject(CourseLevel1Bean.class);
                ContentBean contentBean=level.parseContentBean(mCourseBean);
                ContentDetailActivity.forward(getActivity(),contentBean);
            }

            @Override
            public void onError(Throwable e) {
                if (e != null) {
                    ToastUtil.show(e.getMessage());
                }
            }
        });
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return false;
    }
    @Override
    public void onStart() {
        super.onStart();
        if(mIsNeedRefresh){
           mRefreshView.initData();
        }
    }

    private Observable<List<MultiItemEntity>> getData(int p) {
        if (mCourseLevelAdapter != null&&mCourseBean!=null) {
            mCourseLevelAdapter.setBuy(mCourseBean.ifBuy());
        }
        
        String courseId = mCourseBean == null ? null : mCourseBean.getId();
        return CourseAPI.getLessonList(courseId).map(new Function<List<CourseLevel1Bean>, List<MultiItemEntity>>() {
            @Override
            public List<MultiItemEntity> apply(List<CourseLevel1Bean> courseLevel0Beans) throws Exception {
                mIsNeedRefresh = false;
                List<MultiItemEntity>list=new ArrayList<>();
                for(CourseLevel1Bean bean:courseLevel0Beans){
                    list.add(bean);
                    if(mCourseBean!=null){
                       int isEneter= mCourseBean.ifBuy()?1:0;
                       bean.setIsenter(isEneter);
                    }
                    if(bean.getIslast()==1){
                        bean.setStatus(CourseLevel1Bean.STATUS_LAST);
                    }
                }
                mCourseLevelAdapter.resumeExpand();
                return list;
            }
        }).compose(this.<List<MultiItemEntity>>bindUntilOnDestoryEvent());
    }

    public String getLectureId() {
        if(mCourseBean==null){
            return mLectureId;
        }
        if(mLectureId==null){
           mLectureId=mCourseBean.getLecturerBean()==null?null:mCourseBean.getLecturerBean().getId();
        }
        return mLectureId;
    }

    public String getProjectId() {
        if(mCourseBean==null){
            return null;
        }
        return mCourseBean.getId();
    }


    public void setCourseBean(CourseBean courseBean) {
        if(courseBean==null){
            return;
        }
        mCourseBean = courseBean;
        reFreshData();
    }

    private void reFreshData() {
        if(mRefreshView!=null){
           mRefreshView.initData();
        }
    }
}

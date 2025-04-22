package com.wanyue.course.view.activity;

import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.course.R;
import com.wanyue.course.adapter.CourseRecordAdapter;
import com.wanyue.course.api.CourseAPI;
import com.wanyue.course.bean.RecordBean;
import java.util.List;
import io.reactivex.Observable;

public class MyCourseRecordActivity extends BaseActivity {
    private RxRefreshView<RecordBean> mRefreshView;
    private CourseRecordAdapter mMyCourseRecordAdapter;

    @Override
    public void init() {
        setTabTitle("购买记录");
        mRefreshView = (RxRefreshView) findViewById(R.id.refreshView);
        mMyCourseRecordAdapter=new CourseRecordAdapter(null);
        mRefreshView.setAdapter(mMyCourseRecordAdapter);
        mRefreshView.setEmptyLevel(1);
        mRefreshView.setNoDataTip("暂无购买记录");

        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this));
        mRefreshView.setDataListner(new RxRefreshView.DataListner<RecordBean>() {
            @Override
            public Observable<List<RecordBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<RecordBean> data) {

            }
            @Override
            public void error(Throwable e) {

            }
        });
        mRefreshView.initData();

    }

    private Observable<List<RecordBean>> getData(int p) {
        return CourseAPI.getRecordList(p).compose(this.<List<RecordBean>>bindToLifecycle());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_course_record;
    }
}
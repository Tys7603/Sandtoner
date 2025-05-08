package com.wanyue.course.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.course.R;
import com.wanyue.course.api.CourseAPI;
import com.wanyue.course.bean.ProjectBean;
import com.wanyue.course.view.proxy.ProjectListProxy;

import java.util.List;

import io.reactivex.Observable;

public class MyCourseActivity extends BaseActivity {
    private ViewGroup mContainer;

    private TextView mTvRightTitle;

    @Override
    public void init() {
        setTabTitle("My Courses");
        mTvRightTitle = (TextView) findViewById(R.id.tv_right_title);
        mTvRightTitle.setText("Purchase history");
        mTvRightTitle.setTextColor(ResourceUtil.getColor(R.color.gray1));
        mTvRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startActivity(MyCourseRecordActivity.class);
            }
        });

        mContainer = (ViewGroup) findViewById(R.id.container);
        ProjectListProxy projectListProxy=new ProjectListProxy() {
            @Override
            public Observable<List<ProjectBean>> getData(int p) {
                return CourseAPI.getMyCourse(p);
            }
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {

            }
        };
        getViewProxyMannger().addViewProxy(mContainer,projectListProxy,projectListProxy.getDefaultTag());
        projectListProxy.initData();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_course;
    }
}
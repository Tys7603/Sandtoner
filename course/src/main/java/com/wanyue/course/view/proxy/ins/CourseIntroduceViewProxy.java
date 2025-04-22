package com.wanyue.course.view.proxy.ins;

import android.text.TextUtils;
import android.view.ViewGroup;
import com.wanyue.course.R;
import com.wanyue.course.bean.CourseBean;
import butterknife.BindView;

public class CourseIntroduceViewProxy extends WebViewInsViewProxy<CourseBean> {

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mTvFirstTitle.setText("内容介绍");
    }

    @Override
    public Class<CourseBean> initClass() {
        return CourseBean.class;
    }

    @Override
    protected void setData(CourseBean data) {

        super.setData(data);
        String lesson=data.getLesson();
        if(TextUtils.isEmpty(lesson)||lesson.equals("0课时")){
            mTvCenterTag.setText(R.string.no_add_content);
        }else{
            mTvCenterTag.setText(getString(R.string.total_class_hour,data.getLesson()));
        }
    }

    @Override
    public int getLayoutId() {
         return R.layout.view_course_introduce;
    }
}

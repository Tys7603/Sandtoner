package com.wanyue.main.adapter;

import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.course.bean.CourseBean;
import com.wanyue.main.R;
import java.util.List;

public class HomePageCourseAdapter extends BaseRecyclerAdapter<CourseBean, BaseReclyViewHolder> {
    public HomePageCourseAdapter(List<CourseBean> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_relcy_home_page_course;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, CourseBean courseBean) {
        helper.setImageUrl(courseBean.getThumb(),R.id.img_thumb);

    }
}

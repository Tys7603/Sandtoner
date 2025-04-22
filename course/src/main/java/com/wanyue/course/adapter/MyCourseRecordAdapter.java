package com.wanyue.course.adapter;

import androidx.annotation.NonNull;

import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.course.R;
import com.wanyue.course.bean.RecordBean;

import java.util.List;

public class MyCourseRecordAdapter extends BaseRecyclerAdapter<RecordBean, BaseReclyViewHolder> {
    public MyCourseRecordAdapter(List<RecordBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_my_course_record;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder viewHolder, RecordBean recordBean) {

    }
}

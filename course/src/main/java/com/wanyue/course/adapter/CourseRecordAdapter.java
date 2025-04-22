package com.wanyue.course.adapter;

import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.course.R;
import com.wanyue.course.bean.RecordBean;
import com.wanyue.course.busniess.ProjectAdapterHelper;
import java.util.List;

public class CourseRecordAdapter extends BaseRecyclerAdapter<RecordBean, BaseReclyViewHolder> {
    private ProjectAdapterHelper mProjectAdapterHelper;

    public CourseRecordAdapter(List<RecordBean> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_relcy_course_recorder;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, RecordBean item) {
        if(mProjectAdapterHelper==null){
           mProjectAdapterHelper=new ProjectAdapterHelper();
        }
            mProjectAdapterHelper.convert(helper,item);
            helper.setText(R.id.tv_add_time,item.getAddTime());
    }
}

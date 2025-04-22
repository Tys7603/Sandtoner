package com.wanyue.course.adapter;

import android.view.View;

import com.wanyue.common.adapter.base.BaseMutiRecyclerAdapter;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.course.R;
import com.wanyue.course.bean.ProjectBean;
import com.wanyue.course.busniess.CourseConstants;
import com.wanyue.course.busniess.ProjectAdapterHelper;

import java.util.List;

public class ProjectListAdapter extends BaseMutiRecyclerAdapter<ProjectBean, BaseReclyViewHolder> {
    private ProjectAdapterHelper mProjectAdapterHelper;

    public ProjectListAdapter(List<ProjectBean> data) {
        super(data);
        addItemType(CourseConstants.TYPE_PROJECT_COURSE, R.layout.item_relcy_project_course);
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, ProjectBean item) {
        View itemView=helper.getView(R.id.item);
        if(itemView!=null&&itemView.getBackground()==null){
            itemView.setBackground(ResourceUtil.getDrawable(R.drawable.press_raidius_5_color_white,false));
        }
        if(mProjectAdapterHelper==null){
           mProjectAdapterHelper=new ProjectAdapterHelper();
        }
        mProjectAdapterHelper.convert(helper,item);
    }

}


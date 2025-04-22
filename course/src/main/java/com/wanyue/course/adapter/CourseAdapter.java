package com.wanyue.course.adapter;

import android.text.TextUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.course.R;
import com.wanyue.course.bean.CourseBean;
import com.wanyue.course.bean.ProjectBean;
import com.wanyue.course.busniess.UIFactory;
import java.util.List;

public class CourseAdapter extends BaseRecyclerAdapter<CourseBean, BaseReclyViewHolder> {

    public CourseAdapter(List<CourseBean> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_relcy_project_course;
    }
    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, CourseBean projectBean) {
        helper.setText(R.id.tv_title,projectBean.getTitle());
        helper.setImageUrl(projectBean.getThumb(),R.id.img_thumb);
        TextView tvPrice=helper.getView(R.id.tv_price);
        tvPrice.setText(projectBean.getHandlePrice());
        tvPrice.setTextColor(getPriceTextViewColor(projectBean));

        String  lesson=projectBean.getLesson();
        if(TextUtils.isEmpty(lesson)||lesson.equals("0课时")){
            helper.setText(R.id.tv_lesson, WordUtil.getString(R.string.no_add_content));
        }else{
            helper.setText(R.id.tv_lesson,lesson);
        }
        TextView lessonView=helper.getView(R.id.tv_lesson);
        lessonView.setText(lesson);
    }


    public int getPriceTextViewColor(ProjectBean projectBean) {
        int paytype=projectBean.getPaytype();
        return UIFactory.getPriceViewColor(paytype);
    }
}

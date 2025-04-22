package com.wanyue.course.busniess;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.course.R;
import com.wanyue.course.bean.CourseBean;
import com.wanyue.course.bean.ProjectBean;


public class ProjectAdapterHelper {

    public  void convert(BaseReclyViewHolder helper, ProjectBean projectBean){
        if(projectBean==null){
            return;
         }
        switch (projectBean.getProjectType()){
            case CourseConstants.TYPE_PROJECT_COURSE:
                convertCourse(helper,projectBean);
                break;

            default:
                break;
        }
    }


    /*解析课程类型*/
    private  void convertCourse(BaseReclyViewHolder helper, ProjectBean projectBean) {
        if(projectBean==null){
            return;
        }
        CourseBean courseBean= (CourseBean) convertCommon(helper,projectBean);
        String  lesson=courseBean.getLesson();
        helper.setText(R.id.tv_lesson,lesson);
    }

    /*通用的*/
    private  ProjectBean convertCommon(BaseReclyViewHolder helper, ProjectBean projectBean) {
        helper.setText(R.id.tv_title,projectBean.getTitle());
        helper.setImageUrl(projectBean.getThumb(),R.id.img_thumb);
        TextView tvPrice=helper.getView(R.id.tv_price);
        tvPrice.setText(projectBean.getHandlePrice());
        tvPrice.setTextColor(getPriceTextViewColor(projectBean));
        helper.addOnClickListener(R.id.img_avator);
        helper.addOnClickListener(R.id.tv_name);
        return projectBean;
    }


    public int getPriceTextViewColor(ProjectBean projectBean) {
       int paytype=projectBean.getPaytype();
       return UIFactory.getPriceViewColor(paytype);
    }


}

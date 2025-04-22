package com.wanyue.course.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.wanyue.common.adapter.base.BaseMutiRecyclerAdapter;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.course.R;
import com.wanyue.course.bean.CourseLevel0Bean;
import com.wanyue.course.bean.CourseLevel1Bean;
import com.wanyue.course.busniess.CourseConstants;
import com.wanyue.course.busniess.UIFactory;

import java.util.HashSet;
import java.util.List;

public class CourseLevelAdapter<T extends MultiItemEntity> extends BaseMutiRecyclerAdapter<T, BaseReclyViewHolder> {
    private Drawable mLockedDrawable;
    private int globalColor;
    private int grayColor;
    private int textColor;
    private boolean mIsBuy;
    private OnChildItemClickLisnter mOnChildItemClickLisnter;
    private HashSet<Integer> mCacheOpenSet;

    public CourseLevelAdapter(List<T> data) {
        super(data);
        mCacheOpenSet=new HashSet();
        mCacheOpenSet.add(0);
        addItemType(CourseLevel0Bean.LEVEL_TYPE, R.layout.item_recly_course_level_0);
        addItemType(CourseLevel1Bean.LEVEL_TYPE,R.layout.item_recly_course_level_1);

        globalColor=ResourceUtil.getColor(R.color.global);
        grayColor=ResourceUtil.getColor(R.color.gray1);
        textColor=ResourceUtil.getColor(R.color.textColor);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MultiItemEntity multiItemEntity=getItem(position);
                if(multiItemEntity==null){
                    return;
                }
                int itemType=multiItemEntity.getItemType();
                if(itemType==CourseLevel0Bean.LEVEL_TYPE&&multiItemEntity instanceof CourseLevel0Bean){
                    clickHead(multiItemEntity,position);
                }else if(itemType==CourseLevel1Bean.LEVEL_TYPE&&multiItemEntity instanceof CourseLevel1Bean){
                    clickNormal(multiItemEntity,position);
                }
            }
        });
    }


    /*点击二级列表*/
    private void clickNormal(MultiItemEntity multiItemEntity, int position) {
        CourseLevel1Bean courseLevel1Bean= (CourseLevel1Bean) multiItemEntity;
        if(mOnChildItemClickLisnter!=null){
           mOnChildItemClickLisnter.onItemClick(courseLevel1Bean,position);
        }
    }

    /*点击一级列表*/
    private void clickHead(MultiItemEntity multiItemEntity, int position) {
        CourseLevel0Bean level0Bean= (CourseLevel0Bean) multiItemEntity;
        if (level0Bean.isExpanded()) {
            collapse(position);
        } else {
            expand(position);
        }
    }


    @Override
    public int expand(int position) {
        if(mCacheOpenSet==null){
           mCacheOpenSet=new HashSet<>();
        }
        mCacheOpenSet.add(position);
        return super.expand(position);
    }

    @Override
    public int collapse(int position) {
        if(mCacheOpenSet==null){
            mCacheOpenSet=new HashSet<>();
        }
        mCacheOpenSet.remove(position);
        return super.collapse(position);
    }

    @Override
    protected void convert(final BaseReclyViewHolder helper, MultiItemEntity item) {
            switch (item.getItemType()){
                case CourseLevel0Bean.LEVEL_TYPE:
                    convertLevel0(helper,item);
                    break;
                case CourseLevel1Bean.LEVEL_TYPE:
                    convertLevel1(helper,item);
                    break;
                default:
                    break;
            }
    }





    private void convertLevel0(final BaseReclyViewHolder helper, MultiItemEntity item) {
        final CourseLevel0Bean level0Bean= (CourseLevel0Bean) item;
        View arrowView=helper.getView(R.id.img_arrow);
        if(level0Bean.isExpanded()){
            arrowView.setRotation(180);
        }else{
            arrowView.setRotation(0);
        }
        helper.setText(R.id.tv_title,level0Bean.getName());
    }
    private void convertLevel1(BaseReclyViewHolder helper, MultiItemEntity item){
        final CourseLevel1Bean level1Bean= (CourseLevel1Bean) item;
        ImageView imgCourseType=helper.getView(R.id.img_course_type);
        Drawable drawable=null;
        int type=level1Bean.getType();
        switch (type){
            case CourseConstants.LESSON_TYPE_COTENT_AUDIO:
                drawable=ResourceUtil.getDrawable(R.drawable.icon_lesson_audio,true);
                break;
            case CourseConstants.LESSON_TYPE_COTENT_VIDEO:
                drawable=ResourceUtil.getDrawable(R.drawable.icon_lesson_video,true);
                break;
            case CourseConstants.LESSON_TYPE_COTENT_IMG_TEXT:
                drawable=ResourceUtil.getDrawable(R.drawable.icon_lesson_img_text,true);
                break;
            case CourseConstants.LESSON_TYPE_LIVE_PPT:
                drawable=ResourceUtil.getDrawable(R.drawable.icon_lesson_live,true);
                break;
            case CourseConstants.LESSON_TYPE_LIVE_VIDEO:
                drawable=ResourceUtil.getDrawable(R.drawable.icon_lesson_live,true);
                break;
            case CourseConstants.LESSON_TYPE_LIVE_AUDIO:
                drawable=ResourceUtil.getDrawable(R.drawable.icon_lesson_live,true);
                break;
            default:
                break;
        }

        imgCourseType.setImageDrawable(drawable);
        TextView tvTitle=helper.getView(R.id.tv_title);
        tvTitle.setText(level1Bean.getImageName());
        ImageView imgLock=helper.getView(R.id.img_lock);
        TextView tvTag=helper.getView(R.id.tv_tag);

        if(level1Bean.isLiveType()){
            helper.setText(R.id.tv_message,level1Bean.getTime_date()+"\t"+ UIFactory.getLiveTypeName(type));
            helper.setVisible(R.id.tv_message,true);
        }else{
            helper.setText(R.id.tv_message,null);
            helper.setVisible(R.id.tv_message,false);
        }
        if(level1Bean.getIsenter()==1){
            tvTitle.setTextColor(textColor);
        }else{
            tvTitle.setTextColor(grayColor);
        }
        int status=level1Bean.getStatus();
        if(status==CourseLevel1Bean.STATUS_TRY){
            tvTag.setTextColor(globalColor);
            tvTag.setText(R.string.try_to_learn);
            imgLock.setImageDrawable(null);
        }else if(status==CourseLevel1Bean.STATUS_STUDYED){
            tvTag.setBackground(null);
            tvTag.setTextColor(grayColor);
            tvTag.setText(R.string.studyed);
            imgLock.setImageDrawable(null);
        }else if(status==CourseLevel1Bean.STATUS_LIVING){
            tvTag.setTextColor(globalColor);
            tvTag.setText(R.string.in_live);
            imgLock.setImageDrawable(null);
        }else if(status==CourseLevel1Bean.STATUS_LOCK){
            tvTag.setText(null);
            imgLock.setImageDrawable(mLockedDrawable);
        }else if(status==CourseLevel1Bean.STATUS_LAST){

        }
        else{
            tvTag.setText(null);
            imgLock.setImageDrawable(null);
        }
    }

    public void setOnChildItemClickLisnter(OnChildItemClickLisnter onChildItemClickLisnter) {
        mOnChildItemClickLisnter = onChildItemClickLisnter;
    }

    public void resumeExpand() {
        if(mCacheOpenSet==null){
            return;
        }
        for(Integer position:mCacheOpenSet){
             if(position<size()){
                expand(position);
             }
        }
    }

    public HashSet<Integer> getCacheOpenSet() {
        return mCacheOpenSet;
    }

    @Override
    public int size(){
        return mData==null?0:mData.size();
    }

    public  interface OnChildItemClickLisnter{
        public void onItemClick(CourseLevel1Bean courseLevel1Bean, int position);
    }

    public void setBuy(boolean buy) {
        mIsBuy = buy;
    }
}

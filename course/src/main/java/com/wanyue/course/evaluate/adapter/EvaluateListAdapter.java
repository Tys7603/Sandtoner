package com.wanyue.course.evaluate.adapter;

import android.graphics.Bitmap;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.course.R;
import com.wanyue.course.bean.EvaluateBean;
import com.wanyue.course.widet.RatingStar;
import java.util.List;

/**/
public class EvaluateListAdapter extends BaseRecyclerAdapter<EvaluateBean, BaseReclyViewHolder> {
    private Bitmap mNormalStarBitmap;
    private Bitmap mSelectStarBitmap;

    public EvaluateListAdapter(List<EvaluateBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_course_evaluate;
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, EvaluateBean item) {
        RatingStar ratingStar=helper.getView(R.id.rating_star);
        ratingStar.setNormalImg(mNormalStarBitmap);
        ratingStar.setFocusImg(mSelectStarBitmap);
        ratingStar.setNumber(item.getStarCount());
        ratingStar.setPosition(item.getStarCount()-1);

        helper.setImageUrl(item.getUserAvator(),R.id.img_avator);
        helper.setText(R.id.tv_user_name,item.getUserNickName());
        String des=item.getDes();
        helper.setText(R.id.tv_study_progress,des);
        helper.setText(R.id.tv_content,item.getContent());
        helper.setText(R.id.tv_time,item.getStudyRate());
    }

    public void setNormalStarBitmap(Bitmap normalStarBitmap) {
        mNormalStarBitmap = normalStarBitmap;
    }
    public void setSelectStarBitmap(Bitmap selectStarBitmap) {
        mSelectStarBitmap = selectStarBitmap;
    }

}

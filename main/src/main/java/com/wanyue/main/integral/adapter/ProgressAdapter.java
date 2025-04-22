package com.wanyue.main.integral.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.main.R;
import com.wanyue.main.integral.bean.WeekSignBean;
import com.wanyue.shop.view.widet.ViewGroupLayoutMutiBaseAdapter;
import java.util.List;

/**
 * The type Progress adapter.
 */
public class ProgressAdapter extends ViewGroupLayoutMutiBaseAdapter<WeekSignBean> {
    private int mCurrentLevel;
    private int mSmallSize;
    private int mBigSize;

    /**
     * Instantiates a new Progress adapter.
     *
     * @param data the data
     */
    public ProgressAdapter(List<WeekSignBean> data) {
        super(data);
        addItemType(WeekSignBean.NORMAL,R.layout.item_reclt_normal_progress);
        addItemType(WeekSignBean.LINE,R.layout.item_reclt_normal_line);
        mSmallSize= DpUtil.dp2px(10);
        mBigSize=DpUtil.dp2px(15);
    }

    @Override
    public void convert(BaseReclyViewHolder helper, WeekSignBean item) {
        int type=item.getItemType();
        if(type==WeekSignBean.NORMAL){
            convertNormal(helper,item);
        }else if(type==WeekSignBean.LINE){
            convertLine(helper,item);
        }
    }

    private void convertLine(BaseReclyViewHolder helper, WeekSignBean item) {
        int id=item.getId();
        View line=helper.getView(R.id.line);
        if(mCurrentLevel>=id){
            line.setBackgroundColor(ResourceUtil.getColor(R.color.global));
        }else{
            line.setBackgroundColor(ResourceUtil.getColor(R.color.gray_dc));
        }
    }

    private void convertNormal(BaseReclyViewHolder helper, WeekSignBean item) {
        helper.setText(R.id.tv_day,item.getDay());
        helper.setText(R.id.tv_code,"+"+item.getReward());

        TextView tvDay=helper.getView(R.id.tv_day);
        TextView tvCode=helper.getView(R.id.tv_code);
        int id=item.getId();

        ImageView  imageView=helper.getView(R.id.img);
        if(mCurrentLevel>id){
            tvDay.setTextColor(ResourceUtil.getColor(R.color.global));
            tvCode.setTextColor(ResourceUtil.getColor(R.color.global));
            imageView.getLayoutParams().width=mSmallSize;
            imageView.getLayoutParams().height=mSmallSize;
            helper.setImageResouceId(R.drawable.round_global,R.id.img);
        }else if(mCurrentLevel==id){
            imageView.getLayoutParams().width=mBigSize;
            imageView.getLayoutParams().height=mBigSize;
            tvDay.setTextColor(ResourceUtil.getColor(R.color.global));
            tvCode.setTextColor(ResourceUtil.getColor(R.color.global));
            helper.setImageResouceId(R.drawable.icon_signing,R.id.img);
        }else{
            imageView.getLayoutParams().width=mSmallSize;
            imageView.getLayoutParams().height=mSmallSize;
            tvDay.setTextColor(ResourceUtil.getColor(R.color.gray_dc));
            tvCode.setTextColor(ResourceUtil.getColor(R.color.gray_dc));
            helper.setImageResouceId(R.drawable.round_graydc,R.id.img);
        }
    }

    /**
     * Sets current level.
     *
     * @param currentLevel the current level
     */
    public void setCurrentLevel(int currentLevel) {
        mCurrentLevel = currentLevel;
    }
}

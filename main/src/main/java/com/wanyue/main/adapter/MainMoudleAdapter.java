package com.wanyue.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.main.R;
import com.wanyue.main.bean.MoudleBean;
import com.wanyue.shop.view.activty.GoodsDetailActivity;

import java.util.List;

/**
 * The type Main moudle adapter.
 */
public class MainMoudleAdapter extends BaseMultiItemQuickAdapter<MoudleBean, BaseReclyViewHolder> {
    private Context mContext;

    /**
     * Instantiates a new Main moudle adapter.
     *
     * @param data    the data
     * @param context the context
     */
    public MainMoudleAdapter(List<MoudleBean> data,Context context) {
        super(data);
        addItemType(1,R.layout.item_recly_main_moudle_helf);
        addItemType(2,R.layout.item_recly_main_moudle_full);
        mContext=context;
    }
    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, MoudleBean moudleBean) {
        helper.setText(R.id.tv_title,moudleBean.getName());
        helper.setText(R.id.tv_tag,moudleBean.getTag());
        helper.setText(R.id.tv_content,moudleBean.getDes());

        TextView textView=helper.getView(R.id.tv_title);
        String textColor=moudleBean.getColor();
        if(!TextUtils.isEmpty(textColor)){
            int color= Color.parseColor(textColor);
            textView.setTextColor(color);
        }
        ViewGroup vpThumb=helper.getView(R.id.vp_img_thumb);
        int childCount=vpThumb.getChildCount();
        List<GoodsBean>list=moudleBean.getList();
        for(int i=0;i<childCount;i++){
           ImageView view= (ImageView) vpThumb.getChildAt(i);
           final GoodsBean item= ListUtil.safeGetData(list,i);
           if(item!=null){
              ImgLoader.display(mContext,item.getThumb(),view);
               view.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                      GoodsDetailActivity.forward(mContext,item.getId());
                   }
               });
           }else{
              view.setOnClickListener(null);
              ImgLoader.display(mContext,0,view);
           }
        }
    }
}

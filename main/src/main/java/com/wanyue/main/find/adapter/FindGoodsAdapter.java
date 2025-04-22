package com.wanyue.main.find.adapter;

import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.course.busniess.UIFactory;
import com.wanyue.main.R;
import java.util.List;

public class FindGoodsAdapter extends BaseRecyclerAdapter<GoodsBean, BaseReclyViewHolder> {
    private int mWidth;
    private boolean isVertiral;

    public FindGoodsAdapter(List<GoodsBean> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_relcy_find_goods;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder viewHolder, GoodsBean goodsBean) {
        ViewGroup viewGroup=viewHolder.getView(R.id.vp_goods_container);
        viewGroup.getLayoutParams().width=mWidth;
        convertGoods(viewHolder,goodsBean);
    }

    public static void convertGoods(BaseReclyViewHolder viewHolder, GoodsBean goodsBean) {
        viewHolder.setText(R.id.tv_goods_title,goodsBean.getName());
        viewHolder.setText(R.id.tv_price, UIFactory.createPrice(goodsBean.getPriceNow()));
        viewHolder.setImageUrl(goodsBean.getThumb(),R.id.img_goods);
    }

    @Override
    public void setData(List<GoodsBean> data) {

        if(isVertiral||ListUtil.size(data)<=1){
            mWidth= ViewGroup.LayoutParams.MATCH_PARENT;
        }else{
            mWidth= (int) (CommonAppConfig.getWindowWidth()*0.6);
        }
        super.setData(data);
    }

    public void setVertiral(boolean vertiral) {
        isVertiral = vertiral;
    }
}

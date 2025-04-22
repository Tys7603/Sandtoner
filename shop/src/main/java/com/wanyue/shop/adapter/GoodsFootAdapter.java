package com.wanyue.shop.adapter;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wanyue.common.adapter.base.BaseMutiRecyclerAdapter;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.shop.R;
import com.wanyue.shop.bean.FootSectionBean;
import java.util.List;

public class GoodsFootAdapter extends BaseMutiRecyclerAdapter<FootSectionBean, BaseReclyViewHolder> implements RxRefreshView.DataAdapter<FootSectionBean> {

    public GoodsFootAdapter(List<FootSectionBean> data) {
        super(data);
        addItemType(1,R.layout.item_recly_foot_goods_title);
        addItemType(2,R.layout.item_recly_foot_goods_content);
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, FootSectionBean item) {
        switch (helper.getItemViewType()){
            case 1:
                helper.setText(R.id.tv_title,item.header);
                break;
            case 2:
                GoodsBean goodsBean=item.t;
                if(goodsBean!=null){
                    helper.setText(R.id.tv_price, goodsBean.getUnitPrice());
                    helper.setImageUrl(goodsBean.getThumb(),R.id.img_thumb);
                }
                break;
        }
    }

    @Override
    public void setData(List<FootSectionBean> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void appendData(List<FootSectionBean> data) {
        if(mData!=null){
            addData(data);
        }else{
            setData(data);
        }
    }

    @Override
    public void appendData(int index, List<FootSectionBean> data) {
        if(data!=null){

            addData(index,data);
            notifyDataSetChanged();
        }else{
            setData(data);
        }

    }

    @Override
    public List<FootSectionBean> getArray() {
        return mData;
    }

    @Override
    public RecyclerView.Adapter returnRecyclerAdapter() {
        return this;
    }

    @Override
    public void notifyReclyDataChange() {
        notifyDataSetChanged();
    }
}

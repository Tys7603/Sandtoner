package com.wanyue.main.adapter;

import androidx.annotation.NonNull;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.wanyue.common.adapter.base.BaseMutiRecyclerAdapter;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.main.R;
import com.wanyue.main.bean.SpreadOrderBean;
import com.wanyue.main.bean.SpreadOrderSectionBean;
import java.util.List;

public class SpreadOrderAdapter extends BaseMutiRecyclerAdapter<MultiItemEntity, BaseReclyViewHolder> {
    public SpreadOrderAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(SpreadOrderSectionBean.TYPE, R.layout.item_recly_spread_order_head);
        addItemType(SpreadOrderBean.TYPE, R.layout.item_recly_spread_order_normal);
    }
    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, MultiItemEntity item) {
            switch (helper.getItemViewType()){
                case  SpreadOrderSectionBean.TYPE:
                    convertHead(helper,item);
                    break;
                case  SpreadOrderBean.TYPE:
                    convertNormal(helper,item);
                    break;
                default:
                    break;
            }
    }


    private void convertHead(BaseReclyViewHolder helper, MultiItemEntity item) {
        SpreadOrderSectionBean spreadOrderSectionBean= (SpreadOrderSectionBean) item;
        helper.setText(R.id.tv_time,spreadOrderSectionBean.getTime());
        int spreadCount=spreadOrderSectionBean.getCount();
        helper.setText(R.id.tv_title, WordUtil.getString(R.string.spread_tip_14,Integer.toString(spreadCount)));
    }

    private void convertNormal(BaseReclyViewHolder helper, MultiItemEntity item) {
        SpreadOrderBean orderBean= (SpreadOrderBean) item;
        helper.setImageUrl(orderBean.getAvatar(),R.id.img_avator);
        helper.setText(R.id.tv_name,orderBean.getNickname());
        double price=orderBean.getNumber();
        String type=orderBean.getType();
        if(StringUtil.equals(type,"pay_money")){
            helper.setVisible(R.id.tv_price,false);
            helper.setText(R.id.tv_tip, WordUtil.getString(R.string.spread_tip15));
        }else{
            helper.setText(R.id.tv_price, StringUtil.getFormatPrice(price));
            helper.setVisible(R.id.tv_price,true);
            helper.setText(R.id.tv_tip, WordUtil.getString(R.string.spread_tip19));
        }
          helper.setText(R.id.tv_detail,WordUtil.getString(R.string.spead_tip16,orderBean.getOrderId(),orderBean.getTime()));
    }
}

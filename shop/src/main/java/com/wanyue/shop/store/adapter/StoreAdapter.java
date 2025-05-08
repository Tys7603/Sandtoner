package com.wanyue.shop.store.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.bean.StoreBean;
import com.wanyue.shop.store.bean.GoodsSelectStoreBean;

import java.util.List;

/**
 * The type Store adapter.
 */
public class StoreAdapter extends BaseRecyclerAdapter<StoreBean, BaseReclyViewHolder> {
    /**
     * Instantiates a new Store adapter.
     *
     * @param data the data
     */
    public StoreAdapter(List<StoreBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_store_simple;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, StoreBean storeBean) {
        helper.setText(R.id.tv_store_name,storeBean.getName());
        helper.setText(R.id.tv_fans,"Followers :"+storeBean.getFansNum());
        helper.setText(R.id.tv_addr,storeBean.getAddr());
        helper.setImageUrl(storeBean.getAvatar(),R.id.img_store_avator);

        TextView tv_store_self=helper.getView(R.id.tv_store_self);
        int shoptype=storeBean.getShoptype();
        if(shoptype==2){
            ViewUtil.setVisibility(tv_store_self, ViewGroup.VISIBLE);
        }else{
            ViewUtil.setVisibility(tv_store_self, ViewGroup.GONE);
        }

    }
}

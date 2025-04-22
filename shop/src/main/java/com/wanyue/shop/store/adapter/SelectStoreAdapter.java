package com.wanyue.shop.store.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.store.bean.GoodsSelectStoreBean;
import java.util.List;


public class SelectStoreAdapter extends BaseRecyclerAdapter<GoodsSelectStoreBean, BaseReclyViewHolder> {

    public SelectStoreAdapter(List<GoodsSelectStoreBean> data) {
        super(data);
    }


    @Override
    public int getLayoutId() {
        return R.layout.item_recly_goods_select_store;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, GoodsSelectStoreBean storeBean) {
        helper.setText(R.id.tv_store_name,storeBean.getName());
        helper.setText(R.id.tv_fans,"粉丝 :"+storeBean.getFansNum());
        helper.setImageUrl(storeBean.getAvatar(),R.id.img_store_avator);
        View tvSelf=helper.getView(R.id.tv_store_self);
        if(storeBean.getShoptype()==2){
            ViewUtil.setVisibility(tvSelf,View.VISIBLE);
        }else{
            ViewUtil.setVisibility(tvSelf,View.INVISIBLE);
        }
        List<GoodsBean>list=storeBean.getSelectGoodsList();
        for(int i=0;i<4;i++){
            final GoodsBean item= ListUtil.safeGetData(list,i);
            if(i==0){
              setImageView(item,helper,R.id.img_thumb1);
            }else if(i==1){
                setImageView(item,helper,R.id.img_thumb2);
            }else if(i==2){
                setImageView(item,helper,R.id.img_thumb3);
            }else if(i==3){
                setImageView(item,helper,R.id.img_thumb4);
            }
        }
    }

    private void setImageView(GoodsBean item, BaseReclyViewHolder helper, int id) {
       ImageView img= helper.getView(id);
       if(img==null){
           return;
       }
        if(item!=null){
           ImgLoader.display(mContext,item.getThumb(),img);
        }else{
            img.setOnClickListener(null);
            ImgLoader.display(mContext,0,img);
        }
    }
}

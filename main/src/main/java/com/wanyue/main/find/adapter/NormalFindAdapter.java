package com.wanyue.main.find.adapter;

import android.content.Context;
import android.graphics.Outline;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.wanyue.common.adapter.base.BaseMutiRecyclerAdapter;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.business.list.ListHelper;
import com.wanyue.common.custom.ItemDecoration;
import com.wanyue.common.custom.refresh.ControllLayoutManager;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.live.bean.LiveBean;
import com.wanyue.main.R;
import com.wanyue.main.find.bean.FindBean;
import com.wanyue.main.find.bean.FindLiveBean;
import com.wanyue.shop.store.view.activity.BusniessStoreActivity;

import java.util.List;

public class NormalFindAdapter extends BaseMutiRecyclerAdapter<FindBean, BaseReclyViewHolder> {
    private Context mContext;
    private OnItemClickListener mPhotoListner;
    private OnItemClickListener mGoodsListner;
    private ListHelper<View,FindBean> mAttentHelper;
    private ListHelper<View,FindBean> mZanHelper;
    private ViewOutlineProvider mViewOutlineProvider;

    public NormalFindAdapter(List<FindBean> data, Context context) {
        super(data);
        mContext=context;
        addItemType(FindBean.TYPE_NEW, R.layout.item_recly_find_normal);
        addItemType(FindBean.TYPE_RECOMMEND, R.layout.item_recly_find_normal);
        addItemType(FindBean.TYPE_LIVE, R.layout.item_recly_find_live);

        mViewOutlineProvider=new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int radius= DpUtil.dp2px(5);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        };
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, FindBean findBean) {
        switch (helper.getItemViewType()){
            case FindBean.TYPE_NEW:
            case FindBean.TYPE_RECOMMEND:
                convertNormal(helper,findBean);
                break;
            case FindBean.TYPE_LIVE:
                convertLive(helper,findBean);
        }
    }

    private void convertLive(BaseReclyViewHolder helper, FindBean findBean) {
        FindLiveBean findLiveBean= (FindLiveBean) findBean;
        helper.setText(R.id.tv_read_num, WordUtil.getString(R.string.read_tip2,findBean.getLookNum()));

        convertCommon(helper,findBean);
        ImageView img1=helper.getView(R.id.img_thumb_1);
        ImageView img2=helper.getView(R.id.img_thumb_2);
        LiveBean liveBean=findLiveBean.getLiveBean();

        List<String>path=findBean.getPhotoList();
        String path1=ListUtil.safeGetData(path,0);
        String path2=ListUtil.safeGetData(path,1);
        String path3=ListUtil.safeGetData(path,2);

        View vpLiveContainer=helper.getView(R.id.vp_live_container);
        if(vpLiveContainer.getOutlineProvider()!=mViewOutlineProvider){
            vpLiveContainer.setClipToOutline(true);
            vpLiveContainer.setOutlineProvider(mViewOutlineProvider);
        }

        if(path1!=null){
            helper.setImageUrl(liveBean.getThumb(),R.id.img_live_thumb);
        }

        if(path2!=null){
            ViewUtil.setVisibility(img1,View.VISIBLE);
            helper.setImageUrl(path2,R.id.img_thumb_1);
        }else{
            ViewUtil.setVisibility(img1,View.GONE);
            img1.setImageResource(0);
        }

        if(path3!=null){
            ViewUtil.setVisibility(img2,View.VISIBLE);
            helper.setImageUrl(path3,R.id.img_thumb_2);
        }else{
            img2.setImageResource(0);
            ViewUtil.setVisibility(img2,View.GONE);
        }

    }

    private void convertNormal(BaseReclyViewHolder helper, FindBean findBean) {
        convertCommon(helper,findBean);
        helper.setText(R.id.tv_read_num, WordUtil.getString(R.string.read_tip1,findBean.getLookNum()));

        RecyclerView rcPhoto=helper.getView(R.id.rc_photo);
        if(rcPhoto.getAdapter()==null){
            rcPhoto.setClipToOutline(true);
            rcPhoto.setOutlineProvider(mViewOutlineProvider);
            FindPhotoAdapter photoAdapter=new FindPhotoAdapter(findBean.getPhotoList());
            photoAdapter.setOnItemClickListener(mPhotoListner);
            RxRefreshView.ReclyViewSetting.createGridSetting(mContext,3).settingRecyclerView(rcPhoto);
            rcPhoto.setAdapter(photoAdapter);
        }else{
            FindPhotoAdapter adapter= (FindPhotoAdapter) rcPhoto.getAdapter();
            adapter.setData(findBean.getPhotoList());
        }
    }

    public FindLiveBean convertCommon(BaseReclyViewHolder helper, FindBean findBean) {
        View btnFollow=helper.getView(R.id.btn_follow);
        if(mAttentHelper!=null){
           mAttentHelper.attach(btnFollow,findBean);
           mAttentHelper.toChange(btnFollow,findBean);
        }

        TextView tv_store_self=helper.getView(R.id.tv_store_self);
        int shoptype=findBean.getShoptype();
        if(shoptype==2){
            ViewUtil.setVisibility(tv_store_self, ViewGroup.VISIBLE);
        }else{
            ViewUtil.setVisibility(tv_store_self, ViewGroup.GONE);
        }
        View btnZan=helper.getView(R.id.btn_zan);
        if(mZanHelper!=null){
           mZanHelper.attach(btnZan,findBean);
           mZanHelper.toChange(btnZan,findBean);
        }

        helper.setOnChildClickListner(R.id.btn_follow,mOnClickListener);
        helper.setOnChildClickListner(R.id.btn_zan,mOnClickListener);


        helper.setText(R.id.tv_store_name,findBean.getStoreName());
        helper.setImageUrl(findBean.getStoreAvatar(),R.id.img_store_avator);


        helper.setText(R.id.tv_title,findBean.getContentTip());
        helper.setText(R.id.tv_time,findBean.getAddTime());

        helper.setOnChildClickListner(R.id.btn_zan,mOnClickListener);

        RecyclerView rcGoods=helper.getView(R.id.rc_goods);
        List<GoodsBean>goodsList=findBean.getGoodsList();
        if(ListUtil.haveData(goodsList)){
            ViewUtil.setVisibility(rcGoods, View.VISIBLE);
            if(rcGoods.getAdapter()==null){
                FindGoodsAdapter adapter=new FindGoodsAdapter(null);
                adapter.setOnItemClickListener(mGoodsListner);
                ControllLayoutManager linearLayoutManager=new ControllLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
                ItemDecoration decoration= new ItemDecoration(mContext, 0xffdd00, DpUtil.dp2px(5),  DpUtil.dp2px(5));
                rcGoods.addItemDecoration(decoration);
                rcGoods.setLayoutManager(linearLayoutManager);
                rcGoods.setAdapter(adapter);
                adapter.setData(goodsList);
            }else{
                FindGoodsAdapter adapter= (FindGoodsAdapter) rcGoods.getAdapter();
                adapter.setData(goodsList);
            }
        }else{
            ViewUtil.setVisibility(rcGoods, View.GONE);
        }
        return null;
    }

    public void setAttentHelper(ListHelper<View, FindBean> attentHelper) {
        this.mAttentHelper = attentHelper;
    }

    public void setmZanHelper(ListHelper<View, FindBean> mZanHelper) {
        this.mZanHelper = mZanHelper;
    }

    public void setGoodsListner(OnItemClickListener goodsListner) {
        mGoodsListner = goodsListner;
    }

    public void setPhotoListner(OnItemClickListener photoListner) {
        mPhotoListner = photoListner;
    }

    public interface GoodsOnItemClickListener{

    }
}

package com.wanyue.main.find.view.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.custom.CheckImageView;
import com.wanyue.common.custom.DrawableTextView;
import com.wanyue.common.custom.UIFactory;
import com.wanyue.common.custom.refresh.ControllLayoutManager;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.proxy.function.GalleryViewProxy;
import com.wanyue.common.server.observer.DialogObserver;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.main.R;
import com.wanyue.main.R2;
import com.wanyue.main.find.adapter.FindGoodsAdapter;
import com.wanyue.main.find.api.FindAPI;
import com.wanyue.main.find.bean.FindBean;
import com.wanyue.main.find.bean.FindLiveBean;
import com.wanyue.shop.bean.StoreBean;
import com.wanyue.shop.business.ShopEvent;
import com.wanyue.shop.store.api.StoreAPI;
import com.wanyue.shop.store.view.activity.StoreActivity;
import com.wanyue.shop.view.activty.GoodsDetailActivity;
import com.wanyue.shop.view.dialog.GalleryDialogFragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.OnClick;
import io.reactivex.observers.DefaultObserver;

public class FindDetailActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {
    private RoundedImageView imgTitleAvatar;
    private FrameLayout vpPhotoContainer;
    private LinearLayout vpContainer;
    private WebView mWbContent;
    private TextView mTvStoreNameTitle;
    private RoundedImageView mImgStoreAvator;
    private TextView mTvStoreName;
    private TextView mTvTime;
    private LinearLayout mBtnFollow;
    private TextView mTvNoFollow;
    private DrawableTextView mTvFollow;

    private FindBean mData;
    private BaseReclyViewHolder mBaseReclyHelper;


    @Override
    public void init() {
        String json=getIntent().getStringExtra(Constants.DATA);
        mData=JSON.parseObject(json,FindBean.class);
        mTvStoreNameTitle = (TextView) findViewById(R.id.tv_store_name_title);
        imgTitleAvatar = (RoundedImageView) findViewById(R.id.img_title_avatar);
        vpPhotoContainer = (FrameLayout) findViewById(R.id.vp_photo_container);
        mWbContent = findViewById(R.id.wb_content);

        mImgStoreAvator = (RoundedImageView) findViewById(R.id.img_store_avator);
        mTvStoreName = (TextView) findViewById(R.id.tv_store_name);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mBtnFollow = (LinearLayout) findViewById(R.id.btn_follow);
        mTvNoFollow = (TextView) findViewById(R.id.tv_no_follow);
        mTvFollow = (DrawableTextView) findViewById(R.id.tv_follow);

        vpContainer = findViewById(R.id.vp_container);
        mBaseReclyHelper=new BaseReclyViewHolder(vpContainer);
        convertCommon(mBaseReclyHelper,mData);
        initGallery();
        FindAPI.getFindDetail(mData.getId()).compose(this.<FindBean>bindToLifecycle())
                .subscribe(new DefaultObserver<FindBean>() {
                    @Override
                    public void onNext(@NonNull FindBean findBean) {
                        String conetnt=findBean.getContent();
                        if(!TextUtils.isEmpty(conetnt)){
                                ViewUtil.setVisibility(mWbContent,View.VISIBLE);
                                ViewUtil.settingWebView(mWbContent);
                                ViewUtil.loadHtml(mWbContent,conetnt);
                        }
                    }
                    @Override
                    public void onError(@NonNull Throwable throwable) {

                    }
                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void initGallery() {
        List<String>path=mData.getPhotoList();
        if(ListUtil.haveData(path)){
            final GalleryViewProxy galleryViewProxy=new GalleryViewProxy();
            galleryViewProxy.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                    List<String>path=galleryViewProxy.getData();
                    GalleryDialogFragment galleryDialogFragment=new GalleryDialogFragment();
                    galleryDialogFragment.setGalleryViewProxy(path,i,getViewProxyMannger());
                    galleryDialogFragment.show(FindDetailActivity.this.getSupportFragmentManager());
                }
            });
            galleryViewProxy.setCanScorll(true);
            galleryViewProxy.enableZoom(false);
            galleryViewProxy.setGravity(Gravity.RIGHT|Gravity.BOTTOM);
            galleryViewProxy.setScaleType(ImageView.ScaleType.CENTER_CROP);
            vpPhotoContainer.getLayoutParams().height= (int) (CommonAppConfig.getWindowHeight()*0.6F);
            getViewProxyMannger().addViewProxy(vpPhotoContainer,galleryViewProxy,galleryViewProxy.getDefaultTag());
            galleryViewProxy.setData(path,0);
        }
    }


    public void convertCommon(BaseReclyViewHolder helper, FindBean findBean) {
        ImgLoader.display(this,findBean.getStoreAvatar(),imgTitleAvatar);
        mTvStoreNameTitle.setText(findBean.getStoreName());


        TextView tv_store_self=helper.getView(R.id.tv_store_self);
        int shoptype=findBean.getShoptype();
        if(shoptype==2){
            ViewUtil.setVisibility(tv_store_self, ViewGroup.VISIBLE);
        }else{
            ViewUtil.setVisibility(tv_store_self, ViewGroup.GONE);
        }

        ImgLoader.display(this,findBean.getStoreAvatar(),mImgStoreAvator);
        mTvStoreName.setText(findBean.getStoreName());
        mTvTime.setText(findBean.getAddTime());
        boolean isFollow=findBean.getIsattent()==1;
        checkStoreFollow(isFollow);

        helper.setText(R.id.tv_title,findBean.getContentTip());
        helper.setText(R.id.tv_zan,findBean.getZanNum()+"");
        CheckImageView checkImageView=helper.getView(R.id.img_zan);
        checkImageView.setChecked(findBean.getIsZan()==1);
        helper.setText(R.id.tv_read_num, WordUtil.getString(R.string.read_tip1,findBean.getLookNum()));

        RecyclerView rcGoods=helper.getView(R.id.rc_goods);
        List<GoodsBean> goodsList=findBean.getGoodsList();
        if(ListUtil.haveData(goodsList)){
            ViewUtil.setVisibility(rcGoods, View.VISIBLE);
            if(rcGoods.getAdapter()==null){
                FindGoodsAdapter adapter=new FindGoodsAdapter(null){
                    @Override
                    public void setData(List<GoodsBean> data) {
                        super.setData(data);
                    }
                };
                adapter.setOnItemClickListener(this);
                adapter.setVertiral(true);
                RxRefreshView.ReclyViewSetting.createLinearSetting(this).settingRecyclerView(rcGoods);
                rcGoods.setAdapter(adapter);
                adapter.setData(goodsList);
            }else{
                FindGoodsAdapter adapter= (FindGoodsAdapter) rcGoods.getAdapter();
                adapter.setData(goodsList);
            }
        }else{
            ViewUtil.setVisibility(rcGoods, View.GONE);
        }
    }


    private void checkStoreFollow(boolean isFollow) {
        if(isFollow){
            ViewUtil.setVisibility(mTvFollow,View.VISIBLE);
            ViewUtil.setVisibility(mTvNoFollow,View.GONE);
        }else{
            ViewUtil.setVisibility(mTvNoFollow,View.VISIBLE);
            ViewUtil.setVisibility(mTvFollow,View.GONE);
        }
    }

    @OnClick(R2.id.btn_follow)
    public void follow(){
        if(mData==null){
            return;
        }
        final int type=mData.getIsattent()==1?0:1;
        StoreAPI.attenStore(mData.getStoreId(),type).subscribe(new DialogObserver<Boolean>(this) {
            @Override
            public void onNextTo(Boolean aBoolean) {
                if(!aBoolean){
                    return;
                }

                if(type==1){
                    ToastUtil.show("关注成功");
                }else{
                    ToastUtil.show("取消成功");
                }
                LiveEventBus.get(ShopEvent.FOLLOW_STORE).post(new StoreBean(mData.getStoreId(),type));
                mData.setIsattent(type);
                checkStoreFollow(type==1);
            }
        });
    }

    @OnClick(R2.id.btn_zan)
    public void zan(){
        if(mData==null){
           return;
        }

        final int type=mData.getIsZan()==1?0:1;
        FindAPI.findToZan(mData.getId(),type).subscribe(new DialogObserver<JSONObject>(this) {
            @Override
            public void onNextTo(JSONObject data) {
                mData.setIsZan(type);
                mData.setZanNum(data.getIntValue("likes"));
                LiveEventBus.get(ShopEvent.ZAN_STORE).post(mData);
                if(mBaseReclyHelper!=null){
                    mBaseReclyHelper.setText(R.id.tv_zan,mData.getZanNum()+"");
                    CheckImageView checkImageView=mBaseReclyHelper.getView(R.id.img_zan);
                    checkImageView.setChecked(mData.getIsZan()==1);
                }
            }
        });
    }


    @OnClick(R2.id.btn_title_store)
    public void toStore(){
        if(mData==null){
            return;
        }
        String id=mData.getStoreId();
        StoreActivity.forward(this,id);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_find_detail;
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }

    public static void forward(Context context, FindBean findBean){
        Intent intent=new Intent(context,FindDetailActivity.class);
        String json= JSON.toJSONString(findBean);
        intent.putExtra(Constants.DATA,json);
        context.startActivity(intent);
    }

    @Override
    public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
        GoodsBean goodsBean= (GoodsBean) baseQuickAdapter.getItem(i);
        GoodsDetailActivity.forward(this,goodsBean.getId());

    }
}
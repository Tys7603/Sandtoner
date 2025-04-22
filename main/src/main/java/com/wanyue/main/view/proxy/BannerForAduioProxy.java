package com.wanyue.main.view.proxy;

import android.graphics.Color;
import android.graphics.Outline;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.to.aboomy.pager2banner.Banner;
import com.to.aboomy.pager2banner.IndicatorView;
import com.wanyue.common.activity.WebViewActivity;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.live.activity.LiveAudienceActivity;
import com.wanyue.live.bean.LiveBean;
import com.wanyue.live.presenter.LiveRoomCheckLivePresenter;
import com.wanyue.main.R;
import com.wanyue.main.bean.ReCommentBean;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;

public class BannerForAduioProxy extends RxViewProxy {
    private Banner mBanner;
    private List<LiveBean> mData;
    private LiveAdapter mImageAdapter;
    private IndicatorView mIndicatorView;
    private LiveRoomCheckLivePresenter mCheckLivePresenter;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        initBanner();
    }


    public void setData(List<LiveBean> list) {
        mData=list;
        if(mImageAdapter!=null){
            mImageAdapter.setNewData(list);
        }
    }

    public void update(List<LiveBean> list){
        mData=list;
        if(mBanner!=null){
            mImageAdapter.setNewData(list);
        }
    }


    private void initBanner() {
        mBanner =  findViewById(R.id.banner);
        //设置banner样式(显示圆形指示器)
        mBanner.setClipToOutline(true);
        mBanner.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int radius= DpUtil.dp2px(5);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });

        if(mIndicatorView==null){
            mIndicatorView=findViewById(R.id.indicator);
            mIndicatorView.setIndicatorSelectorColor(Color.parseColor("#FF581E"));
            mIndicatorView.setIndicatorColor(Color.parseColor("#CCCCCC"));
            mIndicatorView .setIndicatorRadius(1.5f);
            mIndicatorView .setIndicatorSelectedRatio(2.5f);
            mIndicatorView .setIndicatorRatio(2.5f);
        }

        mImageAdapter= new LiveAdapter(null);
        mImageAdapter.setNewData(mData);
        mImageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                LiveBean liveBean=mImageAdapter.getItem(mBanner.getCurrentPager());
                if(mCheckLivePresenter==null){
                   mCheckLivePresenter=new LiveRoomCheckLivePresenter(getActivity(), new LiveRoomCheckLivePresenter.ActionListener() {
                        @Override
                        public void onLiveRoomChanged(LiveBean liveBean, String data) {
                            LiveAudienceActivity.forward(getActivity(),liveBean,data);
                        }
                    });
                }
                mCheckLivePresenter.checkLive(liveBean);
            }
        });

        mBanner.setAutoPlay(true).setIndicator(mIndicatorView,false)
                .setAdapter(mImageAdapter);
        mBanner.setAutoTurningTime(10000);
    }

    @Override
    public void onPause() {
        super.onPause();
        L.e("onPause=a=");
    }

    @Override
    public void onResume() {
        super.onResume();
        L.e("onResume=b=");
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_banner;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBanner!=null){
            mBanner.stopTurning();
        }
    }

    //或者使用其他三方框架，都是支持的，如：BRVAH
    public class LiveAdapter extends BaseRecyclerAdapter<LiveBean, BaseReclyViewHolder> {
        public LiveAdapter(List<LiveBean> data) {
            super(data);
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_recommend_live;
        }

        @Override
        protected void convert(BaseReclyViewHolder helper, LiveBean item) {
            helper.setText(R.id.tv_title,item.getTitle());
            helper.setText(R.id.tv_user_name,item.getUserNiceName());
            helper.setText(R.id.tv_zan,item.getLikes());
            helper.setText(R.id.live_num,item.getNums()+"人观看");
            helper.setImageUrl(item.getAvatar(),R.id.img_user_avatar);
            helper.setImageUrl(item.getThumb(),R.id.img_thumb);
            helper.setImageResouceId(R.drawable.icon_live_ing,R.id.img_living_tag);
        }
    }

    public void setIndicatorView(IndicatorView indicatorView) {
        mIndicatorView = indicatorView;
    }



}

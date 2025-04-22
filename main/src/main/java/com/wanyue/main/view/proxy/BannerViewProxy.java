package com.wanyue.main.view.proxy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.text.TextUtils;
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
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.main.R;
import com.wanyue.main.bean.IBanner;
import java.util.List;
import io.reactivex.Observable;

/**
 * The type Banner view proxy.
 *
 * @param <T> the type parameter
 */
/*banner封装*/
public  class BannerViewProxy< T extends IBanner> extends RxViewProxy {
    private Banner mBanner;
    private List<T> mData;
    private ImageAdapter2 mImageAdapter;
    private IndicatorView mIndicatorView;


    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        initBanner();
        requestData();
    }

    /**
     * Request data.
     */
    public void requestData() {
        Observable<List<T>> observable=getRequest();
        if(observable==null){
            return;
        }
        observable.compose(this.<List<T>>bindUntilOnDestoryEvent()).subscribe(new DefaultObserver<List<T>>() {
            @Override
            public void onNext(List<T> list) {
                setData(list);
            }
        });
    }

    /**
     * Sets data.
     *
     * @param list the list
     */
    public void setData(List<T> list) {
        mData=list;
        if(mImageAdapter!=null){
           mImageAdapter.setNewData(list);
        }
    }

    /**
     * Update.
     *
     * @param list the list
     */
    public void update(List<T> list){
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
            mIndicatorView=defaultIndicator();
        }

        mImageAdapter=new ImageAdapter2();
        mImageAdapter.setNewData(mData);
        mImageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(mImageAdapter==null){
                    return;
                }
                View tagView= view.findViewById(R.id.container);
                if(tagView==null){
                    return;
                }
                T t= (T) tagView.getTag();
                if(t==null){
                    return;
                }
                String url= t.getData();
                if(!TextUtils.isEmpty(url)&&url.contains("http")){
                    WebViewActivity.forward(getActivity(),url,false);
                }
            }
        });
        mBanner.setAutoPlay(true).setIndicator(mIndicatorView)
                .setAdapter(mImageAdapter);
        mBanner.setAutoTurningTime(10000);
    }

    private IndicatorView defaultIndicator() {
        IndicatorView indicator = new IndicatorView(getActivity())
                .setIndicatorColor(ResourceUtil.getColor(getActivity(),R.color.alpha_white_3f))
                .setIndicatorSelectorColor(Color.WHITE)
                .setIndicatorRatio(5f) //ratio，默认值是1 ，也就是说默认是圆点，根据这个值，值越大，拉伸越长，就成了矩形，小于1，就变扁了呗
                .setIndicatorRadius(2f) // radius 点的大小
                .setIndicatorSelectedRatio(5f)
                .setIndicatorSelectedRadius(2f)
                .setIndicatorStyle(IndicatorView.IndicatorStyle.INDICATOR_BIG_CIRCLE);
        return indicator;
    }

    /**
     * Get request observable.
     *
     * @return the observable
     */
    public  Observable<List<T>> getRequest(){
        return null;
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


    /**
     * The type Image adapter.
     */
//或者使用其他三方框架，都是支持的，如：BRVAH
    public class ImageAdapter extends BaseRecyclerAdapter<T, BaseReclyViewHolder> {
        /**
         * Instantiates a new Image adapter.
         *
         * @param data the data
         */
        public ImageAdapter(List<T> data) {
            super(data);
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_banner_image;
        }
        @Override
        protected void convert(BaseReclyViewHolder helper, T item) {
            helper.setImageUrl(item.getImageUrl(),R.id.img);
        }
    }

    /**
     * Sets indicator view.
     *
     * @param indicatorView the indicator view
     */
    public void setIndicatorView(IndicatorView indicatorView) {
        mIndicatorView = indicatorView;
    }

    /**
     * The type Image adapter 2.
     */
    public class ImageAdapter2 extends BaseQuickAdapter<T, BaseViewHolder> {
        /**
         * Instantiates a new Image adapter 2.
         */
        public ImageAdapter2() {
            super(R.layout.item_banner_image);
        }
        @Override
        protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
            BaseViewHolder baseViewHolder = super.onCreateDefViewHolder(parent, viewType);
            return baseViewHolder;
        }
        @Override
        protected void convert(@NonNull BaseViewHolder helper, T item) {
            Glide.with(mContext)
                    .load(item.getImageUrl())
                    .into((ImageView) helper.getView(R.id.img));

        }
    }


}

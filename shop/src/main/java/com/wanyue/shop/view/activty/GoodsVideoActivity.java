package com.wanyue.shop.view.activty;


import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.shop.R;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.video.bean.VideoBean;
import com.wanyue.video.components.view.VideoListProxy;
import java.util.List;
import io.reactivex.Observable;

/**
 * The type Goods video activity.
 */
public class GoodsVideoActivity extends BaseActivity {
    private ViewGroup mContainer;

    private String mId;
    @Override
    public void init() {
        setTabTitle("Product Related Videos");
        mId=getIntent().getStringExtra(Constants.KEY_ID);
        mContainer =  findViewById(R.id.container);
        VideoListProxy videoListProxy=new VideoListProxy() {
            @Override
            public Observable<List<VideoBean>> getData(int p) {
                return ShopAPI.getGoodsVideoList(p,mId).compose(this.<List<VideoBean>>bindToLifecycle());
            }
            @Override
            public String getKey() {
                return "productvideo";
            }
        };
        videoListProxy.setLoadByUserVisible(true);
        getViewProxyMannger().addViewProxy(mContainer,videoListProxy,videoListProxy.getDefaultTag());
    }

    /**
     * Forward.
     *
     * @param context the context
     * @param id      the id
     */
    public static void  forward(Context context, String id){
        Intent intent=new Intent(context,GoodsVideoActivity.class);
        intent.putExtra(Constants.KEY_ID,id);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_goods_video;
    }
}
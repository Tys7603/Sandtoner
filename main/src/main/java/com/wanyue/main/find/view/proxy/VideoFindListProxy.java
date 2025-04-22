package com.wanyue.main.find.view.proxy;

import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.adapter.base.BaseMutiRecyclerAdapter;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.utils.DpUtil;

import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.RouteUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.main.R;
import com.wanyue.main.find.adapter.FindVideoAdapter;
import com.wanyue.shop.view.activty.GoodsDetailActivity;
import com.wanyue.video.activity.VideoPlayActivity;
import com.wanyue.video.api.VideoAPI;
import com.wanyue.video.bean.VideoBean;
import com.wanyue.video.components.view.VideoListProxy;
import com.wanyue.video.utils.VideoStorge;

import java.util.List;
import io.reactivex.Observable;

/**
 * The type Video find list proxy.
 */
public class VideoFindListProxy extends RxViewProxy {
    private static final String KEY="VideoFindListProxy";
    private RxRefreshView<VideoBean> refreshView;
    private FindVideoAdapter mAdapter;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        refreshView = findViewById(R.id.refreshView);
        ViewUtil.setViewOutlineProvider(refreshView, DpUtil.dp2px(10));
        refreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.creatStaggeredGridSetting(getActivity(),2));
        refreshView.setEmptyLevel(1);
        refreshView.setNoDataTip("暂无数据，去其他频道看看吧～");

        mAdapter=new FindVideoAdapter(null);
        mAdapter.setOnItemChildClickListener2(new BaseMutiRecyclerAdapter.OnItemChildClickListener2<VideoBean>() {
            @Override
            public void onItemClick(int position, VideoBean findVideoBean, View view) {
               GoodsBean goodsBean= ListUtil.safeGetData(findVideoBean.getGoods(),0);
               if(goodsBean==null){
                   return;
               }
                RouteUtil.forwardGoodsDetailNoCart(goodsBean.getId(),findVideoBean.getUid());
            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                VideoPlayActivity.forward(getActivity(),i,KEY,1);
            }
        });

        refreshView.setAdapter(mAdapter);
        refreshView.setDataListner(new RxRefreshView.DataListner<VideoBean>() {
            @Override
            public Observable<List<VideoBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<VideoBean> data) {
                VideoStorge.getInstance().put(KEY,data);
            }
            @Override
            public void error(Throwable e) {
            }
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser&refreshView!=null){
            refreshView.initData();
        }
    }
    private Observable<List<VideoBean>> getData(int p) {
        return VideoAPI.getVideoList("0",p);

    }

    @Override

    public int getLayoutId() {
        return R.layout.view_find_video_list;
    }
}

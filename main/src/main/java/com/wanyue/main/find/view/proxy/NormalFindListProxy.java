package com.wanyue.main.find.view.proxy;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wanyue.common.adapter.base.BaseMutiRecyclerAdapter;
import com.wanyue.common.api.CommonAPI;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.business.list.ListHelper;
import com.wanyue.common.custom.CheckImageView;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.http.ParseSingleHttpCallback;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.server.observer.DialogObserver;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.RouteUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.live.activity.LiveAudienceActivity;
import com.wanyue.live.bean.LiveBean;
import com.wanyue.live.presenter.LiveRoomCheckLivePresenter;
import com.wanyue.main.R;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.find.adapter.FindPhotoAdapter;
import com.wanyue.main.find.adapter.NormalFindAdapter;
import com.wanyue.main.find.api.FindAPI;
import com.wanyue.main.find.bean.FindBean;
import com.wanyue.main.find.bean.FindLiveBean;
import com.wanyue.main.find.view.activity.FindDetailActivity;
import com.wanyue.shop.bean.StoreBean;
import com.wanyue.shop.business.ShopEvent;
import com.wanyue.shop.store.api.StoreAPI;
import com.wanyue.shop.view.activty.GoodsDetailActivity;
import com.wanyue.shop.view.dialog.GalleryDialogFragment;
import java.util.List;
import io.reactivex.Observable;

/**
 * The type Normal find list proxy.
 */
public class NormalFindListProxy  extends RxViewProxy {

    private RxRefreshView<FindBean> mRefreshView;
    private NormalFindAdapter mNormalFindAdapter;
    private int mType;

    private ListHelper <View, FindBean> mZanHelper;
    /**
     * The M att helper.
     */
    ListHelper <View, FindBean>mAttHelper;
    private LiveRoomCheckLivePresenter mCheckLivePresenter;

    private int mIconId;
    private String mTitle;
    private int mLevel;
    private  int mBackgroundColor;

    private boolean mFirstVisibility=true;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEmptyLevel(mLevel);
        mRefreshView.setIconId(mIconId);
        mRefreshView.setNoDataTip(mTitle);
        mRefreshView.setBackgroundColor(mBackgroundColor);

        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(getActivity()));
        ViewUtil.setViewOutlineProvider(mRefreshView, DpUtil.dp2px(15));
        mNormalFindAdapter=new NormalFindAdapter(null,getActivity());

        LiveEventBus.get(ShopEvent.FOLLOW_STORE, StoreBean.class).observe(getActivity(), new Observer<StoreBean>() {
            @Override
            public void onChanged(StoreBean storeBean) {
                onChangeByFollow(storeBean);
            }
        });

        LiveEventBus.get(ShopEvent.ZAN_STORE,FindBean.class).observe(getActivity(), new Observer<FindBean>() {
            @Override
            public void onChanged(FindBean findBean) {
                onChangeByZan(findBean);
            }
        });

        //关注
        mAttHelper=new ListHelper<View, FindBean>();
        mAttHelper.setmChangeListner(new ListHelper.OnDataChangeListner<View, FindBean>() {
            @Override
            public void toChange(View view, FindBean findBean) {
                View tvFollow=view.findViewById(R.id.tv_follow);
                View tvNoFollow=view.findViewById(R.id.tv_no_follow);
                boolean isFollow=findBean.getIsattent()==1;
                if(isFollow){
                    ViewUtil.setVisibility(tvFollow,View.VISIBLE);
                    ViewUtil.setVisibility(tvNoFollow,View.GONE);
                }else{
                    ViewUtil.setVisibility(tvNoFollow,View.VISIBLE);
                    ViewUtil.setVisibility(tvFollow,View.GONE);
                }
            }
            @Override
            public String getId(FindBean findBean) {
                return findBean.getStoreId();
            }
        });

        mNormalFindAdapter.setAttentHelper(mAttHelper);
        mZanHelper=new ListHelper<View, FindBean>();
        mZanHelper.setmChangeListner(new ListHelper.OnDataChangeListner<View, FindBean>() {
            @Override
            public void toChange(View view, FindBean findBean) {
                L.e("findBean=="+findBean.getZanNum());
                TextView tvZan=view.findViewById(R.id.tv_zan);
                tvZan.setText(findBean.getZanNum()+"");
                CheckImageView checkImageView=view.findViewById(R.id.img_zan);
                checkImageView.setChecked(findBean.getIsZan()==1);
            }

            @Override
            public String getId(FindBean findBean) {
                return findBean.getId();
            }
        });
        mNormalFindAdapter.setmZanHelper(mZanHelper);
        mNormalFindAdapter.setPhotoListner(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                FindPhotoAdapter adapter= (FindPhotoAdapter) baseQuickAdapter;
                List<String>path=adapter.getArray();
                GalleryDialogFragment galleryDialogFragment=new GalleryDialogFragment();
                galleryDialogFragment.setGalleryViewProxy(path,i,getViewProxyChildMannger());
                galleryDialogFragment.show(getActivity().getSupportFragmentManager());
            }
        });

        mNormalFindAdapter.setGoodsListner(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                GoodsBean goodsBean= (GoodsBean) baseQuickAdapter.getItem(i);
                String spreadUid=goodsBean.getSpreadUid();
                if(spreadUid!=null){
                   RouteUtil.forwardGoodsDetailNoCart(goodsBean.getId(),spreadUid);
                }else{
                    RouteUtil.forwardGoodsDetail(goodsBean.getId());
                }

            }
        });

        mNormalFindAdapter.setOnItemChildClickListener2(new BaseMutiRecyclerAdapter.OnItemChildClickListener2<FindBean>() {
            @Override
            public void onItemClick(int position, FindBean findBean, View view) {
                int id= view.getId();
                if(id==R.id.btn_follow){
                    toFollow(findBean);
                }else if(id==R.id.btn_zan){
                    toZan(findBean);
                }
            }
        });

        mNormalFindAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                FindBean findBean= mNormalFindAdapter.getItem(i);
                int type=findBean.getType();
                if(type==FindBean.TYPE_LIVE){
                    FindLiveBean findLiveBean= (FindLiveBean) findBean;
                    if(mCheckLivePresenter==null){
                       mCheckLivePresenter=new LiveRoomCheckLivePresenter(getActivity(), new LiveRoomCheckLivePresenter.ActionListener() {
                           @Override
                           public void onLiveRoomChanged(LiveBean liveBean, String data) {
                               LiveAudienceActivity.forward(getActivity(),liveBean,data);
                           }
                       });
                    }
                      mCheckLivePresenter.checkLive(findLiveBean.getLiveBean());
                }else {
                    FindDetailActivity.forward(getActivity(),findBean);
                }
            }
        });
        mRefreshView.setAdapter(mNormalFindAdapter);
        mRefreshView.setDataListner(new RxRefreshView.DataListner<FindBean>() {
            @Override
            public Observable<List<FindBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });
    }

    private void onChangeByFollow(StoreBean storeBean) {
        String storeid=storeBean.getId();
        if(mNormalFindAdapter==null){
            return;
        }
        List<FindBean>list=mNormalFindAdapter.getArray();
        for(FindBean item:list){
            if(StringUtil.equals(item.getStoreId(),storeid)){
                item.setIsattent(storeBean.getIsFollow());
            }
        }
        if(mAttHelper!=null){
           mAttHelper.notifyAllChange(storeid);
        }
    }

    private void onChangeByZan(FindBean findBean){
        if(mZanHelper!=null){
           mZanHelper.notifyAllChange(findBean.getId());
        }
    }

    private void toFollow(final FindBean findBean) {
        if(findBean instanceof FindLiveBean){
            attentLive(findBean);
        }else{
            attentNormal(findBean);
        }
    }

    private void attentNormal(final FindBean findBean) {
        final int targetType=findBean.getIsattent()==1?0:1;
        StoreAPI.attenStore(findBean.getStoreId(),targetType).compose(this.<Boolean>bindToLifecycle())
        .subscribe(new DialogObserver<Boolean>(getActivity()) {
            @Override
            public void onNextTo(Boolean aBoolean) {
                if(!aBoolean){
                    return;
                }
                if(targetType==1){
                   ToastUtil.show("关注成功");
                }else{
                    ToastUtil.show("取消成功");
                }

                findBean.setIsattent(targetType);
                LiveEventBus.get(ShopEvent.FOLLOW_STORE).post(new StoreBean(findBean.getStoreId(),targetType));
            }
        });
    }

    private void attentLive(final FindBean findBean) {
        final int type=findBean.getIsattent();
        CommonAPI.setAttention(type, findBean.getStoreId(), new ParseSingleHttpCallback<Integer>("isattent") {
            @Override
            public void onSuccess(Integer data) {
                if(data==1){
                    ToastUtil.show("关注成功");
                }else{
                    ToastUtil.show("取消成功");
                }

                findBean.setIsattent(data);
                LiveEventBus.get(ShopEvent.FOLLOW_STORE).post(new StoreBean(findBean.getStoreId(),data));
            }
        });
    }

    private void toZan(final FindBean findBean) {
        final int type=findBean.getIsZan()==1?0:1;
        FindAPI.findToZan(findBean.getId(),type).subscribe(new DialogObserver<JSONObject>(getActivity()) {
            @Override
            public void onNextTo(JSONObject data) {
                findBean.setIsZan(type);
                findBean.setZanNum(data.getIntValue("likes"));
                LiveEventBus.get(ShopEvent.ZAN_STORE).post(findBean);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser&&mFirstVisibility){

            initData();
        }
    }

    /**
     * Init data.
     */
    public void initData() {
        if(mRefreshView!=null){
           mRefreshView.initData();
        }
    }

    /**
     * Gets data.
     *
     * @param p the p
     * @return the data
     */
    public Observable<List<FindBean>> getData(int p) {
        return FindAPI.getFindList(mType,p).compose(this.<List<FindBean>>bindToLifecycle());
    }


    /**
     * Gets icon id.
     *
     * @return the icon id
     */
    public int getIconId() {
        return mIconId;
    }

    /**
     * Sets icon id.
     *
     * @param iconId the icon id
     */
    public void setIconId(int iconId) {
        mIconId = iconId;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Sets background color.
     *
     * @param backgroundColor the background color
     */
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    /**
     * Gets level.
     *
     * @return the level
     */
    public int getLevel() {
        return mLevel;
    }

    /**
     * Sets level.
     *
     * @param level the level
     */
    public void setLevel(int level) {
        mLevel = level;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(int type) {
        mType = type;
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_normal_find;
    }
}

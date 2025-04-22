package com.wanyue.main.store.view.proxy;

import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.Observer;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wanyue.common.adapter.base.BaseMutiRecyclerAdapter;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.server.observer.DialogObserver;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.main.R;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.business.MainEvent;
import com.wanyue.main.store.adapter.GoodsAddAdapter;
import com.wanyue.main.store.bean.ConsignMentGoodsBean;
import com.wanyue.shop.view.activty.GoodsDetailActivity;
import java.util.List;
import io.reactivex.Observable;

public class GoodsManngerAddViewProxy extends RxViewProxy implements BaseMutiRecyclerAdapter.OnItemChildClickListener2<ConsignMentGoodsBean>, BaseQuickAdapter.OnItemClickListener {
    private String mCid;
    private RxRefreshView<ConsignMentGoodsBean> mRefreshView;
    private GoodsAddAdapter mGoodsAddAdapter;
    private boolean mIsFirstVisible=true;
    private String mKeyword;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mRefreshView =  findViewById(R.id.refreshView);
        mGoodsAddAdapter=new GoodsAddAdapter(null);
        mRefreshView.setAdapter(mGoodsAddAdapter);
        mRefreshView.setIconId(R.drawable.icon_empty_no_goods);

        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(getActivity(),1));
        mRefreshView.setDataListner(new RxRefreshView.DataListner<ConsignMentGoodsBean>() {
            @Override
            public Observable<List<ConsignMentGoodsBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<ConsignMentGoodsBean> data) {
            }

            @Override
            public void error(Throwable e) {

            }
        });
        mGoodsAddAdapter.setOnItemChildClickListener2(this);
        mGoodsAddAdapter.setOnItemClickListener(this);

        LiveEventBus.get(MainEvent.ADD_GOODS,String.class).observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String id) {
                watchDelete(id);
            }
        });

    }

    private void watchDelete(String id) {
        List<ConsignMentGoodsBean>list=mGoodsAddAdapter==null?null:mGoodsAddAdapter.getArray();
        if(!ListUtil.haveData(list)){
            return;
        }
        int size=list.size();
        for(int i=0;i<size;i++){
            ConsignMentGoodsBean consignMentGoodsBean=list.get(i) ;
            if(StringUtil.equals(consignMentGoodsBean.getId(),id)){
                mGoodsAddAdapter.remove(i);
                break;
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!mIsFirstVisible||!isVisibleToUser){
            return;
        }
        mIsFirstVisible=false;
       initData();
    }

    public void initData() {
        if(mRefreshView!=null){
           mRefreshView.initData();
        }
    }

    public void setCid(String cid) {
        mCid = cid;
    }

    public void setKeyword(String keyword) {
        if(StringUtil.equals(mKeyword,keyword)){
            return;
        }
        mIsFirstVisible=true;
        mKeyword = keyword;
    }
    private Observable<List<ConsignMentGoodsBean>> getData(int p) {
        return MainAPI.waitConsignmentGoodsList(mCid,mKeyword,p);
    }
    @Override
    public int getLayoutId() {
        return R.layout.view_single_refresh;
    }
    @Override
    public void onItemClick(final int position, ConsignMentGoodsBean consignMentGoodsBean, View view) {
            if(consignMentGoodsBean==null){
                return;
            }
            MainAPI.addConsignmentGoods(consignMentGoodsBean.getId()).compose(this.<Boolean>bindToLifecycle()).subscribe(new DialogObserver<Boolean>(getActivity()) {
                @Override
                public void onNextTo(Boolean aBoolean) {
                    if(aBoolean&&mGoodsAddAdapter!=null){
                        mGoodsAddAdapter.remove(position);
                    }
                }
            });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        GoodsAddAdapter goodsAddAdapter= (GoodsAddAdapter) adapter;
        ConsignMentGoodsBean bean=goodsAddAdapter.getItem(position);
        GoodsDetailActivity.forward(getActivity(),bean.getId(),GoodsAddDetailBottomViewProxy.class);

    }
}

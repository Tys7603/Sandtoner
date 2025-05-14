package com.wanyue.shop.store.view.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ViewGroup;
import com.alibaba.fastjson.JSON;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.shop.R;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.GoodsSearchArgs;
import com.wanyue.shop.components.view.GoodsListProxy;
import com.wanyue.shop.store.api.StoreAPI;
import com.wanyue.shop.store.bean.ClassifyBean;
import com.wanyue.shop.view.view.SearchViewProxy;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;

public class SearchStoreActivity extends BaseActivity {
    private ViewGroup mVpContentContainer;
    private ViewGroup mVpSearchContainer;
    private ClassifyBean mClassifyBean;
    private GoodsSearchArgs mGoodsSearchArgs;
    private GoodsListProxy mGoodsSearchViewProxy;
    @Override
    public void init() {


        String json=getIntent().getStringExtra(Constants.DATA);
        if(!TextUtils.isEmpty(json)){
            mClassifyBean=JSON.parseObject(json,ClassifyBean.class);
        }
        mVpSearchContainer =  findViewById(R.id.vp_search_container);
        SearchViewProxy searchViewProxy=new SearchViewProxy();
        searchViewProxy.setSeacherListner(new SearchViewProxy.SeacherListner() {
            @Override
            public void search(String keyward) {
                if(mGoodsSearchArgs!=null){
                   mGoodsSearchArgs.keyword=keyward;
                }
                if(mGoodsSearchViewProxy!=null){
                    mGoodsSearchViewProxy.initData();
                }
            }
        });

        mGoodsSearchArgs=new GoodsSearchArgs();
        String id=mClassifyBean.getId();
        mGoodsSearchArgs.sid=mClassifyBean.getId();
        mGoodsSearchArgs.cid=mClassifyBean.getPid();
        mGoodsSearchArgs.mer_id=mClassifyBean.getStoreId();

        if(!TextUtils.isEmpty(id)){
            searchViewProxy.setHint("Current search category："+mClassifyBean.getName());
        }else{
            searchViewProxy.setHint("Search all products");
        }
        getViewProxyMannger().addViewProxy(mVpSearchContainer,searchViewProxy,searchViewProxy.getDefaultTag());
        mVpContentContainer =findViewById(R.id.vp_content_container);
        mGoodsSearchViewProxy=new GoodsListProxy(){
            @Override
            public Observable<List<GoodsBean>> getData(int p) {
                return SearchStoreActivity.this.getData(p);
            }
        };
        getViewProxyMannger().addViewProxy(mVpContentContainer,mGoodsSearchViewProxy,mGoodsSearchViewProxy.getDefaultTag());
        mGoodsSearchViewProxy.initData();
    }

    private Observable<List<GoodsBean>> getData(int p) {
       return StoreAPI.getProductList(mGoodsSearchArgs,p).compose(this.<List<GoodsBean>>bindToLifecycle());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_search_store;
    }

    public static void forward(Context context, ClassifyBean classifyBean){
        Intent intent=new Intent(context,SearchStoreActivity.class);
        String jons= JSON.toJSONString(classifyBean);
        intent.putExtra(Constants.DATA,jons);
        context.startActivity(intent);
    }
}
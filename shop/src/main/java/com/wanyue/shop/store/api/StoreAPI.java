package com.wanyue.shop.store.api;

import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.http.BaseHttpCallBack;
import com.wanyue.common.http.HttpClient;
import com.wanyue.common.server.MapBuilder;
import com.wanyue.common.server.RequestFactory;
import com.wanyue.shop.bean.FootSectionBean;
import com.wanyue.shop.bean.GoodsSearchArgs;
import com.wanyue.shop.bean.GoodsTypeBean;
import com.wanyue.shop.bean.StoreBean;
import com.wanyue.shop.store.bean.ClassifyBean;
import com.wanyue.shop.store.bean.GoodsSelectStoreBean;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;


public class StoreAPI {

    /**
     * 获取分类列表
     */

    public static Observable<List<ClassifyBean>> getCategory(String storeId,String  mer_id) {
        Map<String,Object> map= MapBuilder.factory().put("page",1).
                put("id",storeId).
                put("mer_id", mer_id).
                build();
        return RequestFactory.getRequestManager().get("category",map,ClassifyBean.class,false);
    }

    public static Observable<Boolean> attenStore(String id,int type) {
        Map<String,Object> map= MapBuilder.factory().
                put("shopid",id).
                put("type",type).
                build();
        return RequestFactory.getRequestManager().commit("shopsetattent",map,false);
    }

    //获取关注店铺
    public static Observable<List<StoreBean>>getFollowStore(int page){
        Map<String,Object> map= MapBuilder.factory().
                put("page",page).
                build();
        return RequestFactory.getRequestManager().get("shopattent",map,StoreBean.class,false);
    }


    //获取精选店铺
    public static Observable<List<GoodsSelectStoreBean>>getSelectedStore(int page){
        Map<String,Object> map= MapBuilder.factory().
                put("page",page).
                build();
        return RequestFactory.getRequestManager().get("business",map,GoodsSelectStoreBean.class,false);
    }




    //获取店铺详情
    public static Observable<StoreBean>getStoreDetail(String storeId){
        Map<String,Object> map= MapBuilder.factory().
                put("shopid",storeId).
                build();
        return RequestFactory.getRequestManager().valueGet("shophome",map,StoreBean.class,false);
    }

    //商品列表
    public static Observable<List<GoodsBean>> getProductList(GoodsSearchArgs goodsSearchArgs, int p){
        if(goodsSearchArgs==null){
           goodsSearchArgs=new GoodsSearchArgs();
        }
        Map<String,Object> map= MapBuilder.factory().put("page",p).
                put("sid",goodsSearchArgs.sid).
                put("cid",goodsSearchArgs.cid).
                put("cid",goodsSearchArgs.cid).
                put("keyword",goodsSearchArgs.keyword).
                put("order",goodsSearchArgs.order).
                put("mer_id",goodsSearchArgs.mer_id).
                build();
        return RequestFactory.getRequestManager().get("products",map, GoodsBean.class,false);
    }


}

package com.wanyue.main.find.api;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.server.MapBuilder;
import com.wanyue.common.server.RequestFactory;
import com.wanyue.common.utils.JsonUtil;
import com.wanyue.live.bean.LiveBean;
import com.wanyue.main.bean.FeatureBean;
import com.wanyue.main.find.bean.FindBean;
import com.wanyue.main.find.bean.FindLiveBean;
import com.wanyue.main.find.bean.FindVideoBean;
import com.wanyue.main.store.bean.ConsignMentGoodsBean;
import com.wanyue.shop.bean.StoreBean;
import com.wanyue.video.bean.VideoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class FindAPI {

    public static Observable<List<FindBean>> getFindList(final int type,int p){
        String router=null;
        if(type==0){
            router="kolfollow";
        }else if(type==FindBean.TYPE_NEW){
            router="kollist";
        }else if(type==FindBean.TYPE_RECOMMEND){
            router="kollist";
        }else{
            router="kollive";
        }
        Map<String,Object> map= MapBuilder.factory().put("page",p)
                .put("type",type)
                .build();
        return RequestFactory.getRequestManager().get(router,map, FindBean.class,false);

    }

    public static Observable<List<FindBean>> getFindLiveList(int p){
        Map<String,Object> map= MapBuilder.factory().put("page",p)
                .build();
        return RequestFactory.getRequestManager().get("kollive",map, FindLiveBean.class,false)
                .map(new Function<List<FindLiveBean>, List<FindBean>>() {
                    @Override
                    public List<FindBean> apply(@NonNull List<FindLiveBean> findLiveBeans) throws Exception {
                        List<FindBean>list=new ArrayList<>();
                        list.addAll(findLiveBeans);
                        return list;
                    }
                });
    }



    public static Observable<FindBean> getFindDetail(String id){
        Map<String,Object> map= MapBuilder.factory()
                .put("kolid",id)
                .build();
        return RequestFactory.getRequestManager().valueGet("kolinfo",map, FindBean.class,false);
    }



    //点赞
    public static Observable<JSONObject> findToZan(String kolid,int type ){
        Map<String,Object> map= MapBuilder.factory()
                .put("kolid",kolid)
                .put("type",type)
                .build();
        return RequestFactory.getRequestManager().valuePost("kollike",map,JSONObject.class,false);
    }




    public static Observable<List<FindVideoBean>> getFindList(int p){
        List<FindVideoBean> list= new ArrayList<FindVideoBean>();
        for(int i=0;i<9;i++){
            FindVideoBean findBean=new FindVideoBean();
            VideoBean videoBean=new VideoBean();
            videoBean.setTitle("视频标题");
            videoBean.setThumb("https://t7.baidu.com/it/u=2604797219,1573897854&fm=193&f=GIF");
            findBean.setVideoBean(videoBean);

            GoodsBean goodsBean=new GoodsBean();
            goodsBean.setName("好喝的饮料");
            goodsBean.setThumb("https://img0.baidu.com/it/u=2003447407,2898615048&fm=26&fmt=auto&gp=0.jpg");
            findBean.setGoodsBean(goodsBean);

            list.add(findBean);
        }
        return Observable.just(list);
    }


    /**
     * 获取分类下直播列表
     */

    public static Observable<List<FindBean>> getLiveListByClass(String id,int p){
        String path="classlive"+"/"+id;
        Map<String,Object>map=MapBuilder.factory().
                put("page",p).build();
        return RequestFactory.getRequestManager().get(path,map, FindLiveBean.class,false)
                .map(new Function<List<FindLiveBean>, List<FindBean>>() {
                          @Override
                          public List<FindBean> apply(@NonNull List<FindLiveBean> findLiveBeans) throws Exception {
                              List<FindBean>list=new ArrayList<>();
                              list.addAll(findLiveBeans);
                              return list;
                         }
                      });
    }

    /*获取精选列表*/
    public static Observable<List<FindBean>> getFeatured(int p){
        Map<String,Object>map=MapBuilder.factory().put("page",p).build();
        return RequestFactory.getRequestManager().valueGet("featured",map, JSONObject.class,false)
                .map(new Function<JSONObject, List<FindBean>>() {
                    @Override
                    public List<FindBean> apply(@NonNull JSONObject jsonObject) throws Exception {
                       JSONArray json= jsonObject.getJSONArray("list");
                       List<FindBean>list=new ArrayList<>();
                        List<FindLiveBean>temp= JsonUtil.getData(json,FindLiveBean.class);
                        list.addAll(temp);
                        return list;
                    }
                }) ;
    }


    /*获取关注下直播*/
    public static Observable<List<FindBean>> getLiveListByFollow(int p){
        Map<String,Object>map=MapBuilder.factory().put("page",p).build();
        return RequestFactory.getRequestManager().get("livefollow",map, FindLiveBean.class,false)
                .map(new Function<List<FindLiveBean>, List<FindBean>>() {
                    @Override
                    public List<FindBean> apply(@NonNull List<FindLiveBean> findLiveBeans) throws Exception {
                        List<FindBean>list=new ArrayList<>();
                        list.addAll(findLiveBeans);
                        return list;
                    }
                })
                ;
    }



    public static Observable<List<FindBean>> getLiveAboutGoods(int p,String id){
        Map<String,Object>map=MapBuilder.factory().put("page",p).
                put("productid",id).
                build();
        return RequestFactory.getRequestManager().get("productlive",map, FindLiveBean.class,false)
                .map(new Function<List<FindLiveBean>, List<FindBean>>() {
                    @Override
                    public List<FindBean> apply(@NonNull List<FindLiveBean> findLiveBeans) throws Exception {
                        List<FindBean>list=new ArrayList<>();
                        list.addAll(findLiveBeans);
                        return list;
                    }
                })
                ;
    }
}

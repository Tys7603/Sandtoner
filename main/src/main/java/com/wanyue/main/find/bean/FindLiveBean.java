package com.wanyue.main.find.bean;


import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.live.bean.LiveBean;

import java.util.List;

public class FindLiveBean extends FindBean {

    @JSONField(name = "thumb")
    @SerializedName( "thumb")
    private String liveThumb;

    private String pull;
    private String stream;
    private String uid;

    private boolean addSpread;

    private LiveBean mLiveBean;
    public FindLiveBean() {
        type=FindBean.TYPE_LIVE;
    }

    public String getLiveThumb() {
        return liveThumb;
    }

   
    public void setLiveThumb(String liveThumb) {
        this.liveThumb = liveThumb;
    }

   
    public String getPull() {
        return pull;
    }

   
    public void setPull(String pull) {
        this.pull = pull;
    }

   
    public String getStream() {
        return stream;
    }

   
    public void setStream(String stream) {
        this.stream = stream;
    }

   
    public String getUid() {
        return uid;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    public LiveBean getLiveBean() {
        if(mLiveBean==null){
            mLiveBean=new LiveBean();
            mLiveBean.setThumb(liveThumb);
            mLiveBean.setPull(pull);
            mLiveBean.setStream(stream);
            mLiveBean.setUid(uid);
            mLiveBean.setAvatar(storeAvatar);
            mLiveBean.setUserNiceName(storeName);
            mLiveBean.setTitle(title);
        }
        storeId=uid;
        return mLiveBean;
    }

    public void setLiveBean(LiveBean liveBean) {
        mLiveBean = liveBean;
    }

    @Override
    public String getStoreId() {
        return uid;
    }
    @Override
    public int getItemType() {
        return FindLiveBean.TYPE_LIVE;
    }


    @Override
    public List<GoodsBean> getGoodsList() {
        if(!addSpread){ //添加推广ID
            int size= ListUtil.size(goodsList);
            for(int i=0;i<size;i++){
               GoodsBean item= goodsList.get(i);
               item.setSpreadUid(uid);
            }
        }
        return super.getGoodsList();
    }

    @Override
    public void setGoodsList(List<GoodsBean> goodsList) {
        super.setGoodsList(goodsList);
        addSpread=false;
    }

    @Override
    public int getType() {
        return FindLiveBean.TYPE_LIVE;
    }
}

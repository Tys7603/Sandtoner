package com.wanyue.main.find.bean;

import com.wanyue.common.bean.GoodsBean;
import com.wanyue.video.bean.VideoBean;

public class FindVideoBean {
    private VideoBean videoBean;
    private GoodsBean goodsBean;

    public VideoBean getVideoBean() {
        return videoBean;
    }
    public void setVideoBean(VideoBean videoBean) {
        this.videoBean = videoBean;
    }
    public GoodsBean getGoodsBean() {
        return goodsBean;
    }

    public void setGoodsBean(GoodsBean goodsBean) {
        this.goodsBean = goodsBean;
    }
}

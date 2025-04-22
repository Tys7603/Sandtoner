package com.wanyue.main.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.course.bean.CourseBean;
import com.wanyue.live.bean.LiveBean;
import com.wanyue.video.bean.VideoBean;
import java.util.List;
public class HomePageBean {
    @SerializedName("banner")
    @JSONField(name="banner")
    private List<BannerBean>bannerList;

    @SerializedName("live")
    @JSONField(name="live")
    private List<LiveBean> recommendLiveList;//推荐直播


    @SerializedName("video")
    @JSONField(name="video")
    private List<VideoBean> recommendVideoList;//推荐视频

    @SerializedName("course")
    @JSONField(name="course")
    private List<CourseBean> mCourseBeanList;//推荐课程


    @SerializedName("adv_space")
    @JSONField(name="adv_space")
    private AdvBean adv;//广告


    private int style;

    @SerializedName("list")
    @JSONField(name="list")
    private List<GoodsBean>goodsList;


    @SerializedName("menus")
    @JSONField(name="menus")
    private List<ReCommentBean> classList; //栏目


    @SerializedName("module")
    @JSONField(name="module")
    private List<MoudleBean>moudleList;


    public List<LiveBean> getRecommendLiveList() {
        return recommendLiveList;
    }
    public void setRecommendLiveList(List<LiveBean> recommendLiveList) {
        this.recommendLiveList = recommendLiveList;
    }
    public List<VideoBean> getRecommendVideoList() {
        return recommendVideoList;
    }
    public void setRecommendVideoList(List<VideoBean> recommendVideoList) {
        this.recommendVideoList = recommendVideoList;
    }
    public List<CourseBean> getCourseBeanList() {
        return mCourseBeanList;
    }
    public void setCourseBeanList(List<CourseBean> courseBeanList) {
        mCourseBeanList = courseBeanList;
    }
    public AdvBean getAdv() {
        return adv;
    }
    public void setAdv(AdvBean adv) {
        this.adv = adv;
    }

    public List<GoodsBean> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<GoodsBean> goodsList) {
        this.goodsList = goodsList;
    }

    public List<ReCommentBean> getClassList() {
        return classList;
    }

    public void setClassList(List<ReCommentBean> classList) {
        this.classList = classList;
    }

    public List<BannerBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BannerBean> bannerList) {
        this.bannerList = bannerList;
    }


    public List<MoudleBean> getMoudleList() {
        int size= ListUtil.size(moudleList);
        for(int i=0;i<size;i++){
            MoudleBean moudleBean=  moudleList.get(i);
            if(style==2&&i==0){
                moudleBean.setType(2);
            }else{
                moudleBean.setType(1);
            }
        }
        return moudleList;
    }

    public void setMoudleList(List<MoudleBean> moudleList) {
        this.moudleList = moudleList;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
}

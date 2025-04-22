package com.wanyue.main.bean;

import com.wanyue.common.bean.LiveClassBean;
import com.wanyue.live.bean.LiveBean;

import java.util.List;

/**
 * The type Feature bean.
 */
public class FeatureBean {
    private List<BannerBean>banner;
    private List<LiveClassBean>liveclass;
    private List<LiveBean>list;
    //推荐and活动
    private List<ReCommentBean>navlist;
    //推荐直播
    private List<LiveBean>recommendList;
    private List<LiveBean>wondervideolist;

    /**
     * Gets navlist.
     *
     * @return the navlist
     */
    public List<ReCommentBean> getNavlist() {
        return navlist;
    }

    /**
     * Sets navlist.
     *
     * @param navlist the navlist
     */
    public void setNavlist(List<ReCommentBean> navlist) {
        this.navlist = navlist;
    }


    /**
     * Gets banner.
     *
     * @return the banner
     */
    public List<BannerBean> getBanner() {
        return banner;
    }

    /**
     * Sets banner.
     *
     * @param banner the banner
     */
    public void setBanner(List<BannerBean> banner) {
        this.banner = banner;
    }

    /**
     * Gets liveclass.
     *
     * @return the liveclass
     */
    public List<LiveClassBean> getLiveclass() {
        return liveclass;
    }

    /**
     * Sets liveclass.
     *
     * @param liveclass the liveclass
     */
    public void setLiveclass(List<LiveClassBean> liveclass) {
        this.liveclass = liveclass;
    }

    /**
     * Gets list.
     *
     * @return the list
     */
    public List<LiveBean> getList() {
        return list;
    }

    /**
     * Sets list.
     *
     * @param list the list
     */
    public void setList(List<LiveBean> list) {
        this.list = list;
    }

    /**
     * Gets recommend list.
     *
     * @return the recommend list
     */
    public List<LiveBean> getRecommendList() {
        return recommendList;
    }

    /**
     * Sets recommend list.
     *
     * @param recommendList the recommend list
     */
    public void setRecommendList(List<LiveBean> recommendList) {
        this.recommendList = recommendList;
    }

    /**
     * Gets wondervideolist.
     *
     * @return the wondervideolist
     */
    public List<LiveBean> getWondervideolist() {
        return wondervideolist;
    }

    /**
     * Sets wondervideolist.
     *
     * @param wondervideolist the wondervideolist
     */
    public void setWondervideolist(List<LiveBean> wondervideolist) {
        this.wondervideolist = wondervideolist;
    }
}

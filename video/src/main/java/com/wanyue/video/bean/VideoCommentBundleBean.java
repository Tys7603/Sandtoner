package com.wanyue.video.bean;

import java.util.List;

public class VideoCommentBundleBean {
    private int ismore;
    private List<VideoCommentChildBean>list;

    public int getIsmore() {
        return ismore;
    }
    public void setIsmore(int ismore) {
        this.ismore = ismore;
    }
    public List<VideoCommentChildBean> getList() {
        return list;
    }
    public void setList(List<VideoCommentChildBean> list) {
        this.list = list;
    }
}

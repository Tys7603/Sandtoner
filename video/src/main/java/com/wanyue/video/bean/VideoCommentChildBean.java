package com.wanyue.video.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

public class VideoCommentChildBean extends VideoCommentBean{

    @JSONField(serialize =false)
    protected VideoCommentBundleBean bundle;
}

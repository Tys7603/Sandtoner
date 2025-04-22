package com.wanyue.course.bean;

import android.text.TextUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;
import com.wanyue.course.R;
import com.wanyue.course.busniess.CourseConstants;
import com.wanyue.course.busniess.UIFactory;

public class CourseLevel1Bean implements MultiItemEntity {
    public static final String TAG_LAST_STUDEYED="/t~0~";

    public static final int STATUS_NORMAL=0; //普通状态
    public static final int STATUS_TRY=1; //试学
    public static final int STATUS_STUDYED=2;//已经学完
    public static final int STATUS_LAST=5;//已经学完
    public static final int STATUS_LIVING=3; //是否是在直播
    public static final int STATUS_LOCK=4; //等待解锁

    public static final int LEVEL_TYPE=1;
    private String id;
    private String pid;
    private int type;
    private String name;
    private String des;
    private String url;



    private int istrial;
    private int islive;
    private String starttime;
    private int status;
    private String time_date;
    private int isenter;
    private int islast;


    @SerializedName("tx_fileid")
    @JSONField(name = "tx_fileid")
    private String fileId;

    @SerializedName("content_url")
    @JSONField(name = "content_url")
    private String contentUrl;




    private CharSequence imageName;
    @Override
    public int getItemType() {
        return LEVEL_TYPE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
       return name;
    }


    public CharSequence getImageName(){
        if(name==null){
           name="";
        }
        if(islast==1&&!name.toString().contains(TAG_LAST_STUDEYED)){
           imageName= UIFactory.getTypeSPanTag(name,TAG_LAST_STUDEYED,13, R.drawable.icon_last_study);
        }else{
            imageName=name;
        }
        return imageName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }


    public String getUrl() {
        if(!TextUtils.isEmpty(fileId)){
            return fileId;
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIstrial() {
        return istrial;
    }

    public void setIstrial(int istrial) {
        this.istrial = istrial;
    }

    public int getIslive() {
        return islive;
    }

    public void setIslive(int islive) {
        this.islive = islive;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /*是否允许进行下一步的操作*/
    public boolean isEnable(boolean ifBuy) {
        if(!ifBuy&&istrial==0){
            return false;
        }

        if(istrial==1){
            return true;
        }

        if(status==STATUS_LOCK){
            return false;
        }

        if(isLiveType()&&islive==0){
            return false;
        }

        return true;
    }
    public boolean isLiveType(){
      return   (type== CourseConstants.LESSON_TYPE_LIVE_AUDIO||
                type==CourseConstants.LESSON_TYPE_LIVE_VIDEO||
                type==CourseConstants.LESSON_TYPE_LIVE_PPT||
                type==CourseConstants.LESSON_TYPE_LIVE_TEACHING||
                type==CourseConstants.LESSON_TYPE_LIVE_NORMAL);

    }

    public ContentBean parseContentBean(CourseBean courseBean){
        ContentBean contentBean=new ContentBean();
        contentBean.setTitle(getName());
        contentBean.setId(courseBean.getId());
        contentBean.setLessionId(id);
        contentBean.setThumb(courseBean.getThumb());
        contentBean.setContentType(CourseConstants.CONTENT_VIDEO);
        contentBean.setUrl(getUrl());
        contentBean.setContentType(type);
        contentBean.setAddTime(time_date);
        contentBean.setIntroduce(getDes());
        contentBean.setContentUrl(contentUrl);
        return contentBean;
    }

    private String getNormalName() {
      String title=name!=null?name.toString():"";
      int index=title.indexOf(TAG_LAST_STUDEYED);
      if(title.contains(TAG_LAST_STUDEYED)){
        title=title.replace(TAG_LAST_STUDEYED,"");
      }
      return title;
    }


    public int getIsenter() {
        return isenter;
    }
    public void setIsenter(int isenter) {
        this.isenter = isenter;
    }
    public int getIslast() {
        return islast;
    }
    public void setIslast(int islast) {
        this.islast = islast;
    }

    public String getTime_date() {
        return time_date;
    }

    public void setTime_date(String time_date) {
        this.time_date = time_date;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }


    public String getContentUrl() {
        return contentUrl;
    }
    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }



}

package com.wanyue.course.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

public class ContentBean extends ProjectBean implements Parcelable{
    @SerializedName("type")
    @JSONField(name="type")
    private int contentType;

    private int trialtype; //试学方式
    private String trialval; //试学附加数据

    @SerializedName("add_time")
    @JSONField(name="add_time")
    private String addTime;
    private String url;
    private String lesson;

    private String contentUrl;

    private boolean isTrial;


    public ContentBean(){

    }

    protected ContentBean(Parcel in) {
        super(in);
        contentType = in.readInt();
        trialtype = in.readInt();
        trialval = in.readString();
        addTime = in.readString();
        url = in.readString();
        lesson = in.readString();
        contentUrl= in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeInt(contentType);
        dest.writeInt(trialtype);
        dest.writeString(trialval);
        dest.writeString(addTime);
        dest.writeString(url);
        dest.writeString(lesson);
        dest.writeString(contentUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContentBean> CREATOR = new Creator<ContentBean>() {
        @Override
        public ContentBean createFromParcel(Parcel in) {
            return new ContentBean(in);
        }

        @Override
        public ContentBean[] newArray(int size) {
            return new ContentBean[size];
        }
    };

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public int getContentType() {
        return contentType;
    }
    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getTrialtype() {
        return trialtype;
    }

    public void setTrialtype(int trialtype) {
        this.trialtype = trialtype;
    }

    public String getTrialval() {
        return trialval;
    }

    public void setTrialval(String trialval) {
        this.trialval = trialval;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}

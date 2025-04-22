package com.wanyue.course.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

public class CourseBean extends ProjectBean  {
    private String studyProgress;
    private int classHour;
    private int mode;


    @SerializedName("lessons")
    @JSONField(name="lessons")
    private String lesson;

    public CourseBean(){

    }
    protected CourseBean(Parcel in) {
        super(in);
        studyProgress = in.readString();
        classHour = in.readInt();
        mode= in.readInt();
        lesson = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeString(studyProgress);
        dest.writeInt(classHour);
        dest.writeInt(mode);
        dest.writeString(lesson);

    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<CourseBean> CREATOR = new Parcelable.Creator<CourseBean>() {
        @Override
        public CourseBean createFromParcel(Parcel in) {
            return new CourseBean(in);
        }

        @Override
        public CourseBean[] newArray(int size) {
            return new CourseBean[size];
        }
    };


    public String getLesson() {
        if(TextUtils.isEmpty(lesson)){
            lesson="0课时";
        }
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getStudyProgress() {
        return studyProgress;
    }

    public void setStudyProgress(String studyProgress) {
        this.studyProgress = studyProgress;
    }

    public int getClassHour() {
        return classHour;
    }

    public void setClassHour(int classHour) {
        this.classHour = classHour;
    }

}

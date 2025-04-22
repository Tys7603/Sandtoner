package com.wanyue.course.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.bean.commit.CommitEntity;

/*教师简历*/
public class ResumeBean extends CommitEntity implements Parcelable {
    private String school; //毕业院校

    @SerializedName("experience")
    @JSONField(name = "experience")
    private String teachingExperience; //教学经历
    @JSONField(name = "feature")
    @SerializedName("feature")
    private String  peculiarity; //教学特点;

    public ResumeBean() {

    }
    public ResumeBean(Parcel in) {
        school = in.readString();
        teachingExperience = in.readString();
        peculiarity = in.readString();
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
        observer();
    }

    public String getTeachingExperience() {
        return teachingExperience;
    }

    public void setTeachingExperience(String teachingExperience) {
        this.teachingExperience = teachingExperience;
        observer();
    }

    public String getPeculiarity() {
        return peculiarity;
    }

    public void setPeculiarity(String peculiarity) {
        this.peculiarity = peculiarity;
        observer();
    }

    @Override
    public boolean observerCondition() {
        return fieldNotEmpty(school)&&fieldNotEmpty(teachingExperience)&&fieldNotEmpty(peculiarity);
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(school);
        dest.writeString(teachingExperience);
        dest.writeString(peculiarity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResumeBean> CREATOR = new Creator<ResumeBean>() {
        @Override
        public ResumeBean createFromParcel(Parcel in) {
            return new ResumeBean(in);
        }

        @Override
        public ResumeBean[] newArray(int size) {
            return new ResumeBean[size];
        }
    };
}

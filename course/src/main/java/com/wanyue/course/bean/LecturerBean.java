package com.wanyue.course.bean;

import android.os.Parcel;

import com.wanyue.common.bean.UserBean;

public class LecturerBean extends  UserBean  {
    public static final int STUDENT=0;
    public static final int LECTURER=1;

    private String  introduce; //简历
    private Identity[] identitys; //教师身份哦
    private int type;
    private String experience;
    private String feature;

    private int isMainLecturer;

    public LecturerBean(){
    }

    protected LecturerBean(Parcel in) {
        super(in);
        introduce = in.readString();
        identitys = in.createTypedArray(Identity.CREATOR);
        type = in.readInt();
        experience = in.readString();
        feature = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeString(introduce);
        dest.writeTypedArray(identitys, flags);
        dest.writeInt(type);
        dest.writeString(experience);
        dest.writeString(feature);
    }


    public ResumeBean getResumeBean(){
        ResumeBean resumeBean=new ResumeBean();
        resumeBean.setPeculiarity(feature);
        resumeBean.setTeachingExperience(experience);
        return resumeBean;
    }

    public int getIsMainLecturer() {
        return isMainLecturer;
    }

    public void setIsMainLecturer(int isMainLecturer) {
        this.isMainLecturer = isMainLecturer;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LecturerBean> CREATOR = new Creator<LecturerBean>() {
        @Override
        public LecturerBean createFromParcel(Parcel in) {
            return new LecturerBean(in);
        }

        @Override
        public LecturerBean[] newArray(int size) {
            return new LecturerBean[size];
        }
    };

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Identity[] getIdentitys() {
        return identitys;
    }

    public void setIdentitys(Identity[] identitys) {
        this.identitys = identitys;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getExperience() {
        return experience;
    }
    public void setExperience(String experience) {
        this.experience = experience;
    }
    public String getFeature() {
        return feature;
    }
    public void setFeature(String feature) {
        this.feature = feature;
    }
}

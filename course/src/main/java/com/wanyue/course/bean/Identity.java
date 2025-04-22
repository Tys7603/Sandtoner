package com.wanyue.course.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;

public  class Identity implements Parcelable {
        private String id;
        private String name;
        @SerializedName("colour")
        @JSONField(name="colour")
        private String color;

        public Identity(){

        }
        protected Identity(Parcel in) {
            id = in.readString();
            name = in.readString();
            color = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(name);
            dest.writeString(color);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Identity> CREATOR = new Creator<Identity>() {
            @Override
            public Identity createFromParcel(Parcel in) {
                return new Identity(in);
            }

            @Override
            public Identity[] newArray(int size) {
                return new Identity[size];
            }
        };

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
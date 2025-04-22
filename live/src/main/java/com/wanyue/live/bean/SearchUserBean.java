package com.wanyue.live.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.wanyue.common.bean.UserBean;

/**
 * 2018/9/29.
 */
public class SearchUserBean extends UserBean {

    private int attention;

    /**
     * Gets attention.
     *
     * @return the attention
     */
    @JSONField(name = "isattention")
    public int getAttention() {
        return attention;
    }

    /**
     * Sets attention.
     *
     * @param attention the attention
     */
    @JSONField(name = "isattention")
    public void setAttention(int attention) {
        this.attention = attention;
    }

    /**
     * Instantiates a new Search user bean.
     */
    public SearchUserBean() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.attention);
    }

    /**
     * Instantiates a new Search user bean.
     *
     * @param in the in
     */
    public SearchUserBean(Parcel in) {
        super(in);
        this.attention = in.readInt();
    }

    /**
     * The constant CREATOR.
     */
    public static final Parcelable.Creator<SearchUserBean> CREATOR = new Parcelable.Creator<SearchUserBean>() {
        @Override
        public SearchUserBean[] newArray(int size) {
            return new SearchUserBean[size];
        }

        @Override
        public SearchUserBean createFromParcel(Parcel in) {
            return new SearchUserBean(in);
        }
    };
}

package com.wanyue.video.bean;

import android.os.Parcel;
import android.os.Parcelable;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.bean.UserBean;
import com.wanyue.common.utils.ListUtil;

import java.util.List;

public class VideoBean implements Parcelable {
    private String id;
    private String uid;
    @SerializedName("name")
    @JSONField(name = "name")
    private String title;
    private String thumb;

    @SerializedName("url")
    @JSONField(name = "url")
    private String href;


    @SerializedName("likes")
    @JSONField(name = "likes")
    private String likeNum;


    @SerializedName("plays")
    @JSONField(name = "plays")
    private String viewNum;


    @SerializedName("comments")
    @JSONField(name = "comments")
    private String commentNum;
    private String stepNum;


    @SerializedName("shares")
    @JSONField(name = "shares")
    private String shareNum;

    private String addtime;
    private String lat;
    private String lng;
    private String city;

    private String datetime;
    private String distance;
    private int step;//是否踩过

    @SerializedName("islike")
    @JSONField(name = "islike")
    private int like;//是否赞过

    private int attent;//是否关注过作者
    private int status;
    private GoodsBean goodsinfo;
    private List<GoodsBean> goods;
    private int goodsid;

    private UserBean userBean;
    private String nickname;
    private String avatar;

    public VideoBean() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }


    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }


    public String getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(String likeNum) {
        this.likeNum = likeNum;
    }


    public String getViewNum() {
        return viewNum;
    }

    public void setViewNum(String viewNum) {
        this.viewNum = viewNum;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public String getStepNum() {
        return stepNum;
    }

    public void setStepNum(String stepNum) {
        this.stepNum = stepNum;
    }

    public String getShareNum() {
        return shareNum;
    }

    public void setShareNum(String shareNum) {
        this.shareNum = shareNum;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



    public UserBean getUserBean() {
        if(userBean==null){
           userBean=new UserBean();
           userBean.setId(uid);
           userBean.setUserNiceName(nickname);
           userBean.setAvatar(avatar);
        }
        return userBean;
    }


    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }


    public int getStep() {
        return step;
    }


    public void setStep(int step) {
        this.step = step;
    }


    public int getLike() {
        return like;
    }


    public void setLike(int like) {
        this.like = like;
    }


    public int getAttent() {
        return attent;
    }


    public void setAttent(int attent) {
        this.attent = attent;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.uid);
        dest.writeString(this.title);
        dest.writeString(this.thumb);
        dest.writeString(this.href);
        dest.writeString(this.likeNum);
        dest.writeString(this.viewNum);
        dest.writeString(this.commentNum);
        dest.writeString(this.stepNum);
        dest.writeString(this.shareNum);
        dest.writeString(this.addtime);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.city);
        dest.writeString(this.datetime);
        dest.writeInt(this.like);
        dest.writeInt(this.attent);
        dest.writeString(this.distance);
        dest.writeInt(this.step);
        dest.writeParcelable(this.userBean, flags);
        dest.writeInt(this.status);
        dest.writeInt(this.goodsid);
        dest.writeParcelable(this.goodsinfo, flags);

    }

    public VideoBean(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.title = in.readString();
        this.thumb = in.readString();
        this.href = in.readString();
        this.likeNum = in.readString();
        this.viewNum = in.readString();
        this.commentNum = in.readString();
        this.stepNum = in.readString();
        this.shareNum = in.readString();
        this.addtime = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.city = in.readString();
        this.datetime = in.readString();
        this.like = in.readInt();
        this.attent = in.readInt();
        this.distance = in.readString();
        this.step = in.readInt();
        this.goodsid = in.readInt();
        this.userBean = in.readParcelable(UserBean.class.getClassLoader());
        this.status = in.readInt();
        this.goodsinfo = in.readParcelable(GoodsBean.class.getClassLoader());
    }


    public GoodsBean getGoodsinfo() {
        if(goodsinfo==null){
           goodsinfo= ListUtil.safeGetData(goods,0);
        }
        return goodsinfo;
    }

    public void setGoodsinfo(GoodsBean goodsinfo) {
        this.goodsinfo = goodsinfo;
    }

    public int getGoodsid() {
        return goodsid;
    }

    public void setGoodsid(int goodsid) {
        this.goodsid = goodsid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        @Override
        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }

        @Override
        public VideoBean createFromParcel(Parcel in) {
            return new VideoBean(in);
        }
    };





    @Override
    public String toString() {
        return "VideoBean{" +
                "title='" + title + '\'' +
                ",href='" + href + '\'' +
                ",id='" + id + '\'' +
                ",uid='" + uid + '\'' +
                ",userNiceName='" + userBean.getUserNiceName() + '\'' +
                ",thumb='" + thumb + '\'' +
                '}';
    }

    public List<GoodsBean> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsBean> goods) {
        this.goods = goods;
    }

    public String getTag() {
        return "VideoBean" + this.getId() + this.hashCode();
    }

}

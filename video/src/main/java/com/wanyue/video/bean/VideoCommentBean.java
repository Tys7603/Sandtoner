package com.wanyue.video.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.bean.UserBean;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.video.R;

import java.util.ArrayList;
import java.util.List;

/**
 *  on 2017/7/14.
 */

public class VideoCommentBean implements Parcelable {

    private static final String REPLY = WordUtil.getString(R.string.video_comment_reply) + " ";

    @SerializedName("id")
    @JSONField(name="id")

    private String mId;

    @SerializedName("uid")
    @JSONField(name="uid")
    private String mUid;

    @SerializedName("touid")
    @JSONField(name="touid")
    private String mToUid;

    @SerializedName("vid")
    @JSONField(name="vid")
    private String mVideoId;

    @SerializedName("cid")
    @JSONField(name="cid")
    private String mCommentId;

    @SerializedName("pid")
    @JSONField(name="pid")
    private String mParentId;

    @SerializedName("content")
    @JSONField(name="content")
    private String mContent;

    @SerializedName("likes")
    @JSONField(name="likes")
    private String mLikeNum;


    private String mAtInfo;

    @SerializedName("islike")
    @JSONField(name="islike")
    private int mIsLike;

    private int targetLike;

    private int mReplyNum;

    private String add_time;
    private String addtime;

    private UserBean mUserBean;
    private UserBean mToUserBean;


    @SerializedName("replay")
    @JSONField(name="replay")
    protected VideoCommentBundleBean bundle;

    private List<VideoCommentBean> mChildList;


    private boolean mParentNode;//是否是父元素
    private int mPosition;
    private VideoCommentBean mParentNodeBean;
    private int mChildPage = 1;

    private String avatar;
    private String nickname;
    private boolean haveMore;

    public VideoCommentBean() {

    }

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }


    public String getUid() {
        return mUid;
    }


    public void setUid(String uid) {
        mUid = uid;
    }


    public String getToUid() {
        return mToUid;
    }


    public void setToUid(String toUid) {
        mToUid = toUid;
    }

    public String getVideoId() {
        return mVideoId;
    }

    public void setVideoId(String videoId) {
        mVideoId = videoId;
    }

    public String getCommentId() {
        return mCommentId;
    }

    public void setCommentId(String commentId) {
        mCommentId = commentId;
    }

    public String getParentId() {
        return mParentId;
    }

    public void setParentId(String parentId) {
        mParentId = parentId;
    }

    public String getContent() {
        if (!mParentNode && this.mToUserBean != null && !TextUtils.isEmpty(mToUserBean.getId())) {
            String userName = mToUserBean.getUserNiceName();
            if (!TextUtils.isEmpty(userName)) {
                return REPLY + userName + " : " + mContent;
            }
        }
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }


    public String getLikeNum() {
        return mLikeNum;
    }

    public void setLikeNum(String likeNum) {
        mLikeNum = likeNum;
    }

    public UserBean getUserBean() {
        if(mUserBean==null){
           mUserBean=new UserBean();
           mUserBean.setUserNiceName(nickname);
           mUserBean.setAvatar(avatar);
        }
        return mUserBean;
    }

    public void setUserBean(UserBean userBean) {
        mUserBean = userBean;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public int getIsLike() {
        return mIsLike;
    }

    public void setIsLike(int like) {
        mIsLike = like;
    }

    public String getAtInfo() {
        return mAtInfo;
    }

    public void setAtInfo(String atInfo) {
        mAtInfo = atInfo;
    }


    public UserBean getToUserBean() {
        return mToUserBean;
    }

    public void setToUserBean(UserBean toUserBean) {
        mToUserBean = toUserBean;
    }

    public int getReplyNum() {
        return mReplyNum;
    }

    public void setReplyNum(int replyNum) {
        mReplyNum = replyNum;
    }


    public List<VideoCommentBean> getChildList() {

        if(mChildList==null&&bundle!=null){
            mChildList=new ArrayList<>();
            mChildList.addAll(bundle.getList());
            for(VideoCommentBean item:mChildList){
                item.setParentNodeBean(this);
            }
            haveMore=bundle.getIsmore()==1;
        }
        return mChildList;
    }

    public void setChildList(List<VideoCommentBean> childList) {
        mChildList = childList;

        for (VideoCommentBean bean : childList) {
            if (bean != null) bean.setParentNodeBean(this);
        }
    }

    public VideoCommentBean getParentNodeBean() {
        return mParentNodeBean;
    }

    public void setParentNodeBean(VideoCommentBean parentNodeBean) {
        mParentNodeBean = parentNodeBean;
    }

    public boolean isParentNode() {
        return mParentNode;
    }

    public void setParentNode(boolean parentNode) {
        mParentNode = parentNode;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    @JSONField(serialize = false)
    public int getChildPage() {
        return mChildPage;
    }
    @JSONField(serialize = false)
    public void setChildPage(int childPage) {
        mChildPage = childPage;
    }

    public boolean isFirstChild(VideoCommentBean parentNodeBean) {
        if (!mParentNode && parentNodeBean != null) {
            List<VideoCommentBean> parentChildList = parentNodeBean.getChildList();
            if (parentChildList != null && parentChildList.size() > 0) {
                return parentChildList.get(0) == this;
            }
        }
        return false;
    }


    public boolean needShowExpand(VideoCommentBean parentNodeBean) {
        if (!mParentNode && parentNodeBean != null) {
            List<VideoCommentBean> parentChildList = parentNodeBean.getChildList();
            if (parentChildList != null) {
                int size = parentChildList.size();
                if (parentNodeBean.isHaveMore() && this == parentChildList.get(size - 1)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean needShowCollapsed(VideoCommentBean parentNodeBean) {
        if (!mParentNode && parentNodeBean != null) {
            List<VideoCommentBean> parentChildList = parentNodeBean.getChildList();
            if (parentChildList != null) {
                int size = parentChildList.size();
                if (size > 3 && !parentNodeBean.isHaveMore() && this == parentChildList.get(size - 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeChild() {
        if (mChildList != null && mChildList.size() > 3) {
            mChildList = mChildList.subList(0, 3);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mUid);
        dest.writeString(mToUid);
        dest.writeString(mVideoId);
        dest.writeString(mCommentId);
        dest.writeString(mParentId);
        dest.writeString(mContent);
        dest.writeString(mLikeNum);
        dest.writeString(mAtInfo);
        dest.writeInt(mIsLike);
        dest.writeInt(mReplyNum);
        dest.writeString(add_time);
        dest.writeParcelable(mUserBean, flags);
        dest.writeParcelable(mToUserBean, flags);
        dest.writeTypedList(mChildList);
        dest.writeByte((byte) (mParentNode ? 1 : 0));
        dest.writeInt(mPosition);
    }

    public VideoCommentBean(Parcel in) {
        mId = in.readString();
        mUid = in.readString();
        mToUid = in.readString();
        mVideoId = in.readString();
        mCommentId = in.readString();
        mParentId = in.readString();
        mContent = in.readString();
        mLikeNum = in.readString();
        mAtInfo = in.readString();
        mIsLike = in.readInt();
        mReplyNum = in.readInt();
        add_time = in.readString();
        mUserBean = in.readParcelable(UserBean.class.getClassLoader());
        mToUserBean = in.readParcelable(UserBean.class.getClassLoader());
        mChildList = in.createTypedArrayList(VideoCommentBean.CREATOR);
        mParentNode = in.readByte() != 0;
        mPosition = in.readInt();
    }

    public static final Creator<VideoCommentBean> CREATOR = new Creator<VideoCommentBean>() {
        @Override
        public VideoCommentBean createFromParcel(Parcel in) {
            return new VideoCommentBean(in);
        }

        @Override
        public VideoCommentBean[] newArray(int size) {
            return new VideoCommentBean[size];
        }
    };

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }


    public int getTargetLike() {
        return targetLike;
    }


    public VideoCommentBundleBean getBundle() {
        return bundle;
    }

    public void setBundle(VideoCommentBundleBean bundle) {
        this.bundle = bundle;
    }

    public void margeLikeChange(){
        mIsLike=targetLike;

    }

    public void setTargetLike(int targetLike) {
        this.targetLike = targetLike;
    }


    public boolean isHaveMore() {
        return haveMore;
    }

    public void setHaveMore(boolean haveMore) {
        this.haveMore = haveMore;
    }
}

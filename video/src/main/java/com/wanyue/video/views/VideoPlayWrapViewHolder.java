package com.wanyue.video.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.AbsActivity;
import com.wanyue.common.api.CommonAPI;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.bean.UserBean;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.http.ParseSingleHttpCallback;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.views.AbsViewHolder;
import com.wanyue.video.R;
import com.wanyue.video.activity.AbsVideoPlayActivity;
import com.wanyue.video.bean.VideoBean;
import com.wanyue.video.dialog.VideoGoodsDialogFragment;
import com.wanyue.video.dialog.VideoShareDialogFragment;
import com.wanyue.video.event.VideoLikeEvent;
import com.wanyue.video.api.VideoAPI;
import org.greenrobot.eventbus.EventBus;


/**
 *  on 2018/11/26.
 * 视频播放外框
 */

public class VideoPlayWrapViewHolder extends AbsViewHolder implements View.OnClickListener {

    private ViewGroup mVideoContainer;
    private ImageView mCover;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mTitle;
    private ImageView mBtnLike;//点赞按钮
    private TextView mLikeNum;//点赞数
    private TextView mCommentNum;//评论数
    private TextView mShareNum;//分享数
    private ImageView mBtnFollow;//关注按钮
    private View mBtnGoods;//查看同款商品

    private TextView mTvGoodsName;

    private VideoBean mVideoBean;
    private Drawable mFollowDrawable;//已关注
    private Drawable mUnFollowDrawable;//未关注
    private Animation mFollowAnimation;
    private boolean mCurPageShowed;//当前页面是否可见
    private ValueAnimator mLikeAnimtor;
    private Drawable[] mLikeAnimDrawables;//点赞帧动画
    private int mLikeAnimIndex;
    private String mTag;


    public VideoPlayWrapViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_video_play_wrap;
    }

    @Override
    public void init() {
        mTag = this.toString();
        mVideoContainer = (ViewGroup) findViewById(R.id.video_container);
        mCover = (ImageView) findViewById(R.id.cover);
        mAvatar = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mTitle = (TextView) findViewById(R.id.title);
        mBtnLike = (ImageView) findViewById(R.id.btn_like);
        mLikeNum = (TextView) findViewById(R.id.like_num);
        mCommentNum = (TextView) findViewById(R.id.comment_num);
        mShareNum = (TextView) findViewById(R.id.share_num);
        mBtnFollow = (ImageView) findViewById(R.id.btn_follow);
        mTvGoodsName = (TextView) findViewById(R.id.tv_goods_name);


        mFollowDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_video_follow_1);
        mUnFollowDrawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_video_follow_0);
        mAvatar.setOnClickListener(this);
        mBtnFollow.setOnClickListener(this);
        mBtnLike.setOnClickListener(this);
        findViewById(R.id.btn_comment).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
        mBtnGoods = findViewById(R.id.btn_goods);
        mBtnGoods.setOnClickListener(this);
    }

    /**
     * 初始化点赞动画
     */

    private void initLikeAnimtor() {
        if (mLikeAnimDrawables != null && mLikeAnimDrawables.length > 0) {
            mLikeAnimtor = ValueAnimator.ofFloat(0, mLikeAnimDrawables.length);
            mLikeAnimtor.setDuration(800);
            mLikeAnimtor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float v = (float) animation.getAnimatedValue();
                    int index = (int) v;
                    if (mLikeAnimIndex != index) {
                        mLikeAnimIndex = index;
                        if (mBtnLike != null && mLikeAnimDrawables != null && index < mLikeAnimDrawables.length) {
                            mBtnLike.setImageDrawable(mLikeAnimDrawables[index]);
                        }
                    }
                }
            });
        }
    }

    /**
     * 初始化关注动画
     */

    public void initFollowAnimation() {
        mFollowAnimation = new ScaleAnimation(1, 0.3f, 1, 0.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mFollowAnimation.setRepeatMode(Animation.REVERSE);
        mFollowAnimation.setRepeatCount(1);
        mFollowAnimation.setDuration(200);
        mFollowAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mBtnFollow != null && mVideoBean != null) {
                    if (mVideoBean.getAttent() == 1) {
                        mBtnFollow.setImageDrawable(mFollowDrawable);
                    } else {
                        mBtnFollow.setImageDrawable(mUnFollowDrawable);
                    }
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setLikeAnimDrawables(Drawable[] drawables) {
        mLikeAnimDrawables = drawables;
    }

    private  HttpCallback mCallback=new HttpCallback() {
        @Override
        public void onSuccess(int code, String msg, String[] info) {

        }
    };

    public void setData(VideoBean bean, Object payload) {
        if (bean == null) {
            return;
        }
        mVideoBean = bean;
        VideoAPI.setPlays(mVideoBean.getId(),mCallback );
        UserBean u = mVideoBean.getUserBean();
        if (payload == null) {
            if (mCover != null) {
                setCoverImage();
            }
            if (mTitle != null) {
                mTitle.setText(bean.getTitle());
            }
            if (u != null) {
                if (mAvatar != null) {
                    ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
                }
                if (mName != null) {
                    mName.setText("@" + u.getUserNiceName());
                }
            }
        }
        if (mBtnLike != null) {
            if (bean.getLike() == 1) {
                if (mLikeAnimDrawables != null && mLikeAnimDrawables.length > 0) {
                    mBtnLike.setImageDrawable(mLikeAnimDrawables[mLikeAnimDrawables.length - 1]);
                }
            } else {
                mBtnLike.setImageResource(R.mipmap.icon_video_zan_01);
            }
        }
        if (mLikeNum != null) {
            mLikeNum.setText(bean.getLikeNum());
        }
        if (mCommentNum != null) {
            mCommentNum.setText(bean.getCommentNum());
        }
        if (mShareNum != null) {
            mShareNum.setText(bean.getShareNum());
        }
        if (u != null && mBtnFollow != null) {
            String toUid = u.getId();
            if (!TextUtils.isEmpty(toUid) && !toUid.equals(CommonAppConfig.getUid())) {
                if (mBtnFollow.getVisibility() != View.VISIBLE) {
                    mBtnFollow.setVisibility(View.VISIBLE);
                }
                if (bean.getAttent() == 1) {
                    mBtnFollow.setImageDrawable(mFollowDrawable);
                } else {
                    mBtnFollow.setImageDrawable(mUnFollowDrawable);
                }
            } else {
                if (mBtnFollow.getVisibility() == View.VISIBLE) {
                    mBtnFollow.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (bean.getGoodsid() == 0) {
            if (mBtnGoods.getVisibility() == View.VISIBLE) {
                mBtnGoods.setVisibility(View.GONE);
            }

        } else {
            GoodsBean goodsBean= ListUtil.safeGetData(mVideoBean.getGoods(),0);
            if(goodsBean!=null){
               mTvGoodsName.setText("购物 |"+goodsBean.getName());
            }
            if (mBtnGoods.getVisibility() != View.VISIBLE) {
                mBtnGoods.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setCoverImage() {
        ImgLoader.displayDrawable(mContext, mVideoBean.getThumb(), new ImgLoader.DrawableCallback() {
            @Override
            public void onLoadSuccess(Drawable drawable) {
                if (mCover != null && drawable != null) {
                    float w = drawable.getIntrinsicWidth();
                    float h = drawable.getIntrinsicHeight();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mCover.getLayoutParams();
                    int targetH = 0;
                    if (w / h > 0.5625f) {//横屏  9:16=0.5625
                        targetH = (int) (mCover.getWidth() / w * h);
                    } else {
                        targetH = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    if (targetH != params.height) {
                        params.height = targetH;
                        mCover.requestLayout();
                    }
                    mCover.setImageDrawable(drawable);
                }
            }

            @Override
            public void onLoadFailed() {

            }
        });
    }

    public void addVideoView(View view) {
        if (mVideoContainer != null && view != null) {
            ViewParent parent = view.getParent();
            if (parent != null) {
                ViewGroup viewGroup = (ViewGroup) parent;
                if (viewGroup != mVideoContainer) {
                    viewGroup.removeView(view);
                    mVideoContainer.addView(view);
                }
            } else {
                mVideoContainer.addView(view);
            }
        }
    }

    public VideoBean getVideoBean() {
        return mVideoBean;
    }


    /**
     * 获取到视频首帧回调
     */
    public void onFirstFrame() {
        if (mCover != null && mCover.getVisibility() == View.VISIBLE) {
            mCover.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 滑出屏幕
     */
    public void onPageOutWindow() {
        mCurPageShowed = false;
        if (mCover != null && mCover.getVisibility() != View.VISIBLE) {
            mCover.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 滑入屏幕
     */
    public void onPageInWindow() {
        if (mCover != null) {
            if (mCover.getVisibility() != View.VISIBLE) {
                mCover.setVisibility(View.VISIBLE);
            }
            mCover.setImageDrawable(null);
            setCoverImage();
            getData();
        }
    }

    private void getData() {
        if(mVideoBean!=null){
          /* VideoAPI.getVideoDetail(mVideoBean.getId(), new ParseHttpCallback<VideoBean>() {
               @Override
               public void onSuccess(int code, String msg, VideoBean info) {
                   if(isSuccess(code)){
                      mVideoBean= info;
                      setData(mVideoBean,null);
                   }
               }
           });*/
        }
    }

    /**
     * 滑动到这一页 准备开始播放
     */
    public void onPageSelected() {
        mCurPageShowed = true;
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_follow) {
            clickFollow();

        } else if (i == R.id.btn_comment) {
            clickComment();

        } else if (i == R.id.btn_share) {
            clickShare();

        } else if (i == R.id.btn_like) {
            clickLike();

        } else if (i == R.id.avatar) {
            clickAvatar();

        } else if (i == R.id.btn_goods) {
            clickGoods();
        }
    }

    /**
     * 点击  查看同款商品
     */
    private void clickGoods() {
        if (mContext != null && mVideoBean != null) {
            GoodsBean goodsinfo = mVideoBean.getGoodsinfo();
            if (goodsinfo != null) {
                goodsinfo.setUid(mVideoBean.getUid());
            } else {
                return;
            }
            VideoGoodsDialogFragment fragment = new VideoGoodsDialogFragment();
            fragment.setGoodBean(goodsinfo);
            fragment.setUid(mVideoBean.getUid());
            fragment.show(((AbsActivity) mContext).getSupportFragmentManager(), "VideoGoodsDialogFragment");
        }

    }


    /**
     * 点击头像
     */
    public void clickAvatar() {
        if (mVideoBean != null) {
           // RouteUtil.forwardUserHome( mVideoBean.getUid());
        }
    }

    /**
     * 点赞,取消点赞
     */
    private void clickLike() {
        if (mVideoBean == null) {
            return;
        }
        final int targetType=mVideoBean.getLike()==1?0:1;
        VideoAPI.setVideoLike(mVideoBean.getId(),targetType, new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                if(!isSuccess(code)){
                    return;
                }
                String likeNum = info.getString("likes");
                int like = targetType;
                if (mVideoBean != null) {
                    mVideoBean.setLikeNum(likeNum);
                    mVideoBean.setLike(like);
                    EventBus.getDefault().post(new VideoLikeEvent(mVideoBean.getId(), like, likeNum));
                }
                if (mLikeNum != null) {
                    mLikeNum.setText(likeNum);
                }
                if (mBtnLike != null) {
                    if (like == 1) {
                        if (mLikeAnimtor == null) {
                            initLikeAnimtor();
                        }
                        mLikeAnimIndex = -1;
                        if (mLikeAnimtor != null) {
                            mLikeAnimtor.start();
                        }
                    } else {
                        mBtnLike.setImageResource(R.mipmap.icon_video_zan_01);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                // Handle error case
            }
        });
    }

    /**
     * 点击关注按钮
     */
    private void clickFollow() {
        if (mVideoBean == null) {
            return;
        }
        final UserBean u = mVideoBean.getUserBean();
        if (u == null) {
            return;
        }

        CommonAPI.setAttention(mVideoBean.getAttent(),mTag, new ParseSingleHttpCallback<Integer>("isattent") {
            @Override
            public void onSuccess(Integer attent) {
                mVideoBean.setAttent(attent);
                LiveEventBus.get("video_isattent").post(mVideoBean);
                if (mCurPageShowed) {
                    if (mFollowAnimation == null) {
                        initFollowAnimation();
                    }
                    mBtnFollow.startAnimation(mFollowAnimation);
                } else {
                    if (attent == 1) {
                        mBtnFollow.setImageDrawable(mFollowDrawable);
                    } else {
                        mBtnFollow.setImageDrawable(mUnFollowDrawable);
                    }
                }
            }
        });

    }

    /**
     * 点击评论按钮
     */
    private void clickComment() {
        ((AbsVideoPlayActivity) mContext).openCommentWindow(mVideoBean.getId(), mVideoBean.getUid(),mVideoBean.getCommentNum());
    }

    /**
     * 点击分享按钮
     */
    private void clickShare() {
        if (mVideoBean == null) {
            return;
        }
        VideoShareDialogFragment fragment = new VideoShareDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.VIDEO_BEAN, mVideoBean);
        fragment.setArguments(bundle);
        fragment.show(((AbsVideoPlayActivity) mContext).getSupportFragmentManager(), "VideoShareDialogFragment");
    }

    public void release() {
        VideoAPI.cancel(mTag);
        if (mLikeAnimtor != null) {
            mLikeAnimtor.cancel();
        }
        if (mBtnFollow != null && mFollowAnimation != null) {
            mBtnFollow.clearAnimation();
        }
    }


}

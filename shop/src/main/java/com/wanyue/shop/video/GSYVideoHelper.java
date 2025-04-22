package com.wanyue.shop.video;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack;
import com.shuyu.gsyvideoplayer.render.view.GSYVideoGLView;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationOption;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;


import java.io.File;
import java.util.Map;

import androidx.transition.TransitionManager;

import static com.shuyu.gsyvideoplayer.utils.CommonUtil.getActionBarHeight;
import static com.shuyu.gsyvideoplayer.utils.CommonUtil.getStatusBarHeight;
import static com.shuyu.gsyvideoplayer.utils.CommonUtil.hideNavKey;
import static com.shuyu.gsyvideoplayer.utils.CommonUtil.showNavKey;

/**
 * 视频帮助类，更加节省资源
 * Created by guoshuyu on 2018/1/15.
 */
public class GSYVideoHelper {

    /**
     * 播放的标志
     */
    private String TAG = "NULL";
    /**
     * 播放器
     */
    private StandardGSYVideoPlayer mGsyVideoPlayer;
    /**
     * 全屏承载布局
     */
    private ViewGroup mFullViewContainer;
    /**
     * 全屏承载布局
     */
    private ViewGroup mWindowViewContainer;
    /**
     * 记录列表中item的父布局
     */
    private ViewGroup mParent;
    /**
     * 布局
     */
    private ViewGroup.LayoutParams mNormalParams;
    /**
     * 选择工具类
     */
    private OrientationUtils mOrientationUtils;
    /**
     * 可配置旋转 OrientationUtils
     */
    private OrientationOption mOrientationOption;
    /**
     * 播放配置
     */
    private GSYVideoHelperBuilder mVideoOptionBuilder;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 播放的位置
     */
    private int mPlayPosition = -1;
    /**
     * 可视保存
     */
    private int mSystemUiVisibility;
    /**
     * 当前是否全屏
     */
    private boolean isFull;
    /**
     * 当前是否小屏
     */
    private boolean isSmall;
    /**
     * 当前item框的屏幕位置
     */
    private int[] mNormalItemRect;
    /**
     * 当前item的大小
     */
    private int[] mNormalItemSize;
    /**
     * handler
     */
    private Handler mHandler = new Handler();


    /**
     * Instantiates a new Gsy video helper.
     *
     * @param context the context
     */
    public GSYVideoHelper(Context context) {
        this(context, new StandardGSYVideoPlayer(context));
    }

    /**
     * Instantiates a new Gsy video helper.
     *
     * @param context the context
     * @param player  the player
     */
    public GSYVideoHelper(Context context, StandardGSYVideoPlayer player) {
        mGsyVideoPlayer = player;
        this.mContext = context;
        this.mWindowViewContainer = (ViewGroup) (CommonUtil.scanForActivity(context)).findViewById(Window.ID_ANDROID_CONTENT);

    }

    /**
     * 处理全屏逻辑
     */
    private void resolveToFull() {
        mSystemUiVisibility = ((Activity) mContext).getWindow().getDecorView().getSystemUiVisibility();
        CommonUtil.hideSupportActionBar(mContext, mVideoOptionBuilder.isHideActionBar(), mVideoOptionBuilder.isHideStatusBar());
        if (mVideoOptionBuilder.isHideKey()) {
            hideNavKey(mContext);
        }
        isFull = true;
        ViewGroup viewGroup = (ViewGroup) mGsyVideoPlayer.getParent();
        mNormalParams = mGsyVideoPlayer.getLayoutParams();
        if (viewGroup != null) {
            mParent = viewGroup;
            viewGroup.removeView(mGsyVideoPlayer);
        }
        mGsyVideoPlayer.setIfCurrentIsFullscreen(true);
        mGsyVideoPlayer.getFullscreenButton().setImageResource(mGsyVideoPlayer.getShrinkImageRes());
        mGsyVideoPlayer.getBackButton().setVisibility(View.VISIBLE);
        //设置旋转
        mOrientationUtils = new OrientationUtils((Activity) mContext, mGsyVideoPlayer, mOrientationOption);
        mOrientationUtils.setEnable(mVideoOptionBuilder.isRotateViewAuto());
        mGsyVideoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveMaterialToNormal(mGsyVideoPlayer);
            }
        });
        if (mVideoOptionBuilder.isShowFullAnimation()) {
            if (mFullViewContainer instanceof FrameLayout) {
                //目前只做了frameLoayout的判断
                resolveMaterialAnimation();
            } else {
                resolveFullAdd();
            }

        } else {
            resolveFullAdd();
        }
    }

    /**
     * 添加到全屏父布局里
     */
    private void resolveFullAdd() {
        if (mVideoOptionBuilder.isShowFullAnimation()) {
            if (mFullViewContainer != null) {
                mFullViewContainer.setBackgroundColor(Color.BLACK);
            }
        }
        resolveChangeFirstLogic(0);
        if (mFullViewContainer != null) {
            mFullViewContainer.addView(mGsyVideoPlayer);
        } else {
            mWindowViewContainer.addView(mGsyVideoPlayer);
        }
    }

    /**
     * 如果是5.0的动画开始位置
     */
    private void resolveMaterialAnimation() {
        mNormalItemRect = new int[2];
        mNormalItemSize = new int[2];
        saveLocationStatus(mContext, mVideoOptionBuilder.isHideActionBar(), mVideoOptionBuilder.isHideStatusBar());
        FrameLayout.LayoutParams lpParent = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.setBackgroundColor(Color.BLACK);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(mNormalItemSize[0], mNormalItemSize[1]);
        lp.setMargins(mNormalItemRect[0], mNormalItemRect[1], 0, 0);
        frameLayout.addView(mGsyVideoPlayer, lp);
        if (mFullViewContainer != null) {
            mFullViewContainer.addView(frameLayout, lpParent);
        } else {
            mWindowViewContainer.addView(frameLayout, lpParent);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //开始动画
                if (mFullViewContainer != null) {
                    TransitionManager.beginDelayedTransition(mFullViewContainer);
                } else {
                    TransitionManager.beginDelayedTransition(mWindowViewContainer);
                }
                resolveMaterialFullVideoShow(mGsyVideoPlayer);
                resolveChangeFirstLogic(600);
            }
        }, 300);
    }

    /**
     * 如果是5.0的，要从原位置过度到全屏位置
     */
    private void resolveMaterialFullVideoShow(GSYBaseVideoPlayer gsyVideoPlayer) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) gsyVideoPlayer.getLayoutParams();
        lp.setMargins(0, 0, 0, 0);
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        gsyVideoPlayer.setLayoutParams(lp);
        gsyVideoPlayer.setIfCurrentIsFullscreen(true);
    }


    /**
     * 处理正常逻辑
     */
    private void resolveToNormal() {
        int delay = mOrientationUtils.backToProtVideo();
        if (!mVideoOptionBuilder.isShowFullAnimation()) {
            delay = 0;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFull = false;
                removeWindowContainer();
                if (mFullViewContainer != null) {
                    mFullViewContainer.removeAllViews();
                }
                if (mGsyVideoPlayer.getParent() != null) {
                    ((ViewGroup) mGsyVideoPlayer.getParent()).removeView(mGsyVideoPlayer);
                }
                mOrientationUtils.setEnable(false);
                mGsyVideoPlayer.setIfCurrentIsFullscreen(false);
                if (mFullViewContainer != null) {
                    mFullViewContainer.setBackgroundColor(Color.TRANSPARENT);
                }
                mParent.addView(mGsyVideoPlayer, mNormalParams);
                mGsyVideoPlayer.getFullscreenButton().setImageResource(mGsyVideoPlayer.getEnlargeImageRes());
                mGsyVideoPlayer.getBackButton().setVisibility(View.GONE);
                mGsyVideoPlayer.setIfCurrentIsFullscreen(false);
                mGsyVideoPlayer.restartTimerTask();
                if (mVideoOptionBuilder.getVideoAllCallBack() != null) {
                    Debuger.printfLog("onQuitFullscreen");
                    mVideoOptionBuilder.getVideoAllCallBack().onQuitFullscreen(mVideoOptionBuilder.getUrl(), mVideoOptionBuilder.getVideoTitle(), mGsyVideoPlayer);
                }
                if (mVideoOptionBuilder.isHideKey()) {
                    showNavKey(mContext, mSystemUiVisibility);
                }
                CommonUtil.showSupportActionBar(mContext, mVideoOptionBuilder.isHideActionBar(), mVideoOptionBuilder.isHideStatusBar());
            }
        }, delay);
    }


    /**
     * 动画回到正常效果
     */
    private void resolveMaterialToNormal(final GSYVideoPlayer gsyVideoPlayer) {
        if (mVideoOptionBuilder.isShowFullAnimation() && mFullViewContainer instanceof FrameLayout) {
            int delay = mOrientationUtils.backToProtVideo();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(mFullViewContainer);
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) gsyVideoPlayer.getLayoutParams();
                    lp.setMargins(mNormalItemRect[0], mNormalItemRect[1], 0, 0);
                    lp.width = mNormalItemSize[0];
                    lp.height = mNormalItemSize[1];
                    //注意配置回来，不然动画效果会不对
                    lp.gravity = Gravity.NO_GRAVITY;
                    gsyVideoPlayer.setLayoutParams(lp);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resolveToNormal();
                        }
                    }, 400);
                }
            }, delay);
        } else {
            resolveToNormal();
        }
    }


    /**
     * 是否全屏一开始马上自动横屏
     */
    private void resolveChangeFirstLogic(int time) {
        if (mVideoOptionBuilder.isLockLand()) {
            if (time > 0) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mOrientationUtils.getIsLand() != 1) {
                            if (mFullViewContainer != null) {
                                mFullViewContainer.setBackgroundColor(Color.BLACK);
                            }
                            mOrientationUtils.resolveByClick();
                        }
                    }
                }, time);
            } else {
                if (mOrientationUtils.getIsLand() != 1) {
                    if (mFullViewContainer != null) {
                        mFullViewContainer.setBackgroundColor(Color.BLACK);
                    }
                    mOrientationUtils.resolveByClick();
                }
            }
        }
        mGsyVideoPlayer.setIfCurrentIsFullscreen(true);
        mGsyVideoPlayer.restartTimerTask();
        if (mVideoOptionBuilder.getVideoAllCallBack() != null) {
            Debuger.printfLog("onEnterFullscreen");
            mVideoOptionBuilder.getVideoAllCallBack().onEnterFullscreen(mVideoOptionBuilder.getUrl(), mVideoOptionBuilder.getVideoTitle(), mGsyVideoPlayer);
        }
    }

    /**
     * 保存大小和状态
     */
    private void saveLocationStatus(Context context, boolean statusBar, boolean actionBar) {
        mParent.getLocationOnScreen(mNormalItemRect);
        int statusBarH = getStatusBarHeight(context);
        int actionBerH = getActionBarHeight((Activity) context);
        if (statusBar) {
            mNormalItemRect[1] = mNormalItemRect[1] - statusBarH;
        }
        if (actionBar) {
            mNormalItemRect[1] = mNormalItemRect[1] - actionBerH;
        }
        mNormalItemSize[0] = mParent.getWidth();
        mNormalItemSize[1] = mParent.getHeight();
    }


    /**
     * 是否当前播放
     */
    private boolean isPlayView(int position, String tag) {
        return mPlayPosition == position && TAG.equals(tag);
    }

    private boolean isCurrentViewPlaying(int position, String tag) {
        return isPlayView(position, tag);
    }

    private boolean removeWindowContainer() {
        if (mWindowViewContainer != null && mWindowViewContainer.indexOfChild(mGsyVideoPlayer) != -1) {
            mWindowViewContainer.removeView(mGsyVideoPlayer);
            return true;
        }
        return false;
    }

    /**
     * 动态添加视频播放
     *
     * @param position  位置
     * @param imgView   封面
     * @param tag       TAG类型
     * @param container player的容器
     * @param playBtn   播放按键
     */
    public void addVideoPlayer(final int position, View imgView, String tag,
                               ViewGroup container, View playBtn) {
        container.removeAllViews();
        if (isCurrentViewPlaying(position, tag)) {
            if (!isFull) {
                ViewGroup viewGroup = (ViewGroup) mGsyVideoPlayer.getParent();
                if (viewGroup != null) {
                    viewGroup.removeAllViews();
                }
                container.addView(mGsyVideoPlayer);
                playBtn.setVisibility(View.INVISIBLE);
            }
        } else {
            playBtn.setVisibility(View.VISIBLE);
            container.removeAllViews();   //增加封面
            container.addView(imgView);
        }
    }

    /**
     * 设置列表播放中的位置和TAG,防止错位，回复播放位置
     *
     * @param playPosition 列表中的播放位置
     * @param tag          播放的是哪个列表的tag
     */
    public void setPlayPositionAndTag(int playPosition, String tag) {
        this.mPlayPosition = playPosition;
        this.TAG = tag;
    }

    /**
     * 开始播放
     */
    public void startPlay() {

        if (isSmall()) {
            smallVideoToNormal();
        }

        mGsyVideoPlayer.release();


        if (mVideoOptionBuilder == null) {
            throw new NullPointerException("mVideoOptionBuilder can't be null");
        }

        mVideoOptionBuilder.build(mGsyVideoPlayer);

        //增加title
        if (mGsyVideoPlayer.getTitleTextView() != null) {
            mGsyVideoPlayer.getTitleTextView().setVisibility(View.GONE);
        }

        //设置返回键
        if (mGsyVideoPlayer.getBackButton() != null) {
            mGsyVideoPlayer.getBackButton().setVisibility(View.GONE);
        }

        //设置全屏按键功能
        if (mGsyVideoPlayer.getFullscreenButton() != null) {
            mGsyVideoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doFullBtnLogic();
                }
            });
        }
        mGsyVideoPlayer.startPlayLogic();
    }

    /**
     * 全屏按键逻辑
     */
    public void doFullBtnLogic() {
        if (!isFull) {
            resolveToFull();
        } else {
            resolveMaterialToNormal(mGsyVideoPlayer);
        }
    }


    /**
     * 处理返回正常逻辑
     *
     * @return the boolean
     */
    public boolean backFromFull() {
        boolean isFull = false;
        if (mFullViewContainer != null && mFullViewContainer.getChildCount() > 0) {
            isFull = true;
            resolveMaterialToNormal(mGsyVideoPlayer);
        } else if (mWindowViewContainer != null && mWindowViewContainer.indexOfChild(mGsyVideoPlayer) != -1) {
            isFull = true;
            resolveMaterialToNormal(mGsyVideoPlayer);
        }
        return isFull;
    }

    /**
     * 释放持有的视频
     */
    public void releaseVideoPlayer() {
        removeWindowContainer();
        ViewGroup viewGroup = (ViewGroup) mGsyVideoPlayer.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        mPlayPosition = -1;
        TAG = "NULL";
        if (mOrientationUtils != null) {
            mOrientationUtils.releaseListener();
        }

    }

    /**
     * 显示小屏幕效果
     *
     * @param size      小视频的大小
     * @param actionBar 是否有actionBar
     * @param statusBar 是否有状态栏
     */
    public void showSmallVideo(Point size, final boolean actionBar, final boolean statusBar) {
        if (mGsyVideoPlayer.getCurrentState() == GSYVideoPlayer.CURRENT_STATE_PLAYING) {
            mGsyVideoPlayer.showSmallVideo(size, actionBar, statusBar);
            isSmall = true;
        }
    }


    /**
     * 恢复小屏幕效果
     */
    public void smallVideoToNormal() {
        isSmall = false;
        mGsyVideoPlayer.hideSmallVideo();
    }


    /**
     * 设置全屏显示的viewGroup
     * 如果不设置即使用默认的 mWindowViewContainer
     *
     * @param fullViewContainer viewGroup
     */
    public void setFullViewContainer(ViewGroup fullViewContainer) {
        this.mFullViewContainer = fullViewContainer;
    }

    /**
     * 可配置旋转 OrientationUtils
     *
     * @param orientationOption the orientation option
     */
    public void setOrientationOption(OrientationOption orientationOption) {
        this.mOrientationOption = orientationOption;
    }

    /**
     * 是否全屏
     *
     * @return the boolean
     */
    public boolean isFull() {
        return isFull;
    }

    /**
     * 设置配置
     *
     * @param mVideoOptionBuilder the m video option builder
     */
    public void setGsyVideoOptionBuilder(GSYVideoHelperBuilder mVideoOptionBuilder) {
        this.mVideoOptionBuilder = mVideoOptionBuilder;
    }

    /**
     * Gets gsy video option builder.
     *
     * @return the gsy video option builder
     */
    public GSYVideoOptionBuilder getGsyVideoOptionBuilder() {
        return mVideoOptionBuilder;
    }

    /**
     * Gets play position.
     *
     * @return the play position
     */
    public int getPlayPosition() {
        return mPlayPosition;
    }

    /**
     * Gets play tag.
     *
     * @return the play tag
     */
    public String getPlayTAG() {
        return TAG;
    }

    /**
     * Is small boolean.
     *
     * @return the boolean
     */
    public boolean isSmall() {
        return isSmall;
    }

    /**
     * 获取播放器,直接拿播放器，根据需要自定义配置
     *
     * @return the gsy video player
     */
    public StandardGSYVideoPlayer getGsyVideoPlayer() {
        return mGsyVideoPlayer;
    }

    /**
     * 配置
     */
    public static class GSYVideoHelperBuilder extends GSYVideoOptionBuilder {

        /**
         * The M hide action bar.
         */
        protected boolean mHideActionBar;

        /**
         * The M hide status bar.
         */
        protected boolean mHideStatusBar;

        /**
         * Is hide action bar boolean.
         *
         * @return the boolean
         */
        public boolean isHideActionBar() {
            return mHideActionBar;
        }

        /**
         * Sets hide action bar.
         *
         * @param hideActionBar the hide action bar
         * @return the hide action bar
         */
        public GSYVideoHelperBuilder setHideActionBar(boolean hideActionBar) {
            this.mHideActionBar = hideActionBar;
            return this;
        }

        /**
         * Is hide status bar boolean.
         *
         * @return the boolean
         */
        public boolean isHideStatusBar() {
            return mHideStatusBar;
        }

        /**
         * Sets hide status bar.
         *
         * @param hideStatusBar the hide status bar
         * @return the hide status bar
         */
        public GSYVideoHelperBuilder setHideStatusBar(boolean hideStatusBar) {
            this.mHideStatusBar = hideStatusBar;
            return this;
        }

        /**
         * Gets shrink image res.
         *
         * @return the shrink image res
         */
        public int getShrinkImageRes() {
            return mShrinkImageRes;
        }

        /**
         * Gets enlarge image res.
         *
         * @return the enlarge image res
         */
        public int getEnlargeImageRes() {
            return mEnlargeImageRes;
        }

        /**
         * Gets play position.
         *
         * @return the play position
         */
        public int getPlayPosition() {
            return mPlayPosition;
        }

        /**
         * Gets dialog progress high light color.
         *
         * @return the dialog progress high light color
         */
        public int getDialogProgressHighLightColor() {
            return mDialogProgressHighLightColor;
        }

        /**
         * Gets dialog progress normal color.
         *
         * @return the dialog progress normal color
         */
        public int getDialogProgressNormalColor() {
            return mDialogProgressNormalColor;
        }

        /**
         * Gets dismiss control time.
         *
         * @return the dismiss control time
         */
        public int getDismissControlTime() {
            return mDismissControlTime;
        }

        /**
         * Gets seek on start.
         *
         * @return the seek on start
         */
        public long getSeekOnStart() {
            return mSeekOnStart;
        }

        /**
         * Gets seek ratio.
         *
         * @return the seek ratio
         */
        public float getSeekRatio() {
            return mSeekRatio;
        }

        /**
         * Gets speed.
         *
         * @return the speed
         */
        public float getSpeed() {
            return mSpeed;
        }

        /**
         * Is hide key boolean.
         *
         * @return the boolean
         */
        public boolean isHideKey() {
            return mHideKey;
        }

        /**
         * Is show full animation boolean.
         *
         * @return the boolean
         */
        public boolean isShowFullAnimation() {
            return mShowFullAnimation;
        }

        /**
         * Is need show wifi tip boolean.
         *
         * @return the boolean
         */
        public boolean isNeedShowWifiTip() {
            return mNeedShowWifiTip;
        }

        /**
         * Is rotate view auto boolean.
         *
         * @return the boolean
         */
        public boolean isRotateViewAuto() {
            return mRotateViewAuto;
        }

        /**
         * Is lock land boolean.
         *
         * @return the boolean
         */
        public boolean isLockLand() {
            return mLockLand;
        }

        /**
         * Is looping boolean.
         *
         * @return the boolean
         */
        public boolean isLooping() {
            return mLooping;
        }

        /**
         * Is is touch wiget boolean.
         *
         * @return the boolean
         */
        public boolean isIsTouchWiget() {
            return mIsTouchWiget;
        }

        /**
         * Is is touch wiget full boolean.
         *
         * @return the boolean
         */
        public boolean isIsTouchWigetFull() {
            return mIsTouchWigetFull;
        }

        /**
         * Is show pause cover boolean.
         *
         * @return the boolean
         */
        public boolean isShowPauseCover() {
            return mShowPauseCover;
        }

        /**
         * Is rotate with system boolean.
         *
         * @return the boolean
         */
        public boolean isRotateWithSystem() {
            return mRotateWithSystem;
        }

        /**
         * Is cache with play boolean.
         *
         * @return the boolean
         */
        public boolean isCacheWithPlay() {
            return mCacheWithPlay;
        }

        /**
         * Is need lock full boolean.
         *
         * @return the boolean
         */
        public boolean isNeedLockFull() {
            return mNeedLockFull;
        }

        /**
         * Is thumb play boolean.
         *
         * @return the boolean
         */
        public boolean isThumbPlay() {
            return mThumbPlay;
        }

        /**
         * Is sounch touch boolean.
         *
         * @return the boolean
         */
        public boolean isSounchTouch() {
            return mSounchTouch;
        }

        /**
         * Is set up lazy boolean.
         *
         * @return the boolean
         */
        public boolean isSetUpLazy() {
            return mSetUpLazy;
        }

        /**
         * Gets play tag.
         *
         * @return the play tag
         */
        public String getPlayTag() {
            return mPlayTag;
        }

        /**
         * Gets url.
         *
         * @return the url
         */
        public String getUrl() {
            return mUrl;
        }

        /**
         * Gets video title.
         *
         * @return the video title
         */
        public String getVideoTitle() {
            return mVideoTitle;
        }

        /**
         * Gets cache path.
         *
         * @return the cache path
         */
        public File getCachePath() {
            return mCachePath;
        }

        /**
         * Gets map head data.
         *
         * @return the map head data
         */
        public Map<String, String> getMapHeadData() {
            return mMapHeadData;
        }

        /**
         * Gets video all call back.
         *
         * @return the video all call back
         */
        public VideoAllCallBack getVideoAllCallBack() {
            return mVideoAllCallBack;
        }

        /**
         * Gets lock click listener.
         *
         * @return the lock click listener
         */
        public LockClickListener getLockClickListener() {
            return mLockClickListener;
        }

        /**
         * Gets thumb image view.
         *
         * @return the thumb image view
         */
        public View getThumbImageView() {
            return mThumbImageView;
        }

        /**
         * Gets bottom progress drawable.
         *
         * @return the bottom progress drawable
         */
        public Drawable getBottomProgressDrawable() {
            return mBottomProgressDrawable;
        }

        /**
         * Gets bottom show progress drawable.
         *
         * @return the bottom show progress drawable
         */
        public Drawable getBottomShowProgressDrawable() {
            return mBottomShowProgressDrawable;
        }

        /**
         * Gets bottom show progress thumb drawable.
         *
         * @return the bottom show progress thumb drawable
         */
        public Drawable getBottomShowProgressThumbDrawable() {
            return mBottomShowProgressThumbDrawable;
        }

        /**
         * Gets volume progress drawable.
         *
         * @return the volume progress drawable
         */
        public Drawable getVolumeProgressDrawable() {
            return mVolumeProgressDrawable;
        }

        /**
         * Gets dialog progress bar drawable.
         *
         * @return the dialog progress bar drawable
         */
        public Drawable getDialogProgressBarDrawable() {
            return mDialogProgressBarDrawable;
        }

        /**
         * Gets effect filter.
         *
         * @return the effect filter
         */
        public GSYVideoGLView.ShaderInterface getEffectFilter() {
            return mEffectFilter;
        }

        /**
         * Gets gsy video progress listener.
         *
         * @return the gsy video progress listener
         */
        public GSYVideoProgressListener getGSYVideoProgressListener() {
            return mGSYVideoProgressListener;
        }
    }


}

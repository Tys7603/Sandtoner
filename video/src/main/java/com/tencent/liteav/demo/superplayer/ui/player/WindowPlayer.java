package com.tencent.liteav.demo.superplayer.ui.player;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wanyue.common.custom.CustomPopupWindow;
import com.wanyue.video.R;
import com.tencent.liteav.demo.superplayer.SuperPlayerDef;
import com.tencent.liteav.demo.superplayer.model.utils.VideoGestureDetector;
import com.tencent.liteav.demo.superplayer.ui.view.PointSeekBar;
import com.tencent.liteav.demo.superplayer.ui.view.VideoProgressLayout;
import com.tencent.liteav.demo.superplayer.ui.view.VolumeBrightnessProgressLayout;


/**
 * 窗口模式播放控件
 *
 * 除基本播放控制外，还有手势控制快进快退、手势调节亮度音量等
 *
 * 1、点击事件监听{@link #onClick(View)}
 *
 * 2、触摸事件监听{@link #onTouchEvent(MotionEvent)}
 *
 * 2、进度条事件监听{@link #onProgressChanged(PointSeekBar, int, boolean)}
 *                {@link #onStartTrackingTouch(PointSeekBar)}
 *                {@link #onStopTrackingTouch(PointSeekBar)}
 */

public class WindowPlayer extends AbsPlayer implements View.OnClickListener,
        PointSeekBar.OnSeekBarChangeListener {


    // UI控件
    private LinearLayout                        mLayoutTop;                             // 顶部标题栏布局
    private LinearLayout                        mLayoutBottom;                          // 底部进度条所在布局
    private ImageView                           mIvPause;                               // 暂停播放按钮
    private ImageView                           mIvFullScreen;                          // 全屏按钮
    private TextView                            mTvTitle;                               // 视频名称文本
    private TextView                            mTvBackToLive;                          // 返回直播文本
    private ImageView                           mBackground;                            // 背景
    private ImageView                           mIvWatermark;                           // 水印
    private TextView                            mTvCurrent;                             // 当前进度文本
    private TextView                            mTvDuration;                            // 总时长文本
    private PointSeekBar                        mSeekBarProgress;                       // 播放进度条
    private LinearLayout                        mLayoutReplay;                          // 重播按钮所在布局
    private ProgressBar                         mPbLiveLoading;                         // 加载圈
    private VolumeBrightnessProgressLayout      mGestureVolumeBrightnessProgressLayout; // 音量亮度调节布局
    private VideoProgressLayout                 mGestureVideoProgressLayout;            // 手势快进提示布局

    private GestureDetector                     mGestureDetector;                       // 手势检测监听器
    private VideoGestureDetector                mVideoGestureDetector;                      // 手势控制工具


    private boolean                             isShowing;                              // 自身是否可见
    private boolean                             mIsChangingSeekBarProgress;             // 进度条是否正在拖动，避免SeekBar由于视频播放的update而跳动
    private SuperPlayerDef.PlayerType           mPlayType;                              // 当前播放视频类型
    private SuperPlayerDef.PlayerState          mCurrentPlayState = SuperPlayerDef.PlayerState.END;                 // 当前播放状态
    private long                                mDuration;                              // 视频总时长
    private long                                mLivePushDuration;                      // 直播推流总时长
    private long                                mProgress;                              // 当前播放进度


    private Bitmap                              mBackgroundBmp;                         // 背景图
    private Bitmap                              mWaterMarkBmp;                          // 水印图
    private float                               mWaterMarkBmpX;                         // 水印x坐标
    private float                               mWaterMarkBmpY;                         // 水印y坐标
    private long                                mLastClickTime;                         // 上次点击事件的时间
    private CustomPopupWindow mCustomPopupWindow;


    public WindowPlayer(Context context) {
        super(context);
        initialize(context);
    }

    public WindowPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public WindowPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    /**
     * 初始化控件、手势检测监听器、亮度/音量/播放进度的回调
     */
    private void initialize(Context context) {
        initView(context);

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                togglePlayState();
                show();
                if (mHideViewRunnable != null) {
                    removeCallbacks(mHideViewRunnable);
                    postDelayed(mHideViewRunnable, 7000);
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                toggle();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent downEvent, MotionEvent moveEvent, float distanceX, float distanceY) {
                if (downEvent == null || moveEvent == null) {
                    return false;
                }
                if (mVideoGestureDetector != null && mGestureVolumeBrightnessProgressLayout != null) {
                    mVideoGestureDetector.check(mGestureVolumeBrightnessProgressLayout.getHeight(), downEvent, moveEvent, distanceX, distanceY);
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                if (mVideoGestureDetector != null) {
                    mVideoGestureDetector.reset(getWidth(), mSeekBarProgress.getProgress());
                }
                return true;
            }
        });

        mGestureDetector.setIsLongpressEnabled(false);

        mVideoGestureDetector = new VideoGestureDetector(getContext());
        mVideoGestureDetector.setVideoGestureListener(new VideoGestureDetector.VideoGestureListener() {
            @Override
            public void onBrightnessGesture(float newBrightness) {
                if (mGestureVolumeBrightnessProgressLayout != null) {
                    mGestureVolumeBrightnessProgressLayout.setProgress((int) (newBrightness * 100));
                    mGestureVolumeBrightnessProgressLayout.setImageResource(R.drawable.superplayer_ic_light_max);
                    mGestureVolumeBrightnessProgressLayout.show();
                }
            }
            @Override
            public void onVolumeGesture(float volumeProgress) {
                if (mGestureVolumeBrightnessProgressLayout != null) {
                    mGestureVolumeBrightnessProgressLayout.setImageResource(R.drawable.superplayer_ic_volume_max);
                    mGestureVolumeBrightnessProgressLayout.setProgress((int) volumeProgress);
                    mGestureVolumeBrightnessProgressLayout.show();
                }
            }
            @Override
            public void onSeekGesture(int progress) {
                mIsChangingSeekBarProgress = true;
                if (mGestureVideoProgressLayout != null) {

                    if (progress > mSeekBarProgress.getMax()) {
                        progress = mSeekBarProgress.getMax();
                    }
                    if (progress < 0) {
                        progress = 0;
                    }
                    mGestureVideoProgressLayout.setProgress(progress);
                    mGestureVideoProgressLayout.show();

                    float percentage = ((float) progress) / mSeekBarProgress.getMax();
                    float currentTime = (mDuration * percentage);
                    if (mPlayType == SuperPlayerDef.PlayerType.LIVE || mPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
                        if (mLivePushDuration > MAX_SHIFT_TIME) {
                            currentTime = (int) (mLivePushDuration - MAX_SHIFT_TIME *  (1 - percentage));
                        } else {
                            currentTime  = mLivePushDuration * percentage;
                        }
                        mGestureVideoProgressLayout.setTimeText(formattedTime((long) currentTime));
                    } else {
                        mGestureVideoProgressLayout.setTimeText(formattedTime((long) currentTime) + " / " + formattedTime((long) mDuration));
                    }

                }
                if (mSeekBarProgress!= null) {
                    mSeekBarProgress.setProgress(progress);
                }
            }
        });
    }

    /**
     * 初始化view
     */

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.superplayer_vod_player_window, this);
        mLayoutTop = (LinearLayout) findViewById(R.id.superplayer_rl_top);
        mLayoutTop.setOnClickListener(this);
        mLayoutBottom = (LinearLayout) findViewById(R.id.superplayer_ll_bottom);
        mLayoutBottom.setOnClickListener(this);
        mLayoutReplay = (LinearLayout) findViewById(R.id.superplayer_ll_replay);
        mTvTitle = (TextView) findViewById(R.id.superplayer_tv_title);
        mIvPause = (ImageView) findViewById(R.id.superplayer_iv_pause);
        mTvCurrent = (TextView) findViewById(R.id.superplayer_tv_current);
        mTvDuration = (TextView) findViewById(R.id.superplayer_tv_duration);
        mSeekBarProgress = (PointSeekBar) findViewById(R.id.superplayer_seekbar_progress);
        mSeekBarProgress.setProgress(0);
        mSeekBarProgress.setMax(100);
        mIvFullScreen = (ImageView) findViewById(R.id.superplayer_iv_fullscreen);
        mTvBackToLive = (TextView) findViewById(R.id.superplayer_tv_back_to_live);
        mPbLiveLoading = (ProgressBar) findViewById(R.id.superplayer_pb_live);

        mTvBackToLive.setOnClickListener(this);
        mIvPause.setOnClickListener(this);
        mIvFullScreen.setOnClickListener(this);
        mLayoutTop.setOnClickListener(this);
        mLayoutReplay.setOnClickListener(this);
        mTvSpeed = (TextView) findViewById(R.id.superplayer_tv_speed);
        mTvSpeed.setOnClickListener(this);
        mTvSpeed.setText("1x");
        mSeekBarProgress.setOnSeekBarChangeListener(this);

        mGestureVolumeBrightnessProgressLayout = (VolumeBrightnessProgressLayout)findViewById(R.id.superplayer_gesture_progress);

        mGestureVideoProgressLayout = (VideoProgressLayout) findViewById(R.id.superplayer_video_progress_layout);

        mBackground = (ImageView)findViewById(R.id.superplayer_small_iv_background);
        setBackground(mBackgroundBmp);

        mIvWatermark = (ImageView)findViewById(R.id.superplayer_small_iv_water_mark);
        setSpeed(mSpeed);
    }

    /**
     * 切换播放状态
     *
     * 双击和点击播放/暂停按钮会触发此方法
     */
    public void togglePlayState() {
        switch (mCurrentPlayState) {
            case PAUSE:
            case END:
                if (mControllerCallback != null) {
                    mControllerCallback.onResume();
                }
                break;
            case PLAYING:
            case LOADING:
                if (mControllerCallback != null) {
                    mControllerCallback.onPause();
                }
                mLayoutReplay.setVisibility(View.GONE);
                break;
        }
        show();
    }

    /**
     * 切换自身的可见性
     */
    private void toggle() {
        if (isShowing) {
            hide();
        } else {
            show();
            if (mHideViewRunnable != null) {
                removeCallbacks(mHideViewRunnable);
                postDelayed(mHideViewRunnable, 7000);
            }
        }
    }

    /**
     * 设置水印
     *
     * @param bmp 水印图
     * @param x   水印的x坐标
     * @param y   水印的y坐标
     */
    @Override
    public void setWatermark(final Bitmap bmp, float x, float y) {
        mWaterMarkBmp = bmp;
        mWaterMarkBmpX = x;
        mWaterMarkBmpY = y;
        if (bmp != null) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    int width = WindowPlayer.this.getWidth();
                    int height = WindowPlayer.this.getHeight();

                    int x = (int) (width * mWaterMarkBmpX) - bmp.getWidth() / 2;
                    int y = (int) (height * mWaterMarkBmpY) - bmp.getHeight() / 2;

                    mIvWatermark.setX(x);
                    mIvWatermark.setY(y);

                    mIvWatermark.setVisibility(VISIBLE);
                    setBitmap(mIvWatermark, bmp);
                }
            });
        } else {
            mIvWatermark.setVisibility(GONE);
        }
    }
    /**
     * 显示控件
     */

    @Override
    public void show() {
        isShowing = true;
        mLayoutTop.setVisibility(View.INVISIBLE);
        mLayoutBottom.setVisibility(View.VISIBLE);

        if (mPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
            mTvBackToLive.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 隐藏控件
     */


    @Override
    public void hide() {
        isShowing = false;
        mLayoutTop.setVisibility(View.GONE);
        mLayoutBottom.setVisibility(View.GONE);

        if (mPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
            mTvBackToLive.setVisibility(View.GONE);
        }
        disMissSpeedPop();
    }

    @Override
    public void updatePlayState(SuperPlayerDef.PlayerState playState) {
        switch (playState) {
            case PLAYING:
                mIvPause.setImageResource(R.drawable.superplayer_ic_vod_pause_normal);
                toggleView(mPbLiveLoading, false);
                toggleView(mLayoutReplay, false);
                break;
            case LOADING:
                mIvPause.setImageResource(R.drawable.superplayer_ic_vod_pause_normal);
                toggleView(mPbLiveLoading, true);
                toggleView(mLayoutReplay, false);
                break;
            case PAUSE:
                mIvPause.setImageResource(R.drawable.superplayer_ic_vod_play_normal);
                toggleView(mPbLiveLoading, false);
                toggleView(mLayoutReplay, false);
                break;
            case END:
                mIvPause.setImageResource(R.drawable.superplayer_ic_vod_play_normal);
                toggleView(mPbLiveLoading, false);
                toggleView(mLayoutReplay, true);
                break;
        }
        mCurrentPlayState = playState;
    }

    /**
     * 更新视频名称
     *
     * @param title 视频名称
     */
    @Override
    public void updateTitle(String title) {
        mTvTitle.setText(title);
    }

    /**
     * 更新视频播放进度
     *
     * @param current  当前进度(秒)
     * @param duration 视频总时长(秒)
     */
    @Override
    public void updateVideoProgress(long current, long duration) {
        mProgress = current < 0 ? 0 : current;
        mDuration = duration < 0 ? 0 : duration;

        if(mShowLocked){
           mDuration=mLimitDurcation;
        }

        mTvCurrent.setText(formattedTime(mProgress));

        float percentage = mDuration > 0 ? ((float) mProgress / (float) mDuration) : 1.0f;
        if (mProgress == 0) {
            mLivePushDuration = 0;
            percentage = 0;
        }
        if (mPlayType == SuperPlayerDef.PlayerType.LIVE || mPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
            mLivePushDuration = mLivePushDuration > mProgress ? mLivePushDuration : mProgress;
            long leftTime = mDuration - mProgress;
            mDuration = mDuration > MAX_SHIFT_TIME ? MAX_SHIFT_TIME : mDuration;
            percentage = 1 - (float) leftTime / (float) mDuration;
        }

        if (percentage >= 0 && percentage <= 1) {
            int progress = Math.round(percentage * mSeekBarProgress.getMax());
            if (!mIsChangingSeekBarProgress) {
                if (mPlayType == SuperPlayerDef.PlayerType.LIVE) {
                    mSeekBarProgress.setProgress(mSeekBarProgress.getMax());
                } else {
                    mSeekBarProgress.setProgress(progress);
                }
            }
            mTvDuration.setText(formattedTime(mDuration));
        }
    }

    @Override
    public void updatePlayType(SuperPlayerDef.PlayerType type) {
        mPlayType = type;
        switch (type) {
            case VOD:
                mTvBackToLive.setVisibility(View.GONE);
                mTvDuration.setVisibility(View.VISIBLE);
                break;
            case LIVE:
                mTvBackToLive.setVisibility(View.GONE);
                mTvDuration.setVisibility(View.GONE);
                mSeekBarProgress.setProgress(100);
                break;
            case LIVE_SHIFT:
                if (mLayoutBottom.getVisibility() == VISIBLE) {
                    mTvBackToLive.setVisibility(View.VISIBLE);
                }
                mTvDuration.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 设置背景
     *
     * @param bitmap 背景图
     */


    @Override
    public void setBackground(final Bitmap bitmap) {
        this.post(new Runnable() {
            @Override
            public void run() {
                if (bitmap == null) {
                    return;
                }
                if (mBackground == null) {
                    mBackgroundBmp = bitmap;
                } else {
                    setBitmap(mBackground, mBackgroundBmp);
                }
            }
        });
    }

    /**
     * 设置目标ImageView显示的图片
     */
    private void setBitmap(ImageView view, Bitmap bitmap) {
        if (view == null || bitmap == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(new BitmapDrawable(getContext().getResources(), bitmap));
        } else {
            view.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
        }
    }


    /**
     * 显示背景
     */


    @Override
    public void showBackground() {
        post(new Runnable() {
            @Override
            public void run() {
                ValueAnimator alpha = ValueAnimator.ofFloat(0.0f, 1);
                alpha.setDuration(500);
                alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (Float) animation.getAnimatedValue();
                        mBackground.setAlpha(value);
                        if (value == 1) {
                            mBackground.setVisibility(VISIBLE);
                        }
                    }
                });
                alpha.start();
            }
        });
    }

    /**
     * 隐藏背景
     */

    @Override
    public void hideBackground() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mBackground.getVisibility() != View.VISIBLE) {
                    return;
                }
                ValueAnimator alpha = ValueAnimator.ofFloat(1.0f, 0.0f);
                alpha.setDuration(500);
                alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (Float) animation.getAnimatedValue();
                        mBackground.setAlpha(value);
                        if (value == 0) {
                            mBackground.setVisibility(GONE);
                        }
                    }
                });
                alpha.start();
            }
        });
    }

    /**
     * 重写触摸事件监听，实现手势调节亮度、音量以及播放进度
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            mGestureDetector.onTouchEvent(event);
        }

        if (event.getAction() == MotionEvent.ACTION_UP && mVideoGestureDetector != null && mVideoGestureDetector.isVideoProgressModel()) {
            int progress = mVideoGestureDetector.getVideoProgress();
            if (progress > mSeekBarProgress.getMax()) {
                progress = mSeekBarProgress.getMax();
            }
            if (progress < 0) {
                progress = 0;
            }
            mSeekBarProgress.setProgress(progress);

            int seekTime;
            float percentage = progress * 1.0f / mSeekBarProgress.getMax();
            if (mPlayType == SuperPlayerDef.PlayerType.LIVE || mPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
                if (mLivePushDuration > MAX_SHIFT_TIME) {
                    seekTime = (int) (mLivePushDuration - MAX_SHIFT_TIME *  (1 - percentage));
                } else {
                    seekTime  = (int) (mLivePushDuration * percentage);
                }
            }else {
                seekTime = (int) (percentage * mDuration);
            }
            if (mControllerCallback != null) {
                mControllerCallback.onSeekTo(seekTime);
            }
            mIsChangingSeekBarProgress = false;
        }

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            removeCallbacks(mHideViewRunnable);
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            postDelayed(mHideViewRunnable, 7000);
        }
        return true;
    }


    private void convertPopupWindow(CustomPopupWindow.Builder builder) {

    }

    /**
     * 设置点击事件监听
     */


    @Override
    public void onClick(View view) {
        if (System.currentTimeMillis() - mLastClickTime < 300) { //限制点击频率
            return;
        }
        mLastClickTime = System.currentTimeMillis();
        int id = view.getId();
        if (id == R.id.superplayer_rl_top) { //顶部标题栏
            if (mControllerCallback != null) {
                mControllerCallback.onBackPressed(SuperPlayerDef.PlayerMode.WINDOW);
            }
        } else if (id == R.id.superplayer_iv_pause) { //暂停\播放按钮
            togglePlayState();
        } else if (id == R.id.superplayer_iv_fullscreen) { //全屏按钮
            if (mControllerCallback != null) {
                mControllerCallback.onSwitchPlayMode(SuperPlayerDef.PlayerMode.FULLSCREEN);
            }
        } else if (id == R.id.superplayer_ll_replay) { //重播按钮
            if (mControllerCallback != null) {
                mControllerCallback.onResume();
            }
        } else if (id == R.id.superplayer_tv_back_to_live) { //返回直播按钮
            if (mControllerCallback != null) {
                mControllerCallback.onResumeLive();
            }
        }

        else if (id == R.id.superplayer_tv_speed) { //返回直播按钮
           changeSpeed();
        }
    }

    @Override
    public void onProgressChanged(PointSeekBar seekBar, int progress, boolean fromUser) {

        if (mGestureVideoProgressLayout != null && fromUser) {
            mGestureVideoProgressLayout.show();
            float percentage = ((float) progress) / seekBar.getMax();
            float currentTime = (mDuration * percentage);
            if (mPlayType == SuperPlayerDef.PlayerType.LIVE || mPlayType == SuperPlayerDef.PlayerType.LIVE_SHIFT) {
                if (mLivePushDuration > MAX_SHIFT_TIME) {
                    currentTime = (int) (mLivePushDuration - MAX_SHIFT_TIME *  (1 - percentage));
                } else {
                    currentTime  = mLivePushDuration * percentage;
                }
                mGestureVideoProgressLayout.setTimeText(formattedTime((long) currentTime));
            } else {
                mGestureVideoProgressLayout.setTimeText(formattedTime((long) currentTime) + " / " + formattedTime((long) mDuration));
            }
            mGestureVideoProgressLayout.setProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(PointSeekBar seekBar) {
        removeCallbacks(mHideViewRunnable);
    }

    @Override
    public void onStopTrackingTouch(PointSeekBar seekBar) {
        int curProgress = seekBar.getProgress();
        int maxProgress = seekBar.getMax();


        switch (mPlayType) {
            case VOD:
                if (curProgress >= 0 && curProgress <= maxProgress) {
                    // 关闭重播按钮
                    toggleView(mLayoutReplay, false);
                    float percentage = ((float) curProgress) / maxProgress;
                    int position = (int) (mDuration * percentage);
                    if (mControllerCallback != null) {
                        mControllerCallback.onSeekTo(position);
                        mControllerCallback.onResume();
                    }
                }
                break;
            case LIVE:
            case LIVE_SHIFT:
                toggleView(mPbLiveLoading, true);
                int seekTime = (int) (mLivePushDuration * curProgress * 1.0f / maxProgress);
                if (mLivePushDuration > MAX_SHIFT_TIME) {
                    seekTime = (int) (mLivePushDuration - MAX_SHIFT_TIME *  (maxProgress - curProgress) * 1.0f / maxProgress);
                }
                if (mControllerCallback != null) {
                    mControllerCallback.onSeekTo(seekTime);
                }
                break;
        }
        postDelayed(mHideViewRunnable, 7000);
    }

    public void hideScreenBtn() {
        if(mIvFullScreen!=null){
            mIvFullScreen.setVisibility(View.GONE);
        }
    }
}

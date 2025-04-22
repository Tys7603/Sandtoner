package com.wanyue.course.video;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.tencent.liteav.demo.superplayer.SuperPlayerDef;
import com.tencent.liteav.demo.superplayer.model.SuperPlayerImpl;
import com.tencent.liteav.demo.superplayer.model.SuperPlayerObserver;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.course.R;
import com.wanyue.course.R2;
import butterknife.BindView;

public class TxAudioPlayViewProxy extends AbsLockPlayerViewProxy implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    @BindView(R2.id.img_thumb)
    ImageView mImgThumb;

    @BindView(R2.id.container)
    FrameLayout mContainer;

    @BindView(R2.id.seek_bar)
    SeekBar mSeekBar;

    @BindView(R2.id.tv_current_time)
    TextView mTvCurrentTime;

    @BindView(R2.id.tv_total_time)
    TextView mTvTotalTime;
    @BindView(R2.id.btn_controll)
    ImageView mBtnControll;

    private SuperPlayerImpl mSuperPlayer;

    private boolean  mShowLocked;
    private int mLimitDurcation;
    private View mHintView;
    private long mProgress;
    private long mDuration;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mSeekBar.setMax(100);
        mSeekBar.setOnSeekBarChangeListener(this);
        mBtnControll.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_tx_audio;
    }

    @Override
    public void startPlay(String path, String thumb) {
        initPlayer();
        mSuperPlayer.play(path);
        ImgLoader.display(getActivity(),thumb,mImgThumb);
    }

    private void lockVideo(long current, long duration) {
        if(mShowLocked&&current>=mLimitDurcation){
            if(mSuperPlayer!=null){
                mSuperPlayer.seek(mLimitDurcation);
                mSuperPlayer.pause();
            }
        }
    }

    private void initPlayer() {
       if(mSuperPlayer==null){
          mSuperPlayer = new SuperPlayerImpl(getActivity(),null);
          mSuperPlayer.setObserver(new SuperPlayerObserver() {
              @Override
              public void onPlayProgress(long current, long duration) {
                  super.onPlayProgress(current, duration);
                  lockVideo(current,duration);
                  updatePlayProgress(current,duration);
                  checkPlayLimit(current,duration);
              }
              @Override
              public void onPlayPause() {
                  super.onPlayPause();
                  updatePlayState(SuperPlayerDef.PlayerState.PAUSE);
              }
              @Override
              public void onPlayBegin(String name) {
                  super.onPlayBegin(name);
                  updatePlayState(SuperPlayerDef.PlayerState.PLAYING);
              }
              @Override
              public void onPlayStop() {
                  super.onPlayStop();
                  updatePlayState(SuperPlayerDef.PlayerState.END);
              }
          });
       }
    }

    private SuperPlayerDef.PlayerState mPlayState;
    public void updatePlayState(SuperPlayerDef.PlayerState playState){
        mPlayState=playState;
        switch (playState){
            case PLAYING:
                if(mBtnControll!=null){
                   mBtnControll.setImageResource(R.drawable.icon_audio_pause);
                }
            break;
            case  END:
            case PAUSE:
            case  LOADING:
            if(mBtnControll!=null){
               mBtnControll.setImageResource(R.drawable.icon_audio_play);
            }

             break;
            default:
        }
    }

    private void togglePlayState() {
        switch (mPlayState) {
            case PAUSE:
            case END:
                if(mSuperPlayer!=null){
                   mSuperPlayer.resume();
                }
                break;
            case PLAYING:
            case LOADING:
                if(mSuperPlayer!=null){
                    mSuperPlayer.pause();
                }
                break;
            default:
        }
    }

    private void updatePlayProgress(long current, long duration) {
        mProgress = current < 0 ? 0 : current;
        mDuration = duration < 0 ? 0 : duration;

        if(mShowLocked){
           mDuration=mLimitDurcation;
        }
        float percentage = mDuration > 0 ? ((float) mProgress / (float) mDuration) : 1.0f;
        if (mProgress == 0) {
            percentage = 0;
        }
        if (percentage >= 0 && percentage <= 1) {
            int progress = Math.round(percentage * mSeekBar.getMax());
            mSeekBar.setProgress(progress);
            mTvCurrentTime.setText(formattedTime(mProgress));
            mTvTotalTime.setText(formattedTime(mDuration));
        }
    }


    protected String formattedTime(long second) {
        String formatTime;
        long h, m, s;
        h = second / 3600;
        m = (second % 3600) / 60;
        s = (second % 3600) % 60;
        if (h == 0) {
            formatTime = asTwoDigit(m) + ":" + asTwoDigit(s);
        } else {
            formatTime = asTwoDigit(h) + ":" + asTwoDigit(m) + ":" + asTwoDigit(s);
        }
        return formatTime;
    }

    protected String asTwoDigit(long digit) {
        String value = "";
        if (digit < 10) {
            value = "0";
        }
        value += String.valueOf(digit);
        return value;
    }



    @Override
    public void addTintView(View view, int position) {
        if(mContainer!=null){
           mHintView=view;
           ViewUtil.addToViewGroup(mContainer,view,position);
           ViewUtil.setVisibility(mHintView,View.INVISIBLE);
        }
    }

    private void checkPlayLimit(long current, long duration) {
        if(!mShowLocked){
            return;
        }
        if(current>=mLimitDurcation){
            ViewUtil.setVisibility(mHintView,View.VISIBLE);
        }else{
            ViewUtil.setVisibility(mHintView,View.INVISIBLE);
        }
    }


    @Override
    public void setShowLocked(boolean showLocked, int limitDurcation) {
        mShowLocked=showLocked;
        mLimitDurcation=limitDurcation;
        if(!mShowLocked){
            ViewUtil.setVisibility(mHintView,View.INVISIBLE);
        }
    }

    @Override
    public void seekTo(long position) {
        if (mSuperPlayer != null) {
            mSuperPlayer.seek((int) position);
        }
    }
    @Override
    public void resume() {
        if (mSuperPlayer != null) {
            mSuperPlayer.resume();
        }
    }

    @Override
    public void pause() {
        if (mSuperPlayer != null) {
            mSuperPlayer.pause();
        }
    }
    @Override
    public void release() {
        if (mSuperPlayer != null) {
            mSuperPlayer.destroy();
        }
    }


    @Override
    public void onClick(View v) {
        togglePlayState();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int curProgress = seekBar.getProgress();
        int maxProgress = seekBar.getMax();
        if (curProgress >= 0 && curProgress <= maxProgress) {
            float percentage = ((float) curProgress) / maxProgress;
            int position = (int) (mDuration * percentage);
            if (mSuperPlayer != null) {
                mSuperPlayer.seek(position);
                mSuperPlayer.resume();
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSuperPlayer!=null){
           mSuperPlayer.destroy();
        }
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }
}

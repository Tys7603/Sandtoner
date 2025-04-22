package com.wanyue.course.video;

import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.tencent.liteav.demo.superplayer.SuperPlayerModel;
import com.tencent.liteav.demo.superplayer.SuperPlayerView;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.Constants;
import com.wanyue.common.bean.UserBean;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.course.R;

import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class TxVideoPlayViewProxy extends AbsLockPlayerViewProxy implements IPlayerViewProxy {

    private boolean  mShowLocked;
    private int mLimitDurcation;
    private View mHintView;
    private SuperPlayerView mPlayer;
    private boolean mShowVideo=true;
    private String mPath;
    private String mThumb;
    private boolean mLockedScreen;
    private Disposable mDisposable;
    private OnInfoListner mOnInfoListner;
    private int mSeekPosition=0;
    private boolean mFirstPlay=true;
    private boolean mIsPlay;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mPlayer =findViewById(R.id.player);
        if(mLockedScreen){
           mPlayer.hideScreen();
        }

        mPlayer.setPlayerViewCallback(new SuperPlayerView.OnSuperPlayerViewCallback() {
            @Override
            public void onStartFullScreenPlay() {
                TxVideoPlayViewProxy.this.onStartFullScreenPlay();
            }
            @Override
            public void onStopFullScreenPlay() {
                TxVideoPlayViewProxy.this.onStopFullScreenPlay();
            }
            @Override
            public void onClickFloatCloseBtn() {

            }
            @Override
            public void onClickSmallReturnBtn() {

            }
            @Override
            public void onStartFloatWindowPlay() {

            }
        });
        mPlayer.setOnPlayerListner(new SuperPlayerView.OnPlayerListner() {
            @Override
            public void onPlayProgress(long current, long duration) {
                mIsPlay=true;
               // mSeekPosition= (int) current;
                if(mOnInfoListner!=null){
                    mOnInfoListner.onProgress(current,duration);
                }
                checkPlayLimit(current,duration);
            }

            @Override
            public void onPlayBegin(String name) {
                if(mShowVideo){
                   mPlayer.hideThumb(mShowVideo);
                }
                if(mFirstPlay&&mSeekPosition>0&&mIsPlay){
                   mFirstPlay=false;
                   mPlayer.seek(mSeekPosition);
                }
            }
        });
        if(mPath!=null){
           startPlay(mPath,mThumb);
        }
    }

    public void onStopFullScreenPlay() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void onStartFullScreenPlay() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
    public int getLayoutId() {
        return R.layout.view_tx_video;
    }

    @Override
    public void startPlay(String path, String thumb) {
        L.e("mSeekPosition==startPlay");
        this.mPath=path;
        this.mThumb=thumb;
        if(mPlayer==null){
           return;
        }
        if(mPlayer!=null){
           mPlayer.setThumb(thumb);
        }

        String title= (String) getArgMap().get(Constants.KEY_TITLE);
        mPlayer.updateTitle(title);
        SuperPlayerModel model = new SuperPlayerModel();
        model.url = mPath;
        mPlayer.playWithModel(model);

    }

    @Override
    public void addTintView(View view, int position) {
        /*if(mContentView!=null){
           mHintView=view;
           mContentView.addView(view,position);
           ViewUtil.addToViewGroup(mContentView,view,position);
           ViewUtil.setVisibility(mHintView,View.INVISIBLE);
        }*/
    }

    @Override
    public void setShowLocked(boolean showLocked, int limitDurcation) {
        mShowLocked=showLocked;
        mLimitDurcation=limitDurcation;
        if(mPlayer!=null){
           mPlayer.setShowLocked(showLocked,limitDurcation);
        }
        if(!showLocked){
           ViewUtil.setVisibility(mHintView,View.INVISIBLE);
        }
    }

    @Override
    public void seekTo(long position) {
        if(mPlayer!=null){
           mPlayer.seek((int) position);
        }
    }

    @Override
    public void resume() {
        if(mPlayer!=null){
           mPlayer.onResume();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
       // pause();
    }





    @Override
    public void onResume() {
        super.onResume();
        //resume();
    }


    @Override
    public void pause() {
        if(mPlayer!=null){
           mPlayer.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    @Override
    public void release() {
        if(mPlayer!=null){
           mPlayer.release();
        }
        if(mDisposable!=null&&!mDisposable.isDisposed()){
           mDisposable.dispose();
        }
    }
    public void setShowVideo(boolean b) {
        this.mShowVideo=b;
    }
    public void setShowTitle(boolean b) {
    }
    public void setPath(String path, String thumb){
        this.mPath=path;
        this.mThumb=thumb;
    }

    public void setSeekPosition(int seekPosition) {
        mSeekPosition = seekPosition;
    }

    public void setOnInfoListner(OnInfoListner onInfoListner) {
        mOnInfoListner = onInfoListner;
    }

    public interface OnInfoListner{
        public void onProgress(long current, long total);
    }

    public void setLockedScreen(boolean mLockScreen) {
       mLockedScreen=mLockScreen;
    }
}

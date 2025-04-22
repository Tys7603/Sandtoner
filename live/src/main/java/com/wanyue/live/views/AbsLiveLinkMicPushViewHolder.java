package com.wanyue.live.views;

import android.content.Context;
import android.view.ViewGroup;

import com.wanyue.common.views.AbsViewHolder;
import com.wanyue.live.interfaces.LivePushListener;

/**
 * 2018/10/26.
 * 连麦推流小窗口基类
 */
public abstract class AbsLiveLinkMicPushViewHolder extends AbsViewHolder  {

    /**
     * The M live push listener.
     */
    protected LivePushListener mLivePushListener;
    /**
     * The M paused.
     */
    protected boolean mPaused;
    /**
     * The M start push.
     */
    protected boolean mStartPush;

    /**
     * Instantiates a new Abs live link mic push view holder.
     *
     * @param context    the context
     * @param parentView the parent view
     */
    public AbsLiveLinkMicPushViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    /**
     * Instantiates a new Abs live link mic push view holder.
     *
     * @param context    the context
     * @param parentView the parent view
     * @param args       the args
     */
    public AbsLiveLinkMicPushViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    /**
     * 开始推流
     *
     * @param pushUrl 推流地址
     */
    public abstract  void startPush(String pushUrl) ;

    public abstract void release() ;

    /**
     * Pause.
     */
    public abstract void pause() ;

    /**
     * Resume.
     */
    public abstract void resume() ;

    /**
     * Sets live push listener.
     *
     * @param livePushListener the live push listener
     */
    public void setLivePushListener(LivePushListener livePushListener) {
        mLivePushListener = livePushListener;
    }
}

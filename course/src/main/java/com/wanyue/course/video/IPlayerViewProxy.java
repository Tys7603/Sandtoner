package com.wanyue.course.video;

import android.view.View;

public interface IPlayerViewProxy {
    public static final int NO_LIMIT=0;

    public void startPlay(String path, String thumb);//开始播放
    public void addTintView(View view, int position); //添加封面
    public void setShowLocked(boolean showLocked, int limitDurcation);//设置限制
    public void seekTo(long position); //快进到制定位置
    public void resume();
    public void pause();
    public void release();
}

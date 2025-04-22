package com.tencent.liteav.demo.superplayer.ui.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wanyue.common.custom.CustomPopupWindow;
import com.wanyue.video.R;
import com.tencent.liteav.demo.superplayer.SuperPlayerDef;
import com.tencent.liteav.demo.superplayer.model.entity.PlayImageSpriteInfo;
import com.tencent.liteav.demo.superplayer.model.entity.PlayKeyFrameDescInfo;
import com.tencent.liteav.demo.superplayer.model.entity.VideoQuality;
import com.wanyue.common.utils.DpUtil;

import java.util.List;

/**
 * 播放器公共逻辑
 */
public abstract class AbsPlayer extends RelativeLayout implements Player {

    protected boolean  mShowLocked;
    protected int mLimitDurcation;

    protected float                               mSpeed=1;                                 // 当前播放速率
    protected CustomPopupWindow mCustomPopupWindow;
    protected TextView mTvSpeed;  // 速度

    protected static final int MAX_SHIFT_TIME = 7200; // demo演示直播时移是MAX_SHIFT_TIMEs，即2小时

    protected Callback mControllerCallback; // 播放控制回调

    protected Runnable mHideViewRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };


    public AbsPlayer(Context context) {
        super(context);
    }

    public AbsPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setCallback(Callback callback) {
        mControllerCallback = callback;
    }

    @Override
    public void setWatermark(Bitmap bmp, float x, float y) {
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void release() {

    }

    @Override
    public void updatePlayState(SuperPlayerDef.PlayerState playState) {

    }

    @Override
    public void setVideoQualityList(List<VideoQuality> list) {

    }

    @Override
    public void updateTitle(String title) {

    }

    @Override
    public void updateVideoProgress(long current, long duration) {

    }

    @Override
    public void updatePlayType(SuperPlayerDef.PlayerType type) {

    }

    @Override
    public void setBackground(Bitmap bitmap) {

    }

    @Override
    public void showBackground() {

    }

    @Override
    public void hideBackground() {

    }

    @Override
    public void updateVideoQuality(VideoQuality videoQuality) {

    }

    @Override
    public void updateImageSpriteInfo(PlayImageSpriteInfo info) {

    }

    @Override
    public void updateKeyFrameDescInfo(List<PlayKeyFrameDescInfo> list) {

    }

    /**
     * 设置控件的可见性
     *
     * @param view      目标控件
     * @param isVisible 显示：true 隐藏：false
     */
    protected void toggleView(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 将秒数转换为hh:mm:ss的格式
     *
     * @param second
     * @return
     */
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


    public void setSpeed(float speed) {
        if(mSpeed==speed){
           return;
        }
        mSpeed=speed;
        String speedText="";
        if(speed==1.0F){
           speedText = "1x";
        }else if(mSpeed==2.0F){
           speedText = "2x";
        }else if(mSpeed==1.5F){
           speedText = "1.5x";
        }else if(mSpeed==0.5F){
           speedText = "0.5x";
        }
        if(mTvSpeed!=null){
           mTvSpeed.setText(speedText);
        }
    }




    protected void changeSpeed() {
        int width = DpUtil.dp2px(50);
        int height = DpUtil.dp2px(104);
        CustomPopupWindow.Builder builder = new CustomPopupWindow.Builder(getContext());
        builder.setContentView(R.layout.item_speed_adjust)
                .setFouse(true)
                .setOutSideCancel(true)
                .setwidth(width)
                .setheight(height);
        mCustomPopupWindow = new CustomPopupWindow(builder);
        RadioGroup radioGroup=mCustomPopupWindow.getItemView(R.id.radio_group);

        showView(mTvSpeed,height);
        if(mSpeed==1.0F){
            radioGroup.check(R.id.rb_speed1);
        }else if(mSpeed==2.0F){
            radioGroup.check(R.id.rb_speed2);
        }else if(mSpeed==1.5F){
            radioGroup.check(R.id.rb_speed15);
        }else if(mSpeed==0.5F){
            radioGroup.check(R.id.rb_rb_speed05);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.rb_speed2){
                    clickSetSpeed(2.0F);
                }else if(checkedId==R.id.rb_speed15){
                    clickSetSpeed(1.5F);
                }else if(checkedId==R.id.rb_speed1){
                    clickSetSpeed(1F);
                }else{
                    clickSetSpeed(0.5F);
                }
            }
        });
    }
    
    private void clickSetSpeed(float v) {
        if(mControllerCallback!=null){
           mControllerCallback.onSpeedChange(v);
        }
        disMissSpeedPop();
    }

    public void disMissSpeedPop(){
       if(mCustomPopupWindow!=null){
          mCustomPopupWindow.dismiss();
           mCustomPopupWindow=null;
       }
    }

    public void showView(View view, int height) {
        int xOffect =  view.getWidth()/2;
        height=height+DpUtil.dp2px(20);
        mCustomPopupWindow.showAsLaction(view, Gravity.CENTER, -xOffect, -height);
    }


    public void setShowLocked(boolean showLocked, int limitDurcation){
        this.mShowLocked=showLocked;
        this.mLimitDurcation=limitDurcation;
    }

}

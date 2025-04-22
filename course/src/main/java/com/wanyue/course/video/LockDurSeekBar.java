package com.wanyue.course.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.wanyue.common.utils.DpUtil;

public class LockDurSeekBar extends androidx.appcompat.widget.AppCompatSeekBar {
    private OnLockExactSeekChangeListner mOnLockExactSeekChangeListner;

    public LockDurSeekBar(Context context) {
        super(context);
        init(context);
    }



    public LockDurSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LockDurSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        setBackground(null);
        int defaultPadding= DpUtil.dp2px(3);
        setPadding(0,defaultPadding,0,defaultPadding);
        setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mOnLockExactSeekChangeListner!=null){
                   mOnLockExactSeekChangeListner.onProgressChanged(seekBar,progress,fromUser);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mOnLockExactSeekChangeListner!=null){
                    mOnLockExactSeekChangeListner.onStartTrackingTouch(seekBar);
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mOnLockExactSeekChangeListner!=null){
                   mOnLockExactSeekChangeListner.onStopTrackingTouch(seekBar);
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(lockEvent(event)){
            return false;
        }
        return super.onTouchEvent(event);
    }

    /*锁定滑动事件*/
    private boolean lockEvent(MotionEvent event) {
        int paddingLeft=getPaddingLeft();
        int paddingRight=getPaddingRight();
        double width= getMeasuredWidth()+paddingLeft+paddingRight;
        double x=event.getX();
        double currentScale=x/width;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
               if(mOnLockExactSeekChangeListner!=null){
                 return mOnLockExactSeekChangeListner.needLock(currentScale);
               }
        }
        return false;
    }


    public void setOnLockExactSeekChangeListner(OnLockExactSeekChangeListner onLockExactSeekChangeListner) {
        mOnLockExactSeekChangeListner = onLockExactSeekChangeListner;
    }




    public interface OnLockExactSeekChangeListner extends OnSeekBarChangeListener{
        public boolean needLock(double currentScale);
    }

}

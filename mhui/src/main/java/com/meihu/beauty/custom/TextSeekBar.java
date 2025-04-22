package com.meihu.beauty.custom;

import android.content.Context;


import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.meihu.beauty.R;

/**
 * The type Text seek bar.
 */
public class TextSeekBar extends FrameLayout {

    private TextView mTextView;
    private SeekBar mSeekBar;
    private float mScale;
    private int mDp20;
    private int mLeft;
    private int mRight;
    private ActionListener mActionListener;

    /**
     * Instantiates a new Text seek bar.
     *
     * @param context the context
     */
    public TextSeekBar(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Text seek bar.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public TextSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Text seek bar.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public TextSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScale = dm.density;
        mDp20 = dp2px(20);
        View v = LayoutInflater.from(context).inflate(R.layout.view_text_seekbar, this, false);
        mTextView = v.findViewById(R.id.text);
        mSeekBar = v.findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    changeTextViewX();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        addView(v);
        mLeft = mDp20 + mSeekBar.getPaddingLeft();
        mRight = dm.widthPixels - mDp20 - mSeekBar.getPaddingLeft();
    }

    /**
     * Sets progress.
     *
     * @param progress the progress
     */
    public void setProgress(int progress) {
        if (mSeekBar != null) {
            mSeekBar.setProgress(progress);
        }
        changeTextViewX();
    }

    /**
     * Sets max.
     *
     * @param max the max
     */
    public void setMax(int max) {
        if (mSeekBar != null && mSeekBar.getMax() != max) {
            mSeekBar.setMax(max);
        }
    }

    private void changeTextViewX() {
        if (mSeekBar != null) {
            int progress = mSeekBar.getProgress();
            float rate = ((float) progress) / mSeekBar.getMax();
            if (mTextView != null) {
                mTextView.setX(mLeft + (mRight - mLeft) * rate - mDp20);
                mTextView.setText(String.valueOf(progress));
            }
            if (mActionListener != null) {
                mActionListener.onProgressChanged(rate, progress);
            }
        }
    }


    private int dp2px(int dpVal) {
        return (int) (dpVal * mScale + 0.5f);
    }

    /**
     * Sets action listener.
     *
     * @param actionListener the action listener
     */
    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    /**
     * The interface Action listener.
     */
    public interface ActionListener {
        /**
         * On progress changed.
         *
         * @param rate     the rate
         * @param progress the progress
         */
        void onProgressChanged(float rate, int progress);
    }
}

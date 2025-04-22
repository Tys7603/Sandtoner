package com.wanyue.live.custom;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;

/**
 * 2018/10/13.
 */
public class GiftPageViewPager extends ViewPager {


    /**
     * Instantiates a new Gift page view pager.
     *
     * @param context the context
     */
    public GiftPageViewPager(@NonNull Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Gift page view pager.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public GiftPageViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize / 2, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}

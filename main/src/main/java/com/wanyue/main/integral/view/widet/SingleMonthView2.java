package com.wanyue.main.integral.view.widet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.StringUtil;

public class SingleMonthView2 extends MonthView {
    private  Paint mBorderPaint;
    private int mRadius=-1;
    protected Paint mCurMonthTextPaint = new Paint();

    /**
     * 不可用画笔
     */

    private Paint mDisablePaint = new Paint();
    private int mH;
    /*单项选择不支持*/

    public SingleMonthView2(Context context) {
        super(context);
        L.e("SingleMonthView=="+this.hashCode());
        mH = dipToPx(context, 18);

        mDisablePaint.setAntiAlias(true);
        mDisablePaint.setStrokeWidth(dipToPx(context,2));
        mDisablePaint.setFakeBoldText(true);

        mCurMonthTextPaint.setAntiAlias(true);
        mCurMonthTextPaint.setTextAlign(Paint.Align.CENTER);
        mCurMonthTextPaint.setColor(Color.BLACK);
        mCurMonthTextPaint.setFakeBoldText(true);
        mCurMonthTextPaint.setTextSize(dipToPx(getContext(),TEXT_SIZE));
        mCurDayTextPaint.setColor(0xFF2B7BFF);
    }

    @Override
    protected void onPreviewHook() {
        if(mRadius==-1){
            mRadius = Math.min(mItemWidth, mItemHeight) / 6 * 2;
            mSelectTextPaint.setTextSize(dipToPx(getContext(),17));
        }
        mCurMonthTextPaint.setColor(Color.BLACK);
    }

    /**
     * 如果需要点击Scheme没有效果，则return true
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param y         日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return false 则不绘制onDrawScheme，因为这里背景色是互斥的
     */

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {

        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        L.e("onDrawText==."+calendar.toString());
        float baselineY = mTextBaseLine + y - dipToPx(getContext(),1);
        int cx = x + mItemWidth / 2 - dipToPx(getContext(),1);
        String expand="";
        if(calendar.isCurrentDay()){
            Paint.FontMetrics metrics = mCurMonthTextPaint.getFontMetrics();
            float line1=mTextBaseLine + y - dipToPx(getContext(),1)-(metrics.bottom - metrics.top) / 2;
            ;
            canvas.drawText(String.valueOf(calendar.getDay()),
                   cx,
                    line1,
                   mCurDayTextPaint);

            if(hasScheme){
                expand="已签";
            }else{
                expand="今";
            }
            float line2=mTextBaseLine + y - dipToPx(getContext(),1)+(metrics.bottom - metrics.top) / 2;
            canvas.drawText(expand,
                    cx,
                    line2,
                    mCurDayTextPaint);

        }else{
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentMonth()?mCurMonthTextPaint : mOtherMonthTextPaint);
        }

        //日期是否可用？拦截
        if (onCalendarIntercept(calendar)) {
            canvas.drawLine(x + mH, y + mH, x + mItemWidth - mH, y + mItemHeight - mH, mDisablePaint);
        }
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

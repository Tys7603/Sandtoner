package com.wanyue.main.integral.view.widet;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;

public  class SignMonthView extends MonthView {
    private  int mH;
    private Paint mTodayPaint ;
    private Paint mNormalPaint;
    private Paint mDisablePaint ;

    public SignMonthView(Context context) {
        super(context);
        mH = dipToPx(context, 18);
        mTodayPaint = new Paint();
        mTodayPaint.setColor(0xFFFF5121);
        mTodayPaint.setAntiAlias(true);
        mTodayPaint.setStyle(Paint.Style.FILL);

        mNormalPaint = new Paint();
        mNormalPaint.setColor(Color.BLACK);
        mNormalPaint.setStyle(Paint.Style.FILL);
        mNormalPaint.setTextSize(13);
        mNormalPaint.setAntiAlias(true);
        mNormalPaint.setTextAlign(Paint.Align.CENTER);
        mNormalPaint.setFakeBoldText(true);

        mDisablePaint= new Paint();
        mDisablePaint.setColor(0xFF9f9f9f);
        mDisablePaint.setAntiAlias(true);
        mDisablePaint.setStrokeWidth(dipToPx(context,2));
        mDisablePaint.setFakeBoldText(true);
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y - dipToPx(getContext(),1);
        int cx = x + mItemWidth / 2;

    }

    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

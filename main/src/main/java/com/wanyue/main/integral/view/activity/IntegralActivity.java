package com.wanyue.main.integral.view.activity;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONObject;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.lxj.xpopup.XPopup;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.server.observer.DialogObserver;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.main.R;
import com.wanyue.main.R2;
import com.wanyue.main.integral.api.SignAPI;
import com.wanyue.main.integral.bean.SignRecordBean;
import com.wanyue.main.integral.bean.WeekSignBean;
import com.wanyue.main.integral.view.proxy.SignProgressViewProxy;
import butterknife.OnClick;

public class IntegralActivity extends BaseActivity implements CalendarView.OnMonthChangeListener {

    private TextView mTvIntegralCode;
    private TextView mBtnSign;
    private TextView mTvTime;
    private CalendarView mCalendarView;
    private SignRecordBean mData;
    private TextView mTvCurrentProfit;
    private ViewGroup mVpIntegral;

    private SignProgressViewProxy mProgressViewProxy;



    @Override
    public void init() {
        setTabTitle("Points Sign In");
        mTvIntegralCode = findViewById(R.id.tv_integral_code);
        mBtnSign = findViewById(R.id.btn_sign);
        mTvTime =  findViewById(R.id.tv_time);
        mTvCurrentProfit = (TextView) findViewById(R.id.tv_current_profit);
        mVpIntegral = (ViewGroup) findViewById(R.id.vp_integral);


        mProgressViewProxy=new SignProgressViewProxy();
        getViewProxyMannger().addViewProxy(mVpIntegral,mProgressViewProxy,mProgressViewProxy.getDefaultTag());
        mCalendarView = findViewById(R.id.calendarView);
        mCalendarView.setOnMonthChangeListener(this);
        setCurrentYearAndMonth(mCalendarView.getCurYear(),mCalendarView.getCurMonth());
        initData();
    }

    private void initData() {
        SignAPI.getSignRecord().compose(this.<SignRecordBean>bindToLifecycle())
                .subscribe(new DefaultObserver<SignRecordBean>() {
                    @Override
                    public void onNext(@NonNull SignRecordBean signRecordBean) {
                        mData=signRecordBean;
                        if(mProgressViewProxy!=null){
                           signRecordBean.formatWeekSign();
                           mProgressViewProxy.setCurrentLevel(signRecordBean.getTotalDay());
                           mProgressViewProxy.setData(signRecordBean.getWeekSign());
                        }
                        String integral= CommonAppConfig.getUserBean().getIntegral();
                        signRecordBean.setCode(integral);

                        mTvIntegralCode.setText(getString(R.string.sign_tip0,mData.getCode()));
                        mTvCurrentProfit.setText(getString(R.string.sign_tip1,mData.getTodayReward()));
                        checkSignUIState();
                    }
                });
    }

    private void checkSignUIState() {
        if(mData.getIsSign()==1){
            setCurrentDaySign();
            mBtnSign.setText("已签到");
            mBtnSign.setEnabled(false);
            Drawable drawable=ResourceUtil.getDrawable(R.drawable.bg_color_global_radius_20,false);
            drawable.setTint(ResourceUtil.getColor(R.color.gray_dc));
            mBtnSign.setBackground(drawable);
        }else{
            mBtnSign.setText("Sign In");
            mBtnSign.setEnabled(true);
            Drawable drawable=ResourceUtil.getDrawable(R.drawable.bg_color_global_radius_20,false);
            mBtnSign.setBackground(drawable);
        }
    }

    private void setCurrentDaySign() {
        Calendar calendar=getCalendar(mCalendarView.getCurYear(),mCalendarView.getCurMonth(),mCalendarView.getCurDay());
        mCalendarView.addSchemeDate(calendar);
    }

    private Calendar getCalendar(int year,int month,int day){
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        return calendar;
    }

    @OnClick(R2.id.btn_sign)
    public void startsign(){
        if(mData==null){
            return;
        }
        SignAPI.startSign().subscribe(new DialogObserver<JSONObject>(this) {
            @Override
            public void onNextTo(JSONObject jsonObject) {
                String code=jsonObject.getString("integral");
                CommonAppConfig.getUserBean().setIntegral(code);
                initData();
                openSuccDialog();
            }
        });
    }
    private void openSuccDialog() {
        SignSuccDialog dialog=new SignSuccDialog(this);
        new XPopup.Builder(mContext)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asCustom(dialog)
                .show();
    }

    @OnClick(R2.id.btn_arrange_left)
    public void backMonth() {
        if(mCalendarView==null){
            return;
        }
        mCalendarView.scrollToPre();
    }

    @OnClick(R2.id.btn_arrange_right)
    public void nextMonth() {
        if(mCalendarView==null){
            return;
        }
        mCalendarView.scrollToNext();

    }

    private String setCurrentYearAndMonth(int year,int month) {
        L.e("mCalendarView.getCurMonth()=="+mCalendarView.getCurMonth());
        String timeString=null;
        if(month<10){
            timeString=year + "-0" + month;
        }else{
            timeString=year + "-" + month;
        }
        mTvTime.setText(timeString);
        return timeString;
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_integral;
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }

    @Override
    public void onMonthChange(int year, int month) {
        setCurrentYearAndMonth(year,month);
    }
}
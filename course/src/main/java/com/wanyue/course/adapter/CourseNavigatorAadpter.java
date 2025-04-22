package com.wanyue.course.adapter;

import android.content.Context;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.bean.ExportNamer;
import com.wanyue.common.custom.ScaleTransitionPagerTitleView;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.course.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class CourseNavigatorAadpter extends CommonNavigatorAdapter {

    private String [] mTitle;
    private Context mContext;
    private ViewPager mViewPager;
    private int itemWidth;

    private int mSelectColor;
    private int mDefaultColor;
    private int mLineColor;
    private boolean mEableScale=false;
    private List<SimplePagerTitleView>mTitleViewList;


    public CourseNavigatorAadpter(@NotNull String []list, Context context, ViewPager viewPager) {
        mTitle = list;
        mContext = context;
        mViewPager = viewPager;
        //itemWidth= CommonAppConfig.getWindowWidth()/5;
        mSelectColor=ContextCompat.getColor(mContext, R.color.textColor);
        mDefaultColor=ContextCompat.getColor(mContext, R.color.textColor2);
        mLineColor=ContextCompat.getColor(mContext, R.color.global);
    }

    @Override
    public int getCount()  {
        return mTitle.length;
    }
    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        if(mTitleViewList==null){
            mTitleViewList =new ArrayList<>(mTitle.length);
        }
        SimplePagerTitleView simplePagerTitleView =null;
        if(mEableScale){
           simplePagerTitleView=new ScaleTransitionPagerTitleView(context);
        }else{
            simplePagerTitleView=new SimplePagerTitleView(context);
        }

        simplePagerTitleView.setNormalColor(mDefaultColor);
        simplePagerTitleView.setSelectedColor(mSelectColor);
        //simplePagerTitleView.setWidth(itemWidth);
        simplePagerTitleView.setText(mTitle[index]);
        simplePagerTitleView.setTextSize(13);
        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(index);
                }
            }
        });
        mTitleViewList.add(simplePagerTitleView);
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
        linePagerIndicator.setXOffset(DpUtil.dp2px(10));
        linePagerIndicator.setLineWidth(DpUtil.dp2px(15));
        linePagerIndicator.setRoundRadius(DpUtil.dp2px(2));
        linePagerIndicator.setColors(mLineColor);
        return linePagerIndicator;
    }

    public void setEableScale(boolean eableScale) {
        mEableScale = eableScale;
    }
}

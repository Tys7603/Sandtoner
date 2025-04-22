package com.wanyue.main.find.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.L;
import com.wanyue.main.R;
import com.wanyue.main.find.widet.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import org.jetbrains.annotations.NotNull;

public class MainFindIndicatorAdapter extends CommonNavigatorAdapter {
    private String[] mTitle;
    private Context mContext;
    private ViewPager mViewPager;
    private int itemWidth;

    private int mSelectColor;
    private int mDefaultColor;
    private int mLineColor= Color.WHITE;

    public MainFindIndicatorAdapter(@NotNull String[]list, Context context, ViewPager viewPager) {
        mTitle = list;
        mContext = context;
        mViewPager = viewPager;
        mSelectColor= ContextCompat.getColor(mContext, R.color.white);
        mDefaultColor=ContextCompat.getColor(mContext, R.color.white);
    }

    @Override
    public int getCount()  {
        return mTitle.length;
    }
    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
        simplePagerTitleView.setNormalColor(mDefaultColor);
        simplePagerTitleView.setSelectedColor(mSelectColor);
        simplePagerTitleView.setText(mTitle[index]);
        simplePagerTitleView.setTextSize(15);

        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(index);
                }
            }
        });
        return simplePagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_EXACTLY);
        linePagerIndicator.setXOffset(DpUtil.dp2px(10));
        linePagerIndicator.setLineHeight(DpUtil.dp2px(15));
        linePagerIndicator.setLineWidth(DpUtil.dp2px(20));
        linePagerIndicator.setYOffset(DpUtil.dp2px(2));
        linePagerIndicator.setColors(mLineColor);
        return linePagerIndicator;
    }

}

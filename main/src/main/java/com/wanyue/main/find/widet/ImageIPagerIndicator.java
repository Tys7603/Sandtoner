package com.wanyue.main.find.widet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;
import java.util.List;

public class ImageIPagerIndicator extends ImageView implements IPagerIndicator {
    public ImageIPagerIndicator(Context context) {
        super(context);
    }

    public ImageIPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageIPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ImageIPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }
    @Override
    public void onPageSelected(int i) {

    }
    @Override
    public void onPageScrollStateChanged(int i) {

    }
    @Override
    public void onPositionDataProvide(List<PositionData> list) {

    }
}

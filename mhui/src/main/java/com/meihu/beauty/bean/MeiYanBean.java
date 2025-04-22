package com.meihu.beauty.bean;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beautylibrary.bean.MHCommonBean;

/**
 * The type Mei yan bean.
 */
public class MeiYanBean extends MHCommonBean {
    private int mName;
    private int mThumb0;
    private int mThumb1;
    private boolean mChecked;
    private Drawable mDrawable0;
    private Drawable mDrawable1;

    /**
     * Instantiates a new Mei yan bean.
     *
     * @param name   the name
     * @param thumb0 the thumb 0
     * @param thumb1 the thumb 1
     * @param key    the key
     */
    public MeiYanBean(int name, int thumb0, int thumb1,String key) {
        mName = name;
        mThumb0 = thumb0;
        mThumb1 = thumb1;
        mKey = key;
    }


    /**
     * Instantiates a new Mei yan bean.
     *
     * @param name    the name
     * @param thumb0  the thumb 0
     * @param thumb1  the thumb 1
     * @param checked the checked
     * @param key     the key
     */
    public MeiYanBean(int name, int thumb0, int thumb1, boolean checked,String key) {
        this(name, thumb0, thumb1,key);
        mChecked = checked;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public int getName() {
        return mName;
    }

    /**
     * Gets thumb 0.
     *
     * @return the thumb 0
     */
    public int getThumb0() {
        return mThumb0;
    }

    /**
     * Is checked boolean.
     *
     * @return the boolean
     */
    public boolean isChecked() {
        return mChecked;
    }

    /**
     * Sets checked.
     *
     * @param checked the checked
     */
    public void setChecked(boolean checked) {
        mChecked = checked;
    }

    /**
     * Gets drawable 0.
     *
     * @return the drawable 0
     */
    public Drawable getDrawable0() {
        if (mDrawable0 == null) {
            mDrawable0 = ContextCompat.getDrawable(MhDataManager.getInstance().getContext(), mThumb0);
        }
        return mDrawable0;
    }


    /**
     * Gets drawable 1.
     *
     * @return the drawable 1
     */
    public Drawable getDrawable1() {
        if (mDrawable1 == null) {
            mDrawable1 = ContextCompat.getDrawable(MhDataManager.getInstance().getContext(), mThumb1);
        }
        return mDrawable1;
    }

    public String getKey() {
        return mKey;
    }
}

package com.meihu.beauty.bean;

import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beautylibrary.bean.MHCommonBean;

/**
 * The type Te xiao action bean.
 */
public class TeXiaoActionBean extends MHCommonBean {

    private int mName;
    private int mThumb0;
    private int mThumb1;
    private boolean mChecked;
    private Drawable mDrawable0;
    private Drawable mDrawable1;
    private String mUrl;
    private int mAction;
    private String stickerName;
    private String mResouce;

    /**
     * Instantiates a new Te xiao action bean.
     *
     * @param name   the name
     * @param thumb0 the thumb 0
     * @param thumb1 the thumb 1
     * @param url    the url
     * @param action the action
     * @param key    the key
     */
    public TeXiaoActionBean(int name, int thumb0, int thumb1,String url,int action,String key) {
        mName = name;
        mThumb0 = thumb0;
        mThumb1 = thumb1;
        mUrl = url;
        mAction = action;
        mKey = key;
    }

    /**
     * Instantiates a new Te xiao action bean.
     *
     * @param name    the name
     * @param thumb0  the thumb 0
     * @param thumb1  the thumb 1
     * @param checked the checked
     * @param key     the key
     */
    public TeXiaoActionBean(int name, int thumb0, int thumb1, boolean checked,String  key) {
        this(name, thumb0, thumb1,"",0,key);
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


    /**
     * Gets resouce.
     *
     * @return the resouce
     */
    public String getResouce() {
        return mResouce;
    }

    /**
     * Sets resouce.
     *
     * @param resouce the resouce
     */
    public void setResouce(String resouce) {
        mResouce = resouce;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Gets action.
     *
     * @return the action
     */
    public int getAction() {
        return mAction;
    }

    /**
     * Sets sticker name.
     *
     * @param stickerName the sticker name
     */
    public void setStickerName(String stickerName) {
        this.stickerName = stickerName;
    }

    /**
     * Gets sticker name.
     *
     * @return the sticker name
     */
    public String getStickerName() {
        return stickerName;
    }
}

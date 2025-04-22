package com.meihu.beauty.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.meihu.beauty.utils.MhDataManager;

/**
 * The type Tie zhi bean.
 */
public class TieZhiBean {

    private String mName;
    private String mThumb;
    private String mResource;
    private boolean mChecked;
    private boolean mDownLoading;
    private Boolean mDownLoaded;
    private String  mKey;


    /**
     * Gets name.
     *
     * @return the name
     */
    @JSONField(name = "name")
    public String getName() {
        return mName;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    @JSONField(name = "name")
    public void setName(String name) {
        mName = name;
    }

    /**
     * Gets thumb.
     *
     * @return the thumb
     */
    @JSONField(name = "thumb")
    public String getThumb() {
        return mThumb;
    }

    /**
     * Sets thumb.
     *
     * @param thumb the thumb
     */
    @JSONField(name = "thumb")
    public void setThumb(String thumb) {
        mThumb = thumb;
    }

    /**
     * Gets resource.
     *
     * @return the resource
     */
    @JSONField(name = "resource")
    public String getResource() {
        return mResource;
    }

    /**
     * Sets resource.
     *
     * @param resource the resource
     */
    @JSONField(name = "resource")
    public void setResource(String resource) {
        mResource = resource;
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
     * Is down loading boolean.
     *
     * @return the boolean
     */
    public boolean isDownLoading() {
        return mDownLoading;
    }

    /**
     * Sets down loading.
     *
     * @param downLoading the down loading
     */
    public void setDownLoading(boolean downLoading) {
        mDownLoading = downLoading;
    }

    /**
     * Is down loaded boolean.
     *
     * @return the boolean
     */
    public boolean isDownLoaded() {
        return mDownLoaded;
    }

    /**
     * Sets down loaded.
     *
     * @param downLoaded the down loaded
     */
    public void setDownLoaded(boolean downLoaded) {
        mDownLoaded = downLoaded;
    }

    /**
     * Check downloaded.
     */
    public void checkDownloaded() {
        mDownLoaded = TextUtils.isEmpty(mName) ? true : MhDataManager.isTieZhiDownloaded(mName);
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() {
        return mKey;
    }

    /**
     * Sets key.
     *
     * @param key the key
     */
    public void setKey(String key) {
        mKey = key;
    }
}

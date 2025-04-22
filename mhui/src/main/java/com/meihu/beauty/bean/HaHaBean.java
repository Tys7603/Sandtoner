package com.meihu.beauty.bean;

import com.meihu.beautylibrary.bean.MHCommonBean;

/**
 * The type Ha ha bean.
 */
public class HaHaBean extends MHCommonBean {
    private int mId;
    private int mName;
    private int mThumb;
    private boolean mChecked;

    /**
     * Instantiates a new Ha ha bean.
     *
     * @param id    the id
     * @param name  the name
     * @param thumb the thumb
     * @param key   the key
     */
    public HaHaBean(int id, int name, int thumb,String key) {
        mId = id;
        mName = name;
        mThumb = thumb;
        mKey  = key;
    }

    /**
     * Instantiates a new Ha ha bean.
     *
     * @param id      the id
     * @param name    the name
     * @param thumb   the thumb
     * @param checked the checked
     * @param key     the key
     */
    public HaHaBean(int id, int name, int thumb, boolean checked,String key) {
        this(id, name, thumb,key);
        mChecked = checked;
    }


    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return mId;
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
     * Gets thumb.
     *
     * @return the thumb
     */
    public int getThumb() {
        return mThumb;
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
}

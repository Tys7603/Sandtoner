package com.meihu.beauty.views;

import android.content.Context;
import android.view.ViewGroup;

/**
 * on 2018/10/26.
 */
public abstract class AbsCommonViewHolder extends AbsViewHolder {

    /**
     * The M first load data.
     */
    protected boolean mFirstLoadData = true;
    private boolean mShowed;

    /**
     * Instantiates a new Abs common view holder.
     *
     * @param context    the context
     * @param parentView the parent view
     */
    public AbsCommonViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    /**
     * Instantiates a new Abs common view holder.
     *
     * @param context    the context
     * @param parentView the parent view
     * @param args       the args
     */
    public AbsCommonViewHolder(Context context, ViewGroup parentView, Object... args) {
        super(context, parentView, args);
    }

    /**
     * Load data.
     */
    public void loadData() {
    }

    /**
     * Is first load data boolean.
     *
     * @return the boolean
     */
    protected boolean isFirstLoadData() {
        if (mFirstLoadData) {
            mFirstLoadData = false;
            return true;
        }
        return false;
    }


    /**
     * Sets showed.
     *
     * @param showed the showed
     */
    public void setShowed(boolean showed) {
        mShowed = showed;
    }

    /**
     * Is showed boolean.
     *
     * @return the boolean
     */
    public boolean isShowed() {
        return mShowed;
    }
}

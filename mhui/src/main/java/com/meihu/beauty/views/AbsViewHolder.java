package com.meihu.beauty.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.meihu.beauty.utils.ClickUtil;

/**
 * on 2018/9/22.
 */
public abstract class AbsViewHolder   {

    /**
     * The M tag.
     */
    protected String mTag;
    /**
     * The M context.
     */
    protected Context mContext;
    /**
     * The M parent view.
     */
    protected ViewGroup mParentView;
    /**
     * The M content view.
     */
    protected View mContentView;


    /**
     * Instantiates a new Abs view holder.
     *
     * @param context    the context
     * @param parentView the parent view
     */
    public AbsViewHolder(Context context, ViewGroup parentView) {
        mTag = getClass().getSimpleName();
        mContext = context;
        mParentView = parentView;
        mContentView = LayoutInflater.from(context).inflate(getLayoutId(), mParentView, false);

        init();
    }

    /**
     * Instantiates a new Abs view holder.
     *
     * @param context    the context
     * @param parentView the parent view
     * @param args       the args
     */
    public AbsViewHolder(Context context, ViewGroup parentView, Object... args) {
        mTag = getClass().getSimpleName();
        processArguments(args);
        mContext = context;
        mParentView = parentView;
        mContentView = LayoutInflater.from(context).inflate(getLayoutId(), mParentView, false);

        init();
    }


    /**
     * Process arguments.
     *
     * @param args the args
     */
    protected void processArguments(Object... args) {

    }

    /**
     * Gets layout id.
     *
     * @return the layout id
     */
    protected abstract int getLayoutId();

    /**
     * Init.
     */
    public abstract void init();

    /**
     * Find view by id t.
     *
     * @param <T> the type parameter
     * @param res the res
     * @return the t
     */
    protected <T extends View> T findViewById(int res) {
        return mContentView.findViewById(res);
    }

    /**
     * Gets content view.
     *
     * @return the content view
     */
    public View getContentView() {
        return mContentView;
    }

    /**
     * Can click boolean.
     *
     * @return the boolean
     */
    protected boolean canClick() {
        return ClickUtil.canClick();
    }

    /**
     * Add to parent.
     */
    public void addToParent() {
        if (mParentView != null && mContentView != null) {
            mParentView.addView(mContentView);
        }
    }

    /**
     * Remove from parent.
     */
    public void removeFromParent() {
        ViewParent parent = mContentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(mContentView);
        }
    }




}

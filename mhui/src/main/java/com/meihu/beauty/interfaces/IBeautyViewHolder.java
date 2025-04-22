package com.meihu.beauty.interfaces;

/**
 * The interface Beauty view holder.
 */
public interface IBeautyViewHolder {

    /**
     * Show.
     */
    void show();

    /**
     * Hide.
     */
    void hide();

    /**
     * Is showed boolean.
     *
     * @return the boolean
     */
    boolean isShowed();

    /**
     * Sets visible listener.
     *
     * @param visibleListener the visible listener
     */
    void setVisibleListener(VisibleListener visibleListener);


    /**
     * The interface Visible listener.
     */
    interface VisibleListener {
        /**
         * On visible changed.
         *
         * @param visible the visible
         */
        void onVisibleChanged(boolean visible);
    }

}

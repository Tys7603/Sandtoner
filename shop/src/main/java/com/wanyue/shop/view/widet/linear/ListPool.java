package com.wanyue.shop.view.widet.linear;

import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;

/**
 * The type List pool.
 */
public class ListPool {
    private ArrayMap<View, BaseReclyViewHolder>mHolderMap;

    /**
     * Instantiates a new List pool.
     */
    public ListPool() {
       mHolderMap = new ArrayMap<>();
    }

    /**
     * Put.
     *
     * @param view           the view
     * @param baseViewHolder the base view holder
     */
    public void put(View view,BaseReclyViewHolder baseViewHolder){
        if(mHolderMap==null){
           mHolderMap = new ArrayMap<>();
        }
        removeToParent(view);
        mHolderMap.put(view,baseViewHolder);
    }

    /**
     * Remove to parent.
     *
     * @param view the view
     */
    public void removeToParent(View view){
        if(view!=null&&view.getParent()!=null){
          ViewGroup viewGroup= (ViewGroup) view.getParent();
          viewGroup.removeView(view);
        }
    }

    /**
     * Gets cache holder.
     *
     * @return the cache holder
     */
    public BaseReclyViewHolder getCacheHolder() {
        if(mHolderMap==null||mHolderMap.size()<=0){
           return null;
        }
        BaseReclyViewHolder baseViewHolder=mHolderMap.get(0);
       return mHolderMap.remove(baseViewHolder);
    }

}

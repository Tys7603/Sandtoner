package com.wanyue.common.business.list;

import android.util.ArrayMap;
import android.view.View;
import com.wanyue.common.utils.ObjectUtil;
import java.util.ArrayList;
import java.util.List;

public class ListHelper<T extends View,K extends ListBean> { //双向索引
    private ArrayMap<String,List<T>>mArrayMap;
    private ArrayMap<T,K>mViewMap;
    private OnDataChangeListner<T,K>mChangeListner;

    public ListHelper() {

    }
    public void attach(T view,K listBean){
        if(mChangeListner==null){
            return;
        }

        if(mArrayMap==null){
           mArrayMap=new ArrayMap<>();
        }
        if(mViewMap==null){
           mViewMap=new ArrayMap<>();
        }

        K cacheItem=mViewMap.get(view);
        String id=mChangeListner.getId(listBean);

        if(ObjectUtil.equalsObject(cacheItem,listBean)){ //如果是绑定到相同的id返回不处理
           return;
        }

        boolean isHaveCacheId=cacheItem!=null;
        mViewMap.put(view,listBean);
        if(isHaveCacheId){
           String cacheId=mChangeListner.getId(cacheItem);
           List<T>list=mArrayMap.get(cacheId);
           if(list==null){
               return;
           }
            list.remove(view);
        }
        List<T> viewList=mArrayMap.get(id);
        if(viewList==null){
           viewList=new ArrayList<>();
           mArrayMap.put(id,viewList);
        }
        if(!viewList.contains(view)){
            viewList.add(view);
        }
    }
    public void notifyAllChange(String id){
        if(id==null||mArrayMap==null||mChangeListner==null||mArrayMap==null){
           return;
        }
        List<T>viewList=mArrayMap.get(id);
        for(T t:viewList){
           K k= mViewMap.get(t);
            mChangeListner.toChange(t,k);
        }
    }

    public void toChange(T t,K k){
        if(mChangeListner!=null){
           mChangeListner.toChange(t,k);
        }
    }

    public void setmChangeListner(OnDataChangeListner<T, K> mChangeListner) {
        this.mChangeListner = mChangeListner;
    }

    public interface OnDataChangeListner<T,K>{
        public void toChange(T t,K k);
        public String getId(K k);
    }

}

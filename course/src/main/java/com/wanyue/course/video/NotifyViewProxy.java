package com.wanyue.course.video;

import com.wanyue.common.proxy.RxViewProxy;

public abstract class NotifyViewProxy<T> extends RxViewProxy {
    protected NotifyListner mNotifyListner;

    public void setNotifyListner(NotifyListner notifyListner) {
        mNotifyListner = notifyListner;
    }

    public abstract void setData(T data);

    public interface NotifyListner{
        public void notifyData();
    }
}
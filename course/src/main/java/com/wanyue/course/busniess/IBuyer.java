package com.wanyue.course.busniess;

import com.wanyue.course.bean.ProjectBean;

import java.util.List;

public interface IBuyer {
    public  void buy(List<? extends ProjectBean> projectBean, Listner listner);
    public  void clear();
    public interface Listner{
        public static final int UNKOWN=-1;
        public static final int PAY_FAILED=-2;
        public static final int ERROR_BACK=970;

        public void  onSuccess();
        public void onCancle();
        public void onError(int code);
    }
}
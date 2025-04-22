package com.wanyue.course.view.proxy.insbottom;


import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.course.bean.ProjectBean;
import com.wanyue.course.busniess.IBuyer;

public abstract class AbsBottomInsViewProxy extends RxViewProxy {
  protected ProjectBean mProjectBean;
  protected IBuyer.Listner mListner;

  public  void setProject(ProjectBean project){
    mProjectBean=project;
    notifyDataChanged();
  }

  public void  setSuccessListner(IBuyer.Listner listner){
    mListner=listner;
  }



  public void commit(){

  }


}


package com.wanyue.course.view.proxy.insbottom;

import android.view.ViewGroup;
import com.wanyue.common.proxy.BaseProxyMannger;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.course.bean.ProjectBean;
import com.wanyue.course.busniess.IBuyer;

public class BottomViewHelper {

  //0不显示 1查看详情 2输入密码 3购买 4进入直播 5会员专享
 private static final int STATE_NO_SHOW=0;
 private static final int STATE_CHECK_DETAIL=1;
 private static final int STATE_INPUT_PASSWORD=2;
 private static final int STATE_BUYED=3;
 private static final int STATE_IN_LIVE=4;
 private static final int STATE_VIP=5;
 private static final int STATE_PLAYBACK=6;

   private ProjectBean mProjectBean;
   private ViewGroup mViewGroup;
   private BaseProxyMannger mBaseProxyMannger;
   private AbsBottomInsViewProxy mBottomInsViewProxy;
   private IBuyer.Listner mListner;

    public BottomViewHelper(ViewGroup viewGroup, BaseProxyMannger mannger) {
        mViewGroup = viewGroup;
        mBaseProxyMannger=mannger;
    }

    public boolean setProjectBean(ProjectBean projectBean) {
        mProjectBean = projectBean;
        return   changeState();
    }

    public void setListner(IBuyer.Listner listner) {
        mListner = listner;
    }
    private boolean changeState() {
        boolean isAllow=false;
        if(mProjectBean==null){
          return isAllow;
        }
        removeBottomView();
        boolean isBuy=mProjectBean.ifBuy();
        isAllow=isBuy;

        if(!isAllow){
           mBottomInsViewProxy=new BuyBottomVIewProxy();
        }
        if(mBottomInsViewProxy==null){
           ViewUtil.setVisibility(mViewGroup,ViewGroup.GONE);
        }else{
           ViewUtil.setVisibility(mViewGroup,ViewGroup.VISIBLE);
           mBottomInsViewProxy.setSuccessListner(mListner);
           mBaseProxyMannger.addViewProxy(mViewGroup,mBottomInsViewProxy,mBottomInsViewProxy.getDefaultTag());
           mBottomInsViewProxy.setProject(mProjectBean);
        }
        return isAllow;
    }
    private void removeBottomView() {
        if(mBottomInsViewProxy!=null&&mBottomInsViewProxy.isAdd()){
            mBaseProxyMannger.removeViewProxy(mBottomInsViewProxy);
        }
        mBottomInsViewProxy=null;
    }

    public AbsBottomInsViewProxy getBottomInsViewProxy() {
        return mBottomInsViewProxy;
    }
}

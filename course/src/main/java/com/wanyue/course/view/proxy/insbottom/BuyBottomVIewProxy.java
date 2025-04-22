package com.wanyue.course.view.proxy.insbottom;

import android.view.View;
import android.view.ViewGroup;
import com.lxj.xpopup.XPopup;
import com.wanyue.course.R;
import com.wanyue.course.view.dialog.PayOrderPopView;


public class BuyBottomVIewProxy extends AbsBottomInsViewProxy {
     private PayOrderPopView mPayOrderPopView;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        findViewById(R.id.btn_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }


    @Override
    public int getLayoutId() {
        return R.layout.view_course_bottom_buy;
    }

    @Override
    public void commit() {
        super.commit();
        if(mProjectBean==null){
            return;
        }
        if(mPayOrderPopView!=null&&mPayOrderPopView.isShow()){
            return;
        }
        mPayOrderPopView=new PayOrderPopView(getActivity());
        mPayOrderPopView.setId(mProjectBean.getId());
        mPayOrderPopView.setListner(mListner);
        new XPopup.Builder(getActivity())
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asCustom(mPayOrderPopView)
                .show();
    }
}

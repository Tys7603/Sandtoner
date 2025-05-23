package com.wanyue.video.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.dialog.AbsDialogFragment;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.utils.DpUtil;
import com.wanyue.common.utils.RouteUtil;
import com.wanyue.video.R;

/**
 *  on 2019/8/31.
 */

public class VideoGoodsDialogFragment extends AbsDialogFragment implements View.OnClickListener {
    private GoodsBean mGoodBean;

    private ImageView mImgThumb;
    private TextView  mTvTitle;
    private TextView mTvDes;

    private  TextView mTvpPrice;
    private  TextView mTvOriginPrice;
    private String mUid;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_video_goods;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = DpUtil.dp2px(220);
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_buy).setOnClickListener(this);
        mImgThumb=findViewById(R.id.thumb);
        mTvTitle=findViewById(R.id.title);
        mTvDes=findViewById(R.id.des);
        mTvpPrice=findViewById(R.id.price);
        mTvOriginPrice=findViewById(R.id.price_origin);
        layingData();
    }



    private void layingData() {
        if(mGoodBean!=null){
            ImgLoader.display(mContext,mGoodBean.getThumb(),mImgThumb);
            mTvTitle.setText(mGoodBean.getName());
            mTvDes.setText(mGoodBean.getDes());
            mTvpPrice.setText(mGoodBean.getPriceNow());
        }
    }

    public void setGoodBean(GoodsBean mGoodBean) {
        this.mGoodBean = mGoodBean;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_close){
            dismiss();
        }else if(id==R.id.btn_buy){
            buy();
        }
    }


    private void buy() {
        if(mGoodBean!=null){
          RouteUtil.forwardGoodsDetailNoCart(mGoodBean.getId(),mUid);
        }
    }



    public void setUid(String uid) {
        mUid = uid;
    }
}

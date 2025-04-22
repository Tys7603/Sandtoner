package com.meihu.beauty.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.meihu.beauty.R;
import com.meihu.beauty.bean.MeiYanMainBean;
import com.meihu.beautylibrary.MHSDK;
import com.meihu.beautylibrary.bean.MHCommonBean;
import com.meihu.beautylibrary.bean.MHConfigConstants;
import java.util.ArrayList;
import java.util.List;

public class MhMainViewHolder extends AbsMhChildViewHolder implements View.OnClickListener {

    private View mCenterContainer;
    private ImageView mRecord;

    private View mHide;
    private View mTieZhi;
    private View mMeiYan;
    private View mMakeup;
    private View mTeXiao;
    private View mHaHa;
    private ViewGroup mContainer;

    public MhMainViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    public void showSeekBar() {

    }

    @Override
    public void hideSeekBar() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_beauty_mh_main;
    }

    @Override
    public void init() {

        mContainer = findViewById(R.id.container);

        mTieZhi = LayoutInflater.from(mContext).inflate(R.layout.view_beauty_mh_main_tie_zhi,null);
        mMeiYan = LayoutInflater.from(mContext).inflate(R.layout.view_beauty_mh_main_mei_yan,null);
        mCenterContainer = LayoutInflater.from(mContext).inflate(R.layout.view_beauty_mh_main_record,null);
        mMakeup = LayoutInflater.from(mContext).inflate(R.layout.view_beauty_mh_main_mei_zhuang,null);
        mTeXiao = LayoutInflater.from(mContext).inflate(R.layout.view_beauty_mh_main_te_xiao,null);
        mHaHa   = LayoutInflater.from(mContext).inflate(R.layout.view_beauty_mh_main_ha_ha_jing,null);

        LinearLayout.LayoutParams childParam = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        childParam.weight = 1.0f;

        LinearLayout.LayoutParams recordParam = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        recordParam.weight = 1.3f;

        List<MHCommonBean> list = new ArrayList<>();
        list.add(new MeiYanMainBean(mTieZhi,childParam, MHConfigConstants.TIE_ZHI));
        list.add(new MeiYanMainBean(mMeiYan, childParam,MHConfigConstants.MEI_YAN));
        list.add(new MeiYanMainBean(mMakeup,childParam, MHConfigConstants.MEI_ZHUANG));
        list.add(new MeiYanMainBean(mTeXiao,childParam, MHConfigConstants.TE_XIAO));
        list.add(new MeiYanMainBean(mHaHa, childParam,MHConfigConstants.HA_HA_JING));

        list =  MHSDK.getCategories(list,MHConfigConstants.TAKE_PHOTO);

        for (int i = 0; i < list.size(); i++) {
            MeiYanMainBean item = (MeiYanMainBean)list.get(i);
            mContainer.addView(item.getView(),item.getLayoutParams());
        }

        mHide = findViewById(R.id.btn_hide);

        mCenterContainer = findViewById(R.id.center_container);
        mRecord = findViewById(R.id.record);

        mTieZhi = findViewById(R.id.btn_tie_zhi);
        mMeiYan = findViewById(R.id.btn_mei_yan);
        mMakeup = findViewById(R.id.btn_makeup);
        mTeXiao = findViewById(R.id.btn_te_xiao);
        mHaHa   = findViewById(R.id.btn_haha);

        if (mHide != null) {
            mHide.setOnClickListener(this);
        }
        if (mTieZhi != null) {
            mTieZhi.setOnClickListener(this);
        }
        if (mMeiYan != null) {
            mMeiYan.setOnClickListener(this);
        }
        if (mMakeup != null){
            mMakeup.setOnClickListener(this);
        }
        if (mTeXiao != null){
            mTeXiao.setOnClickListener(this);
        }
        if (mHaHa != null){
            mHaHa.setOnClickListener(this);
            mHaHa.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View v) {
        if (mIBeautyClickListener == null) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_hide) {

            mIBeautyClickListener.hideView();
        } else if (i == R.id.btn_tie_zhi) {
            mIBeautyClickListener.tabTieZhi();
        } else if (i == R.id.btn_mei_yan) {
            mIBeautyClickListener.tabMeiYan();
        } else if (i == R.id.btn_te_xiao) {
            mIBeautyClickListener.tabTeXiao();
        } else if (i == R.id.btn_haha) {
            mIBeautyClickListener.tabHaHa();
        } else if (i == R.id.btn_makeup) {
            mIBeautyClickListener.tabMakeup();
        }
    }


    public void showViewContainer(boolean isShow){
        if (isShow){

        }else{

        }
    }

    public View getCenterViewContainer(){
        return mCenterContainer;
    }


    public ImageView getRecordView(){
        return mRecord;
    }


}

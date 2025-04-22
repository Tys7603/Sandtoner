package com.wanyue.main.view.proxy;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.main.R;

public class SpreadManRankHeadVIewProxy extends RxViewProxy {
    private ImageView mImgWrap2;
    private RoundedImageView mImgAvatar2;
    private TextView mTvName2;
    private TextView mTvUserNum2;
    private ImageView mImgWrap1;
    private RoundedImageView mImgAvatar1;
    private TextView mTvName1;
    private TextView mTvUserNum1;
    private ImageView mImgWrap3;
    private RoundedImageView mImgAvatar3;
    private TextView mTvName3;
    private TextView mTvUserNum3;

    @Override
    public int getLayoutId() {
        return R.layout.view_spread_man_rank_head;
    }

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mImgWrap2 = (ImageView) findViewById(R.id.img_wrap_2);
        mImgAvatar2 = (RoundedImageView) findViewById(R.id.img_avatar_2);
        mTvName2 = (TextView) findViewById(R.id.tv_name_2);
        mTvUserNum2 = (TextView) findViewById(R.id.tv_user_num_2);
        mImgWrap1 = (ImageView) findViewById(R.id.img_wrap_1);
        mImgAvatar1 = (RoundedImageView) findViewById(R.id.img_avatar_1);
        mTvName1 = (TextView) findViewById(R.id.tv_name_1);
        mTvUserNum1 = (TextView) findViewById(R.id.tv_user_num_1);
        mImgWrap3 = (ImageView) findViewById(R.id.img_wrap_3);
        mImgAvatar3 = (RoundedImageView) findViewById(R.id.img_avatar_3);
        mTvName3 = (TextView) findViewById(R.id.tv_name_3);
        mTvUserNum3 = (TextView) findViewById(R.id.tv_user_num_3);
    }





}

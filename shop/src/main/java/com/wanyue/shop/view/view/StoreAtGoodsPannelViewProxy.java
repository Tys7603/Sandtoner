package com.wanyue.shop.view.view;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.common.utils.SpannableStringUtils;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.R2;
import com.wanyue.shop.bean.StoreBean;
import com.wanyue.shop.store.view.activity.StoreActivity;
import butterknife.OnClick;

public class StoreAtGoodsPannelViewProxy extends RxViewProxy {

    private ViewGroup mBtnStore;
    private ImageView mImgStoreAvatar;
    private TextView mTvStoreName;
    private ViewGroup mBtnEvaluate;
    private TextView mTvEvaluate1;
    private TextView mTvEvaluate2;
    private TextView mTvEvaluate3;
    private StoreBean mStoreData;

    private TextView mTvStoreSelf;


    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mBtnStore = (ViewGroup) findViewById(R.id.btn_store);
        mImgStoreAvatar = (ImageView) findViewById(R.id.img_store_avatar);
        mTvStoreName = findViewById(R.id.tv_store_name);
        mBtnEvaluate = (ViewGroup) findViewById(R.id.btn_evaluate);
        mTvEvaluate1 = (TextView) findViewById(R.id.tv_evaluate_1);
        mTvEvaluate2 = (TextView) findViewById(R.id.tv_evaluate_2);
        mTvEvaluate3 = (TextView) findViewById(R.id.tv_evaluate_3);
        mTvStoreSelf = (TextView) findViewById(R.id.tv_store_self);


        if(mStoreData!=null){
            mTvStoreName.setText(mStoreData.getName());
            ImgLoader.display(getActivity(),mStoreData.getAvatar(),mImgStoreAvatar);
            mTvEvaluate1.setText(getEvaluateTip("宝贝描述 ",mStoreData.getShopScore1()));
            mTvEvaluate2.setText(getEvaluateTip("商家服务 ",mStoreData.getShopScore2()));
            mTvEvaluate3.setText(getEvaluateTip("物流服务 ",mStoreData.getShopScore3()));
        }

        if(mStoreData.getShoptype()==2){
            ViewUtil.setVisibility(mTvStoreSelf, ViewGroup.VISIBLE);
        }else{
            ViewUtil.setVisibility(mTvStoreSelf, ViewGroup.GONE);
        }
    }


    private CharSequence getEvaluateTip(String left,String right) {
        int textcolor= !StringUtil.contains(right,"高")? ResourceUtil.getColor(R.color.textColor2):ResourceUtil.getColor(R.color.global);
       return SpannableStringUtils.getBuilder(left).append(right).setForegroundColor(textcolor).setBold().create();
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_store_at_goods;
    }

    @OnClick(R2.id.btn_store)
    public void toStore(){
        if(mStoreData==null){
            return;
        }
        StoreActivity.forward(getActivity(),mStoreData.getId());
    }

    public void setStoreData(StoreBean storeData) {
        mStoreData = storeData;
    }

    @Override
    protected boolean shouldBindButterKinfe() {
        return true;
    }
}

package com.wanyue.main.view.activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import androidx.viewpager2.widget.ViewPager2;
import com.alibaba.fastjson.JSONObject;
import com.to.aboomy.pager2banner.ScaleInTransformer;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.http.ParseArrayHttpCallBack;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.ImageUtil;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.main.R;
import com.wanyue.main.api.MainAPI;
import java.util.ArrayList;
import java.util.List;

public class SpreadPillActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager2 mViewPager2;

    private Button mBtnSave;
    private ImageAdapter mImageAdapter;

    private int mSelectPosition;

    @Override
    public void init() {
        setTabTitle(R.string.spread_pill_tip);
        mBtnSave =  findViewById(R.id.btn_save);
        mViewPager2 =  findViewById(R.id.viewPager);
        mImageAdapter=new ImageAdapter(null);
        mViewPager2.setAdapter(mImageAdapter);
        mViewPager2.setPageTransformer(new ScaleInTransformer());
        mBtnSave.setOnClickListener(this);
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mSelectPosition=position;
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_spread_pill;
    }
    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        MainAPI.getSpreadBanner(new ParseArrayHttpCallBack<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, List<JSONObject> info) {
                if(isSuccess(code)&& ListUtil.haveData(info)){
                    if(mBtnSave!=null){
                       mBtnSave.setAlpha(1);
                       mBtnSave.setEnabled(true);
                    }
                    List<String>bannerList=new ArrayList<>(info.size());
                    for(JSONObject jsonObject:info){
                       String url=jsonObject.getString("poster");
                       if(TextUtils.isEmpty(url)){
                           continue;
                       }
                        bannerList.add(url);
                    }
                    if(mImageAdapter!=null){
                       mImageAdapter.setData(bannerList);
                    }
                }
            }

            @Override
            public Dialog createLoadingDialog() {
                return DialogUitl.loadingDialog(SpreadPillActivity.this);
            }
            @Override
            public boolean showLoadingDialog() {
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainAPI.cancle(MainAPI.SPREAD_BANNER);
    }

    @Override
    public void onClick(View v) {
        if(!ClickUtil.canClick()){
            return;
        }
        save();
    }

    public class ImageAdapter extends BaseRecyclerAdapter<String, BaseReclyViewHolder> {
        private ArrayMap<Integer,View>mViewArrayMap;
        public ImageAdapter(List<String> data) {
            super(data);
        }
        @Override
        public int getLayoutId() {
            return R.layout.item_spread_pill_banner;
        }
        @Override
        protected void convert(BaseReclyViewHolder helper, String item) {
            if(mViewArrayMap==null){
               mViewArrayMap=new ArrayMap<>();
            }
            int position=helper.getLayoutPosition();
            mViewArrayMap.put(position,helper.getView(R.id.thumb));
            helper.setImageUrl(item,R.id.thumb);
        }

        public View getImageThumb(int position){
            if(mViewArrayMap==null){
                return null;
            }
            return mViewArrayMap.get(position);
        }
    }

    private void save() {
        if(mImageAdapter==null){
            return;
        }
        View view=mImageAdapter.getImageThumb(mSelectPosition);
        Bitmap bitmap= ImageUtil.convertViewToBitmap2(view);
        ImageUtil.saveAlbum(this,bitmap, System.currentTimeMillis()+"_pill.jpg");
        ToastUtil.show(com.wanyue.shop.R.string.save_succ);
    }
}

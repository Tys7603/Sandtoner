package com.wanyue.main.view.proxy;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.wanyue.common.activity.WebViewActivity;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.main.R;
import com.wanyue.main.bean.AdvBean;

/**
 * The type Adv view proxy.
 */
public class AdvViewProxy extends RxViewProxy implements View.OnClickListener {
    private AdvBean mData;
    private ImageView mThumb;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mThumb = (ImageView) findViewById(R.id.thumb);
        contentView.setOnClickListener(this);
        if(mData!=null){
           ImgLoader.display(getActivity(),mData.getThumb(),mThumb);
        }
    }
    @Override
    public int getLayoutId() {
        return R.layout.view_main_adv;
    }

    /**
     * Sets data.
     *
     * @param data the data
     */
    public void setData(AdvBean data) {
        mData = data;
        if(mThumb!=null){
           ImgLoader.display(getActivity(),data.getThumb(),mThumb);
        }
    }


    @Override
    public void onClick(View v) {
        String url=mData!=null?mData.getUrl():null;
        if(TextUtils.isEmpty(url)){
           return;
        }
        WebViewActivity.forward(getActivity(),url,false);
    }
}

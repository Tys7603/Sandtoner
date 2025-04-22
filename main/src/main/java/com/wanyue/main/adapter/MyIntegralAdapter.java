package com.wanyue.main.adapter;

import android.widget.TextView;
import androidx.annotation.NonNull;

import com.wanyue.common.CommonApplication;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.main.R;
import com.wanyue.main.bean.IntegralRecordBean;

import java.util.List;

public class MyIntegralAdapter extends BaseRecyclerAdapter<IntegralRecordBean, BaseReclyViewHolder> {
    public MyIntegralAdapter(List<IntegralRecordBean> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_recly_commission_detail_normal;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, IntegralRecordBean item) {
        helper.setText(R.id.tv_name,item.getTitle());
        helper.setText(R.id.tv_add_time,item.getAdd_time());
        TextView tvResult=helper.getView(R.id.tv_result);

        if(item.getType()==1){
            tvResult.setTextColor(ResourceUtil.getColor(R.color.green));
        }else{
            tvResult.setTextColor(ResourceUtil.getColor(R.color.global));
        }
        tvResult.setText(item.getResult());


        /*helper.setText(R.id.tv_name,item.getTitle());
        helper.setText(R.id.tv_add_time,item.getAdd_time());
        TextView tvResult=helper.getView(R.id.tv_result);

        if(commissionBean.getPm()==0){
            tvResult.setTextColor(ResourceUtil.getColor(CommonApplication.sInstance,R.color.global));
        }else{
            tvResult.setTextColor(ResourceUtil.getColor(CommonApplication.sInstance, R.color.green));
        }*/
    }
}

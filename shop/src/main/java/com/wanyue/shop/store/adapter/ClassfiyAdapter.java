package com.wanyue.shop.store.adapter;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.shop.R;
import com.wanyue.shop.store.bean.ClassifyBean;
import com.wanyue.shop.store.bean.ClassifySectionBean;
import java.util.List;

public class ClassfiyAdapter extends BaseSectionQuickAdapter<ClassifySectionBean, BaseReclyViewHolder> {

    public ClassfiyAdapter(int layoutResId, int sectionHeadResId, List<ClassifySectionBean> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseReclyViewHolder helper, ClassifySectionBean item) {
        helper.setText(R.id.tv_title,item.header);
    }

    @Override
    protected void convert(BaseReclyViewHolder helper, ClassifySectionBean item) {
        ClassifyBean classifyBean=item.t;
        if(classifyBean!=null){
            helper.setText(R.id.tv_title,classifyBean.getName());
        }
    }

    public void setData(List<ClassifySectionBean> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public int transformRealPosition(int position){
        return -1;
    }

    public int size(){
        return mData==null?0:mData.size();
    }
}

package com.wanyue.shop.store.view.proxy;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.server.observer.DefaultObserver;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.bean.GoodsSearchArgs;
import com.wanyue.shop.store.adapter.ClassfiyAdapter;
import com.wanyue.shop.store.api.StoreAPI;
import com.wanyue.shop.store.bean.ClassifyBean;
import com.wanyue.shop.store.bean.ClassifySectionBean;
import com.wanyue.shop.store.view.activity.SearchStoreActivity;
import java.util.ArrayList;
import java.util.List;

public class StoreClassfiyViewProxy extends BaseStoreChildVIewProxy {

    private RecyclerView mReclyView;
    private ClassfiyAdapter mClassfiyAdapter;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mReclyView = (RecyclerView) findViewById(R.id.reclyView);
        mClassfiyAdapter=new ClassfiyAdapter(R.layout.item_recly_section_store_classify_normal,R.layout.item_recly_section_store_classify_head,null);
        RxRefreshView.ReclyViewSetting.createGridSetting(getActivity(),2).settingRecyclerView(mReclyView);
        mReclyView.setAdapter(mClassfiyAdapter);
        mClassfiyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(mClassfiyAdapter==null){
                    return;
                }
                ClassifySectionBean sectionBean=mClassfiyAdapter.getItem(position);
                if(!sectionBean.isHeader&&sectionBean.t!=null){
                    ClassifyBean classifyBean=  sectionBean.t;
                    classifyBean.setStoreId(mStoreId);
                    SearchStoreActivity.forward(getActivity(),classifyBean);
                }
            }
        });
        getData();
    }

    private void getData() {
        StoreAPI.getCategory(mStoreId,mStoreId).compose(this.<List<ClassifyBean>>bindToLifecycle())
        .subscribe(new DefaultObserver<List<ClassifyBean>>() {
            @Override
            public void onNext(@NonNull List<ClassifyBean> info) {
                List<ClassifySectionBean>sectionBeanList=new ArrayList<>();
                int size=info.size();
                int index=0;
                for(int i=0;i<size;i++){
                    ClassifyBean classifyBean=info.get(i);
                    classifyBean.setIndex(index);
                    ClassifySectionBean classifySectionBean=new ClassifySectionBean(true,classifyBean.getName());
                    classifySectionBean.setIndex(i);
                    index++;
                    sectionBeanList.add(classifySectionBean);
                    List<ClassifyBean>childArray=classifyBean.getChildren();
                    if(!ListUtil.haveData(childArray)){
                        return;
                    }
                    for(ClassifyBean classifyBeanChild :childArray){
                        classifyBeanChild.setIndex(index);
                        index++;
                        classifySectionBean=new ClassifySectionBean(classifyBeanChild);
                        sectionBeanList.add(classifySectionBean);
                    }

                }
                if(mClassfiyAdapter!=null){
                   mClassfiyAdapter.setData(sectionBeanList);
                }
            }
        });
    }


    @Override
    public int getLayoutId() {
        return R.layout.view_store_classfiy;
    }

}

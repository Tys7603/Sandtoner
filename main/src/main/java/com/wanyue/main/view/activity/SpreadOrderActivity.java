package com.wanyue.main.view.activity;

import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.utils.JsonUtil;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.main.R;
import com.wanyue.main.adapter.SpreadOrderAdapter;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.SpreadOrderBean;
import com.wanyue.main.bean.SpreadOrderSectionBean;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class SpreadOrderActivity extends BaseActivity {
    private TextView mTvNumTip;
    private RxRefreshView<MultiItemEntity> mRefreshView;
    private SpreadOrderAdapter mSpreadOrderAdapter;
    @Override
    public void init() {
        setTabTitle(R.string.spread_tip4);
        mTvNumTip =  findViewById(R.id.tv_num_tip);
        mRefreshView =  findViewById(R.id.refreshView);
        mSpreadOrderAdapter=new SpreadOrderAdapter(null);
        mRefreshView.setAdapter(mSpreadOrderAdapter);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,0));
        mRefreshView.setDataListner(new RxRefreshView.DataListner<MultiItemEntity>() {
            @Override
            public Observable<List<MultiItemEntity>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<MultiItemEntity> data) {
            }
            @Override
            public void error(Throwable e) {

            }
        });
    }
    private Observable<List<MultiItemEntity>> getData(int p) {
       return MainAPI.getSpreadOrderList(p).map(new Function<JSONObject, List<MultiItemEntity>>() {
           @Override
           public List<MultiItemEntity> apply(JSONObject jsonObject) throws Exception {
               int count=jsonObject.getIntValue("count");
               if(mTvNumTip!=null){
                  mTvNumTip.setText(Integer.toString(count));
               }
               JSONArray jsonArray=jsonObject.getJSONArray("list");
               List<SpreadOrderSectionBean> sectionBeanList= JsonUtil.getData(jsonArray,SpreadOrderSectionBean.class);
               List<MultiItemEntity>list=new ArrayList<>();
               if(ListUtil.haveData(sectionBeanList)){
                   for(SpreadOrderSectionBean sectionBean:sectionBeanList){
                       list.add(sectionBean);
                       List<SpreadOrderBean>data=sectionBean.getChild();
                       if(ListUtil.haveData(data)){
                           list.addAll(data);
                       }
                   }
               }
               return list;
           }
       }).compose(this.<List<MultiItemEntity>>bindUntilOnDestoryEvent()) ;
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        if(mRefreshView!=null){
           mRefreshView.initData();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_spread_order;
    }
}

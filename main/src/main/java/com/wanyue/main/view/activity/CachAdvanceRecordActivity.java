package com.wanyue.main.view.activity;

import android.os.Bundle;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.main.R;
import com.wanyue.main.adapter.CommissionDetailAdapter;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.CommissionBean;
import com.wanyue.main.bean.CommissionSectionBean;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class CachAdvanceRecordActivity extends BaseActivity {

    private RxRefreshView mRefreshView;
    private CommissionDetailAdapter mDetailAdapter;

    @Override
    public void init() {
        mRefreshView = (RxRefreshView) findViewById(R.id.refreshView);
        setTabTitle(R.string.cash_record);
        mDetailAdapter=new CommissionDetailAdapter(null);
        mDetailAdapter.setMustUnit(true);
        mRefreshView.setAdapter(mDetailAdapter);
        mRefreshView.setIconId(R.drawable.icon_empty_no_tixian);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,1));
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

    @Override
    public int getLayoutId() {
        return R.layout.activity_cach_advance_record;
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        if(mRefreshView!=null){
            mRefreshView.initData();
        }
    }

    private Observable<List<MultiItemEntity>> getData(int p) {
        return MainAPI.getCommissionList(p,4).map(new Function<List<CommissionSectionBean>, List<MultiItemEntity>>() {
            @Override
            public List<MultiItemEntity> apply(List<CommissionSectionBean> commissionSectionBeans) throws Exception {
                List<MultiItemEntity>list=new ArrayList<>();
                if(ListUtil.haveData(commissionSectionBeans)){
                    for(CommissionSectionBean sectionBean:commissionSectionBeans){
                        List<CommissionBean>data=sectionBean.getList();
                        if(ListUtil.haveData(data)){
                            list.addAll(data);
                        }
                    }
                }
                return list;
            }
        }).compose(this.<List<MultiItemEntity>>bindUntilOnDestoryEvent());
    }
}

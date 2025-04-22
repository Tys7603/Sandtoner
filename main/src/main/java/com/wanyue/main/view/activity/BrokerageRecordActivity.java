package com.wanyue.main.view.activity;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.widget.TextView;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.http.ParseSingleHttpCallback;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.main.R;
import com.wanyue.main.adapter.CommissionDetailAdapter;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.CommissionBean;
import com.wanyue.main.bean.CommissionSectionBean;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.functions.Function;


public class BrokerageRecordActivity extends BaseActivity {
    private TextView mTvNumTip;
    private RxRefreshView<MultiItemEntity> mRefreshView;
    private CommissionDetailAdapter mDetailAdapter;
    @Override
    public void init() {
        setTabTitle(R.string.spread_tip3);
        mTvNumTip =findViewById(R.id.tv_num_tip);
        mRefreshView = findViewById(R.id.refreshView);

        mDetailAdapter=new CommissionDetailAdapter(null);
        mRefreshView.setAdapter(mDetailAdapter);
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

    private Observable<List<MultiItemEntity>> getData(int p) {
        return MainAPI.getCommissionList(p,3).map(new Function<List<CommissionSectionBean>, List<MultiItemEntity>>() {
            @Override
            public List<MultiItemEntity> apply(List<CommissionSectionBean> commissionSectionBeans) throws Exception {
                List<MultiItemEntity>list=new ArrayList<>();
                if(ListUtil.haveData(commissionSectionBeans)){
                  for(CommissionSectionBean sectionBean:commissionSectionBeans){
                      list.add(sectionBean);
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


    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        if(mRefreshView!=null){
           mRefreshView.initData();
        }
        getTotalCount();
    }

    private void getTotalCount() {
        MainAPI.getSpreadCount(3, new ParseSingleHttpCallback<String>("count") {
            @Override
            public void onSuccess(String data) {
                String totalPrice= StringUtil.getPrice(data);
                SpannableStringBuilder style = new SpannableStringBuilder(totalPrice);
                style.setSpan(new AbsoluteSizeSpan(19, true), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                mTvNumTip.setText(style);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainAPI.cancle(MainAPI.SPREAD_COUNT);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_brokerage_record;
    }
}

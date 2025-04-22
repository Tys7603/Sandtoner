package com.wanyue.main.view.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.main.R;
import com.wanyue.main.adapter.MyIntegralAdapter;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.IntegralRecordBean;
import java.util.List;
import io.reactivex.Observable;

public class MyIntegralActivity extends BaseActivity {

    private RxRefreshView<IntegralRecordBean> mRxRefreshView;
    private TextView mAll;
    private MyIntegralAdapter mMyIntegral2Adapter;


    @Override
    public void init() {
        mAll = (TextView) findViewById(R.id.all);
        mRxRefreshView =findViewById(R.id.refreshView);
        String integral=getIntent().getStringExtra(Constants.DATA);
        mAll.setText(StringUtil.getFormatFloat(integral+""));
        mMyIntegral2Adapter=new MyIntegralAdapter(null);
        mRxRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,0));
        mRxRefreshView.setAdapter(mMyIntegral2Adapter);
        mRxRefreshView.setEmptyLevel(1);
        mRxRefreshView.setNoDataTip("暂无积分明细");
        mRxRefreshView.setDataListner(new RxRefreshView.DataListner<IntegralRecordBean>() {
            @Override
            public Observable<List<IntegralRecordBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<IntegralRecordBean> data) {
            }
            @Override
            public void error(Throwable e) {
            }
        });
        mRxRefreshView.initData();
    }


    private Observable<List<IntegralRecordBean>> getData(int p) {
        return  MainAPI.getIntegralList(p).compose(this.<List<IntegralRecordBean>>bindToLifecycle());
    }

    public static void forward(Context context, String score){
        Intent intent=new Intent(context,MyIntegralActivity.class);
        intent.putExtra(Constants.DATA,score);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_integral;
    }
}
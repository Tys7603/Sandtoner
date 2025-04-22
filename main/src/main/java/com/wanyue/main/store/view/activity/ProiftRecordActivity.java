package com.wanyue.main.store.view.activity;

import android.content.Context;
import android.content.Intent;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.main.R;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.store.adapter.ProfitRecordAdapter;
import com.wanyue.main.store.bean.ProfitRecordBean;
import java.util.List;
import io.reactivex.Observable;

public class ProiftRecordActivity extends BaseActivity {
    private RxRefreshView<ProfitRecordBean> mRefreshView;
    private ProfitRecordAdapter mProfitRecordAdapter;
    private String mRoute;

    @Override
    public void init() {
        setTabTitle("提现记录");
        mProfitRecordAdapter=new ProfitRecordAdapter(null);
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setIconId(R.drawable.icon_empty_no_tixian);
        mRefreshView.setAdapter(mProfitRecordAdapter);
        mRefreshView.setReclyViewSetting(RxRefreshView.ReclyViewSetting.createLinearSetting(this,1));
        mRefreshView.setDataListner(new RxRefreshView.DataListner<ProfitRecordBean>() {
            @Override
            public Observable<List<ProfitRecordBean>> loadData(int p) {
                return getData(p);
            }
            @Override
            public void compelete(List<ProfitRecordBean> data) {

            }
            @Override
            public void error(Throwable e) {
            }
        });
        mRoute=getIntent().getStringExtra(Constants.URL);
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        if(mRefreshView!=null){
            mRefreshView.initData();
        }
    }

    public static void  forward(Context context, String routeUrl){
        Intent intent=new Intent(context,ProiftRecordActivity.class);
        intent.putExtra(Constants.URL,routeUrl);
        context.startActivity(intent);
    }

    private Observable<List<ProfitRecordBean>> getData(int p) {
        return MainAPI.getProfitRecord(mRoute,p);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_proift_record;
    }
}

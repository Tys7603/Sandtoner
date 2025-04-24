package com.wanyue.main.view.activity;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.custom.DrawableTextView;
import com.wanyue.common.custom.refresh.RxRefreshView;
import com.wanyue.common.http.ParseHttpCallback;
import com.wanyue.common.utils.ListUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.main.R;
import com.wanyue.main.adapter.MySpreadAdapter;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.bean.MySpreadBean;
import java.util.List;

public class MySpreadActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener {
    private static final int SPREAD_QRCODE=1; //
    private static final int SPREAD_CENSUS=2;
    private static final int BROKERAGE_RECORD=3;
    private static final int SPREAD_ORDER=4;
    private static final int SPREAD_RANK=5;
    private static final int ICON_BROKERAGE_RANK=6;

    private TextView mTvCurrentMoney;
    private DrawableTextView mBtnMyOrder;
    private TextView mTvYesterdayMoney;
    private TextView mTvTotalGetMoney;
    private RecyclerView mReclyView;
    private MySpreadAdapter mMySpreadAdapter;
    @Override
    public void init() {
        setTabTitle(WordUtil.getString(R.string.my_spread));
        mTvCurrentMoney = (TextView) findViewById(R.id.tv_current_money);
        mBtnMyOrder = (DrawableTextView) findViewById(R.id.btn_my_order);
        mTvYesterdayMoney = (TextView) findViewById(R.id.tv_yesterday_money);
        mTvTotalGetMoney = (TextView) findViewById(R.id.tv_total_get_money);
        mReclyView = (RecyclerView) findViewById(R.id.reclyView);

        mBtnMyOrder.setOnClickListener(this);
        findViewById(R.id.btn_get_money).setOnClickListener(this);
        initAdapter();
        RxRefreshView.ReclyViewSetting.createGridSetting(this,2,10).settingRecyclerView(mReclyView);
        LiveEventBus.get(CashAdvanceActivity.CASH_SUCCESS).observe(this, new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                requestData();
            }
        });
    }

    private void initAdapter() {
        MySpreadBean spreadBean1=new MySpreadBean(R.drawable.icon_spread_qrcode,getString(R.string.spread_tip1),SPREAD_QRCODE);
        MySpreadBean spreadBean2=new MySpreadBean(R.drawable.icon_spread_census,getString(R.string.spread_tip2),SPREAD_CENSUS);
        MySpreadBean spreadBean3=new MySpreadBean(R.drawable.icon_brokerage_record,getString(R.string.spread_tip3),BROKERAGE_RECORD);
        MySpreadBean spreadBean4=new MySpreadBean(R.drawable.icon_spread_order,getString(R.string.spread_tip4),SPREAD_ORDER);
        MySpreadBean spreadBean5=new MySpreadBean(R.drawable.icon_spread_rank,getString(R.string.spread_tip5),SPREAD_RANK);
        MySpreadBean spreadBean6=new MySpreadBean(R.drawable.icon_brokerage_rank,getString(R.string.spread_tip6),ICON_BROKERAGE_RANK);
        List<MySpreadBean>data=ListUtil.asList(spreadBean1,spreadBean2,spreadBean3,spreadBean4,spreadBean5,spreadBean6);
        mMySpreadAdapter=new MySpreadAdapter(data);
        mMySpreadAdapter.setOnItemClickListener(this);
        mReclyView.setAdapter(mMySpreadAdapter);
    }
    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        requestData();
    }

    private void requestData() {
        MainAPI.getCommission(new ParseHttpCallback<JSONObject>() {
            @Override
            public void onSuccess(int code, String msg, JSONObject info) {
                if(isSuccess(code)&&info!=null){
                    String lastDayCount=info.getString("lastDayCount");
                    String extractCount=info.getString("extractCount");
                    String commissionCount=info.getString("commissionCount");
                    if(mTvYesterdayMoney!=null){
                       mTvYesterdayMoney.setText(lastDayCount);
                    }
                    if(mTvTotalGetMoney!=null){
                        mTvTotalGetMoney.setText(extractCount);
                    }
                    if(mTvCurrentMoney!=null){
                       mTvCurrentMoney.setText(commissionCount);
                    }
                }
            }
            @Override
            public void onError(Throwable e) {
                if (e != null) {
                    ToastUtil.show(e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainAPI.cancle(MainAPI.COMMISSION);
    }

    @Override
    public int getLayoutId() {
     return R.layout.activity_my_spread;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if(mMySpreadAdapter==null){
            return;
        }
        MySpreadBean spreadBean=mMySpreadAdapter.getItem(position);
        int id=spreadBean.getId();
            switch (id){
                case SPREAD_QRCODE:
                    startActivity(SpreadPillActivity.class);
                    break;
                case SPREAD_CENSUS:
                    startActivity(SpreadCensusActivity.class);
                    break;
                case BROKERAGE_RECORD:
                    startActivity(BrokerageRecordActivity.class);
                    break;
                case SPREAD_ORDER:
                    startActivity(SpreadOrderActivity.class);
                    break;
                case SPREAD_RANK:
                    startActivity(SpreadManRankActivity.class);
                    break;
                case ICON_BROKERAGE_RANK:
                    startActivity(BrokerageRankActivity.class);
                    break;
                default:
                    break;
            }
    }


    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id==R.id.btn_get_money){
            toGetMoney();
        }else if(id==R.id.btn_my_order){
            toGetMoneyRecord();
        }
    }

    private void toGetMoneyRecord() {
        startActivity(CachAdvanceRecordActivity.class);
    }

    private void toGetMoney() {
        startActivity(CashAdvanceActivity.class);
    }
}

package com.wanyue.main.store.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.utils.CommonIconUtil;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.RouteUtil;
import com.wanyue.common.utils.SpUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.main.R;
import com.wanyue.main.api.MainAPI;

public class GetProfitActivity extends BaseActivity {
    private TextView mTvCenter;
    private ViewGroup mBtnChooseAccount;
    private TextView mChooseTip;
    private ViewGroup mAccountGroup;
    private ImageView mAccountIcon;
    private TextView mAccount;
    private Button mBtnCommit;
    private String mAccountID;
    private EditText mEdit;


    String mUrl;
    double mMoney;
    @Override
    public void init() {
        setTabTitle("提取收益");
        mTvCenter = (TextView) findViewById(R.id.tv_center);
        mBtnChooseAccount = (ViewGroup) findViewById(R.id.btn_choose_account);
        mChooseTip = (TextView) findViewById(R.id.choose_tip);
        mAccountGroup = (ViewGroup) findViewById(R.id.account_group);
        mAccountIcon = (ImageView) findViewById(R.id.account_icon);
        mAccount = (TextView) findViewById(R.id.account);
        mBtnCommit = (Button) findViewById(R.id.btn_commit);
        mEdit = (EditText) findViewById(R.id.edit);

        mMoney=getIntent().getDoubleExtra(Constants.MONEY,0);
        mUrl=getIntent().getStringExtra(Constants.URL);
        mTvCenter.setText(StringUtil.getFormatPrice(mMoney));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAccount();
    }

    private void getAccount() {
        String[] values = SpUtil.getInstance().getMultiStringValue(Constants.CASH_ACCOUNT_ID, Constants.CASH_ACCOUNT, Constants.CASH_ACCOUNT_TYPE);
        if (values != null && values.length == 3) {
            String accountId = values[0];
            String account = values[1];
            String type = values[2];
            if (!TextUtils.isEmpty(accountId) && !TextUtils.isEmpty(account) && !TextUtils.isEmpty(type)) {
                if (mChooseTip.getVisibility() == View.VISIBLE) {
                    mChooseTip.setVisibility(View.INVISIBLE);
                }
                if (mAccountGroup.getVisibility() != View.VISIBLE) {
                    mAccountGroup.setVisibility(View.VISIBLE);
                }
                mAccountID = accountId;
                mAccountIcon.setImageResource(CommonIconUtil.getCashTypeIcon(Integer.parseInt(type)));
                mAccount.setText(account);
            } else {
                if (mAccountGroup.getVisibility() == View.VISIBLE) {
                    mAccountGroup.setVisibility(View.INVISIBLE);
                }
                if (mChooseTip.getVisibility() != View.VISIBLE) {
                    mChooseTip.setVisibility(View.VISIBLE);
                }
                mAccountID = null;
            }
        } else {
            if (mAccountGroup.getVisibility() == View.VISIBLE) {
                mAccountGroup.setVisibility(View.INVISIBLE);
            }
            if (mChooseTip.getVisibility() != View.VISIBLE) {
                mChooseTip.setVisibility(View.VISIBLE);
            }
            mAccountID = null;
        }
    }

    /**
     * 选择账户
     */
    public void chooseAccount(View view) {
        RouteUtil.forwardCashAccount(mAccountID);
    }


    public static void forward(Context context,String price, String route){
        try {
            double limitMoney=Double.parseDouble(price);
            Intent intent=new Intent(context,GetProfitActivity.class);
            intent.putExtra(Constants.MONEY,limitMoney);
            intent.putExtra(Constants.URL,route);
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_get_profit;
    }

    public void commit(View view) {
        String votes = mEdit.getText().toString().trim();
        double price=-1;
        try {
         price=Double.parseDouble(votes);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(votes)||price==-1) {
            ToastUtil.show(String.format(getString(R.string.profit_coin_empty)));
            return;
        }

        if(price>mMoney){
            ToastUtil.show(String.format(getString(R.string.profit_coin_error)));
            return;
        }

        if(price==0){
            ToastUtil.show(String.format(getString(R.string.profit_coin_error2)));
            return;
        }

        if (TextUtils.isEmpty(mAccountID)) {
            ToastUtil.show(R.string.profit_choose_account);
            return;
        }

        MainAPI.doCash(mUrl,votes, mAccountID, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                ToastUtil.show(msg);
                if(isSuccess(code)){
                    finish();
                }
            }
            @Override
            public boolean showLoadingDialog() {
               return true;
            }
            @Override
            public Dialog createLoadingDialog() {
               return DialogUitl.loadingDialog(mContext);
            }
        });
    }

    @Override
    protected void onDestroy() {
        MainAPI.cancle(mUrl);
        super.onDestroy();
    }
}

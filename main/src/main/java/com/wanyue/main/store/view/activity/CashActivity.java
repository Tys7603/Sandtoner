package com.wanyue.main.store.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.AbsActivity;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.utils.DialogUitl;
import com.wanyue.common.utils.L;
import com.wanyue.common.utils.RouteUtil;
import com.wanyue.common.utils.SpUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.main.R;
import com.wanyue.main.api.MainAPI;
import com.wanyue.main.store.adapter.CashAccountAdapter;
import com.wanyue.main.store.bean.CashAccountBean;
import com.wanyue.main.view.holder.CashAccountViewHolder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Cash activity.
 */
@Route(path = RouteUtil.PATH_CASH_ACCOUNT)
public class CashActivity extends AbsActivity implements View.OnClickListener, CashAccountAdapter.ActionListener  {
    private CashAccountViewHolder mCashAccountViewHolder;
    private View mNoAccount;
    private RecyclerView mRecyclerView;
    private CashAccountAdapter mAdapter;
    private String mCashAccountId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_cash;
    }

    @Override
    protected void main() {
        setTitle("提现账户");
        Intent intent = getIntent();
        mCashAccountId = intent.getStringExtra(Constants.CASH_ACCOUNT_ID);
        if (mCashAccountId == null) {
            mCashAccountId = "";
        }
        findViewById(R.id.btn_add).setOnClickListener(this);
        mNoAccount = findViewById(R.id.no_account);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new CashAccountAdapter(mContext, mCashAccountId);
        mAdapter.setActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        loadData();
    }

    private void loadData() {
        MainAPI.getCashAccountList(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (isSuccess(code)) {
                    List<CashAccountBean> list = JSON.parseArray(Arrays.toString(info), CashAccountBean.class);
                    if (list.size() > 0) {
                        if (mNoAccount.getVisibility() == View.VISIBLE) {
                            mNoAccount.setVisibility(View.INVISIBLE);
                        }
                        mAdapter.setList(list);
                    } else {
                        if (mNoAccount.getVisibility() != View.VISIBLE) {
                            mNoAccount.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_add) {
            addAccount();

        }
    }

    private void addAccount() {
        if (mCashAccountViewHolder == null) {
            mCashAccountViewHolder = new CashAccountViewHolder(mContext, (ViewGroup) findViewById(R.id.root));
        }
        mCashAccountViewHolder.addToParent();
    }

    @Override
    public void onBackPressed() {
        if (mCashAccountViewHolder != null && mCashAccountViewHolder.isShowed()) {
            mCashAccountViewHolder.removeFromParent();
            return;
        }
        super.onBackPressed();
    }

    /**
     * Insert account.
     *
     * @param cashAccountBean the cash account bean
     */
    public void insertAccount(CashAccountBean cashAccountBean) {
        if (mAdapter != null) {
            if (mNoAccount.getVisibility() == View.VISIBLE) {
                mNoAccount.setVisibility(View.INVISIBLE);
            }
            loadData();
        }
    }

    @Override
    public void onItemClick(CashAccountBean bean, int position) {
        if (!bean.getId().equals(mCashAccountId)) {
            Map<String, String> map = new HashMap<>();
            map.put(Constants.CASH_ACCOUNT_ID, bean.getId());
            map.put(Constants.CASH_ACCOUNT, bean.getAccount());
            map.put(Constants.CASH_ACCOUNT_TYPE, String.valueOf(bean.getType()));
            SpUtil.getInstance().setMultiStringValue(map);
        }
        onBackPressed();
    }

    @Override
    public void onItemDelete(final CashAccountBean bean, final int position) {
        DialogUitl.showSimpleDialog(this, WordUtil.getString(R.string.cash_delete), new DialogUitl.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                MainAPI.deleteCashAccount(bean.getId(), new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (isSuccess(code)) {
                            if (bean.getId().equals(mCashAccountId)) {
                                SpUtil.getInstance().removeValue(Constants.CASH_ACCOUNT_ID, Constants.CASH_ACCOUNT, Constants.CASH_ACCOUNT_TYPE);
                            }
                            if (mAdapter != null) {
                                mAdapter.removeItem(position);
                                if (mAdapter.getItemCount() == 0) {
                                    if (mNoAccount.getVisibility() != View.VISIBLE) {
                                        mNoAccount.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                        ToastUtil.show(msg);
                    }
                });
            }
        });
    }



    @Override
    protected void onDestroy() {
        MainAPI.cancle(MainAPI.GET_USER_ACCOUNT_LIST);
        MainAPI.cancle(MainAPI.ADD_CASH_ACCOUNT);
        MainAPI.cancle(MainAPI.DEL_CASH_ACCOUNT);
        super.onDestroy();
    }
}
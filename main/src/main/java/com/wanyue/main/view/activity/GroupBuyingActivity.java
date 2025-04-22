package com.wanyue.main.view.activity;

import androidx.recyclerview.widget.RecyclerView;

import com.wanyue.common.activity.BaseActivity;
import com.wanyue.main.R;

public class GroupBuyingActivity extends BaseActivity {
    @Override
    public void init() {
        setTabTitle("拼团列表");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_group_buy;
    }
}

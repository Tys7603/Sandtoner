package com.wanyue.shop.view.activty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.shop.R;

public class ProductUnavailableActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(this);
        }
    }

    @Override
    public void init() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            finish();
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_product_unavailable;
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, ProductUnavailableActivity.class);
        context.startActivity(intent);
    }
} 
package com.wanyue.main.integral.view.activity;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import com.wanyue.main.R;
import com.wanyue.shop.view.pop.BaseCenterPopView;

public class SignSuccDialog extends BaseCenterPopView {

    public SignSuccDialog(@NonNull Context context) {
        super(context);

    }

    @Override
    protected void init() {
        super.init();
        findViewById(R.id.btn_know).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.dialog_sign_succ;
    }
}

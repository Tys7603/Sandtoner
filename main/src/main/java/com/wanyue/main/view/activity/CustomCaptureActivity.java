package com.wanyue.main.view.activity;

import android.os.Bundle;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.wanyue.main.R;

public class CustomCaptureActivity extends CaptureActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btn_back_scan).setOnClickListener(v -> {
            onBackPressed();
            finish();
        });
    }
}

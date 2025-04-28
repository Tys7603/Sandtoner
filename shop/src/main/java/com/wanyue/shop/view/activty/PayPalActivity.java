package com.wanyue.shop.view.activty;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.wanyue.common.activity.AbsActivity;
import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.http.ShopHttpUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity xử lý kết quả thanh toán PayPal
 */
public class PayPalActivity extends AbsActivity {
    private static final String TAG = "PayPalActivity";
    
    private String mOrderId;
    private String mPaymentId;
    private String mPayerId;
    
    @Override
    protected int getLayoutId() {
        return R.layout.activity_paypal_result;
    }
    
    @Override
    protected void main() {
        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            mOrderId = intent.getStringExtra("orderId");
            mPaymentId = intent.getStringExtra("paymentId");
            mPayerId = intent.getStringExtra("payerId");
            
            // Xác minh thanh toán với máy chủ
            verifyPayment();
        } else {
            ToastUtil.show(R.string.payment_failed);
            finish();
        }
        
        // Hiển thị mã đơn hàng
        TextView tvOrderId = findViewById(R.id.tv_order_id);
        tvOrderId.setText(getString(R.string.order_id_format, mOrderId));
    }
    
    /**
     * Xác minh thanh toán với máy chủ
     */
    private void verifyPayment() {
        if (mOrderId == null || mPaymentId == null || mPayerId == null) {
            ToastUtil.show(R.string.payment_failed);
            finish();
            return;
        }
        
        // Tạo tham số cho API
        Map<String, String> params = new HashMap<>();
        params.put("orderId", mOrderId);
        params.put("paymentId", mPaymentId);
        params.put("payerId", mPayerId);
        
        // Gọi API xác minh thanh toán
        ShopHttpUtil.verifyPayPalPayment(params, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    ToastUtil.show(R.string.payment_successful);
                    // Chuyển đến màn hình kết quả thanh toán
                    navigateToPaymentResult(true);
                } else {
                    ToastUtil.show(msg);
                    navigateToPaymentResult(false);
                }
            }

            @Override
            public void onError(int code, String msg) {

            }

            @Override
            public void onError() {
                ToastUtil.show(R.string.payment_failed);
                navigateToPaymentResult(false);
            }
        });
    }
    
    /**
     * Chuyển đến màn hình kết quả thanh toán
     */
    private void navigateToPaymentResult(boolean success) {
        Intent intent = new Intent(this, OrderPayResultActivity.class);
        intent.putExtra("orderId", mOrderId);
        intent.putExtra("success", success);
        startActivity(intent);
        finish();
    }
}
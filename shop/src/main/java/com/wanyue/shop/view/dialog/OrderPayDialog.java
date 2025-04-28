package com.wanyue.shop.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.Gravity;
import android.widget.TextView;

import com.wanyue.common.Constants;
import com.wanyue.common.dialog.AbsDialogFragment;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.utils.PayPalHelper;

/**
 * Dialog hiển thị các phương thức thanh toán
 */
public class OrderPayDialog extends AbsDialogFragment implements View.OnClickListener, PayPalHelper.PayPalListener {

    private String mOrderId;
    private String mMoney;
    private ActionListener mActionListener;
    private PayPalHelper mPayPalHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_pay_order;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void init() {
        mRootView.findViewById(R.id.btn_close).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_wx).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_coin).setOnClickListener(this);
        mRootView.findViewById(R.id.btn_pp).setOnClickListener(this);
        
        mPayPalHelper = new PayPalHelper(getContext());
        mPayPalHelper.setPayPalListener(this);
        mPayPalHelper.initialize();
        
        TextView tvCoinMoney = mRootView.findViewById(R.id.tv_coin_money);
        if (tvCoinMoney != null && mMoney != null) {
            tvCoinMoney.setText(mMoney);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_close) {
            dismiss();
        } else if (id == R.id.btn_wx) {
            if (mActionListener != null) {
                mActionListener.onPayClick(Constants.PAY_TYPE_WX);
            }
            dismiss();
        } else if (id == R.id.btn_coin) {
            if (mActionListener != null) {
                mActionListener.onPayClick(Constants.PAY_TYPE_COIN);
            }
            dismiss();
        } else if (id == R.id.btn_pp) {
            // Xử lý thanh toán PayPal
            startPayPalPayment();
        }
    }
    
    /**
     * Bắt đầu quá trình thanh toán PayPal
     */
    private void startPayPalPayment() {
        if (mPayPalHelper != null && mMoney != null) {
            // Bắt đầu quá trình thanh toán
            mPayPalHelper.startPayment(mMoney, "USD", "Order #" + mOrderId);
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mPayPalHelper != null) {
            mPayPalHelper.handleActivityResult(requestCode, resultCode, data);
        }
    }
    
    @Override
    public void onPaymentSuccess(String paymentId, String payerId) {
        // Xử lý khi thanh toán thành công
        if (mActionListener != null) {
            mActionListener.onPayClick(Constants.PAY_TYPE_PP);
        }
        
        // Xác minh thanh toán với máy chủ
        if (mPayPalHelper != null && mMoney != null) {
            mPayPalHelper.verifyPayment(paymentId, payerId, mMoney);
        }
        
        dismiss();
    }
    
    @Override
    public void onPaymentError(int errorCode, String errorMessage) {
        // Xử lý khi thanh toán thất bại
        ToastUtil.show(getString(R.string.payment_failed) + ": " + errorMessage);
    }
    
    @Override
    public void onPaymentCancelled() {
        // Xử lý khi người dùng hủy thanh toán
        ToastUtil.show(getString(R.string.payment_cancelled));
    }

    public void setOrderId(String orderId) {
        mOrderId = orderId;
    }

    public void setMoney(String money) {
        mMoney = money;
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onPayClick(String payType);
    }

}
package com.wanyue.malls.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.wanyue.malls.config.PayPalConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class PayPalPaymentActivity extends AppCompatActivity {
    private static final int PAYPAL_REQUEST_CODE = 123;
    private PaymentResultListener paymentResultListener;

    // Interface to callback payment results
    public interface PaymentResultListener {
        void onPaymentSuccess(String paymentId, String paymentStatus, String payerEmail);
        void onPaymentCanceled();
        void onPaymentError(String errorMessage);
    }

    public void setPaymentResultListener(PaymentResultListener listener) {
        this.paymentResultListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Start PayPal Service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PayPalConfig.getPayPalConfig());
        startService(intent);
    }

    public void processPayment(String amount) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), PayPalConfig.CURRENCY,
                "Order description", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PayPalConfig.getPayPalConfig());
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        // Get detailed payment information
                        JSONObject jsonObject = confirmation.toJSONObject();
                        
                        // Get response information
                        JSONObject response = jsonObject.getJSONObject("response");
                        
                        // Get the necessary information
                        String paymentId = response.getString("id");
                        String state = response.getString("state");
                        
                        // Get payer information
                        JSONObject payer = response.getJSONObject("payer");
                        JSONObject payerInfo = payer.getJSONObject("payer_info");
                        String payerEmail = payerInfo.getString("email");
                        
                        // Save payment information to local database
                        savePaymentToLocalDB(paymentId, state, payerEmail);
                        
                        // Send information to server
                        sendPaymentToServer(paymentId, state, payerEmail);
                        
                        // Callback result success
                        if (paymentResultListener != null) {
                            paymentResultListener.onPaymentSuccess(paymentId, state, payerEmail);
                        }
                        
                        // Show success message
                        showSuccessDialog();
                        
                    } catch (JSONException e) {
                        e.printStackTrace();
                        String errorMessage = "Payment information processing error: " + e.getMessage();
                        handlePaymentError(errorMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorMessage = "Unknown error: " + e.getMessage();
                        handlePaymentError(errorMessage);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handling when user cancels payment
                if (paymentResultListener != null) {
                    paymentResultListener.onPaymentCanceled();
                }
                showCancelDialog();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                String errorMessage = "Invalid payment";
                handlePaymentError(errorMessage);
            }
        }
    }

    private void savePaymentToLocalDB(String paymentId, String state, String payerEmail) {
        // TODO: Implement lưu vào database local
        // Ví dụ: sử dụng Room Database hoặc SQLite
    }

    private void sendPaymentToServer(String paymentId, String state, String payerEmail) {
        // TODO: Implement gửi thông tin lên server
        // Ví dụ: sử dụng Retrofit hoặc Volley
    }

    private void handlePaymentError(String errorMessage) {
        if (paymentResultListener != null) {
            paymentResultListener.onPaymentError(errorMessage);
        }
        showErrorDialog(errorMessage);
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Payment Successful")
            .setMessage("Thank you for your payment")
            .setPositiveButton("OK", (dialog, which) -> finish())
            .show();
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Payment Cancelled")
            .setMessage("You have cancelled the payment")
            .setPositiveButton("OK", (dialog, which) -> finish())
            .show();
    }

    private void showErrorDialog(String errorMessage) {
        new AlertDialog.Builder(this)
            .setTitle("Payment Error")
            .setMessage(errorMessage)
            .setPositiveButton("OK", (dialog, which) -> finish())
            .show();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}
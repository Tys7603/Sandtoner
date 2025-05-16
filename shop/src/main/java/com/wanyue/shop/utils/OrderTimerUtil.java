package com.wanyue.shop.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.wanyue.common.utils.ToastUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.api.ShopAPI;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for managing order countdown timers
 * Orders will expire after 1 hour if not paid
 */
public class OrderTimerUtil {
    private static final String PREF_NAME = "order_timers";
    private static final String KEY_ORDER_TIME = "order_time_";
    private static final long ORDER_TIMEOUT_MS = TimeUnit.HOURS.toMillis(1); // 1 hour timeout
    
    private CountDownTimer countDownTimer;
    private View timerContainer;
    private TextView timerTextView;
    private long remainingTimeMs;
    private TimerListener listener;
    private Context context;
    private String orderId;
    private long orderCreatedTimeMs;

    /**
     * Constructor for the OrderTimerUtil
     */
    public OrderTimerUtil(Context context, View timerContainer, TextView timerTextView, String orderId, long orderCreatedTimeMs) {
        this.context = context;
        this.timerContainer = timerContainer;
        this.timerTextView = timerTextView;
        this.orderId = orderId;
        this.orderCreatedTimeMs = orderCreatedTimeMs;
    }

    /**
     * Start or resume the countdown timer based on orderCreatedTimeMs
     */
    public void resumeOrderTimer() {
        long elapsedTimeMs = System.currentTimeMillis() - orderCreatedTimeMs;
        long remainingTimeMs = ORDER_TIMEOUT_MS - elapsedTimeMs;
        if (remainingTimeMs > 0) {
            startTimer(remainingTimeMs);
        } else {
            handleOrderExpired();
        }
    }

    private void startTimer(long durationMs) {
        stopTimer(); // Stop any existing timer
        remainingTimeMs = durationMs;
        timerContainer.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(durationMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMs = millisUntilFinished;
                updateTimerText(millisUntilFinished);
                if (listener != null) {
                    listener.onTimerTick(millisUntilFinished);
                }
            }
            @Override
            public void onFinish() {
                remainingTimeMs = 0;
                updateTimerText(0);
                handleOrderExpired();
            }
        };
        countDownTimer.start();
    }

    private void handleOrderExpired() {
        ShopAPI.cancleOrder(orderId, new com.wanyue.common.http.HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (listener != null) {
                    listener.onTimerFinished();
                }
            }
            @Override
            public void onError(int code, String msg) {}
        });
    }

    private void updateTimerText(long millisUntilFinished) {
        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        timerTextView.setText(context.getString(R.string.order_timeout_message, timeFormatted));
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public void setListener(TimerListener listener) {
        this.listener = listener;
    }

    public interface TimerListener {
        void onTimerTick(long millisUntilFinished);
        void onTimerFinished();
    }
} 
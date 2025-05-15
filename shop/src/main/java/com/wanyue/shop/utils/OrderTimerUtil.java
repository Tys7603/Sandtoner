package com.wanyue.shop.utils;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for managing order countdown timers
 * Orders will expire after 1 hour if not paid
 */
public class OrderTimerUtil {

    private static final long ORDER_TIMEOUT_MS = TimeUnit.HOURS.toMillis(1); // 1 hour timeout
    private CountDownTimer countDownTimer;
    private View timerContainer;
    private TextView timerTextView;
    private long remainingTimeMs;
    private TimerListener listener;

    /**
     * Constructor for the OrderTimerUtil
     * 
     * @param timerContainer Container layout for the timer UI
     * @param timerTextView TextView that displays the countdown
     */
    public OrderTimerUtil(View timerContainer, TextView timerTextView) {
        this.timerContainer = timerContainer;
        this.timerTextView = timerTextView;
    }

    /**
     * Start the countdown timer for a newly created order
     */
    public void startNewOrderTimer() {
        startTimer(ORDER_TIMEOUT_MS);
    }

    /**
     * Start the countdown timer with a specific remaining time
     * 
     * @param createdAtTimestampMs The timestamp when the order was created
     */
    public void resumeOrderTimer(long createdAtTimestampMs) {
        long elapsedTimeMs = System.currentTimeMillis() - createdAtTimestampMs;
        long remainingTimeMs = ORDER_TIMEOUT_MS - elapsedTimeMs;
        
        if (remainingTimeMs > 0) {
            startTimer(remainingTimeMs);
        } else {
            // Order already expired
            if (listener != null) {
                listener.onTimerFinished();
            }
        }
    }

    /**
     * Start the timer with a specific duration
     * 
     * @param durationMs Duration in milliseconds
     */
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
                
                if (listener != null) {
                    listener.onTimerFinished();
                }
            }
        };
        
        countDownTimer.start();
    }
    
    /**
     * Update the timer text view with the formatted time
     * 
     * @param millisUntilFinished Milliseconds until the timer finishes
     */
    private void updateTimerText(long millisUntilFinished) {
        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
        
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        timerTextView.setText(timeFormatted);
    }
    
    /**
     * Stop the countdown timer
     */
    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
    
    /**
     * Get the remaining time in milliseconds
     * 
     * @return Remaining time in milliseconds
     */
    public long getRemainingTimeMs() {
        return remainingTimeMs;
    }
    
    /**
     * Set a listener for timer events
     * 
     * @param listener The timer listener
     */
    public void setListener(TimerListener listener) {
        this.listener = listener;
    }
    
    /**
     * Interface for timer event callbacks
     */
    public interface TimerListener {
        /**
         * Called when the timer ticks
         * 
         * @param millisUntilFinished Milliseconds until finished
         */
        void onTimerTick(long millisUntilFinished);
        
        /**
         * Called when the timer finishes
         */
        void onTimerFinished();
    }
} 
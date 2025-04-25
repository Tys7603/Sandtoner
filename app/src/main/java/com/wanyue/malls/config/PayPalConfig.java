package com.wanyue.malls.config;

import com.paypal.android.sdk.payments.PayPalConfiguration;

public class PayPalConfig {
    public static final String PAYPAL_CLIENT_ID = "YOUR_CLIENT_ID_HERE";
    
    public static final String CURRENCY = "USD";
    
    public static PayPalConfiguration getPayPalConfig() {
        return new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                .clientId(PAYPAL_CLIENT_ID);
    }
}
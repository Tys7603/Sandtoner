package com.wanyue.shop.http;

import com.wanyue.common.http.HttpCallback;
import com.wanyue.common.http.HttpClient;

import java.util.Map;

/**
 * HTTP utility class for Shop module
 */
public class ShopHttpUtil {
    
    // Base API URL
    private static final String BASE_URL = "https://system.sandtoner.com/api/";
    
    /**
     * Verify PayPal payment with server
     * @param params Payment parameters (orderId, paymentId, payerId)
     * @param callback Callback to handle response
     */
    public static void verifyPayPalPayment(Map<String, String> params, final HttpCallback callback) {
        try {
            String paramsString = mapToString(params);
            HttpClient.getInstance().post(BASE_URL + "payment/verify_paypal", paramsString)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (callback != null) {
                            callback.onSuccess(code, msg, info);
                        }
                    }
                    
                    @Override
                    public void onError(int code, String msg) {
                        if (callback != null) {
                            callback.onError(code, msg);
                        }
                    }
                });
        } catch (Exception e) {
            if (callback != null) {
                callback.onError(-1, e.getMessage());
            }
        }
    }
    
    /**
     * Get order details
     * @param orderId Order ID
     * @param callback Callback to handle response
     */
    public static void getOrderDetails(String orderId, final HttpCallback callback) {
        try {
            HttpClient.getInstance().get(BASE_URL + "order/" + orderId, null)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (callback != null) {
                            callback.onSuccess(code, msg, info);
                        }
                    }
                    
                    @Override
                    public void onError(int code, String msg) {
                        if (callback != null) {
                            callback.onError(code, msg);
                        }
                    }
                });
        } catch (Exception e) {
            if (callback != null) {
                callback.onError(-1, e.getMessage());
            }
        }
    }
    
    /**
     * Cancel order
     * @param orderId Order ID
     * @param callback Callback to handle response
     */
    public static void cancelOrder(String orderId, final HttpCallback callback) {
        try {
            Map<String, String> params = new java.util.HashMap<>();
            params.put("orderId", orderId);
            String paramsString = mapToString(params);
            HttpClient.getInstance().post(BASE_URL + "order/cancel", paramsString)
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (callback != null) {
                            callback.onSuccess(code, msg, info);
                        }
                    }
                    
                    @Override
                    public void onError(int code, String msg) {
                        if (callback != null) {
                            callback.onError(code, msg);
                        }
                    }
                });
        } catch (Exception e) {
            if (callback != null) {
                callback.onError(-1, e.getMessage());
            }
        }
    }
    
    /**
     * Add item to cart
     * @param productId Product ID
     * @param quantity Quantity to add
     * @param callback Callback to handle response
     */
    public static void addToCart(String productId, int quantity, final HttpCallback callback) {
        try {
            Map<String, String> params = new java.util.HashMap<>();
            params.put("product_id", productId);
            params.put("quantity", String.valueOf(quantity));
            
            HttpClient.getInstance().post(BASE_URL + "cart/add", mapToString(params))
                .execute(new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (callback != null) {
                            callback.onSuccess(code, msg, info);
                        }
                    }
                    
                    @Override
                    public void onError(int code, String msg) {
                        if (code == 400 && msg.contains("stock is insufficient")) {
                            if (callback != null) {
                                callback.onError(code, "Product quantity is not enough in stock");
                            }
                        } else {
                            if (callback != null) {
                                callback.onError(code, msg);
                            }
                        }
                    }
                });
        } catch (Exception e) {
            if (callback != null) {
                callback.onError(-1, e.getMessage());
            }
        }
    }
    
    /**
     * Convert Map to String (JSON or URL-encoded)
     * @param map Map to convert
     * @return String representation of the map
     */
    private static String mapToString(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        
        return sb.toString();
    }
}
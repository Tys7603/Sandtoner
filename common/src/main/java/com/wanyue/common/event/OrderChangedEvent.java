package com.wanyue.common.event;

/**
 * 2019/8/5.
 */

public class OrderChangedEvent {
    private String mOrderId;

    public OrderChangedEvent(String orderId) {
        mOrderId = orderId;
    }

    public String getOrderId(){
        return mOrderId;
    }
}

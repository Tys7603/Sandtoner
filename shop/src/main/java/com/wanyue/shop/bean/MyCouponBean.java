package com.wanyue.shop.bean;


public class MyCouponBean extends CouponBean {
        private String coupon_title;

    public String getCoupon_title() {
        return coupon_title;
    }

    public void setCoupon_title(String coupon_title) {
        this.coupon_title = coupon_title;
    }

    @Override
    public String getTitle() {
        return coupon_title;
    }
}

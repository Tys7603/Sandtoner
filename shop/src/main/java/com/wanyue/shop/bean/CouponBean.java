package com.wanyue.shop.bean;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.utils.ObjectUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.shop.R;

public class CouponBean {
    private String id;
    private String cid;

    @SerializedName("end_time")
    @JSONField(name = "end_time")
    private String endTime;


    @SerializedName("use_min_price")
    @JSONField(name = "use_min_price")
    private String minPrice;

    @SerializedName("coupon_price")
    @JSONField(name = "coupon_price")
    private String couponPrice;

    @SerializedName("is_use")
    @JSONField(name = "is_use")
    private  boolean isUse;


    @SerializedName("shop_name")
    @JSONField(name = "shop_name")
    private String shopName;
    @SerializedName("mer_id")
    @JSONField(name = "mer_id")
    private String shopId;


    private String title;
    private String type;
    private String applicable_type;


    private int isdel;

    private int shoptype;


    public boolean isUse() {
        return isUse;
    }

    public void setUse(boolean use) {
        isUse = use;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getType() {
        if(!TextUtils.isEmpty(applicable_type)){
            return getApplicable_type();
        }

        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getEndTime() {
        if(TextUtils.isEmpty(endTime)){
            endTime= WordUtil.getString(R.string.no_limit_time);
        }
        return endTime;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj!=null &&obj instanceof CouponBean){
           CouponBean couponBean= (CouponBean) obj;
           return StringUtil.equals(id,couponBean.id);
        }
        return super.equals(obj);
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(String couponPrice) {
        this.couponPrice = couponPrice;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public int getIsdel() {
        return isdel;
    }

    public void setIsdel(int isdel) {
        this.isdel = isdel;
    }

    public int getShoptype() {
        return shoptype;
    }

    public void setShoptype(int shoptype) {
        this.shoptype = shoptype;
    }

    public String getApplicable_type() {
        return applicable_type;
    }

    public void setApplicable_type(String applicable_type) {
        this.applicable_type = applicable_type;
    }
}

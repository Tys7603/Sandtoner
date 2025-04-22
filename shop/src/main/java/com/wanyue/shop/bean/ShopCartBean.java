package com.wanyue.shop.bean;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.annotation.JSONField;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.utils.StringUtil;

/**
 * The type Shop cart bean.
 */
public class ShopCartBean  implements MultiItemEntity {
    /**
     * The constant TYPE_VALID.
     */
    public static final int TYPE_VALID=21;
    /**
     * The constant TYPE_INVALID.
     */
    public static final int TYPE_INVALID=22;
    private boolean isInvalid;

    private GoodsBean productInfo;
    private String id;

    @SerializedName("cart_num")
    @JSONField(name = "cart_num")
    private int cartNum;
    @SerializedName("product_id")
    @JSONField(name = "product_id")
    private String productId;

    @SerializedName("truePrice")
    @JSONField(name = "truePrice")
    private double productPrice;

    private boolean isChecked=true;

    private ShopCartStoreBean store;

    private String unique;

    @SerializedName("is_reply")
    @JSONField(name = "is_reply")
    private String isReply;

    @Override
    public int getItemType() {
        if(isInvalid){
            return TYPE_INVALID;
        }else{
            return TYPE_VALID;
        }
}

    /**
     * Is invalid boolean.
     *
     * @return the boolean
     */
    public boolean isInvalid() {
        return isInvalid;
    }

    /**
     * Sets invalid.
     *
     * @param invalid the invalid
     */
    public void setInvalid(boolean invalid) {
        isInvalid = invalid;
    }

    /**
     * Gets product info.
     *
     * @return the product info
     */
    public GoodsBean getProductInfo() {
        return productInfo;
    }

    /**
     * Sets product info.
     *
     * @param productInfo the product info
     */
    public void setProductInfo(GoodsBean productInfo) {
        this.productInfo = productInfo;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets cart num.
     *
     * @return the cart num
     */
    public int getCartNum() {
        return cartNum;
    }

    /**
     * Sets cart num.
     *
     * @param cartNum the cart num
     */
    public void setCartNum(int cartNum) {
        this.cartNum = cartNum;
    }

    /**
     * Gets product id.
     *
     * @return the product id
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Sets product id.
     *
     * @param productId the product id
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * Gets product price.
     *
     * @return the product price
     */
    public double getProductPrice() {
        return productPrice;
    }

    /**
     * Sets product price.
     *
     * @param productPrice the product price
     */
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    /**
     * Is checked boolean.
     *
     * @return the boolean
     */
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * Sets checked.
     *
     * @param checked the checked
     */
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    /**
     * Sets store.
     *
     * @param store the store
     */
    public void setStore(ShopCartStoreBean store) {
        this.store = store;
    }

    /**
     * Gets store.
     *
     * @return the store
     */
    public ShopCartStoreBean getStore() {
        return store;
    }


    /**
     * Gets is reply.
     *
     * @return the is reply
     */
    public String getIsReply() {
        return isReply;
    }

    /**
     * Is reply boolean.
     *
     * @return the boolean
     */
    public boolean isReply(){
      if(StringUtil.isInt(isReply)){
        return Integer.parseInt(isReply)==1;
      }else if(StringUtil.isBoolean(isReply)){
          return Boolean.parseBoolean(isReply);
      }
      return false;
    }


    /**
     * Sets is reply.
     *
     * @param isReply the is reply
     */
    public void setIsReply(String isReply) {
        this.isReply = isReply;
    }

    /**
     * Gets unique.
     *
     * @return the unique
     */
    public String getUnique() {
        return unique;
    }

    /**
     * Sets unique.
     *
     * @param unique the unique
     */
    public void setUnique(String unique) {
        this.unique = unique;
    }

    @NonNull
    @Override
    public String toString() {
        return id;
    }
}

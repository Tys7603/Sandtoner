package com.wanyue.shop.bean;

public class ChooseCouponBean extends CouponBean {
    private boolean isSelect;
    private int position;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

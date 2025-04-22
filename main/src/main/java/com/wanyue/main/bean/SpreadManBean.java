package com.wanyue.main.bean;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.wanyue.common.utils.WordUtil;
import com.wanyue.main.R;

/**
 * The type Spread man bean.
 */
public class SpreadManBean {

    /*
    ├─ uid	string	非必须		用户ID
├─ nickname	string	非必须		昵称
├─ avatar	string	非必须		头像
├─ time	string	非必须		添加时间
├─ childCount	number	非必须		推广人数
├─ orderCount	number	非必须		订单数量
├─ numberCount
    */


    private String uid;
    private String nickname;
    private String time;
    private int childCount;
    private int orderCount;
    private String numberCount;
    private String avatar;

    private Spannable result;

    /**
     * Gets uid.
     *
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets uid.
     *
     * @param uid the uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Gets nickname.
     *
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets nickname.
     *
     * @param nickname the nickname
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Gets time.
     *
     * @return the time
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Gets child count.
     *
     * @return the child count
     */
    public int getChildCount() {
        return childCount;
    }

    /**
     * Sets child count.
     *
     * @param childCount the child count
     */
    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    /**
     * Gets order count.
     *
     * @return the order count
     */
    public int getOrderCount() {
        return orderCount;
    }

    /**
     * Sets order count.
     *
     * @param orderCount the order count
     */
    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    /**
     * Gets number count.
     *
     * @return the number count
     */
    public String getNumberCount() {
        return numberCount;
    }

    /**
     * Sets number count.
     *
     * @param numberCount the number count
     */
    public void setNumberCount(String numberCount) {
        this.numberCount = numberCount;
    }

    /**
     * Gets avatar.
     *
     * @return the avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Sets avatar.
     *
     * @param avatar the avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public Spannable getResult() {
        if(result==null){
            String childCountString=Integer.toString(childCount);
            String orderCountString=Integer.toString(orderCount);
            String content= WordUtil.getString(R.string.spread_result_tip1,
                    childCountString,
                    orderCountString,
                    numberCount
            );
            SpannableStringBuilder style = new SpannableStringBuilder(content);
            result=style;
            style.setSpan(new ForegroundColorSpan(Color.parseColor("#FF5121")), 0, childCountString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return result;
    }
}

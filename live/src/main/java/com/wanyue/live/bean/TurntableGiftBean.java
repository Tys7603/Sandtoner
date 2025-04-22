package com.wanyue.live.bean;

/**
 * The type Turntable gift bean.
 */
public class TurntableGiftBean {
    /**
     * id : 1
     * type : 1
     * type_val : 200
     * thumb : http://livenewtest.yunbaozb.com/public/app/pay/coin.png
     */

    private String id;
    private int type;
    private String type_val;
    private String thumb;
    private int nums;
    private String name;


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
     * Gets type.
     *
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets type val.
     *
     * @return the type val
     */
    public String getType_val() {
        return type_val;
    }

    /**
     * Sets type val.
     *
     * @param type_val the type val
     */
    public void setType_val(String type_val) {
        this.type_val = type_val;
    }

    /**
     * Gets thumb.
     *
     * @return the thumb
     */
    public String getThumb() {
        return thumb;
    }

    /**
     * Sets thumb.
     *
     * @param thumb the thumb
     */
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if(obj instanceof TurntableGiftBean){
                TurntableGiftBean turntableGiftBean= (TurntableGiftBean) obj;
                return id.equals(turntableGiftBean.id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.equals(obj);
    }

    /**
     * Gets nums.
     *
     * @return the nums
     */
    public int getNums() {
        return nums;
    }

    /**
     * Sets nums.
     *
     * @param nums the nums
     */
    public void setNums(int nums) {
        this.nums = nums;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }



}

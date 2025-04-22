package com.wanyue.main.bean;

import com.wanyue.common.bean.GoodsBean;

import java.util.List;

/**
 * The type Greate select bean.
 */
public class GreateSelectBean {
    private String tips;
    private List<GoodsBean>list;


    /**
     * Gets tips.
     *
     * @return the tips
     */
    public String getTips() {
        return tips;
    }

    /**
     * Sets tips.
     *
     * @param tips the tips
     */
    public void setTips(String tips) {
        this.tips = tips;
    }

    /**
     * Gets list.
     *
     * @return the list
     */
    public List<GoodsBean> getList() {
        return list;
    }

    /**
     * Sets list.
     *
     * @param list the list
     */
    public void setList(List<GoodsBean> list) {
        this.list = list;
    }
}

package com.wanyue.main.bean;

import com.wanyue.common.bean.GoodsBean;
import java.util.List;

/**
 * The type Foot goods bean.
 */
public class FootGoodsBean {
    private String title;
    private List<GoodsBean>list;

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

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }
}

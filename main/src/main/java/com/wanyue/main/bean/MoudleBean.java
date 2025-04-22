package com.wanyue.main.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.wanyue.common.bean.GoodsBean;

import java.util.List;

/**
 * The type Moudle bean.
 */
public class MoudleBean implements MultiItemEntity {

    private String id;
    private int type;
    private String name;
    private String des;
    private String color;
    private String tag;
    private List<GoodsBean>list;


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

    /**
     * Gets color.
     *
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets color.
     *
     * @param color the color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Gets tag.
     *
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Sets tag.
     *
     * @param tag the tag
     */
    public void setTag(String tag) {
        this.tag = tag;
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

    @Override
    public int getItemType() {
        return type;
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
     * Gets des.
     *
     * @return the des
     */
    public String getDes() {
        return des;
    }

    /**
     * Sets des.
     *
     * @param des the des
     */
    public void setDes(String des) {
        this.des = des;
    }
}

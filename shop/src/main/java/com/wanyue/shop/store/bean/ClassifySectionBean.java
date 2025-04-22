package com.wanyue.shop.store.bean;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.wanyue.common.utils.L;

/**
 * The type Classify section bean.
 */
public class ClassifySectionBean extends SectionEntity<ClassifyBean> {
    private int navtionIndex;

    /**
     * Instantiates a new Classify section bean.
     *
     * @param isHeader the is header
     * @param header   the header
     */
    public ClassifySectionBean(boolean isHeader,String header) {
        super(isHeader, header);
    }

    /**
     * Instantiates a new Classify section bean.
     *
     * @param classifyBean the classify bean
     */
    public ClassifySectionBean(ClassifyBean classifyBean) {
        super(classifyBean);
    }

    /**
     * Sets index.
     *
     * @param index the index
     */
    public void setIndex(int index) {
        this.navtionIndex = index;
        L.e("navtionIndex=="+navtionIndex);
    }

    /**
     * Gets index.
     *
     * @return the index
     */
    public int getIndex() {
        return navtionIndex;
    }
}

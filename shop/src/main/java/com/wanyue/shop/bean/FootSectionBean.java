package com.wanyue.shop.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.entity.SectionEntity;
import com.wanyue.common.bean.GoodsBean;
import com.wanyue.common.utils.L;

import java.util.List;

public class FootSectionBean extends SectionEntity<GoodsBean> implements MultiItemEntity {
    private int navtionIndex;
    private List<GoodsBean>list;

    public FootSectionBean(GoodsBean goodsBean) {
        super(goodsBean);
    }

    public FootSectionBean(boolean isHeader, String header) {
        super(isHeader, header);
    }


    public void setIndex(int index) {
        this.navtionIndex = index;
        L.e("navtionIndex=="+navtionIndex);
    }

    public int getIndex() {
        return navtionIndex;
    }

    @Override
    public int getItemType() {
        return isHeader?1:2;
    }

    public List<GoodsBean> getList() {
        return list;
    }

    public void setList(List<GoodsBean> list) {
        this.list = list;
    }
}

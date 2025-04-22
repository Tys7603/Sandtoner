package com.wanyue.main.adapter;

import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.main.R;
import com.wanyue.main.bean.MenuBean;
import java.util.List;

/**
 * The type Menu user adapter.
 */
public class MenuUserAdapter extends BaseRecyclerAdapter<MenuBean,BaseReclyViewHolder> {
    /**
     * Instantiates a new Menu user adapter.
     *
     * @param data the data
     */
    public MenuUserAdapter(List<MenuBean> data) {
        super(data);
    }
    @Override
    public int getLayoutId() {
        return R.layout.item_relcy_menu_user;
    }

    @Override
    public void convert(BaseReclyViewHolder helper, MenuBean item) {
        helper.setText(R.id.tv_name,item.getName());
        int id=item.getLocalIcon();
        String url=item.getPic();
        if(id!=-1&&id!=0){
           helper.setImageResouceId(id,R.id.img_icon);
        }else{
            helper.setImageUrl(url,R.id.img_icon);
        }
    }

}

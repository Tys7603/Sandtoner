package com.wanyue.main.find.adapter;

import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.adapter.base.BaseRecyclerAdapter;
import com.wanyue.common.custom.CheckImageView;
import com.wanyue.main.R;
import java.util.List;

/**
 * The type Find photo adapter.
 */
public class FindPhotoAdapter extends BaseRecyclerAdapter<String, BaseReclyViewHolder> {
    /**
     * Instantiates a new Find photo adapter.
     *
     * @param data the data
     */
    public FindPhotoAdapter(List<String> data) {
        super(data);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_relcy_find_photo;
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, String url) {
        helper.setImageUrl(url,R.id.img);
    }
}

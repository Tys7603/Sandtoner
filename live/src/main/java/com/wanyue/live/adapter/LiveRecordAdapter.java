package com.wanyue.live.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wanyue.common.adapter.RefreshAdapter;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.live.R;
import com.wanyue.live.bean.LiveRecordBean;

/**
 * 2018/9/30.
 */
public class LiveRecordAdapter extends RefreshAdapter<LiveRecordBean> {

    private View.OnClickListener mOnClickListener;

    /**
     * Instantiates a new Live record adapter.
     *
     * @param context the context
     */
    public LiveRecordAdapter(Context context) {
        super(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClick()) {
                    return;
                }
                Object tag = v.getTag();
                if (tag != null) {
                    LiveRecordBean bean = (LiveRecordBean) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean, 0);
                    }
                }
            }
        };
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_live_record, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        ((Vh) vh).setData(mList.get(position));
    }

    /**
     * The type Vh.
     */
    class Vh extends RecyclerView.ViewHolder {

        /**
         * The M title.
         */
        TextView mTitle;
        /**
         * The M time.
         */
        TextView mTime;
        /**
         * The M num.
         */
        TextView mNum;

        /**
         * Instantiates a new Vh.
         *
         * @param itemView the item view
         */
        public Vh(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mTime = (TextView) itemView.findViewById(R.id.time);
            mNum = (TextView) itemView.findViewById(R.id.num);
            itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * Sets data.
         *
         * @param bean the bean
         */
        void setData(LiveRecordBean bean) {
            itemView.setTag(bean);
            mTitle.setText(bean.getTitle());
            mTime.setText(bean.getDateEndTime());
            mNum.setText(StringUtil.toWan(bean.getNums()));
        }
    }

}

package com.wanyue.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wanyue.common.Constants;
import com.wanyue.common.bean.CoinPayBean;
import com.wanyue.common.glide.ImgLoader;
import com.wanyue.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 2019/4/11.
 */
public class CoinPayAdapter extends RecyclerView.Adapter<CoinPayAdapter.Vh> {

    private Context mContext;
    private List<CoinPayBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private int mCheckedPosition;

    /**
     * Instantiates a new Coin pay adapter.
     *
     * @param context the context
     */
    public CoinPayAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mList = new ArrayList<>();
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag == null) {
                    return;
                }
                int position = (int) tag;
                if (mCheckedPosition != position) {
                    if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
                        mList.get(mCheckedPosition).setChecked(false);
                        notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
                    }
                    mList.get(position).setChecked(true);
                    notifyItemChanged(position, Constants.PAYLOAD);
                    mCheckedPosition = position;
                }
            }
        };
    }

    /**
     * Sets list.
     *
     * @param list the list
     */
    public void setList(List<CoinPayBean> list) {
        if (list != null && list.size() > 0) {
            mList.clear();
            if (mCheckedPosition >= 0 && mCheckedPosition < list.size()) {
                list.get(mCheckedPosition).setChecked(true);
            }
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_coin_pay, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * The type Vh.
     */
    class Vh extends RecyclerView.ViewHolder {

        /**
         * The M name.
         */
        TextView mName;
        /**
         * The M thumb.
         */
        ImageView mThumb;
        /**
         * The M wrap.
         */
        View mWrap;

        /**
         * Instantiates a new Vh.
         *
         * @param itemView the item view
         */
        public Vh(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.name);
            mThumb = itemView.findViewById(R.id.thumb);
            mWrap = itemView.findViewById(R.id.wrap);
            itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * Sets data.
         *
         * @param bean     the bean
         * @param position the position
         * @param payload  the payload
         */
        void setData(CoinPayBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                mName.setText(bean.getName());
                ImgLoader.display(mContext, bean.getIcon(), mThumb);
            }
            if (bean.isChecked()) {
                if (mWrap.getVisibility() != View.VISIBLE) {
                    mWrap.setVisibility(View.VISIBLE);
                }
            } else {
                if (mWrap.getVisibility() == View.VISIBLE) {
                    mWrap.setVisibility(View.INVISIBLE);
                }
            }
        }
    }


    /**
     * Gets pay coin pay bean.
     *
     * @return the pay coin pay bean
     */
    public CoinPayBean getPayCoinPayBean() {
        if (mList != null && mList.size() > 0) {
            if (mCheckedPosition >= 0 && mCheckedPosition < mList.size()) {
                CoinPayBean bean = mList.get(mCheckedPosition);
                if (bean != null) {
                    return bean;
                }
            }
        }
        return null;
    }
}

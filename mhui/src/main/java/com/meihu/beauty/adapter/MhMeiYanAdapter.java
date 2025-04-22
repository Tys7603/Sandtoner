package com.meihu.beauty.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.meihu.beauty.R;
import com.meihu.beauty.bean.MeiYanBean;
import com.meihu.beauty.constant.Constants;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beauty.utils.WordUtil;

import java.util.List;

/**
 * The type Mh mei yan adapter.
 */
public class MhMeiYanAdapter extends RecyclerView.Adapter {

    private LayoutInflater mInflater;
    private List<MeiYanBean> mList;
    private View.OnClickListener mOnClickListener;
    private int mCheckedPosition = -1;
    private int mColor0;
    private int mColor1;
    private OnItemClickListener<MeiYanBean> mOnItemClickListener;

    /**
     * Instantiates a new Mh mei yan adapter.
     *
     * @param context the context
     * @param list    the list
     */
    public MhMeiYanAdapter(Context context, List<MeiYanBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mColor0 = ContextCompat.getColor(context, R.color.textColor2);
        mColor1 = ContextCompat.getColor(context, R.color.global);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if (position != mCheckedPosition) {
                    MeiYanBean bean = mList.get(position);
                    bean.setChecked(true);
                    notifyItemChanged(position, Constants.PAYLOAD);
                    if (mCheckedPosition >= 0) {
                        mList.get(mCheckedPosition).setChecked(false);
                        notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
                    }
                    mCheckedPosition = position;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean, position);
                    }
                }
            }
        };
    }

    /**
     * Sets on item click listener.
     *
     * @param onItemClickListener the on item click listener
     */
    public void setOnItemClickListener(OnItemClickListener<MeiYanBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * Gets checked name.
     *
     * @return the checked name
     */
    public int getCheckedName() {
        if (mCheckedPosition == -1) {
            return -1;
        }
        return mList.get(mCheckedPosition).getName();
    }

    /**
     * Gets checked bean.
     *
     * @return the checked bean
     */
    public MeiYanBean getCheckedBean() {
        if (mCheckedPosition == -1) {
            return null;
        }
        return mList.get(mCheckedPosition);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_meiyan_1, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position, @NonNull List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
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
         * The M thumb.
         */
        ImageView mThumb;
        /**
         * The M name.
         */
        TextView mName;

        /**
         * Instantiates a new Vh.
         *
         * @param itemView the item view
         */
        public Vh(@NonNull View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * Sets data.
         *
         * @param bean     the bean
         * @param position the position
         * @param payload  the payload
         */
        void setData(MeiYanBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                mName.setText(WordUtil.getString(MhDataManager.getInstance().getContext(),bean.getName()));
            }
            if (bean.isChecked()) {
                mName.setTextColor(mColor1);
                mThumb.setImageDrawable(bean.getDrawable1());
            } else {
                mName.setTextColor(mColor0);
                mThumb.setImageDrawable(bean.getDrawable0());
            }
        }

    }
}

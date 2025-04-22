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
import com.meihu.beauty.bean.MeiYanFilterBean;
import com.meihu.beauty.constant.Constants;
import com.meihu.beauty.interfaces.OnItemClickListener;
import com.meihu.beauty.utils.MhDataManager;
import com.meihu.beauty.utils.WordUtil;

import java.util.List;

/**
 * The type Mh mei yan filter adapter.
 */
public class MhMeiYanFilterAdapter extends RecyclerView.Adapter {

    private LayoutInflater mInflater;
    private List<MeiYanFilterBean> mList;
    private View.OnClickListener mOnClickListener;
    private int mCheckedPosition;
    private int mColor0;
    private int mColor1;
    private OnItemClickListener<MeiYanFilterBean> mOnItemClickListener;

    /**
     * Instantiates a new Mh mei yan filter adapter.
     *
     * @param context the context
     * @param list    the list
     */
    public MhMeiYanFilterAdapter(Context context, List<MeiYanFilterBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mColor0 = ContextCompat.getColor(context, R.color.textColor2);
        mColor1 = ContextCompat.getColor(context, R.color.global);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if (position != mCheckedPosition) {
                    MeiYanFilterBean bean = mList.get(position);
                    bean.setChecked(true);
                    mList.get(mCheckedPosition).setChecked(false);
                    notifyItemChanged(position, Constants.PAYLOAD);
                    notifyItemChanged(mCheckedPosition, Constants.PAYLOAD);
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
    public void setOnItemClickListener(OnItemClickListener<MeiYanFilterBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Vh(mInflater.inflate(R.layout.item_meiyan_2, viewGroup, false));
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
         * The M check.
         */
        View mCheck;

        /**
         * Instantiates a new Vh.
         *
         * @param itemView the item view
         */
        public Vh(@NonNull View itemView) {
            super(itemView);
            mThumb = itemView.findViewById(R.id.thumb);
            mName = itemView.findViewById(R.id.name);
            mCheck = itemView.findViewById(R.id.check);
            itemView.setOnClickListener(mOnClickListener);
        }

        /**
         * Sets data.
         *
         * @param bean     the bean
         * @param position the position
         * @param payload  the payload
         */
        void setData(MeiYanFilterBean bean, int position, Object payload) {
            if (payload == null) {
                itemView.setTag(position);
                mName.setText(WordUtil.getString(MhDataManager.getInstance().getContext(),bean.getName()));
                mThumb.setImageResource(bean.getThumb());
            }
            if (bean.isChecked()) {
                mName.setTextColor(mColor1);
                if (mCheck.getVisibility() != View.VISIBLE) {
                    mCheck.setVisibility(View.VISIBLE);
                }
            } else {
                mName.setTextColor(mColor0);
                if (mCheck.getVisibility() == View.VISIBLE) {
                    mCheck.setVisibility(View.INVISIBLE);
                }
            }
        }

    }
}

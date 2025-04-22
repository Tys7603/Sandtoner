package com.wanyue.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wanyue.common.bean.CatBean;
import com.wanyue.common.custom.MyRadioButton;
import com.wanyue.common.interfaces.OnItemClickListener;
import com.wanyue.video.R;
import java.util.List;

public class VideoClassifyAdapter extends RecyclerView.Adapter<VideoClassifyAdapter.Vh>{
    private Context mContext;
    private List<CatBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<CatBean> mOnItemClickListener;

    public VideoClassifyAdapter(Context context, List<CatBean> list) {
        mContext=context;
        mList = list;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick((CatBean) tag, 0);
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<CatBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_video_ready_class, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int i) {
        vh.setData(mList.get(i));
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        TextView mName;
        MyRadioButton mRadioButton;

        public Vh(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mRadioButton = (MyRadioButton) itemView.findViewById(R.id.radioButton);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(CatBean bean) {
            itemView.setTag(bean);
            mName.setText(bean.getName());
            mRadioButton.doChecked(bean.isChecked());
        }
    }
}

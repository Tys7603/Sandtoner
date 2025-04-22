package com.wanyue.main.integral.view.proxy;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.wanyue.common.proxy.RxViewProxy;
import com.wanyue.main.R;
import com.wanyue.main.integral.adapter.ProgressAdapter;
import com.wanyue.main.integral.bean.WeekSignBean;
import com.wanyue.shop.view.widet.linear.ListPool;
import com.wanyue.shop.view.widet.linear.PoolLinearListView;
import java.util.List;

public class SignProgressViewProxy extends RxViewProxy {

    private PoolLinearListView mListView;
    private ProgressAdapter mProgressAdapter;
    private int mCurrentLevel;
    private int mTodaySign;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        mListView = findViewById(R.id.listView);
        mListView.setListPool(new ListPool());
        mListView.setOrientation(PoolLinearListView.HORIZONTAL);
        mProgressAdapter=new ProgressAdapter(null);
        mListView.setAdapter(mProgressAdapter);
    }


    @Override
    public int getLayoutId() {
        return R.layout.view_sign_progress;
    }

    public void setCurrentLevel(int currentLevel) {
        mCurrentLevel = currentLevel;
        if(mProgressAdapter!=null){
           mProgressAdapter.setCurrentLevel(mCurrentLevel);
        }
    }
    public void setData(List<WeekSignBean>list){
        if(mProgressAdapter!=null){
           mProgressAdapter.setData(list);
        }
        int childCount=mListView.getChildCount();
        for(int i=0;i<childCount;i++){
           View view= mListView.getChildAt(i);
           if(view.getId()==R.id.line){
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) view.getLayoutParams();
            params.weight=1;
           }
        }

        for(WeekSignBean weekSignBean:list){
            if(mCurrentLevel<weekSignBean.getId()){
                break;
            }
        }
    }


}

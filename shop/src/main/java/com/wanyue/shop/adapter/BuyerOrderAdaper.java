package com.wanyue.shop.adapter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.wanyue.common.adapter.base.BaseMutiRecyclerAdapter;
import com.wanyue.common.adapter.base.BaseReclyViewHolder;
import com.wanyue.common.utils.ClickUtil;
import com.wanyue.common.utils.DebugUtil;
import com.wanyue.common.utils.StringUtil;
import com.wanyue.common.utils.ViewUtil;
import com.wanyue.common.utils.WordUtil;
import com.wanyue.shop.R;
import com.wanyue.shop.api.ShopAPI;
import com.wanyue.shop.bean.OrderBean;
import com.wanyue.shop.bean.OrderStatus;
import com.wanyue.shop.business.ShopState;
import com.wanyue.shop.utils.OrderTimerUtil;
import com.wanyue.shop.view.widet.linear.ListPool;
import com.wanyue.shop.view.widet.linear.PoolLinearListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Buyer order adaper.
 */
public class BuyerOrderAdaper extends BaseMutiRecyclerAdapter<OrderBean, BaseReclyViewHolder> {
    private ListPool mListPool;
    private Map<String, OrderTimerUtil> mOrderTimers;

    /**
     * Instantiates a new Buyer order adaper.
     *
     * @param data the data
     */
    public BuyerOrderAdaper(List<OrderBean> data) {
        super(data);
        addItemType(ShopState.ORDER_STATE_WAIT_PAY,R.layout.item_recly_buyer_order_1);
        addItemType(ShopState.ORDER_STATE_WAIT_DELIVERED,R.layout.item_recly_buyer_order_2);
        addItemType(ShopState.ORDER_STATE_WAIT_RECEIVE,R.layout.item_recly_buyer_order_2);
        addItemType(ShopState.ORDER_STATE_WAIT_EVALUATE,R.layout.item_recly_buyer_order_3);
        addItemType(ShopState.ORDER_STATE_COMPELETE,R.layout.item_recly_buyer_order_4);
        addItemType(-1,R.layout.empty);
        mOrderTimers = new HashMap<>();
    }

    @Override
    protected void convert(@NonNull BaseReclyViewHolder helper, OrderBean item) {
        LinearLayout timerContainer = helper.getView(R.id.layout_order_timer);
        switch (helper.getItemViewType()){
            case ShopState.ORDER_STATE_WAIT_PAY:
                commonConvert(helper,item);
                helper.setOnChildClickListner(R.id.btn_cancel,mOnClickListener);
                helper.setOnChildClickListner(R.id.btn_buy,mOnClickListener);
                if (timerContainer != null) timerContainer.setVisibility(View.VISIBLE);
                setupOrderTimer(helper, item);
                break;
            case ShopState.ORDER_STATE_WAIT_DELIVERED:
            case ShopState.ORDER_STATE_WAIT_RECEIVE:
            case ShopState.ORDER_STATE_WAIT_EVALUATE:
            case ShopState.ORDER_STATE_COMPELETE:
            case -1:
            default:
                commonConvert(helper,item);
                if (timerContainer != null) timerContainer.setVisibility(View.GONE);
                break;
        }
    }

    private void setupOrderTimer(BaseReclyViewHolder helper, OrderBean orderBean) {
        LinearLayout timerContainer = helper.getView(R.id.layout_order_timer);
        TextView timerTextView = helper.getView(R.id.tv_order_timeout);
        
        if (timerContainer != null && timerTextView != null) {
            String orderId = orderBean.getOrderId();
            long orderCreatedTimeMs = 0;
            try {
                String addTimeStr = orderBean.getAddTime();
                if (addTimeStr != null && !addTimeStr.isEmpty()) {
                    long addTimeLong = Long.parseLong(addTimeStr);
                    if (addTimeLong < 1000000000000L) {
                        orderCreatedTimeMs = addTimeLong * 1000L;
                    } else {
                        orderCreatedTimeMs = addTimeLong;
                    }
                }
            } catch (Exception e) {
                orderCreatedTimeMs = System.currentTimeMillis();
            }
            OrderTimerUtil timerUtil = mOrderTimers.get(orderId);
            
            if (timerUtil == null) {
                timerUtil = new OrderTimerUtil(helper.itemView.getContext(), timerContainer, timerTextView, orderId, orderCreatedTimeMs);
                timerUtil.setListener(new OrderTimerUtil.TimerListener() {
                    @Override
                    public void onTimerTick(long millisUntilFinished) {}
                    @Override
                    public void onTimerFinished() {
                        if (mOnOrderExpiredListener != null) {
                            mOnOrderExpiredListener.onOrderExpired(orderId);
                        }
                    }
                });
                mOrderTimers.put(orderId, timerUtil);
            }
            timerUtil.resumeOrderTimer();
        }
    }

    private void commonConvert(BaseReclyViewHolder helper, OrderBean item) {
        helper.setText(R.id.tv_name,item.getShopName());
        helper.setText(R.id.tv_price, StringUtil.getPrice(item.getTotalPrice()));
        helper.setText(R.id.tv_total_num, WordUtil.getString(R.string.total_num_jian2,item.getTotalNum()));
        PoolLinearListView linearListView=helper.getView(R.id.listView);
        if(linearListView.getAdapter()==null){
            if(mListPool==null){
               mListPool=new ListPool();
            }
            linearListView.setOrientation(PoolLinearListView.VERTICAL);
            linearListView.setListPool(mListPool);
            BuyerOrderChildAdapter childAdapter=new BuyerOrderChildAdapter(item.getCartInfo());
            linearListView.setAdapter(childAdapter);
        }else{
            BuyerOrderChildAdapter childAdapter= (BuyerOrderChildAdapter) linearListView.getAdapter();
            childAdapter.setData(item.getCartInfo());
        }
        OrderStatus orderStatus=item.getOrderStatus();
        if(orderStatus!=null){
           helper.setText(R.id.tv_status,orderStatus.getTitle());
        }
    }
    
    /**
     * Interface for order expiration events
     */
    public interface OnOrderExpiredListener {
        void onOrderExpired(String orderId);
    }
    
    private OnOrderExpiredListener mOnOrderExpiredListener;
    
    public void setOnOrderExpiredListener(OnOrderExpiredListener listener) {
        mOnOrderExpiredListener = listener;
    }
    
    @Override
    public void onViewRecycled(@NonNull BaseReclyViewHolder holder) {
        super.onViewRecycled(holder);
        // Stop timers when views are recycled
        if (holder.getItemViewType() == ShopState.ORDER_STATE_WAIT_PAY) {
            OrderBean orderBean = getItem(holder.getLayoutPosition());
            if (orderBean != null) {
                OrderTimerUtil timerUtil = mOrderTimers.get(orderBean.getOrderId());
                if (timerUtil != null) {
                    timerUtil.stopTimer();
                }
            }
        }
    }
    
    @Override
    public void onDetachedFromRecyclerView(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        // Clean up all timers
        for (OrderTimerUtil timer : mOrderTimers.values()) {
            timer.stopTimer();
        }
        mOrderTimers.clear();
    }
}

package com.tencent.qcloud.tim.uikit.modules.chat.layout.message;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.view.View;

import com.tencent.qcloud.tim.uikit.component.CustomLinearLayoutManager;
import com.tencent.qcloud.tim.uikit.component.action.PopMenuAction;
import com.tencent.qcloud.tim.uikit.modules.chat.interfaces.IMessageLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.interfaces.IMessageProperties;
import com.tencent.qcloud.tim.uikit.utils.ScreenUtil;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.MessageListAdapter.OnScrollListener;

import java.util.ArrayList;
import java.util.List;

public abstract class MessageLayoutUI extends RecyclerView implements IMessageLayout {

    protected MessageLayout.OnItemLongClickListener mOnItemLongClickListener;
    protected MessageLayout.OnLoadMoreHandler mHandler;
    protected MessageLayout.OnEmptySpaceClickListener mEmptySpaceClickListener;
    protected MessageListAdapter mAdapter;
    protected List<PopMenuAction> mPopActions = new ArrayList<>();
    protected List<PopMenuAction> mMorePopActions = new ArrayList<>();
    protected MessageLayout.OnPopActionClickListener mOnPopActionClickListener;
    private Properties properties = Properties.getInstance();

    public MessageLayoutUI(Context context) {
        super(context);
        init();
    }

    public MessageLayoutUI(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MessageLayoutUI(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutFrozen(false);
        setItemViewCacheSize(0);
        setHasFixedSize(true);
        setFocusableInTouchMode(false);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setVerticalScrollBarEnabled(true);
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(false);
        setLayoutManager(linearLayoutManager);
        
        addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastScrollY = 0;
            private boolean isScrollingUp = false;
            private static final int SCROLL_THRESHOLD = 10; // Minimum scroll distance to trigger boundary check

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Reset scroll tracking when scrolling stops
                    lastScrollY = 0;
                    
                    // Check if we're at boundaries and need to bounce back
                    LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
                    if (layoutManager != null) {
                        int firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition();
                        int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                        
                        // If we're at the top and trying to scroll up further
                        if (firstVisible == 0 && isScrollingUp) {
                            smoothScrollToPosition(0);
                        }
                        
                        // If we're at the bottom and trying to scroll down further
                        if (lastVisible == getAdapter().getItemCount() - 1 && !isScrollingUp) {
                            smoothScrollToPosition(getAdapter().getItemCount() - 1);
                        }
                    }
                }
            }
            
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                // Track scroll direction
                if (Math.abs(dy) > SCROLL_THRESHOLD) {
                    isScrollingUp = dy < 0;
                    lastScrollY = dy;
                }

                // Prevent over-scrolling at boundaries
                LinearLayoutManager layoutManager = (LinearLayoutManager) getLayoutManager();
                if (layoutManager != null) {
                    int firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition();
                    int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                    
                    // If at top and trying to scroll up
                    if (firstVisible == 0 && dy < 0) {
                        setOverScrollMode(View.OVER_SCROLL_NEVER);
                    }
                    // If at bottom and trying to scroll down
                    else if (lastVisible == getAdapter().getItemCount() - 1 && dy > 0) {
                        setOverScrollMode(View.OVER_SCROLL_NEVER);
                    }
                    // Allow normal over-scroll in middle of list
                    else {
                        setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
                    }
                }

                // Notify scroll position changes
                if (mAdapter != null && mAdapter instanceof MessageListAdapter) {
                    MessageListAdapter adapter = (MessageListAdapter) mAdapter;
                    MessageListAdapter.OnScrollListener scrollListener = adapter.getOnScrollListener();
                    if (scrollListener != null) {
                        scrollListener.onScrollChanged(dx, dy);
                    }
                }
            }
        });
    }

    @Override
    public int getAvatarRadius() {
        return properties.getAvatarRadius();
    }

    @Override
    public void setAvatarRadius(int radius) {
        properties.setAvatarRadius(radius);
    }

    @Override
    public int[] getAvatarSize() {
        return properties.avatarSize;
    }

    @Override
    public void setAvatarSize(int[] size) {
        properties.setAvatarSize(size);
    }

    @Override
    public int getAvatar() {
        return properties.getAvatar();
    }

    @Override
    public void setAvatar(int resId) {
        properties.setAvatar(resId);
    }

    @Override
    public Drawable getRightBubble() {
        return properties.getRightBubble();
    }

    @Override
    public void setRightBubble(Drawable bubble) {
        properties.setRightBubble(bubble);
    }

    @Override
    public Drawable getLeftBubble() {
        return properties.getLeftBubble();
    }

    @Override
    public void setLeftBubble(Drawable bubble) {
        properties.setLeftBubble(bubble);
    }

    @Override
    public int getNameFontSize() {
        return properties.getNameFontSize();
    }

    @Override
    public void setNameFontSize(int size) {
        properties.setNameFontSize(size);
    }

    @Override
    public int getNameFontColor() {
        return properties.getNameFontColor();
    }

    @Override
    public void setNameFontColor(int color) {
        properties.setNameFontColor(color);
    }

    @Override
    public int getLeftNameVisibility() {
        return properties.getLeftNameVisibility();
    }

    @Override
    public void setLeftNameVisibility(int visibility) {
        properties.setLeftNameVisibility(visibility);
    }

    @Override
    public int getRightNameVisibility() {
        return properties.getRightNameVisibility();
    }

    @Override
    public void setRightNameVisibility(int visibility) {
        properties.setRightNameVisibility(visibility);
    }

    @Override
    public int getChatContextFontSize() {
        return properties.getChatContextFontSize();
    }

    @Override
    public void setChatContextFontSize(int size) {
        properties.setChatContextFontSize(size);
    }

    @Override
    public int getRightChatContentFontColor() {
        return properties.getRightChatContentFontColor();
    }

    @Override
    public void setRightChatContentFontColor(int color) {
        properties.setRightChatContentFontColor(color);
    }

    @Override
    public int getLeftChatContentFontColor() {
        return properties.getLeftChatContentFontColor();
    }

    @Override
    public void setLeftChatContentFontColor(int color) {
        properties.setLeftChatContentFontColor(color);
    }

    @Override
    public Drawable getTipsMessageBubble() {
        return properties.getTipsMessageBubble();
    }

    @Override
    public void setTipsMessageBubble(Drawable bubble) {
        properties.setTipsMessageBubble(bubble);
    }

    @Override
    public int getTipsMessageFontSize() {
        return properties.getTipsMessageFontSize();
    }

    @Override
    public void setTipsMessageFontSize(int size) {
        properties.setTipsMessageFontSize(size);
    }

    @Override
    public int getTipsMessageFontColor() {
        return properties.getTipsMessageFontColor();
    }

    @Override
    public void setTipsMessageFontColor(int color) {
        properties.setTipsMessageFontColor(color);
    }

    @Override
    public Drawable getChatTimeBubble() {
        return properties.getChatTimeBubble();
    }

    @Override
    public void setChatTimeBubble(Drawable bubble) {
        properties.setChatTimeBubble(bubble);
    }

    @Override
    public int getChatTimeFontSize() {
        return properties.getChatTimeFontSize();
    }

    @Override
    public void setChatTimeFontSize(int size) {
        properties.setChatTimeFontSize(size);
    }

    @Override
    public int getChatTimeFontColor() {
        return properties.getChatTimeFontColor();
    }

    @Override
    public void setChatTimeFontColor(int color) {
        properties.setChatTimeFontColor(color);
    }

    @Override
    public MessageLayout.OnItemLongClickListener getOnItemClickListener() {
        return mAdapter.getOnItemClickListener();
    }

    @Override
    public void setOnItemClickListener(MessageLayout.OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
        mAdapter.setOnItemClickListener(listener);
    }

    @Override
    public void setAdapter(MessageListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = adapter;
        postSetAdapter(adapter);
    }

    protected abstract void postSetAdapter(MessageListAdapter adapter);

    @Override
    public List<PopMenuAction> getPopActions() {
        return mPopActions;
    }

    @Override
    public void addPopAction(PopMenuAction action) {
        mMorePopActions.add(action);
    }

    public static class Properties implements IMessageProperties {

        private static Properties sP = new Properties();
        private int mAvatarId;
        private int mAvatarRadius;
        private int[] avatarSize = null;
        private int mNameFontSize;
        private int mNameFontColor;
        private int mLeftNameVisibility;
        private int mRightNameVisibility;
        private int mChatContextFontSize;
        private int mMyChatContentFontColor;
        private Drawable mMyBubble;
        private int mFriendChatContentFontColor;
        private Drawable mFriendBubble;
        private int mTipsMessageFontSize;
        private int mTipsMessageFontColor;
        private Drawable mTipsMessageBubble;
        private int mChatTimeFontSize;
        private int mChatTimeFontColor;
        private Drawable mChatTimeBubble;

        private Properties() {

        }

        public static Properties getInstance() {
            if (sP == null) {
                sP = new Properties();
            }
            return sP;
        }

        @Override
        public int getAvatarRadius() {
            return mAvatarRadius;
        }

        @Override
        public void setAvatarRadius(int radius) {
            mAvatarRadius = ScreenUtil.getPxByDp(radius);
        }

        @Override
        public int[] getAvatarSize() {
            return avatarSize;
        }

        @Override
        public void setAvatarSize(int[] size) {
            if (size != null && size.length == 2) {
                avatarSize = new int[2];
                avatarSize[0] = ScreenUtil.getPxByDp(size[0]);
                avatarSize[1] = ScreenUtil.getPxByDp(size[1]);
            }
        }

        @Override
        public int getAvatar() {
            return mAvatarId;
        }

        @Override
        public void setAvatar(int resId) {
            this.mAvatarId = resId;
        }

        @Override
        public Drawable getRightBubble() {
            return mMyBubble;
        }

        @Override
        public void setRightBubble(Drawable bubble) {
            this.mMyBubble = bubble;
        }

        @Override
        public Drawable getLeftBubble() {
            return mFriendBubble;
        }

        @Override
        public void setLeftBubble(Drawable bubble) {
            this.mFriendBubble = bubble;
        }

        @Override
        public int getNameFontSize() {
            return mNameFontSize;
        }

        @Override
        public void setNameFontSize(int size) {
            this.mNameFontSize = size;
        }

        @Override
        public int getNameFontColor() {
            return mNameFontColor;
        }

        @Override
        public void setNameFontColor(int color) {
            this.mNameFontColor = color;
        }

        @Override
        public int getLeftNameVisibility() {
            return mLeftNameVisibility;
        }

        @Override
        public void setLeftNameVisibility(int visibility) {
            mLeftNameVisibility = visibility;
        }

        @Override
        public int getRightNameVisibility() {
            return mRightNameVisibility;
        }

        @Override
        public void setRightNameVisibility(int visibility) {
            mRightNameVisibility = visibility;
        }

        @Override
        public int getChatContextFontSize() {
            return mChatContextFontSize;
        }

        @Override
        public void setChatContextFontSize(int size) {
            this.mChatContextFontSize = size;
        }

        @Override
        public int getRightChatContentFontColor() {
            return mMyChatContentFontColor;
        }

        @Override
        public void setRightChatContentFontColor(int color) {
            this.mMyChatContentFontColor = color;
        }

        @Override
        public int getLeftChatContentFontColor() {
            return mFriendChatContentFontColor;
        }

        @Override
        public void setLeftChatContentFontColor(int color) {
            this.mFriendChatContentFontColor = color;
        }

        @Override
        public Drawable getTipsMessageBubble() {
            return mTipsMessageBubble;
        }

        @Override
        public void setTipsMessageBubble(Drawable bubble) {
            this.mTipsMessageBubble = bubble;
        }

        @Override
        public int getTipsMessageFontSize() {
            return mTipsMessageFontSize;
        }

        @Override
        public void setTipsMessageFontSize(int size) {
            this.mTipsMessageFontSize = size;
        }

        @Override
        public int getTipsMessageFontColor() {
            return mTipsMessageFontColor;
        }

        @Override
        public void setTipsMessageFontColor(int color) {
            this.mTipsMessageFontColor = color;
        }

        @Override
        public Drawable getChatTimeBubble() {
            return mChatTimeBubble;
        }

        @Override
        public void setChatTimeBubble(Drawable bubble) {
            this.mChatTimeBubble = bubble;
        }

        @Override
        public int getChatTimeFontSize() {
            return mChatTimeFontSize;
        }

        @Override
        public void setChatTimeFontSize(int size) {
            this.mChatTimeFontSize = size;
        }

        @Override
        public int getChatTimeFontColor() {
            return mChatTimeFontColor;
        }

        @Override
        public void setChatTimeFontColor(int color) {
            this.mChatTimeFontColor = color;
        }

    }
}

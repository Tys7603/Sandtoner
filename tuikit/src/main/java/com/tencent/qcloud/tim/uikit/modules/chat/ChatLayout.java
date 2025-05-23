package com.tencent.qcloud.tim.uikit.modules.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;

import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.qcloud.tim.uikit.R;
import com.tencent.qcloud.tim.uikit.TUIKit;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.base.TUIKitListenerManager;
import com.tencent.qcloud.tim.uikit.component.TitleBarLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.AbsChatLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatManagerKit;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.input.InputLayout;
import com.tencent.qcloud.tim.uikit.modules.group.apply.GroupApplyInfo;
import com.tencent.qcloud.tim.uikit.modules.group.apply.GroupApplyManagerActivity;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.tencent.qcloud.tim.uikit.modules.group.info.GroupInfoActivity;
import com.tencent.qcloud.tim.uikit.utils.TUIKitConstants;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import java.util.List;


public class ChatLayout extends AbsChatLayout implements GroupChatManagerKit.GroupNotifyHandler {

    private GroupInfo mGroupInfo;
    private GroupChatManagerKit mGroupChatManager;
    private C2CChatManagerKit mC2CChatManager;
    private boolean isGroup = false;

    public ChatLayout(Context context) {
        super(context);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChatInfo(ChatInfo chatInfo) {
        super.setChatInfo(chatInfo);
        mChatInfo = chatInfo;
        if (chatInfo == null) {
            return;
        }

        if (chatInfo.getType() == V2TIMConversation.V2TIM_C2C) {
            isGroup = false;
        } else {
            isGroup = true;
        }

        if (isGroup) {
            mGroupChatManager = GroupChatManagerKit.getInstance();
            mGroupChatManager.setGroupHandler(this);
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setId(chatInfo.getId());
            groupInfo.setChatName(chatInfo.getChatName());
            groupInfo.setGroupType(chatInfo.getGroupType());
            mGroupChatManager.setCurrentChatInfo(groupInfo);
            mGroupInfo = groupInfo;
            mChatInfo = mGroupInfo;
            loadChatMessages(chatInfo.getLocateTimMessage(), chatInfo.getLocateTimMessage() == null ? TUIKitConstants.GET_MESSAGE_FORWARD : TUIKitConstants.GET_MESSAGE_TWO_WAY);
            getConversationLastMessage("group_" + chatInfo.getId());
            loadApplyList();
            getTitleBar().getRightIcon().setImageResource(R.drawable.chat_group);
            getTitleBar().setOnRightClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mGroupInfo != null) {
                        Intent intent = new Intent(getContext(), GroupInfoActivity.class);
                        intent.putExtra(TUIKitConstants.Group.GROUP_ID, mGroupInfo.getId());
                        getContext().startActivity(intent);
                    } else {
                        ToastUtil.toastLongMessage(TUIKit.getAppContext().getString(R.string.wait_tip));
                    }
                }
            });
            mGroupApplyLayout.setOnNoticeClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), GroupApplyManagerActivity.class);
                    intent.putExtra(TUIKitConstants.Group.GROUP_INFO, mGroupInfo);
                    getContext().startActivity(intent);
                }
            });
            TUIKitListenerManager.getInstance().setMessageSender(mGroupChatManager);
        } else {

            getTitleBar().getRightIcon().setImageResource(R.drawable.chat_c2c);
            mC2CChatManager = C2CChatManagerKit.getInstance();
            mC2CChatManager.setCurrentChatInfo(chatInfo);
            loadChatMessages(chatInfo.getLocateTimMessage(), chatInfo.getLocateTimMessage() == null ? TUIKitConstants.GET_MESSAGE_FORWARD : TUIKitConstants.GET_MESSAGE_TWO_WAY);
            getConversationLastMessage("c2c_" + chatInfo.getId());
            TUIKitListenerManager.getInstance().setMessageSender(mC2CChatManager);
        }
    }

    @Override
    public ChatManagerKit getChatManager() {
        if (isGroup) {
            return mGroupChatManager;
        } else {
            return mC2CChatManager;
        }
    }

    private void loadApplyList() {
        mGroupChatManager.getProvider().loadGroupApplies(new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                List<GroupApplyInfo> applies = (List<GroupApplyInfo>) data;
                if (applies != null && applies.size() > 0) {
                    mGroupApplyLayout.getContent().setText(getContext().getString(R.string.group_apply_tips, applies.size()));
                    mGroupApplyLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.toastLongMessage("loadApplyList onError: " + errMsg);
            }
        });
    }

    @Override
    public void onGroupForceExit() {
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).finish();
        }
    }

    @Override
    public void onGroupNameChanged(String newName) {
        getTitleBar().setTitle(newName, TitleBarLayout.POSITION.MIDDLE);
    }

    @Override
    public void onApplied(int size) {
        if (size == 0) {
            mGroupApplyLayout.setVisibility(View.GONE);
        } else {
            mGroupApplyLayout.getContent().setText(getContext().getString(R.string.group_apply_tips, size));
            mGroupApplyLayout.setVisibility(View.VISIBLE);
        }
    }
}

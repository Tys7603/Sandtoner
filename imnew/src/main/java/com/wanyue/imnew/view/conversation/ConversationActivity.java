package com.wanyue.imnew.view.conversation;

import android.view.View;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.ConversationListLayout;
import com.tencent.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.wanyue.imnew.R;
import com.wanyue.common.activity.BaseActivity;
import com.wanyue.imnew.view.chat.ChatActivity;

public class ConversationActivity extends BaseActivity {
    private ConversationLayout mConversationLayout;

    @Override
    public void init() {
        setTabTitle("消息列表");
        mConversationLayout = (ConversationLayout) findViewById(R.id.conversation_layout);
        mConversationLayout.initDefault();
        mConversationLayout.getConversationList().setOnItemClickListener(new ConversationListLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ConversationInfo conversationInfo) {
                if(conversationInfo==null){
                    return;
                }
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setType(conversationInfo.isGroup() ? V2TIMConversation.V2TIM_GROUP : V2TIMConversation.V2TIM_C2C);
                chatInfo.setGroupType(conversationInfo.isGroup() ? conversationInfo.getGroupType() : "");
                chatInfo.setId(conversationInfo.getId());
                chatInfo.setChatName(conversationInfo.getTitle());
                chatInfo.setDraft(conversationInfo.getDraft());
                ChatActivity.forward(mContext,chatInfo);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_conversation;
    }
}
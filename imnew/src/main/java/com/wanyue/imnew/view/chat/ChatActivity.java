package com.wanyue.imnew.view.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.qiniu.android.utils.StringUtils;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.qcloud.tim.uikit.modules.chat.ChatLayout;
import com.tencent.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.tencent.qcloud.tim.uikit.modules.chat.layout.message.MessageLayout;
import com.wanyue.common.CommonAppConfig;
import com.wanyue.common.utils.ResourceUtil;
import com.wanyue.common.utils.ToastUtil;
import com.wanyue.imnew.R;
import com.wanyue.common.Constants;
import com.wanyue.common.activity.BaseActivity;

public class ChatActivity extends BaseActivity {
    private ChatLayout mChatLayout;

    @Override
    public void init() {
        String json=getIntent().getStringExtra(Constants.DATA);
        ChatInfo chatInfo=JSON.parseObject(json,ChatInfo.class);
        setTabTitle(chatInfo.getChatName());
        mChatLayout = (ChatLayout) findViewById(R.id.chat_layout);
        mChatLayout.initDefault();
        mChatLayout.setChatInfo(chatInfo);

        MessageLayout messageLayout = mChatLayout.getMessageLayout();
        messageLayout.setAvatarRadius(360);
        messageLayout.setRightBubble(new ColorDrawable(ResourceUtil.getColor(R.color.global)));
        messageLayout.setLeftChatContentFontColor(Color.BLACK);
        messageLayout.setRightChatContentFontColor(Color.WHITE);


    }

    public static void forward(Context context, String id,String name){
        if(TextUtils.equals(id, CommonAppConfig.getUid())){
           ToastUtil.show("不能与自己发送私信");
           return;
        }


        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setType(V2TIMConversation.V2TIM_C2C);
        chatInfo.setId(id);
        chatInfo.setChatName(name);
        forward(context,chatInfo);
    }

    public static void forward(Context context, ChatInfo chatInfo){
        String json= JSON.toJSONString(chatInfo);
        Intent intent=new Intent(context,ChatActivity.class);
        intent.putExtra(Constants.DATA,json);
        context.startActivity(intent);
    }
    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }


}
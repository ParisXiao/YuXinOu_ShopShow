package com.liren.live.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.liren.live.interf.ListenMessage;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.liren.live.bean.UserBean;
import com.liren.live.utils.UIHelper;
import com.hyphenate.chat.EMMessage;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.adapter.MessageAdapter;
import com.liren.live.base.BaseFragment;
import com.liren.live.bean.PrivateChatUserBean;
import com.liren.live.bean.PrivateMessage;
import com.liren.live.ui.im.PhoneLivePrivateChat;
import com.liren.live.widget.BlackEditText;
import com.liren.live.widget.BlackTextView;

import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;


//个人中心私信发送页面
public class MessageDetailFragment extends BaseFragment implements ListenMessage {
    @BindView(R.id.tv_private_chat_title)
    BlackTextView mTitle;
    @BindView(R.id.et_private_chat_message)
    BlackEditText mMessageInput;
    @BindView(R.id.lv_message)
    ListView mChatMessageListView;
    private List<PrivateMessage> mChats = new ArrayList<>();
    private PrivateChatUserBean mToUser;
    private MessageAdapter mMessageAdapter;
    private UserBean mUser;
    private long lastTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_private_chat_message,null);
        ButterKnife.bind(this,view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {

        mUser = AppContext.getInstance().getLoginUser();
        mToUser = getActivity().getIntent().getParcelableExtra("user");
        mTitle.setText(mToUser.user_nicename);

        try{
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(mToUser.id);
            //指定会话消息未读数清零
            conversation.markAllMessagesAsRead();
        }catch (Exception e){
            e.printStackTrace();
        }

        //获取历史消息
        mChats.addAll(PhoneLivePrivateChat.getUnreadRecord(mUser,mToUser));

        //初始化
        mMessageAdapter = new MessageAdapter(getActivity(),mChats);

        mChatMessageListView.setAdapter(mMessageAdapter);
        mChatMessageListView.setSelection(mChats.size() - 1);

        listenMessage();
    }

    @OnClick({R.id.iv_private_chat_send,R.id.et_private_chat_message,R.id.iv_private_chat_back,R.id.iv_private_chat_user})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //发送私信
            case R.id.iv_private_chat_send:
                sendPrivateChat();
                break;
            case R.id.et_private_chat_message:

                break;
            case R.id.iv_private_chat_back:
                getActivity().finish();
                break;
            case R.id.iv_private_chat_user:
                UIHelper.showHomePageActivity(getActivity(),mToUser.id);
                break;
        }

    }
    //发送私信
    private void sendPrivateChat() {
        //判断是否操作频繁
        if((System.currentTimeMillis() - lastTime) < 1000 && lastTime != 0){
            Toast.makeText(getActivity(),"操作频繁",Toast.LENGTH_SHORT).show();
            return;
        }
        lastTime = System.currentTimeMillis();
        if(mMessageInput.getText().toString().equals("")){
            AppContext.showToast("内容为空");
            return;
        }


        EMMessage emMessage = PhoneLivePrivateChat.sendChatMessage(mMessageInput.getText().toString(),mToUser);

        //更新列表
        updateChatList(PrivateMessage.crateMessage(emMessage,mUser.avatar));
        mMessageInput.setText("");
    }

    private void updateChatList(PrivateMessage message){

        mChats.add(message);

        mMessageAdapter.notifyDataSetChanged();
        mChatMessageListView.setSelection(mMessageAdapter.getCount()-1);
    }

    @Override
    public void onStop() {
        super.onStop();
        unListen();
    }


    private EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(final List<EMMessage> messages) {

            //判断是否是当前回话的消息
            if(messages.get(0).getFrom().trim().equals(String.valueOf(mToUser.id))) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateChatList(PrivateMessage.crateMessage(messages.get(0),mToUser.avatar));
                    }
                });


            }
            //收到消息
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {

        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> messages) {

        }


        @Override
        public void onMessageChanged(EMMessage message, Object change) {

        }
    };

    @Override
    public void listenMessage() {
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    @Override
    public void unListen() {
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }
}
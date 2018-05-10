package com.liren.live.viewpagerfragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.adapter.UserBaseInfoPrivateChatAdapter;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseFragment;
import com.liren.live.bean.PrivateChatUserBean;
import com.liren.live.bean.UserBean;
import com.liren.live.event.Event;
import com.liren.live.utils.TLog;
import com.liren.live.utils.UIHelper;
import com.lzy.okhttputils.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;


public class PrivateChatCorePagerFragment extends BaseFragment {
    private BroadcastReceiver broadCastReceiver;

    //回话列表
    protected ArrayList<PrivateChatUserBean> mPrivateChatListData = new ArrayList<>();
    protected ListView mPrivateListView;
    protected int mPosition = -1;
    protected UserBean mUser;
    protected Map<String, EMConversation> emConversationMap;
    protected Gson mGson = new Gson();
    private UserBaseInfoPrivateChatAdapter mUserBaseInfoPrivateChatAdapter;

    private TextView mTvIgnoreMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_private_core_page, null);

        ButterKnife.bind(this,view);
        initBroadCast();
        initView(view);
        initData();

        return view;
    }

    @Override
    public void initData() {

        mUser = AppContext.getInstance().getLoginUser();
        updatePrivateChatList();
        initConversationList(1);
    }

    @Override
    public void initView(View view) {

        mTvIgnoreMessage = (TextView) view.findViewById(R.id.tv_ignore_message);
        mTvIgnoreMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //所有未读消息数清零
                EMClient.getInstance().chatManager().markAllConversationsAsRead();
                Event.PrivateChatEvent event = new Event.PrivateChatEvent();
                event.action = 1;
                EventBus.getDefault().post(event);
                Toast.makeText(getContext(),"已忽略未读消息",Toast.LENGTH_SHORT).show();
            }
        });


        mPrivateListView = (ListView) view.findViewById(R.id.lv_privatechat);

        mPrivateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                onPrivateChatItemClick(position);
            }
        });

    }

    //回话点击时间
    private void onPrivateChatItemClick(int position) {

        mPosition = position;
        PrivateChatUserBean privateChatUserBean = mPrivateChatListData.get(position);
        //标注私信状态为已读
        mPrivateChatListData.get(position).unreadMessage = false;

        updatePrivateChatList();

        //判断当前页面是直播间弹窗还是单页面
        UIHelper.showPrivateChatMessage(getActivity(),privateChatUserBean);
    }


    //注册监听私信消息广播
    private void initBroadCast() {

        IntentFilter cmdFilter = new IntentFilter("com.liren.live");
        if(broadCastReceiver == null){
            broadCastReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    // TODO Auto-generated method stub
                    onNewMessage((EMMessage)intent.getParcelableExtra("cmd_value"));
                }
            };
        }
        getActivity().registerReceiver(broadCastReceiver,cmdFilter);
    }



    public void onResumeUpdate(){

        if(null == mPrivateChatListData || mPrivateChatListData.size() == 0) return;
        //获取有没有最后一条消息
        try {
            //将该条会话消息未读清零
            EMConversation conversation = EMClient.getInstance()
                    .chatManager()
                    .getConversation(String.valueOf(mPrivateChatListData.get(mPosition).id));
            conversation.markAllMessagesAsRead();
            //设置最后一条消息
            mPrivateChatListData.get(mPosition).lastMessage = ((EMTextMessageBody) conversation.getLastMessage().getBody()).getMessage();

        }catch (Exception e){
            //无最后一条消息
        }
        mPosition = -1;
        updatePrivateChatList();
    }

    //更新回话列表
    protected void updatePrivateChatList(){
        mPrivateListView.setAdapter(mUserBaseInfoPrivateChatAdapter = new UserBaseInfoPrivateChatAdapter(mPrivateChatListData));
    }

    //初始化会话列表
    protected void initConversationList(int action) {
        //获取所有会话消息列表
        emConversationMap = EMClient.getInstance().chatManager().getAllConversations();

        StringBuilder keys = new StringBuilder();
        for(String key : emConversationMap.keySet()){
            keys.append(key + ",");
        }
        if(keys.toString().length() == 0)return;
        String uidList = keys.toString().substring(0,keys.length()-1);

        //获取每个绘画用户的信息和关注状态

        PhoneLiveApi.getMultiBaseInfo(action,mUser.id,uidList,multiBaseInfoCallback);

    }

    protected void fillUI() {
        if(mPrivateChatListData == null){
            return;
        }
        updatePrivateChatList();
    }

    private StringCallback multiBaseInfoCallback = new StringCallback(){

        @Override
        public void onSuccess(String s, Call call, Response response) {
            JSONArray fansJsonArr = ApiUtils.checkIsSuccess(response.body().toString());

            if(null != fansJsonArr){
                TLog.log("[获取会话列表用户信息success]:" + fansJsonArr.toString());
                try {

                    if(fansJsonArr.length() > 0){

                        for(int i =0;i<fansJsonArr.length(); i++){

                            PrivateChatUserBean chatUserBean = mGson.fromJson(fansJsonArr.getString(i), PrivateChatUserBean.class);
                            //获取单条会话信息
                            EMConversation conversation = EMClient.getInstance()
                                    .chatManager()
                                    .getConversation(String.valueOf(chatUserBean.id));

                            try {
                                //设置该会话最后一条信息
                                chatUserBean.lastMessage = (((EMTextMessageBody) conversation.getLastMessage().getBody()).getMessage());
                                //获取该会话是否有未读消息
                                chatUserBean.unreadMessage = (conversation.getUnreadMsgCount() > 0);

                            }catch (Exception e){
                                //无最后一条消息纪录
                            }

                            if(conversation.getUnreadMsgCount() > 0){
                                mPrivateChatListData.add(0,chatUserBean);
                            }else{
                                mPrivateChatListData.add(chatUserBean);
                            }

                        }
                        //填充会话列表
                        fillUI();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    };

    //会话列表中已存在更新该会话最后一条信息
    protected void updateLastMessage(final EMMessage messages) {

        for(int i =0; i < mPrivateChatListData.size(); i++){
            PrivateChatUserBean privateChatUserBean = mPrivateChatListData.get(i);

            //判断当前新消息是那个会话
            if(privateChatUserBean.id.equals(messages.getFrom())){
                privateChatUserBean.lastMessage = ((EMTextMessageBody)(messages.getBody())).getMessage();
                if(i == mPosition){
                    //未读消息
                    privateChatUserBean.unreadMessage = (false);
                }else{
                    privateChatUserBean.unreadMessage = (true);
                }
                //privateChatUserBean.setUnreadMessage(true);
                //将该回话移到第一位
                mPrivateChatListData.remove(i);
                mPrivateChatListData.add(0,privateChatUserBean);
                //mPrivateChatListData.set(i, privateChatUserBean);
                updatePrivateChatList();
                continue;
            }
        }
    }

    //没有在会话列表中,所以添加一个item
    protected void inConversationMapAddItem(final EMMessage messages) {
        emConversationMap = EMClient.getInstance().chatManager().getAllConversations();
        PhoneLiveApi.getPmUserInfo(mUser.id,messages.getFrom(), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                JSONArray res = ApiUtils.checkIsSuccess(response.body().toString());
                if(null != res){
                    PrivateChatUserBean privateChatUserBean = null;
                    try {
                        privateChatUserBean = mGson.fromJson(res.getString(0),PrivateChatUserBean.class);
                        privateChatUserBean.lastMessage = (((EMTextMessageBody)(messages.getBody())).getMessage());
                        privateChatUserBean.unreadMessage = (true);
                        //将该回话移到第一位
                        mPrivateChatListData.add(0,privateChatUserBean);
                        //mPrivateChatListData.add(privateChatUserBean);
                        updatePrivateChatList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.PrivateChatEvent event) {
        if(event.action == 1){

            for(int i = 0; i < mPrivateChatListData.size(); i++){
                PrivateChatUserBean d = mPrivateChatListData.get(i);
                d.unreadMessage = false;
                mPrivateChatListData.set(i,d);
            }
            mUserBaseInfoPrivateChatAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        onResumeUpdate();

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    protected void onNewMessage(final EMMessage message) {
        //收到消息
        addMessage(message);
    }

    private void addMessage(EMMessage message){
        //判断是否在列表中,如果在更新最后一条信息,如果没在添加一条item
        if(!emConversationMap.containsKey(message.getFrom())){
            TLog.log("已关注[不存会话列表]");
            inConversationMapAddItem(message);

        }else{
            if(mPrivateChatListData == null)return;
            TLog.log("已关注[存在会话列表]");
            updateLastMessage(message);

        }
    }

    @Override
    public void onDestroy() {
        //获取是否有未读消息
        super.onDestroy();

        getActivity().unregisterReceiver(broadCastReceiver);
    }
}

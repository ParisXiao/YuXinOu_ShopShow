package com.liren.live.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.liren.live.widget.PhoneLiveChatRow;
import com.liren.live.widget.PhoneLiveChatRowText;
import com.hyphenate.chat.EMMessage;
import com.liren.live.bean.PrivateMessage;

import java.util.List;

//私信
public class MessageAdapter extends BaseAdapter {
    private List<PrivateMessage> mChats;
    private Context context;
    public MessageAdapter(Context context,List<PrivateMessage> mChats) {
        this.context = context;
        this.mChats = mChats;
    }

    @Override
    public int getCount() {
        return mChats.size();
    }

    @Override
    public Object getItem(int position) {
        return mChats.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PrivateMessage e = mChats.get(position);

        convertView = createChatRow(context, e.message, position,e.uHead);
        //缓存的view的message很可能不是当前item的，传入当前message和position更新ui
        ((PhoneLiveChatRow)convertView).setUpView(e.message, position);
        return convertView;
    }

    private View createChatRow(Context context, EMMessage e, int position,String head) {
        PhoneLiveChatRow chatRow = null;
        switch (e.getType()) {
            case TXT:
                chatRow = new PhoneLiveChatRowText(context, e, position, this,head);
                break;

        }
        return chatRow;
    }


}

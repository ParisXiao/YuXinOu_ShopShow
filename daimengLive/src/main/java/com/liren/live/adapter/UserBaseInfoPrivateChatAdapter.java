package com.liren.live.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.bean.PrivateChatUserBean;
import com.liren.live.utils.LiveUtils;
import com.liren.live.utils.SimpleUtils;
import com.liren.live.widget.CircleImageView;

import java.util.ArrayList;


public class UserBaseInfoPrivateChatAdapter extends BaseAdapter {

    protected ArrayList<PrivateChatUserBean> mPrivateChatListData = new ArrayList<>();

    public UserBaseInfoPrivateChatAdapter(ArrayList<PrivateChatUserBean> mPrivateChatListData) {
        this.mPrivateChatListData = mPrivateChatListData;
    }

    @Override
    public int getCount() {
        return mPrivateChatListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mPrivateChatListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = View.inflate(AppContext.getInstance(), R.layout.item_private_chat,null);
            viewHolder = new ViewHolder();
            viewHolder.mUHead = (CircleImageView) convertView.findViewById(R.id.cv_userHead);
            viewHolder.mUSex  = (ImageView) convertView.findViewById(R.id.tv_item_usex);
            viewHolder.mULevel  = (ImageView) convertView.findViewById(R.id.tv_item_ulevel);
            viewHolder.mUNice = (TextView) convertView.findViewById(R.id.tv_item_uname);
            viewHolder.mULastMsg = (TextView) convertView.findViewById(R.id.tv_item_last_msg);
            viewHolder.mUnread = (ImageView) convertView.findViewById(R.id.iv_unread_dot);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PrivateChatUserBean u = mPrivateChatListData.get(position);


        SimpleUtils.loadImageForView(AppContext.getInstance(),viewHolder.mUHead,u.avatar_thumb,0);
        viewHolder.mUSex.setImageResource(LiveUtils.getSexRes(u.sex));
        viewHolder.mULevel.setImageResource(LiveUtils.getLevelRes(u.level));
        viewHolder.mUNice.setText(u.user_nicename);
        viewHolder.mULastMsg.setText(u.lastMessage);
        viewHolder.mUnread.setVisibility(u.unreadMessage ? View.VISIBLE:View.GONE);


        return convertView;
    }



    private class ViewHolder{
        CircleImageView mUHead;
        ImageView mUSex,mULevel,mUnread;
        TextView mUNice,mULastMsg;
    }
}

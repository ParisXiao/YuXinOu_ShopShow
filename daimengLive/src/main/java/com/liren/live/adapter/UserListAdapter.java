package com.liren.live.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liren.live.bean.SimpleUserInfo;
import com.liren.live.R;
import com.liren.live.widget.AvatarView;

import java.util.List;

/**
 * 用户列表adapter
 */
public class UserListAdapter extends BaseQuickAdapter<SimpleUserInfo, BaseViewHolder> {

    public UserListAdapter(List<SimpleUserInfo> data) {
        super(R.layout.item_live_user_list,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SimpleUserInfo item) {
        AvatarView userhead=helper.getView(R.id.av_userHead);
        userhead.setAvatarUrl(item.avatar);
       // SimpleUtils.loadImageForView(AppContext.getInstance(),(ImageView) helper.getView(R.id.av_userHead),item.avatar_thumb,0);
    }

}

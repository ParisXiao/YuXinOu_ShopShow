package com.liren.live.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.bean.SimpleUserInfo;
import com.liren.live.utils.LiveUtils;
import com.liren.live.utils.SimpleUtils;
import com.liren.live.utils.StringUtils;

import java.util.List;

/**
 * Created by weipeng on 2017/4/14.
 */

public class UserInfoListAdapter extends BaseQuickAdapter<SimpleUserInfo, BaseViewHolder> {
    public UserInfoListAdapter(List<SimpleUserInfo> data) {
        super( R.layout.item_attention_fans,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SimpleUserInfo item) {


        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.cv_userHead),item.avatar,0);

        if (item.id.equals(AppContext.getInstance().getLoginUid())){
            helper.setVisible(R.id.iv_item_attention,false);
        }else {
            helper.setVisible(R.id.iv_item_attention,true);
        }
        helper.setImageResource(R.id.tv_item_usex,LiveUtils.getSexRes(item.sex));
        helper.setImageResource(R.id.iv_item_attention,StringUtils.toInt(item.isattention) == 1 ? R.drawable.me_following:R.drawable.me_follow);
        helper.setImageResource(R.id.tv_item_ulevel,LiveUtils.getLevelRes(item.level));
        helper.setText(R.id.tv_item_uname,item.user_nicename);
        helper.setText(R.id.tv_item_usign,item.signature);
    }
}

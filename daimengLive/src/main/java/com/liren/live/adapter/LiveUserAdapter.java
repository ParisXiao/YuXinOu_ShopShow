package com.liren.live.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liren.live.AppContext;
import com.liren.live.bean.LiveJson;
import com.liren.live.utils.SimpleUtils;
import com.liren.live.R;

import java.util.List;

//热门主播
public class LiveUserAdapter extends BaseQuickAdapter<LiveJson,BaseViewHolder> {

    public LiveUserAdapter(List<LiveJson> data) {
        super(R.layout.item_hot_user,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveJson item) {

        //用于平滑加载图片
        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.iv_live_user_pic),item.thumb,0);
        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.iv_live_user_head),item.avatar_thumb,0);
        helper.setText(R.id.tv_live_nick,item.user_nicename);
        helper.setText(R.id.tv_live_local,item.city);
        helper.setText(R.id.tv_hot_room_title,item.title);
        if(!TextUtils.isEmpty(item.title)){
            helper.setVisible(R.id.tv_hot_room_title,true);
        }else{
            helper.setVisible(R.id.tv_hot_room_title,false);
        }
    }

}



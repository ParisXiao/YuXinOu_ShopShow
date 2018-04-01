package com.liren.live.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.bean.LiveJson;
import com.liren.live.utils.SimpleUtils;

import java.util.List;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class HeaderAndFooterAdapter extends BaseQuickAdapter<LiveJson, BaseViewHolder> {

    public int resId;

    public HeaderAndFooterAdapter(List<LiveJson> data,int resId) {

        super(resId,data);

        this.resId = resId;
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveJson item) {

        if(this.resId == R.layout.item_hot_user){
            helper.setText(R.id.tv_live_nick,item.user_nicename);
            helper.setText(R.id.tv_live_local,item.city);
            helper.setText(R.id.tv_live_usernum,item.nums);
            //用于平滑加载图片
            SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.iv_live_user_pic),item.thumb,0);
            SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.iv_live_user_head),item.avatar_thumb,0);

            helper.setVisible(R.id.tv_hot_room_title,!TextUtils.isEmpty(item.title));
            helper.setText(R.id.tv_hot_room_title,item.title);
        }else{

            helper.setText(R.id.item_tv_name,item.user_nicename);
            //helper.setText(R.id.tv_live_usernum,item.nums);
            //用于平滑加载图片
            SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.iv_hot_item_user),item.thumb,0);
        }


    }

}

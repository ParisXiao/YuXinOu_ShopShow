package com.liren.live.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.bean.LiveJson;
import com.liren.live.utils.SimpleUtils;

import java.util.List;

/**
 * Created by liren on 2016/12/23.
 */

public class NewestAdapter extends BaseQuickAdapter<LiveJson, BaseViewHolder> {

    public NewestAdapter(List<LiveJson> data) {
        super(R.layout.item_newest_user, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveJson item) {
        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.iv_newest_item_user),item.thumb,0);
        helper.setText(R.id.item_tv_name,item.user_nicename);
    }
}

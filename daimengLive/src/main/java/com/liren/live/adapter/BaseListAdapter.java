package com.liren.live.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.bean.LiveJson;
import com.liren.live.utils.SimpleUtils;

import java.util.List;


public class BaseListAdapter extends BaseQuickAdapter<LiveJson,BaseViewHolder> {

    public BaseListAdapter(List<LiveJson> data) {
        super(R.layout.item_base_list,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveJson item) {
        helper.setText(R.id.item_tv_name,item.user_nicename);

        //用于平滑加载图片
        SimpleUtils.loadImageForView(AppContext.getInstance(), (ImageView) helper.getView(R.id.iv_item_user),item.thumb,0);
    }
}

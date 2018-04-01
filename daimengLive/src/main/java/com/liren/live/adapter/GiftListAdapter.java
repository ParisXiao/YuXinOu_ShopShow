package com.liren.live.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.bean.GiftBean;
import com.liren.live.utils.SimpleUtils;

import java.util.List;

/**
 * Created by weipeng on 2017/3/29.
 */

public class GiftListAdapter extends BaseQuickAdapter<GiftBean, BaseViewHolder> {

    public GiftListAdapter(List<GiftBean> data) {
        super(R.layout.item_select_gift, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GiftBean item) {

        helper.setText(R.id.tv_show_gift_price,String.valueOf(item.getNeedcoin()));
        helper.setText(R.id.tv_show_gift_experience,"+" + item.getExperience() + "经验值");
        SimpleUtils.loadImageForView(AppContext.context(), (ImageView) helper.getView(R.id.iv_show_gift_img),item.getGifticon(),0);

        if(item.getType() == 1){
            helper.getView(R.id.iv_show_gift_selected).setBackgroundResource(R.drawable.icon_continue_gift);
        }
    }
}

package com.liren.live.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liren.live.R;
import com.liren.live.bean.LiveRecordBean;

import java.util.List;

public class LiveRecordAdapter extends BaseQuickAdapter<LiveRecordBean,BaseViewHolder> {

    public LiveRecordAdapter(List<LiveRecordBean> data) {
        super(R.layout.item_live_record,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveRecordBean item) {
        helper.setText(R.id.tv_item_live_record_num,item.getNums());
        helper.setText(R.id.tv_item_live_record_time,item.getDatetime());
        helper.setText(R.id.tv_item_live_record_title,item.getTitle().equals("")?"无标题":item.getTitle());
    }

}
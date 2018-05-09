package com.liren.live.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liren.live.R;
import com.liren.live.bean.AreaBean;

import java.util.List;

/**
 * Created by JJC on 2018/5/9.
 */

public class CommentAdapter  extends BaseQuickAdapter<AreaBean, BaseViewHolder> {
    public CommentAdapter( @Nullable List<AreaBean> data) {
        super(R.layout.item_comment_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AreaBean item) {

    }
}

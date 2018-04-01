package com.liren.live.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.bean.OrderBean;
import com.liren.live.utils.LiveUtils;
import com.liren.live.utils.SimpleUtils;
import com.liren.live.widget.BlackTextView;
import com.liren.live.widget.CircleImageView;
import com.liren.live.R;

import java.util.ArrayList;

/**
 * 贡献榜
 */
public class OrderAdapter extends BaseAdapter {
    private ArrayList<OrderBean> mOrderList = new ArrayList<>();
    private Context mContext;

    public OrderAdapter(ArrayList<OrderBean> mOrderList, Context mContext) {
        this.mOrderList = mOrderList;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {

        return mOrderList.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){

            convertView = View.inflate(mContext,R.layout.item_order_user,null);
            viewHolder = new ViewHolder();
            viewHolder.mOrderUhead = (CircleImageView) convertView.findViewById(R.id.ci_order_item_u_head);
            viewHolder.mOrderULevel = (ImageView) convertView.findViewById(R.id.tv_order_item_u_level);
            viewHolder.mOrderUSex = (ImageView) convertView.findViewById(R.id.iv_order_item_u_sex);
            viewHolder.mOrderUname = (BlackTextView) convertView.findViewById(R.id.tv_order_item_u_name);
            viewHolder.mOrderUGx = (BlackTextView) convertView.findViewById(R.id.tv_order_item_u_gx);
            viewHolder.mOrderNo = (BlackTextView) convertView.findViewById(R.id.tv_order_item_u_no);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        OrderBean data = mOrderList.get(position);

        SimpleUtils.loadImageForView(AppContext.getInstance(),viewHolder.mOrderUhead,data.getUserinfo().avatar_thumb,R.drawable.null_blacklist);

        viewHolder.mOrderULevel.setImageResource(LiveUtils.getLevelRes(data.getUserinfo().level));
        viewHolder.mOrderUSex.setImageResource(LiveUtils.getSexRes(data.getUserinfo().sex));
        viewHolder.mOrderUname.setText(data.getUserinfo().user_nicename);
        viewHolder.mOrderUGx.setText("贡献:" + data.getTotal() + AppConfig.TICK_NAME);

        viewHolder.mOrderNo.setText("  No." + (position+1));
        return convertView;
    }

    class ViewHolder{
         CircleImageView mOrderUhead;
         ImageView mOrderUSex,mOrderULevel;
         BlackTextView mOrderUname,mOrderUGx,mOrderNo;
    }
}
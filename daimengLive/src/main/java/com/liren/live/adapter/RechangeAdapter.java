package com.liren.live.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.bean.RechargeBean;
import com.liren.live.utils.StringUtils;
import com.liren.live.widget.BlackTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by liren on 2017/1/11.
 */

public class RechangeAdapter extends BaseAdapter {

    private List<RechargeBean> rechanList = new ArrayList<>();

    public RechangeAdapter(List<RechargeBean> rechanList) {
        this.rechanList = rechanList;
    }

    @Override
    public int getCount() {
        return rechanList.size();
    }

    @Override
    public Object getItem(int position) {
        return rechanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RechargeBean rechargeBean = rechanList.get(position);
        ViewHolder holder;
        if(convertView == null){
            convertView = View.inflate(AppContext.getInstance(),R.layout.item_select_num,null);
            holder = new ViewHolder();
            holder.mDiamondsNum = (BlackTextView) convertView.findViewById(R.id.tv_diamondsnum);
            holder.mPrieExplain = (BlackTextView) convertView.findViewById(R.id.tv_price_explain);
            holder.mPriceText = (BlackTextView) convertView.findViewById(R.id.bt_price_text);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mDiamondsNum.setText(rechargeBean.coin + AppConfig.CURRENCY_NAME);
        if(StringUtils.toInt(rechargeBean.give) > 0){
            holder.mPrieExplain.setVisibility(View.VISIBLE);
            holder.mPrieExplain.setText(String.format(Locale.CHINA,"赠送%s",rechargeBean.give + AppConfig.CURRENCY_NAME));
        }else{
            holder.mPrieExplain.setVisibility(View.GONE);
        }

        holder.mPriceText.setText(rechargeBean.money);
        return convertView;
    }
    private class ViewHolder{
        BlackTextView mDiamondsNum,mPrieExplain;
        BlackTextView mPriceText;
    }
}
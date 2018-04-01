package com.liren.live.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liren.live.AppConfig;
import com.liren.live.R;
import com.liren.live.bean.RechargeBean;
import com.liren.live.utils.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * Created by weipeng on 2017/3/26.
 */

public class DiamondsAdapter extends BaseQuickAdapter<RechargeBean,BaseViewHolder> {

    public DiamondsAdapter(List<RechargeBean> data) {

        super(R.layout.item_select_num,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RechargeBean rechargeBean) {

        helper.setText(R.id.tv_diamondsnum,rechargeBean.coin + AppConfig.CURRENCY_NAME);
        if(StringUtils.toInt(rechargeBean.give) > 0){
            helper.setVisible(R.id.tv_price_explain,true);
            helper.setText(R.id.tv_price_explain,String.format(Locale.CHINA,"赠送%s",rechargeBean.give + AppConfig.CURRENCY_NAME));
        }else{
            helper.setVisible(R.id.tv_price_explain,false);
        }

        helper.setText(R.id.bt_price_text,rechargeBean.money);
    }
}

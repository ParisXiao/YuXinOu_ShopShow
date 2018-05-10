package com.liren.live.wxpay;

import android.app.Activity;

import com.liren.live.AppContext;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.lzy.okhttputils.callback.StringCallback;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/4/14.
 */
public class WChatPay {
    IWXAPI msgApi;

    private Activity context;

    public WChatPay(Activity context) {
        this.context = context;
        // 将该app注册到微信
       // msgApi = WXAPIFactory.createWXAPI(context,null);
       // msgApi.registerApp(AppConfig.GLOBAL_WX_KEY);
    }

    /**
     * @dw 初始化微信支付
     * @param price 价格
     * @param num 数量
     * */
    public void initPay(String price,String orderid, String num,String changeid) {
        PhoneLiveApi.wxPay(AppContext.getInstance().getLoginUid(),orderid,changeid
                ,price,num,new StringCallback(){

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONArray res = ApiUtils.checkIsSuccess(s);
                        if(null == res) return;
                    }

                });
    }

    private void callWxPay(JSONArray res) {
        try {
            JSONObject signInfo = res.getJSONObject(0);
            PayReq req = new PayReq();
            req.appId        = signInfo.getString("appid");
            req.partnerId    = signInfo.getString("partnerid");
            req.prepayId     = signInfo.getString("prepayid");//预支付会话ID
            req.packageValue = "Sign=WXPay";
            req.nonceStr     = signInfo.getString("noncestr");
            req.timeStamp    = signInfo.getString("timestamp");
            req.sign         = signInfo.getString("sign");
            if(msgApi.sendReq(req)){
                AppContext.showToast("微信支付");
            }else{
                AppContext.showToast("请查看您是否安装微信");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

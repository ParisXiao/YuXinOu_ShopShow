package com.liren.live.alipay;

import android.util.Log;
import android.widget.Toast;

import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.ui.UserDiamondsActivity;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017-11-06.
 */

public class AliPays {
    public static final String TAG = "alipay-sdk";
    private UserDiamondsActivity mPayActivity;
    public static final String RSA2_PRIVATE = "";
    //public String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    public AliPays(UserDiamondsActivity payActivity) {
        this.mPayActivity = payActivity;
    }

    public void initPay(String money,String orderid, String num, String changeid) {

        final String subject = num + AppConfig.CURRENCY_NAME;
        final String body = num + AppConfig.CURRENCY_NAME;
        final String total_fee = money;
        String uid = AppContext.getInstance().getLoginUid();
        //服务器异步通知页面路径,需要自己定义  参数 notify_url，如果商户没设定，则不会进行该操作
        final String url = AppConfig.AP_LI_PAY_NOTIFY_URL;
        //获取订单号码


        //请求订单号码
        PhoneLiveApi.getAliPayOrderNum(uid,orderid, changeid, num, money, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                JSONArray res = ApiUtils.checkIsSuccess(s);
                if (null != res) {
                    try {
                        JSONObject data = res.getJSONObject(0);
                        String orderId = data.getString("orderid");
                        Log.d("orderid", orderId);
                        //    payV2(orderId,body,total_fee,url);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(mPayActivity, "支付失败", Toast.LENGTH_SHORT).show();
                }

            }

        });

    }
}

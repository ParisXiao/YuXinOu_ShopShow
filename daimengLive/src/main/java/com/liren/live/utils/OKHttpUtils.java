package com.liren.live.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018-03-07.
 */

public class OKHttpUtils {
    private static final String TAG = OKHttpUtils.class.getSimpleName();
    public static final MediaType JSON = MediaType.parse("application/json");

    /**
     * 只有登录和注册调用，其他的用下面那个调用
     * @param context
     * @param Url
     * @param key
     * @param sign 注意
     * @param vally
     * @return
     */
    public static String initHttpData(Context context, String Url,String sign, String[] key, Map<String, String> vally) {
        try {
            JSONObject mJsonData = new JSONObject();
            String json = "{";
            for (String s : key) {
                mJsonData.put(s, vally.get(s));
            }
            String Key = "rj.bQ{]naqPZt}g,O!fE";
            String Data = mJsonData.toString();
            Log.d(TAG, "Data : " + Data);
            JSONObject mJson = new JSONObject();
            mJson.put("platform", "APP");
            mJson.put("sign",Key+sign+Key);
            mJson.put("data", mJsonData);

            OkHttpClient client = new OkHttpClient();
            client.newBuilder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);
            try {
                RequestBody body = RequestBody.create(JSON, new String( Base64Utils.getBase64(mJson.toString())));
                Request request = new Request.Builder()
                        .url(Url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                Log.d(TAG, "json:" + mJson.toString());
                Log.d(TAG, "body:" + new String(Base64Utils.getBase64(mJson.toString()) ));
                Log.d(TAG, "response:" + response);

                if (response.isSuccessful()) {
                    String result= response.body().string();
                    String resultBase64 = Base64Utils.getFromBase64(result);
                    Log.d(TAG, "result:" + result);
                    Log.d(TAG, "resultBase64:" +resultBase64);
                    return resultBase64;
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        } catch (Exception e) {

        }
        return null;
    }
    /**
     * 接口调用
     *  @param context 上下文
     * @param key     参数 key集合
     * @param vally   参数key对应数据
     */
    public static String postData(Context context, String Url,String sign, String[] key, Map<String, String> vally) {
        try {
            JSONObject mJsonData = new JSONObject();
            String json = "{";
            for (String s : key) {
                mJsonData.put(s, vally.get(s));
            }
            String Key = "rj.bQ{]naqPZt}g,O!fE";
            String Data = mJsonData.toString();
            Log.d(TAG, "Data : " + Data);
            JSONObject mJson = new JSONObject();
            mJson.put("platform", "APP");
            mJson.put("sign", Key+sign+Key);
            mJson.put("data", mJsonData);

            OkHttpClient client = new OkHttpClient();
            client.newBuilder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);
            try {
                RequestBody body = RequestBody.create(JSON, new String( Base64Utils.getBase64(mJson.toString())));
                Request request = new Request.Builder()
                        .url(Url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                Log.d(TAG, "json:" + mJson.toString());
                Log.d(TAG, "body:" + new String(Base64Utils.getBase64(mJson.toString()) ));
                Log.d(TAG, "response:" + response);

                if (response.isSuccessful()) {
                    String result= response.body().string();
                    String resultBase64 = Base64Utils.getFromBase64(result);
                    Log.d(TAG, "result:" + result);
                    Log.d(TAG, "resultBase64:" +resultBase64);
                    return resultBase64;
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        } catch (Exception e) {

        }
        return null;
    }
    /**
    /**
     * 判断网络状态
     *
     * @param context
     * @return
     */
    public static boolean isConllection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}

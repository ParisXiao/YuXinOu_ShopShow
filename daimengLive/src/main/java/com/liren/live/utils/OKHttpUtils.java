package com.liren.live.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
     *
     * @param context
     * @param Url
     * @param key
     * @param sign    注意
     * @param vally
     * @return
     */
    public static String initHttpData(Context context, String Url, String sign, String[] key, Map<String, String> vally) {
        try {
            JSONObject mJsonData = new JSONObject();
            String json = "{";
            for (String s : key) {
                mJsonData.put(s, vally.get(s));
            }
            String Key = "rj.bQ{]naqPZt}g,O!fE";
            String Data = mJsonData.toString();
            JSONObject mJson = new JSONObject();
            mJson.put("platform", "APP");
            mJson.put("sign", MD5Util.getMD5(Key + sign + Key));
            Log.d(TAG, "sign1:" + sign);
            Log.d(TAG, "sign:" + MD5Util.getMD5(Key + sign + Key));
            mJson.put("data", mJsonData);
            OkHttpClient client = new OkHttpClient();
            client.newBuilder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);
            try {

                RequestBody body = RequestBody.create(JSON, new String(mJson.toString()));
                Request request = new Request.Builder()
                        .url(Url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                Log.d(TAG, "body:" + new String(mJson.toString()));
                Log.d(TAG, "response:" + response);

                if (response.isSuccessful()) {
                    String result = response.body().string();
//                    String resultBase64 = Base64Utils.getFromBase64(result);
                    Log.d(TAG, "result:" + result);
//                    Log.d(TAG, "resultBase64:" +resultBase64);
                    return result;
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                return "";
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 接口调用
     *
     * @param context 上下文
     * @param key     参数 key集合
     * @param vally   参数key对应数据
     */
    public static String postData(Context context, String Url, String token, String[] key, Map<String, String> vally) {
        String sign="";
        try {
            JSONObject mJsonData = new JSONObject();
            String json = "{";
            for (String s : key) {
                mJsonData.put(s, vally.get(s));
            }
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(vally.entrySet());

            //排序方法
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    //return (o2.getValue() - o1.getValue());
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

//            //排序后
//            for (int i = 0; i < infoIds.size(); i++) {
//                String id = infoIds.get(i).toString();
//                System.out.println(id);
//            }

            //排序后
            for (Map.Entry<String, String> m : infoIds) {
                if (!TextUtils.isEmpty(m.getValue())&&!m.getKey().equals("VideoImagePath")) {
                    sign += m.getKey() + m.getValue();
                }
            }
            Log.d(TAG, ":" + sign);
            String Key = "rj.bQ{]naqPZt}g,O!fE";
            String Data = mJsonData.toString();
            Log.d(TAG, "Data : " + Data);
            JSONObject mJson = new JSONObject();
            mJson.put("platform", "APP");
            mJson.put("token", token);
            mJson.put("sign", MD5Util.getMD5(Key + sign + Key));
            Log.d(TAG, "sign:" + MD5Util.getMD5(Key + sign + Key));
            mJson.put("data", mJsonData);


            OkHttpClient client = new OkHttpClient();
            client.newBuilder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS);
            try {
                RequestBody body = RequestBody.create(JSON, new String(mJson.toString()));
                Request request = new Request.Builder()
                        .url(Url)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                Log.d(TAG, "body:" + new String(mJson.toString()));
                Log.d(TAG, "response:" + response);

                if (response.isSuccessful()) {
                    String result = response.body().string();
//                    String resultBase64 = Base64Utils.getFromBase64(result);
                    Log.d(TAG, "result:" + result);
                    return result;
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                return "";
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * /**
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

package com.liren.live.ui;

import android.content.DialogInterface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liren.live.AppContext;
import com.liren.live.adapter.DiamondsAdapter;
import com.liren.live.alipay.AliPays;
import com.liren.live.utils.DialogHelp;
import com.liren.live.utils.GenerateSequenceUtil;
import com.liren.live.utils.MD5;
import com.liren.live.utils.SharedPreUtil;
import com.google.gson.Gson;
import com.liren.live.AppConfig;
import com.liren.live.R;
import com.liren.live.alipay.Keys;
import com.liren.live.bean.RechargeJson;
import com.liren.live.ui.customviews.ActivityTitle;
import com.liren.live.utils.StringUtils;
import com.liren.live.utils.UIHelper;
import com.liren.live.wxpay.WChatPay;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseActivity;
import com.liren.live.bean.RechargeBean;
import com.liren.live.widget.BlackTextView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 我的钻石
 */
public class UserDiamondsActivity extends BaseActivity {

    @BindView(R.id.lv_select_num_list)
    RecyclerView mRecycleView;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    private List<RechargeBean> mRechargeList = new ArrayList<>();

    private final int WX_PAY    = 1;
    private final int ALI_PAY   = 2;

    private int PAY_MODE = WX_PAY;

    private BlackTextView mTvCoin;
    private View mHeadView;

    private WChatPay mWChatPay;
    private AliPays mAliPayUtils;

    private DiamondsAdapter mRechangeAdapter;
    private RechargeJson mRechargeJson;
    private int selectPosition = 1;
    //private View mFooterView;
     private  String IpAddress="";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_diamonds;
    }

    @Override
    public void initView() {

        mActivityTitle.setTitle("我的"+ AppConfig.CURRENCY_NAME);
       mActivityTitle.setBackgroundColor(getResources().getColor(R.color.white));
        mHeadView = getLayoutInflater().inflate(R.layout.view_diamonds_head,null);
        //mFooterView = getLayoutInflater().inflate(R.layout.view_diamonds_footer,null);
        mTvCoin     = (BlackTextView) mHeadView.findViewById(R.id.tv_coin);

        mRecycleView.setLayoutManager(new GridLayoutManager(this,2));
        mRechangeAdapter = new DiamondsAdapter(mRechargeList);

        //加头部
        mRechangeAdapter.addHeaderView(mHeadView);
        //加尾部
        //mRechangeAdapter.addFooterView(mFooterView);

        mRechangeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                selectPosition = position;

                DialogHelp.getSelectDialog(UserDiamondsActivity.this, new String[]{"支付宝"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(i == 0){
                            PAY_MODE = ALI_PAY;
                        }else if(i == 1){
                            PAY_MODE = WX_PAY;
                        }

                        actionPay(String.valueOf(mRechargeList.get(selectPosition).money), mRechargeList.get(selectPosition).coin
                                ,mRechargeList.get(selectPosition).id);
                    }
                }).show();
            }
        });

        mRecycleView.setAdapter(mRechangeAdapter);

        //点击返回
        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    @Override
    public void initData() {
           requestData();

        mAliPayUtils = new AliPays(this);
        mWChatPay    = new WChatPay(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                IpAddress=getNetIp();
            }
        }).start();

    }

    //支付
    private void actionPay(String money, String num,String changeid) {

        if (PAY_MODE == ALI_PAY) {
            String key="a6a41c6ab871463caf60de9e4f569f57";
            String getway="http://pay.tuan0818.com/ChargeBank.aspx";
            String parter="1311";
            String type="992";
            String value=money;
            String orderid=getUserID()+"_"+ GenerateSequenceUtil.generateSequenceNo();
            String callbackurl=AppConfig.MAIN_URL+"/zlzhifu/callback/pay_bank_callback.php";
            String  refbackurl=AppConfig.MAIN_URL+"/zlzhifu/callback/okweb.php";
            String  ip=IpAddress;
            String  sign= MD5.getMD5("parter="+parter+"&type="+type+"&value="+value+"&orderid="+orderid+"&callbackurl="+callbackurl+key);
            String payurl=getway+"?parter="+parter+"&type="+type+"&value="+value+"&orderid="+orderid+"&callbackurl="+callbackurl+"&hrefbackurl="+refbackurl+"&payerIp ="+ip
                    +"&attach="+num+"&sign="+sign;
           // Log.d("payurl",payurl);
           UIHelper.showWebView(UserDiamondsActivity.this,
                  payurl,"zhifubao");
        } else {
           String key="a6a41c6ab871463caf60de9e4f569f57";
            String getway="http://pay.tuan0818.com/ChargeBank.aspx";
            String parter="1311";
            String type="1004";
            String value=money;
            String orderid=getUserID()+"_"+ GenerateSequenceUtil.generateSequenceNo();
            String callbackurl=AppConfig.MAIN_URL+"/zlzhifu/callback/pay_bank_callback.php";
            String  refbackurl=AppConfig.MAIN_URL+"/zlzhifu/callback/okweb.php";
            String  ip=IpAddress;
            String  sign=MD5.getMD5("parter="+parter+"&type="+type+"&value="+value+"&orderid="+orderid+"&callbackurl="+callbackurl+key);
            String payurl=getway+"?parter="+parter+"&type="+type+"&value="+value+"&orderid="+orderid+"&callbackurl="+callbackurl+"&hrefbackurl="+refbackurl+"&payerIp ="+ip
                    +"&attach="+num+"&sign="+sign;
           // String payurl=AppConfig.MAIN_URL+"/zlzhifu/weixing.php";
            UIHelper.showWebView(UserDiamondsActivity.this,
                    payurl,"weichat");

        }
    }
    //检查支付配置
    private boolean checkPayMode(){

        if(PAY_MODE == ALI_PAY){
            if(mRechargeJson.aliapp_switch.equals("1")){
                return true;
            }else{

                AppContext.showToast("支付宝未开启",0);
                return false;
            }
        }else if(PAY_MODE == WX_PAY){
            if(mRechargeJson.wx_switch.equals("1")){
                return true;
            }else{

                AppContext.showToast("微信未开启",0);
                return false;
            }
        }

        return false;

    }


    //获取数据
    private void requestData() {

        /*if(!TextUtils.isEmpty(SharedPreUtil.getString(this,"requestBalance_temp"))){

            fillUI(SharedPreUtil.getString(this,"requestBalance_temp"));
            return;

        }*/

        PhoneLiveApi.requestBalance(getUserID(),getUserToken(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray array = ApiUtils.checkIsSuccess(response);

                if(array != null){

                    try {
                        fillUI(array.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }
    public static String getNetIp() {
        URL infoUrl = null;
        InputStream inStream = null;
        String ipLine = "";
        HttpURLConnection httpConnection = null;
        try {

            infoUrl = new URL("http://pv.sohu.com/cityjson?ie=utf-8");
            URLConnection connection = infoUrl.openConnection();
            httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inStream, "utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null){
                    strber.append(line + "\n");
                }
                Pattern pattern = Pattern
                        .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
                Matcher matcher = pattern.matcher(strber.toString());
                if (matcher.find()) {
                    ipLine = matcher.group();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inStream.close();
                httpConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return ipLine;
    }
    private void fillUI(String data) {

        mRechargeJson = new Gson().fromJson(data,RechargeJson.class);
        mRechargeList.clear();
        mRechargeList.addAll(mRechargeJson.rules);
        mRechangeAdapter.notifyDataSetChanged();
        mTvCoin.setText(mRechargeJson.coin);

        //微信支付appid
        AppConfig.GLOBAL_WX_KEY = mRechargeJson.wx_appid;

        //支付宝
        Keys.DEFAULT_PARTNER    = mRechargeJson.aliapp_partner;
        Keys.DEFAULT_SELLER     = mRechargeJson.aliapp_seller_id;
        Keys.PRIVATE            = mRechargeJson.aliapp_key_android;
        SharedPreUtil.put(this,"requestBalance_temp",data);

    }

    @Override
    public void onClick(View v) {


    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    //充值结果
    public void rechargeResult(boolean isOk, String rechargeMoney) {
        if(isOk){
            mTvCoin.setText(String.valueOf(StringUtils.toInt(mTvCoin.getText().toString()) +
                    StringUtils.toInt(rechargeMoney)));
        }
    }

}

package com.liren.live.ui;

import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseActivity;
import com.liren.live.bean.ProfitBean;
import com.liren.live.ui.customviews.ActivityTitle;
import com.liren.live.utils.DialogHelp;
import com.liren.live.utils.UIHelper;
import com.liren.live.widget.BlackTextView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 收益
 */
public class UserProfitActivity extends BaseActivity {

    @BindView(R.id.tv_votes)
    BlackTextView mVotes;
    @BindView(R.id.tv_profit_canwithdraw)
    BlackTextView mCanwithDraw;
    @BindView(R.id.tv_profit_withdraw)
    BlackTextView mWithDraw;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;
    private ProfitBean mProfitBean;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_profit;
    }

    @Override
    public void initView() {
        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ((TextView)findViewById(R.id.tv_profit_tick_name)).setText(AppConfig.TICK_NAME);
    }

    @Override
    public void initData() {

    }

    private void  requestData() {

        PhoneLiveApi.getWithdraw(getUserID(),getUserToken(), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                JSONArray res = ApiUtils.checkIsSuccess(s);

                if(null != res){
                    try {
                        mProfitBean = new Gson().fromJson(res.getString(0),ProfitBean.class);
                        fillUI();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        });
    }

    private void fillUI() {
        mCanwithDraw.setText(mProfitBean.total);
        mWithDraw.setText(mProfitBean.todaycash);
        mVotes.setText(mProfitBean.votes);
    }



    @OnClick({R.id.btn_profit_cash,R.id.tv_common_problem})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_profit_cash:

                //UIHelper.showRequestCashActivity(UserProfitActivity.this);
                showWaitDialog2("正在提交信息",false);
                PhoneLiveApi.requestCash(getUserID(),getUserToken(),"",
                        new StringCallback(){

                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                hideWaitDialog();
                                JSONArray res = ApiUtils.checkIsSuccess(s);
                                if(null != res){

                                    try {
                                        DialogHelp.getMessageDialog(UserProfitActivity.this,res.getJSONObject(0).getString("msg"))
                                                .create()
                                                .show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    AppContext.showToast("接口请求失败");
                                }
                            }

                        });
                break;

            //常见问题
            case R.id.tv_common_problem:
                String model = android.os.Build.MODEL;
                String release = android.os.Build.VERSION.RELEASE;
                UIHelper.showWebView(this, AppConfig.MAIN_URL + "/index.php?g=portal&m=page&a=newslist&uid="
                        + getUserID() + "&version=" + release + "&model=" + model,"");
                break;
        }


    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    public void onResume() {

        super.onResume();
        requestData();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getWithdraw");
    }
}

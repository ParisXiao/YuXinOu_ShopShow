package com.liren.live.ui;

import android.view.View;

import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseActivity;
import com.liren.live.ui.customviews.ActivityTitle;
import com.liren.live.utils.DialogHelp;
import com.liren.live.widget.BlackButton;
import com.liren.live.widget.BlackEditText;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class RequestCashActivity extends BaseActivity {


    @BindView(R.id.et_cash_num)
    BlackEditText etCashNum;

    @BindView(R.id.btn_request_cash)
    BlackButton btnRequestCash;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_request_cash;
    }

    @Override
    public void initView() {
        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.btn_request_cash)
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_request_cash:
                if(etCashNum.getText().toString().equals("")) {
                    showToast2("请输入提现金额");
                    return;
                }


                showWaitDialog2("正在提交信息",false);
                PhoneLiveApi.requestCash(getUserID(),getUserToken(),etCashNum.getText().toString(),
                        new StringCallback(){

                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                hideWaitDialog();
                                JSONArray res = ApiUtils.checkIsSuccess(response.body().toString());
                                if(null != res){

                                    try {
                                        DialogHelp.getMessageDialog(RequestCashActivity.this,res.getJSONObject(0).getString("msg"))
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

            default:
                break;
        }


    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("requestCash");
    }
}

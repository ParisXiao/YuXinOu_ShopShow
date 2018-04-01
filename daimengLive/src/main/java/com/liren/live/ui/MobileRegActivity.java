package com.liren.live.ui;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hyphenate.util.NetUtils;
import com.liren.live.AppContext;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.base.BaseActivity;
import com.liren.live.bean.UserBean;
import com.liren.live.ui.customviews.ActivityTitle;
import com.liren.live.utils.DialogHelp;
import com.liren.live.utils.LoginUtils;
import com.liren.live.utils.SimpleUtils;
import com.liren.live.utils.TDevice;
import com.google.gson.Gson;
import com.liren.live.R;
import com.liren.live.api.remote.PhoneLiveApi;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 *手机登陆
 */
public class MobileRegActivity extends BaseActivity {
    @BindView(R.id.et_loginphone)
    EditText mEtUserPhone;
    @BindView(R.id.et_logincode)
    EditText mEtCode;
    @BindView(R.id.btn_phone_login_send_code)
    TextView mBtnSendCode;

    @BindView(R.id.et_password)
    EditText mEtUserPassword;
    @BindView(R.id.et_secondPassword)
    EditText mEtSecondPassword;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    private String mUserName = "";


    @Override
    protected int getLayoutId() {
        return R.layout.activity_reg;
    }

    @Override
    public void initView() {
        mBtnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
            }
        });

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {
        setActionBarTitle("用户注册");
    }


    private void sendCode() {

        mUserName = mEtUserPhone.getText().toString();

        if(!TextUtils.isEmpty(mUserName) && mUserName.length() == 11) {

            if(!NetUtils.hasNetwork(MobileRegActivity.this)){
                AppContext.showToast("请检查网络设置",0);
                return;
            }

            requestGetMessageCode();
            SimpleUtils.startTimer(new WeakReference<TextView>(mBtnSendCode),"发送验证码",60,1);
        }
        else{
            AppContext.showToast(getString(R.string.plase_check_you_num_is_correct),0);
        }

    }

    //获取验证码
    private void requestGetMessageCode() {
        PhoneLiveApi.getMessageCode(mUserName, "Login.getCode", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(res != null){

                    AppContext.showToast(getString(R.string.codehasbeensend),0);
                }
            }
        });
    }



    @OnClick(R.id.btn_doReg)
    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btn_doReg) {

            if (prepareForReg()) {
                return;
            }
            mUserName = mEtUserPhone.getText().toString();
            String mCode = mEtCode.getText().toString();
            String mPassword=  mEtUserPassword.getText().toString();
            String mSecondPassword= mEtSecondPassword.getText().toString();
            showWaitDialog(R.string.loading);
            PhoneLiveApi.reg(mUserName, mPassword,mSecondPassword, mCode, callback);
        }


    }
    //注册回调
    private final StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e,int id) {
            AppContext.showToast("网络请求出错!");
        }

        @Override
        public void onResponse(String s,int id) {

           hideWaitDialog();
           JSONArray requestRes = ApiUtils.checkIsSuccess(s);
           if(requestRes != null){
               Gson gson = new Gson();
               try {
                   UserBean user = gson.fromJson(requestRes.getString(0), UserBean.class);
                   //保存用户信息
                   AppContext.getInstance().saveUserInfo(user);

                   LoginUtils.getInstance().OtherInit(MobileRegActivity.this);
               } catch (JSONException e) {
                   e.printStackTrace();
               }

           }
        }
    };
    private boolean prepareForReg() {
        if (!TDevice.hasInternet()) {

            DialogHelp.getMessageDialog(this,getResources().getString(R.string.tip_no_internet))
                    .create().show();

            return true;
        }
        if (mEtUserPhone.length() == 0) {
            mEtUserPhone.setError("请输入手机号码/用户名");
            mEtUserPhone.requestFocus();
            return true;
        }

        if (mEtCode.length() == 0) {
            mEtCode.setError("请输入验证码");
            mEtCode.requestFocus();
            return true;
        }

        if (mEtUserPassword.length() == 0) {
            mEtUserPassword.setError("请输入密码");
            mEtUserPassword.requestFocus();
            return true;
        }

        if (!mEtSecondPassword.getText().toString().equals(mEtUserPassword.getText().toString())) {
            mEtSecondPassword.setText("");
            mEtSecondPassword.setError("密码不一致，请重新输入");
            mEtSecondPassword.requestFocus();
            return true;
        }

        return false;
    }


    @Override
    protected boolean hasActionBar() {
        return false;
    }
}

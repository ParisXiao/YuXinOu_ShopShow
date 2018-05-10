package com.liren.live.ui;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.util.NetUtils;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseActivity;
import com.liren.live.ui.customviews.ActivityTitle;
import com.liren.live.utils.DialogHelp;
import com.liren.live.utils.SimpleUtils;
import com.liren.live.utils.TDevice;
import com.liren.live.widget.BlackEditText;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 *手机登陆
 */
public class MobileFindPassActivity extends BaseActivity {
    @BindView(R.id.et_loginphone)
    BlackEditText mEtUserPhone;

    @BindView(R.id.et_logincode)
    BlackEditText mEtCode;

    @BindView(R.id.btn_phone_login_send_code)
    TextView mBtnSendCode;

    @BindView(R.id.et_password)
    BlackEditText mEtUserPassword;

    @BindView(R.id.et_secondPassword)
    BlackEditText mEtSecondPassword;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    private String mUserName = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_pass;
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

    private void sendCode() {

        mUserName = mEtUserPhone.getText().toString();
        if(!mUserName.equals("") && mUserName.length() == 11) {


            if(!NetUtils.hasNetwork(MobileFindPassActivity.this)){
                AppContext.showToast("请检查网络设置",0);
                return;
            }
            PhoneLiveApi.getMessageCode(mUserName, "Login.getForgetCode", new StringCallback() {
                @Override
                public void onSuccess(String s, Call call, Response response) {
                    JSONArray res = ApiUtils.checkIsSuccess(s);
                    if(res != null){
                        AppContext.showToast(getString(R.string.codehasbeensend),0);
                    }
                }

            });

            SimpleUtils.startTimer(new WeakReference<TextView>(mBtnSendCode),"发送验证码",60,1);
        } else{
            AppContext.showToast(getString(R.string.plase_check_you_num_is_correct),0);
        }

    }


    @Override
    public void initData() {

    }

    @OnClick(R.id.btn_doResetPassword)
    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btn_doResetPassword) {

            if (prepareForFindPass()) {
                return;
            }
            mUserName = mEtUserPhone.getText().toString();
            String mCode = mEtCode.getText().toString();
            String mPassword=  mEtUserPassword.getText().toString();
            String mSecondPassword= mEtSecondPassword.getText().toString();
            showWaitDialog(R.string.loading);
            PhoneLiveApi.findPass(mUserName, mPassword,mSecondPassword, mCode, callback);
        }


    }
    //注册回调
    private final StringCallback callback = new StringCallback() {
        @Override
        public void onSuccess(String s, Call call, Response response) {
            hideWaitDialog();

            JSONArray res = ApiUtils.checkIsSuccess(s);

            if(res != null){

                AlertDialog alertDialog = DialogHelp.getMessageDialog(MobileFindPassActivity.this, "密码修改成功", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setTitle("提示").create();

                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }

        }

    };

    private boolean prepareForFindPass() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
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

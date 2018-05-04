package com.liren.live.ui.logicactivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liren.live.R;
import com.liren.live.base.MyBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JJC on 2018/4/15.
 */

public class ResetPassActivity extends MyBaseActivity {

    @BindView(R.id.register_titile)
    TextView registerTitile;
    @BindView(R.id.reset_phone)
    EditText resetPhone;
    @BindView(R.id.reset_code)
    EditText resetCode;
    @BindView(R.id.reset_send_code)
    TextView resetSendCode;
    @BindView(R.id.reset_pwd)
    EditText resetPwd;
    @BindView(R.id.reset_pwd_again)
    EditText resetPwdAgain;
    @BindView(R.id.reset)
    Button reset;
    @BindView(R.id.centrol_part)
    RelativeLayout centrolPart;
    private TimeCount time;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_pass;
    }


    @Override
    public void initView() {
        time = new TimeCount(60000, 1000);
    }

    @Override
    public void initData() {

    }
    @OnClick({R.id.reset_send_code,R.id.reset})
    public void resetClick(View v){
        String phone=resetPhone.getText().toString().trim();
        String code=resetCode.getText().toString().trim();
        String pwd=resetPwd.getText().toString().trim();
        String pwdAgain=resetPwdAgain.getText().toString().trim();
        switch (v.getId()){
            case R.id.reset_send_code:
                if (TextUtils.isEmpty(phone)) {
                    showToast("请输入手机号码");
                    return;
                }
                time.start();
                break;
            case R.id.reset:
                if (TextUtils.isEmpty(code)) {
                    showToast("请输入手机号码");
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    showToast("请输入验证码");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    showToast("请输入密码");
                    return;
                }
                if (TextUtils.isEmpty(pwdAgain)) {
                    showToast("请再次输入密码");
                    return;
                }
                if (!pwd.equals(pwdAgain)){
                    showToast("两次输入密码不同");
                    return;
                }
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            resetSendCode.setText("重新获取验证码");
            resetSendCode.setClickable(true);
            resetSendCode.setTextColor(getResources().getColor(R.color.login_text));
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            resetSendCode.setClickable(false);//防止重复点击
            resetSendCode.setBackgroundColor(getResources().getColor(R.color.red));
            resetSendCode.setText(millisUntilFinished / 1000 + "秒重新发送");
        }
    }
}

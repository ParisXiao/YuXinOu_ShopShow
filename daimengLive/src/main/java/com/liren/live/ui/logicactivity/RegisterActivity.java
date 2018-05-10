package com.liren.live.ui.logicactivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.util.NetUtils;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.config.UrlConfig;
import com.liren.live.config.UserConfig;
import com.liren.live.utils.OKHttpUtils;
import com.liren.live.utils.PhoneUtils;
import com.liren.live.utils.PreferenceUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by JJC on 2018/4/12.
 */

public class RegisterActivity extends MyBaseActivity {

    @BindView(R.id.register_titile)
    TextView registerTitile;
    @BindView(R.id.register_phone)
    EditText registerPhone;
    @BindView(R.id.register_code)
    EditText registerCode;
    @BindView(R.id.register_pwd)
    EditText registerPwd;
    @BindView(R.id.register_pwd_again)
    EditText registerPwdAgain;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.centrol_part)
    RelativeLayout centrolPart;
    @BindView(R.id.register_send_code)
    TextView registerSendCode;
    private TimeCount time;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }


    @Override
    public void initView() {
        time = new TimeCount(60000, 1000);

    }

    @Override
    public void initData() {

    }
    @OnClick({R.id.register_send_code,R.id.register})
    public void registerClick(View v){
        String phone=registerPhone.getText().toString().trim();
        String code=registerCode.getText().toString().trim();
        String pwd=registerPwd.getText().toString().trim();
        String pwdAgain=registerPwdAgain.getText().toString().trim();
        switch (v.getId()){
            case R.id.register_send_code:
                if (TextUtils.isEmpty(phone)) {
                    showToast("请输入手机号码");
                    return;
                }
                if (!PhoneUtils.isMobile(phone)) {
                    showToast(getResources().getString(R.string.plase_error_in_phone));
                    return;
                }
                if(!NetUtils.hasNetwork(RegisterActivity.this)){
                    AppContext.showToast("请检查网络设置",0);
                    return;
                }
                time.start();
                requestGetMessageCode();
                break;
            case R.id.register:
                if (TextUtils.isEmpty(code)) {
                    showToast("请输入手机号码");
                    return;
                }
                if (!PhoneUtils.isMobile(phone)) {
                    showToast(getResources().getString(R.string.plase_error_in_phone));
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
                if (pwd.length()<6&&pwd.length()>12){
                    showToast("请输入6至12位长度密码");
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
                registerPost(phone,pwd);
                break;
        }

    }
    //获取验证码
    private void requestGetMessageCode() {
        PhoneLiveApi.getMessageCode(registerPhone.getText().toString().trim(), "Login.getCode", new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                JSONArray res = ApiUtils.checkIsSuccess(response.body().toString());
                if(res != null){

                    AppContext.showToast(getString(R.string.codehasbeensend),0);
                }
            }


        });
    }
    private String msg;
    private void registerPost(final String phone, final String passward){
        showLoadDialog("正在注册...");
        Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (OKHttpUtils.isConllection(RegisterActivity.this)) {
                    String[] key = new String[]{"code", "pwd"};
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("code", phone);
                    map.put("pwd", passward);
                    String sign = "code" + phone + "pwd" + passward;
                    String result = OKHttpUtils.initHttpData(RegisterActivity.this, UrlConfig.MRegister, sign, key, map);
                    if (!TextUtils.isEmpty(result)) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(result);
                            String code = jsonObject.getString("code");
                            msg = jsonObject.getString("desc");
                            if (code.equals("0")) {
                                dismisDialog();
                                subscriber.onNext(0);
                                PreferenceUtils.getInstance(RegisterActivity.this).saveString(UserConfig.UserPhone,phone);
                                PreferenceUtils.getInstance(RegisterActivity.this).saveString(UserConfig.UserPwd,passward);
//                                成功

                            } else {
                                dismisDialog();
                                subscriber.onNext(1);
//                                离线

                            }

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }else {
                        dismisDialog();
                        subscriber.onNext(3);
                    }
                } else {
                    dismisDialog();
                    subscriber.onNext(2);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                switch (integer) {
                    case 0:
                        finish();
                        showToast("注册成功");
                        break;
                    case 1:
                        showToast(msg);
                        break;
                    case 2:
                        showToast("网络异常，请检查网络设置");
                        break;
                    case 3:
                        showToast("服务器异常");
                        break;
                }
            }
        });
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
            registerSendCode.setText("重新获取");
            registerSendCode.setClickable(true);
            registerSendCode.setTextColor(getResources().getColor(R.color.login_text));
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            registerSendCode.setClickable(false);//防止重复点击
            registerSendCode.setBackgroundColor(getResources().getColor(R.color.red));
            registerSendCode.setText(millisUntilFinished / 1000 + "秒重新发送");
        }
    }
}

package com.liren.live.ui.logicactivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.google.gson.Gson;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.bean.UserBean;
import com.liren.live.config.UrlConfig;
import com.liren.live.config.UserConfig;
import com.liren.live.utils.LoginUtils;
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
 * Created by JJC on 2018/4/11.
 */

public class NewLoginActivity extends MyBaseActivity {
    @BindView(R.id.login_titile)
    TextView loginTitile;
    @BindView(R.id.centrol_part)
    RelativeLayout centrolPart;
    @BindView(R.id.other_login)
    TextView otherLogin;
    @BindView(R.id.oter_login_part)
    RelativeLayout oterLoginPart;
    @BindView(R.id.qq_login)
    ImageView qqLogin;
    @BindView(R.id.weibo_login)
    ImageView weiboLogin;
    @BindView(R.id.weixin_login)
    ImageView weixinLogin;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_pwd)
    EditText editPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.register)
    TextView register;
    @BindView(R.id.back_pwd)
    TextView backPwd;
    @BindView(R.id.appname)
    TextView appname;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_new;
    }

    @Override
    protected void initView() {
//        FZFontsUtils.setOcticons(appname);
        Typeface fontFace = Typeface.createFromAsset(getAssets(),
                "fonts/FZCQJT.TTF");
        appname.setTypeface(fontFace);

        //第一次打开应用
        PreferenceUtils.getInstance(NewLoginActivity.this).saveBoolean("APPFirst",true);

    }
    boolean isLogin=false;
    @OnClick({R.id.btn_login, R.id.register, R.id.back_pwd})
    void onclick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (isLogin){
                    return;
                }

                String phone = editPhone.getText().toString().trim();
                String password = editPwd.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    showToast(getResources().getString(R.string.plase_fill_in_phone));
                    return;
                } else {
                    if (!PhoneUtils.isMobile(phone)) {
                        showToast(getResources().getString(R.string.plase_error_in_phone));
                        return;
                    }
                }
                if (TextUtils.isEmpty(password)) {
                    showToast(getResources().getString(R.string.plase_fill_in_pass));
                    return;
                }
                isLogin=true;
                showLoadDialog("正在登录...");
                PhoneLiveApi.login(phone, password, callback);
//                submitLogin(phone, password);
                break;
            case R.id.register:
                Intent intent = new Intent(NewLoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.back_pwd:
                Intent intent2 = new Intent(NewLoginActivity.this, ResetPassActivity.class);
                startActivity(intent2);
                break;
        }
    }
    //登录回调
    private final StringCallback callback = new StringCallback() {
        @Override
        public void onSuccess(String s, Call call, Response response) {
            dismisDialog();
            isLogin=false;
            JSONArray requestRes = ApiUtils.checkIsSuccess(s);
            if (requestRes != null) {
                PreferenceUtils.getInstance(NewLoginActivity.this).saveString(UserConfig.UserPhone, editPhone.getText().toString().trim());
                Gson gson = new Gson();
                try {

                    UserBean user = gson.fromJson(requestRes.getString(0), UserBean.class);

                    AppContext.getInstance().saveUserInfo(user);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            LoginUtils.getInstance().OtherInit(NewLoginActivity.this);
                        }
                    }).start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    };
    @Override
    protected void initData() {
//Android 6.0及以上并且targetSdkVersion 23及以上,摄像头和麦克风权限需要用代码请求
        //这里使用第三方的权限管理库来请求
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this,
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        Log.e("result", "onGranted===> ");

                    }

                    @Override
                    public void onDenied(String permission) {
                        Log.e("result", "onDenied===> " + permission);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(PreferenceUtils.getInstance(NewLoginActivity.this).getString(UserConfig.UserPhone))) {
            editPhone.setText(PreferenceUtils.getInstance(NewLoginActivity.this).getString(UserConfig.UserPhone));
            editPwd.setText("");
        }
    }

    private void submitLogin(final String phone, final String passward) {

        showLoadDialog("正在登录...");
        Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (OKHttpUtils.isConllection(NewLoginActivity.this)) {
                    String[] key = new String[]{"code", "pwd"};
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("code", phone);
                    map.put("pwd", passward);
                    String sign = "code" + phone + "pwd" + passward;
                    String result = OKHttpUtils.initHttpData(NewLoginActivity.this, UrlConfig.MLogin, sign, key, map);
                    if (!TextUtils.isEmpty(result)) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(result);
                            String code = jsonObject.getString("code");
                            if (code.equals("0")) {
                                //获取H5地址
//                                ReturnHome();
                                dismisDialog();
                                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                                PreferenceUtils.getInstance(NewLoginActivity.this).saveString(UserConfig.DToken, jsonObject1.getString("d_token"));
                                PreferenceUtils.getInstance(NewLoginActivity.this).saveString(UserConfig.ZToken, jsonObject1.getString("z_token"));

                                PreferenceUtils.getInstance(NewLoginActivity.this).saveString(UserConfig.UserPwd, passward);
                                subscriber.onNext(0);
//                                成功
                            } else {
                                dismisDialog();
                                subscriber.onNext(1);
//                                离线

                            }

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                            subscriber.onNext(4);
                        }
                    } else {
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
                        showToast("登录成功");
                        Intent intent = new Intent(NewLoginActivity.this, MyMainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 1:
                        showToast("账号或密码错误");
                        break;
                    case 2:
                        showToast("网络异常，请检查网络设置");
                        break;
                    case 3:
                        showToast("服务器异常");
                        break;
                    case 4:
                        showToast("数据解析错误");
                        break;
                }
            }
        });
    }

    private void ReturnHome() {
        Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (OKHttpUtils.isConllection(NewLoginActivity.this)) {
                    String[] key = new String[]{"code", "pwd"};
                    Map<String, String> map = new HashMap<String, String>();
                    String token = PreferenceUtils.getInstance(NewLoginActivity.this).getString(UserConfig.DToken);
                    String result = OKHttpUtils.postData(NewLoginActivity.this, UrlConfig.SelPagesForPC, token,  key, map);
                    if (!TextUtils.isEmpty(result)) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(result);
                            String code = jsonObject.getString("code");
                            if (code.equals("0")) {
                                dismisDialog();
//                                JSONObject jsonObject1=new JSONObject(jsonObject.getString("result"));
                                subscriber.onNext(0);

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
                    } else {
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
                        showToast("登录成功");
                        break;
                    case 1:
                        showToast("账号或密码错误");
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
}

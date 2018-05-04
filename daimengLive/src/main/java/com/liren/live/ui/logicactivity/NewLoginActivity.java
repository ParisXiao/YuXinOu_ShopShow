package com.liren.live.ui.logicactivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liren.live.R;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.base.UrlConfig;
import com.liren.live.utils.MD5;
import com.liren.live.utils.OKHttpUtils;
import com.liren.live.utils.PhoneUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_new;
    }

    @Override
    protected void initView() {
    }

    @OnClick({R.id.btn_login})
    void onclick(View v){
        switch (v.getId()){
            case R.id.btn_login:
                String phone=editPhone.getText().toString().trim();
                String password=editPwd.getText().toString().trim();
                if (TextUtils.isEmpty(phone)){
                    showToast(getResources().getString(R.string.plase_fill_in_phone));
                    return;
                }else {
                    if (!PhoneUtils.isMobile(phone)) {
                        showToast(getResources().getString(R.string.plase_error_in_phone));
                        return;
                    }
                }
                if (TextUtils.isEmpty(password)){
                    showToast(getResources().getString(R.string.plase_fill_in_pass));
                    return;
                }
                submitLogin(phone,password);
                break;
        }
    }
    @Override
    protected void initData() {

    }

    private void submitLogin(final String phone, final String passward) {
        Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (OKHttpUtils.isConllection(NewLoginActivity.this)) {
                    String[] key = new String[]{"code", "pwd"};
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("code", phone);
                    map.put("pwd", passward);

                    String sign = "code" + phone + "pwd" + passward;
                    String signMD5 = MD5.getMd5Value(sign);
                    String result = OKHttpUtils.initHttpData(NewLoginActivity.this, UrlConfig.MLogin, signMD5, key, map);
                    if (!TextUtils.isEmpty(result)) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(result);
                            String code = jsonObject.getString("code");
                            final String msg = jsonObject.getString("desc");
                            if (code.equals("0")) {
//                                成功
                            } else if (code.equals("1")) {
                                subscriber.onNext(4);
//                                离线
                            } else {
//                                失败
                                subscriber.onNext(2);
                            }

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                } else {
                    subscriber.onNext(3);
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
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
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

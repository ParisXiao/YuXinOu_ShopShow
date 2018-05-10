package com.liren.live.ui;

import android.view.View;
import android.widget.ImageView;

import com.liren.live.AppContext;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseActivity;
import com.liren.live.bean.UserBean;
import com.liren.live.ui.customviews.ActivityTitle;
import com.liren.live.utils.LoginUtils;
import com.liren.live.utils.TDevice;
import com.liren.live.utils.UIHelper;
import com.google.gson.Gson;
import com.liren.live.R;
import com.liren.live.widget.BlackEditText;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 手机登陆
 */
public class MobileLoginActivity extends BaseActivity implements PlatformActionListener {

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    @BindView(R.id.et_loginphone)
    BlackEditText mEtUserPhone;

    @BindView(R.id.et_password)
    BlackEditText mEtUserPassword;

    @BindView(R.id.iv_other_login_qq)
    ImageView mIvQQLogin;

    @BindView(R.id.iv_other_login_wechat)
    ImageView mIvWeChatLogin;

    @BindView(R.id.iv_other_login_weibo)
    ImageView mIvWeiBoLogin;

    private String type;
    private String[] names = {QQ.NAME, Wechat.NAME, SinaWeibo.NAME};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {

        //微信登录
        mIvWeChatLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("正在登录...", false);
                type = "wx";
                otherLogin(names[1]);
            }
        });
        //QQ登录
        mIvQQLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("正在登录...", false);
                type = "qq";
                otherLogin(names[0]);

            }
        });

        //新浪
        mIvWeiBoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog("正在登录...", false);
                type = "sina";
                otherLogin(names[2]);

            }
        });

        mActivityTitle.setMoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showMobileRegLogin(MobileLoginActivity.this);
            }
        });
    }

    @Override
    public void initData() {


        PhoneLiveApi.requestOtherLoginStatus(new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                JSONArray res = ApiUtils.checkIsSuccess(response.body().toString());
                if (res != null) {
                    try {
                        JSONObject object = res.getJSONObject(0);
                        if (object.getInt("login_qq") == 1) {
                            mIvQQLogin.setVisibility(View.VISIBLE);
                        }
                        if (object.getInt("login_sina") == 1) {
                            mIvWeiBoLogin.setVisibility(View.VISIBLE);
                        }
                        if (object.getInt("login_wx") == 1) {
                            mIvWeChatLogin.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

    }

    @OnClick({R.id.btn_dologin, R.id.btn_doReg, R.id.tv_findPass})
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_dologin) {

            if (prepareForLogin()) {
                return;
            }
            String mUserName = mEtUserPhone.getText().toString();
            String mPassword = mEtUserPassword.getText().toString();

            showWaitDialog(R.string.loading);

            PhoneLiveApi.login(mUserName, mPassword, callback);

        } else if (v.getId() == R.id.btn_doReg) {

            UIHelper.showMobileRegLogin(this);

        } else if (v.getId() == R.id.tv_findPass) {

            UIHelper.showUserFindPass(this);
        }

    }

    private void otherLogin(String name) {

        ShareSDK.initSDK(this);

        showWaitDialog("正在跳转...", false);
        Platform other = ShareSDK.getPlatform(name);
        //执行登录，登录后在回调里面获取用户资料
        other.showUser(null);
        other.SSOSetting(false);  //设置false表示使用SSO授权方式
        other.setPlatformActionListener(this);
        other.removeAccount(true);
    }


    //登录回调
    private final StringCallback callback = new StringCallback() {
        @Override
        public void onSuccess(String s, Call call, Response response) {
            hideWaitDialog();
            JSONArray requestRes = ApiUtils.checkIsSuccess(s);
            if (requestRes != null) {

                Gson gson = new Gson();
                try {
                    UserBean user = gson.fromJson(requestRes.getString(0), UserBean.class);

                    AppContext.getInstance().saveUserInfo(user);

                    LoginUtils.getInstance().OtherInit(MobileLoginActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    };


    private boolean prepareForLogin() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return true;
        }

        if (mEtUserPhone.length() == 0) {
            mEtUserPhone.setError("请输入手机号码");
            mEtUserPhone.requestFocus();
            return true;
        }
        if (mEtUserPhone.length() != 11) {
            mEtUserPhone.setError("请输入11位的手机号码");
            mEtUserPhone.requestFocus();
            return true;
        }
        if (mEtUserPassword.length() == 0) {
            mEtUserPassword.setError("请输入密码");
            mEtUserPassword.requestFocus();
            return true;
        }


        return false;
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideWaitDialog();
                AppContext.showToast("授权成功正在登录....", 0);
            }
        });

        //用户资源都保存到res
        //通过打印res数据看看有哪些数据是你想要的
        if (i == Platform.ACTION_USER_INFOR) {
            //showWaitDialog("正在登录...");
            PlatformDb platDB = platform.getDb();//获取数平台数据DB
            //通过DB获取各种数据
            PhoneLiveApi.otherLogin(type, platDB, callback);
        }

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        hideWaitDialog();
        AppContext.showToast("授权登录失败", 0);
    }

    @Override
    public void onCancel(Platform platform, int i) {
        hideWaitDialog();
        AppContext.showToast("授权已取消", 0);
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}

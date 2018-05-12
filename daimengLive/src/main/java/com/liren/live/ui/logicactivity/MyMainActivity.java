package com.liren.live.ui.logicactivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BackHandledInterface;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.base.MyBaseFragment;
import com.liren.live.fragment.myfragment.HomeFragment;
import com.liren.live.fragment.myfragment.KaiBoFragment;
import com.liren.live.fragment.myfragment.LiveFragment;
import com.liren.live.fragment.myfragment.UserInforFragment;
import com.liren.live.fragment.myfragment.VideoFragment;
import com.liren.live.utils.LoginUtils;
import com.liren.live.utils.SharedPreUtil;
import com.liren.live.utils.TLog;
import com.liren.live.utils.UIHelper;
import com.liren.live.widget.BlackTextView;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/5/5 0005.
 */

public class MyMainActivity extends MyBaseActivity implements BackHandledInterface {
    @BindView(R.id.toolbar_back)
    ImageView toolbarBack;
    @BindView(R.id.toolbar_title)
    BlackTextView toolbarTitle;
    @BindView(R.id.toolbar_right)
    TextView toolbarRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.container_fragment)
    FrameLayout containerFragment;
    @BindView(R.id.home)
    RadioButton home;
    @BindView(R.id.live)
    RadioButton live;
    @BindView(R.id.kaibo)
    RadioButton kaibo;
    @BindView(R.id.video)
    RadioButton video;
    @BindView(R.id.shop)
    RadioButton shop;
    @BindView(R.id.main_daohang)
    RadioGroup mainDaohang;
    @BindView(R.id.img_kaibo)
    ImageButton imgKaibo;
    private MyBaseFragment mBackHandedFragment;
    private Fragment currentFragment;
    private boolean hadIntercept;
    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    public void setSelectedFragment(MyBaseFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    //弹出提示，可以有多种方式
                    Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    super.onBackPressed(); //退出
                }

            } else {
                getSupportFragmentManager().popBackStack(); //fragment 出栈
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mymain;
    }

    @Override
    protected void initView() {
        toolbarBack.setVisibility(View.GONE);
        toolbarTitle.setText("首页");
        mainDaohang.check(R.id.home);
        home.setTextColor(getResources().getColor(R.color.tab_red));
    }

    @Override
    protected void initData() {
        add(new HomeFragment(), R.id.container_fragment, "HOME");
        imgKaibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kaibo.setChecked(true);
                toolbar.setVisibility(View.VISIBLE);
                mainDaohang.setVisibility(View.VISIBLE);
                add(new KaiBoFragment(), R.id.container_fragment, "KAIBO");
                toolbarTitle.setText("我要开播");
                home.setTextColor(getResources().getColor(R.color.text_gray));
                live.setTextColor(getResources().getColor(R.color.text_gray));
                video.setTextColor(getResources().getColor(R.color.text_gray));
                shop.setTextColor(getResources().getColor(R.color.text_gray));
            }
        });
        mainDaohang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.home:
                        kaibo.setChecked(false);
                        add(new HomeFragment(), R.id.container_fragment, "HOME");
                        toolbarTitle.setText("首页");
                        home.setTextColor(getResources().getColor(R.color.tab_red));
                        live.setTextColor(getResources().getColor(R.color.text_gray));
                        video.setTextColor(getResources().getColor(R.color.text_gray));
                        shop.setTextColor(getResources().getColor(R.color.text_gray));
                        break;

                    case R.id.live:
                        kaibo.setChecked(false);
                        toolbar.setVisibility(View.VISIBLE);
                        mainDaohang.setVisibility(View.VISIBLE);
                        add(new LiveFragment(), R.id.container_fragment, "LIVE");
                        toolbar.setVisibility(View.VISIBLE);
                        mainDaohang.setVisibility(View.VISIBLE);
                        toolbarTitle.setText("直播特卖");
                        home.setTextColor(getResources().getColor(R.color.text_gray));
                        live.setTextColor(getResources().getColor(R.color.tab_red));
                        video.setTextColor(getResources().getColor(R.color.text_gray));
                        shop.setTextColor(getResources().getColor(R.color.text_gray));
                        break;
                    case R.id.video:
                        kaibo.setChecked(false);
                        toolbar.setVisibility(View.VISIBLE);
                        mainDaohang.setVisibility(View.VISIBLE);
                        add(new VideoFragment(), R.id.container_fragment, "VIDEO");
                        toolbarTitle.setText("短视频");
                        home.setTextColor(getResources().getColor(R.color.text_gray));
                        live.setTextColor(getResources().getColor(R.color.text_gray));
                        video.setTextColor(getResources().getColor(R.color.tab_red));
                        shop.setTextColor(getResources().getColor(R.color.text_gray));
                        break;
                    case R.id.shop:
                        kaibo.setChecked(false);
                        toolbar.setVisibility(View.VISIBLE);
                        mainDaohang.setVisibility(View.VISIBLE);
                        add(new UserInforFragment(), R.id.container_fragment, "USETINFO");
//                        Intent intent=new Intent(MyMainActivity.this,CommonLiveActivity.class);
//                        startActivity(intent);
                        toolbarTitle.setText("个人中心");
                        home.setTextColor(getResources().getColor(R.color.text_gray));
                        live.setTextColor(getResources().getColor(R.color.text_gray));
                        video.setTextColor(getResources().getColor(R.color.text_gray));
                        shop.setTextColor(getResources().getColor(R.color.tab_red));
                        break;

                }
            }
        });

        //检查token是否过期
        checkTokenIsOutTime();
        //注册极光推送
        registerJpush();
        //登录环信
        loginIM();
        //更新信息
        updateConfig();
        initAMap();
    }
    private void updateConfig() {

        if(!TextUtils.isEmpty(SharedPreUtil.getString(this,"config_temp"))){

            try {
                fillConfigData(new JSONObject(SharedPreUtil.getString(this,"config_temp")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return;
        }
        PhoneLiveApi.getConfig(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                JSONArray res = ApiUtils.checkIsSuccess(s);
                if(res != null){
                    try {

                        fillConfigData(res.getJSONObject(0));
                        SharedPreUtil.put(MyMainActivity.this,"config_temp",res.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }


        });
    }

    private void fillConfigData(JSONObject data) throws JSONException {
        AppConfig.TICK_NAME     = data.getString("name_votes");
        AppConfig.CURRENCY_NAME = data.getString("name_coin");
        AppConfig.JOIN_ROOM_ANIMATION_LEVEL = data.getInt("enter_tip_level");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    public void add(Fragment fragment, int id, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //优先检查，fragment是否存在，避免重叠
        Fragment tempFragment = (Fragment) fragmentManager.findFragmentByTag(tag);
        if (tempFragment != null) {
            fragment = tempFragment;
        }
        if (fragment.isAdded()) {
            addOrShowFragment(fragmentTransaction, fragment, id, tag);
        } else {
            if (currentFragment != null && currentFragment.isAdded()) {
                fragmentTransaction.hide(currentFragment).add(id, fragment, tag).commit();
            } else {
                fragmentTransaction.add(id, fragment, tag).commit();
            }
            currentFragment = fragment;
        }
    }

    /**
     * 添加或者显示 fragment
     *
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment, int id, String tag) {
        if (currentFragment == fragment)
            return;
        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragment).add(id, fragment, tag).commit();
        } else {
            transaction.hide(currentFragment).show(fragment).commit();
        }
        currentFragment.setUserVisibleHint(false);
        currentFragment = fragment;
        currentFragment.setUserVisibleHint(true);
    }
    //检查token是否过期
    private void checkTokenIsOutTime() {
        LoginUtils.tokenIsOutTime(null);
    }
    /**
     *
     * 直播初始化
     */
    //登录环信即时聊天
    private void loginIM() {
        String uid = String.valueOf(getUserID());

        EMClient.getInstance().login(uid,
                "yuehuanglive" + uid,new EMCallBack() {//回调
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                EMClient.getInstance().groupManager().loadAllGroups();
                                EMClient.getInstance().chatManager().loadAllConversations();
                                TLog.log("环信[登录聊天服务器成功]");
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        if(204 == code){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //AppContext.showToast("聊天服务器登录和失败,请重新登录");
                                }
                            });

                        }
                        TLog.log("环信[主页登录聊天服务器失败" + "code:" +code + "MESSAGE:" + message + "]");
                    }
                });


    }

    //注册极光推送
    private void registerJpush() {
        JPushInterface.setAlias(this, AppContext.getInstance().getLoginUid() + "PUSH",
                new TagAliasCallback() {
                    @Override
                    public void gotResult(int i, String s, Set<String> set) {
                        TLog.log("极光推送注册[" + i +"I" + "S:-----" +s + "]");
                    }
                });

    }
    protected String getUserID()
    {
        return AppContext.getInstance().getLoginUid();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 5: {
                // 判断权限请求是否通过
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults.length > 0 && grantResults[2] != PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[3] != PackageManager.PERMISSION_GRANTED) {

                    showSelectLiveTypeDialog();

                } else if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    AppContext.showToast("权限不足,请去设置中修改");
                } else if(grantResults.length > 0 && grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    AppContext.showToast("权限不足,请去设置中修改");
                } else if(grantResults.length > 0 && grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults.length > 0 && grantResults[3] != PackageManager.PERMISSION_GRANTED){
                    AppContext.showToast("权限不足,请去设置中修改",0);
                } else if(grantResults.length > 0 && grantResults[3] != PackageManager.PERMISSION_GRANTED){
                    AppContext.showToast("定位权限未打开",0);
                }
                else if(grantResults.length > 0 && grantResults[4] != PackageManager.PERMISSION_GRANTED ){
                    AppContext.showToast("权限不足,请去设置中修改",0);
                }
            }
        }
    }
    //选择直播模式弹窗
    private void showSelectLiveTypeDialog() {

        UIHelper.showRtmpPushActivity(MyMainActivity.this,0);

    }


}

package com.liren.live.ui;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;

import com.liren.live.AppConfig;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.interf.BaseViewInterface;
import com.liren.live.utils.SharedPreUtil;
import com.liren.live.utils.TDevice;
import com.liren.live.utils.TLog;
import com.liren.live.utils.UIHelper;
import com.liren.live.utils.UpdateManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.liren.live.AppContext;
import com.liren.live.AppManager;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.base.BaseActivity;
import com.liren.live.em.MainTab;
import com.liren.live.utils.LoginUtils;
import com.liren.live.widget.BlackTextView;
import com.liren.live.widget.MyFragmentTabHost;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import butterknife.BindView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;


//主页面
public class MainActivity extends BaseActivity implements
        TabHost.OnTabChangeListener, BaseViewInterface,
        View.OnTouchListener {
    @BindView(android.R.id.tabhost)
    MyFragmentTabHost mTabHost;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    public boolean isStartingLive = true;

    @Override
    public void initView() {
        AppManager.getAppManager().addActivity(this);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        if (Build.VERSION.SDK_INT > 10) {
            mTabHost.getTabWidget().setShowDividers(0);
        }
        initTabs();
        mTabHost.setCurrentTab(100);
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setNoTabChangedTag("1");


    }
    private void initTabs() {

        final MainTab[] tabs = MainTab.values();
        final int size = tabs.length;
        String[] title = new String[]{"首页","","我"};

        for (int i = 0; i < size; i++) {
            MainTab mainTab = tabs[i];

            TabHost.TabSpec tab = mTabHost.newTabSpec(String.valueOf(mainTab.getResName()));
            View indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            ImageView tabImg = (ImageView) indicator.findViewById(R.id.tab_img);
            BlackTextView tabTv = (BlackTextView) indicator.findViewById(R.id.tv_wenzi);
            Drawable drawable = this.getResources().getDrawable(
                    mainTab.getResIcon());
            tabTv.setText(title[i]);

            if (i == 1) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)TDevice.dpToPixel(30), ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,2,0);
                tabImg.setLayoutParams(params);
                tabImg.setVisibility(View.GONE);
            }
            tabImg.setImageDrawable(drawable);
            tab.setIndicator(indicator);
            tab.setContent(new TabHost.TabContentFactory() {

                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });

            mTabHost.addTab(tab, mainTab.getClz(), null);

            mTabHost.getTabWidget().getChildAt(i).setOnTouchListener(this);

        }

        mTabHost.getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLive();
            }
        });
    }

    @Override
    public void initData() {
        //检查token是否过期
        checkTokenIsOutTime();
        //注册极光推送
        registerJpush();
        //登录环信
        loginIM();
        //检查是否有最新版本
        checkNewVersion();

        updateConfig();

        mTabHost.setCurrentTab(0);

        Bundle bundle = getIntent().getBundleExtra("USER_INFO");

        if(bundle != null){
            UIHelper.showLookLiveActivity(this,bundle);
        }
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
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(res != null){
                    try {

                        fillConfigData(res.getJSONObject(0));
                        SharedPreUtil.put(MainActivity.this,"config_temp",res.getString(0));
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

        UIHelper.showRtmpPushActivity(MainActivity.this,0);

    }

    @Override
    protected void onStop() {
        super.onStop();
        isStartingLive = true;
    }

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

    //检查token是否过期
    private void checkTokenIsOutTime() {
        LoginUtils.tokenIsOutTime(null);
    }

    //检查是否有最新版本
    private void checkNewVersion() {

        UpdateManager manager = new UpdateManager(this,false);
        manager.checkUpdate();

    }

    //开始直播初始化
    public void startLive() {
        if(android.os.Build.VERSION.SDK_INT >= 23) {
            //摄像头权限检测
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                    ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //进行权限请求
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        5);

            }else{
                showSelectLiveTypeDialog();
            }
        }else{
            showSelectLiveTypeDialog();
        }

    }




    @Override
    public void onTabChanged(String tabId) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return false;
    }


    @Override
    public void onClick(View view) {

    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}

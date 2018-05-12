package com.liren.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.hyphenate.chat.EMClient;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.ui.MainActivity;
import com.liren.live.ui.SplashActivity;
import com.liren.live.ui.logicactivity.MyMainActivity;
import com.liren.live.utils.PreferenceUtils;
import com.liren.live.utils.SharedPreUtil;
import com.liren.live.utils.UIHelper;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * 应用启动界面
 *
 */
public class AppStart extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 防止第三方跳转时出现双实例
        Activity aty = AppManager.getActivity(MainActivity.class);
        if (aty != null && !aty.isFinishing()) {
            finish();
        }
        // SystemTool.gc(this); //针对性能好的手机使用，加快应用相应速度

        final View view = View.inflate(this, R.layout.app_start, null);
        setContentView(view);
        // 渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
        aa.setDuration(800);
        view.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                PhoneLiveApi.getConfig(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONArray res = ApiUtils.checkIsSuccess(s);
                        if(res != null){
                            try {

                                SharedPreUtil.put(AppStart.this,"config_temp",res.getString(0));
                                redirectTo();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationStart(Animation animation) {}
        });
    }

    //字体库
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    /**
     * 跳转到...
     */
    private void redirectTo() {
        if (!PreferenceUtils.getInstance(AppStart.this).getBoolean("APPFirst",false)){
            Intent intent=new Intent(AppStart.this, SplashActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if(!AppContext.getInstance().isLogin()){

            UIHelper.showLoginSelectActivity(this);
            finish();
            return;
        }

        EMClient.getInstance().groupManager().loadAllGroups();
        EMClient.getInstance().chatManager().loadAllConversations();
        Intent intent = new Intent(this, MyMainActivity.class);
        startActivity(intent);
        finish();
    }
    /**
     * 数据提交测试
     */
}

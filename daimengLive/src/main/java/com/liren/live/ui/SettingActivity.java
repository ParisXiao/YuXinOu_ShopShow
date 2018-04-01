package com.liren.live.ui;

import android.view.View;

import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.AppManager;
import com.liren.live.R;
import com.liren.live.base.BaseActivity;
import com.liren.live.ui.customviews.ActivityTitle;
import com.liren.live.ui.customviews.LineControllerView;
import com.liren.live.utils.LoginUtils;
import com.liren.live.utils.TDevice;
import com.liren.live.utils.UIHelper;
import com.liren.live.utils.UpdateManager;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity {


    @BindView(R.id.ll_check_update)
    LineControllerView mTvVersion;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void initData() {

        mTvVersion.setContent(String.format(Locale.CHINA,"(当前版本%s)",TDevice.getVersionName()));
    }

    @OnClick({R.id.ll_login_out,R.id.ll_room_setting,R.id.ll_clearCache,R.id.ll_push_manage,R.id.ll_about,R.id.ll_feedback,R.id.ll_blank_list,R.id.rl_change_pass,R.id.ll_check_update})
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ll_login_out:
                LoginUtils.outLogin(this);
                AppManager.getAppManager().finishAllActivity();
                finish();
                break;
            //房间设置
            case R.id.ll_room_setting:

                break;
            case R.id.ll_clearCache:
                clearCache();
                break;
            case R.id.ll_push_manage:
                UIHelper.showPushManage(this);
                break;
            case R.id.ll_about:
                UIHelper.showWebView(this,AppConfig.MAIN_URL + "/index.php?g=portal&m=page&a=lists","服务条款");
                break;
            //用户反馈
            case R.id.ll_feedback:
                String model = android.os.Build.MODEL;
                String release = android.os.Build.VERSION.RELEASE;
                UIHelper.showWebView(this, AppConfig.MAIN_URL + "/index.php?g=portal&m=page&a=newslist&uid="
                        + getUserID() + "&version=" + release + "&model=" + model,"");
                break;
            case R.id.ll_blank_list:
                UIHelper.showBlackList(SettingActivity.this);
                break;
            case R.id.rl_change_pass:
                UIHelper.showChangePassActivity(SettingActivity.this);
                break;
            case R.id.ll_check_update:
                checkNewVersion();
                break;
        }
    }

    private void checkNewVersion() {
        UpdateManager manager = new UpdateManager(this,true);
        manager.checkUpdate();


    }

    private void clearCache() {
        AppContext.getInstance().clearAppCache();
        AppContext.showToast("缓存清理成功");
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}

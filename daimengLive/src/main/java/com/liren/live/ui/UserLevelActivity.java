package com.liren.live.ui;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.liren.live.AppConfig;
import com.liren.live.base.BaseActivity;
import com.liren.live.R;
import com.liren.live.ui.customviews.ActivityTitle;

import butterknife.BindView;

/**
 * 等级
 */
public class UserLevelActivity extends BaseActivity {
    @BindView(R.id.wv_level)
    WebView mWbView;
    @BindView(R.id.pb_loading)
    ProgressBar mProgressBar;
    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_level;
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
        setActionBarTitle(getString(R.string.myleve));
        mProgressBar.setMax(100);
        mWbView.setWebChromeClient(new WebViewClient());
        WebSettings settings = mWbView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWbView.loadUrl(AppConfig.MAIN_URL + "/index.php?g=Appapi&m=level&a=index&uid="+getUserID());
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    private class WebViewClient extends WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            mProgressBar.setProgress(newProgress);
            if(newProgress == 100){
                mProgressBar.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);

        }
    }

    @Override
    protected void onDestroy() {
        mWbView.destroy();
        super.onDestroy();

    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }
}

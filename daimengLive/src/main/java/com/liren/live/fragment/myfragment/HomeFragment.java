package com.liren.live.fragment.myfragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RadioGroup;

import com.liren.live.R;
import com.liren.live.base.MyBaseFragment;
import com.liren.live.config.HtmlConfig;
import com.liren.live.ui.logicactivity.MyMainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/5/5 0005.
 */

public class HomeFragment extends MyBaseFragment {
    @BindView(R.id.web_home)
    WebView webHome;
    Unbinder unbinder;
    @BindView(R.id.web_refresh)
    SwipeRefreshLayout webRefresh;
    Toolbar toolbar;
    RadioGroup radioGroup;

    @Override
    public boolean onBackPressed() {
        if(webHome.canGoBack()){
            webHome.goBack();
            Log.v("webView.goBack()", "webView.goBack()");
            return true;

        }else{
            Log.v("Conversatio退出","Conversatio退出");
            return false;
        }

    }
    @Override
    public void onAttach(Activity activity) {
// TODO Auto-generated method stub
        super.onAttach(activity);
        if (activity instanceof MyMainActivity) {
            MyMainActivity mainActivity = (MyMainActivity) activity;
            toolbar = (Toolbar) mainActivity.findViewById(R.id.toolbar);
            radioGroup = (RadioGroup) mainActivity.findViewById(R.id.main_daohang);
        }
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View view) {
        WebSettings webSettings = webHome.getSettings();
        webSettings.setJavaScriptEnabled(true);//打开js支持 /** * 打开js接口給H5调用，参数1为本地类名，参数2为别名；h5用window.别名.类名里的方法名才能调用方法里面的内容，例如：window.android.back(); * */
//         mWebView.addJavascriptInterface(new JsInteration(), "android");
        webHome.setWebViewClient(new WebViewClient());
        webHome.setWebChromeClient(new WebChromeClient());
        webHome.loadUrl(HtmlConfig.HOME);
        webRefresh.setColorSchemeResources(R.color.pro_start, R.color.pro_center, R.color.pro_end);
        webRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webHome.loadUrl(HtmlConfig.HOME);
            }
        });
        webHome.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webRefresh.setRefreshing(false);
                if (!url.equals(HtmlConfig.HOME)){
                    toolbar.setVisibility(View.GONE);
                    radioGroup.setVisibility(View.GONE);
                }else {
                    toolbar.setVisibility(View.VISIBLE);
                    radioGroup.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
                webRefresh.setRefreshing(true);

            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    //使用Webview的时候，返回键没有重写的时候会直接关闭程序，这时候其实我们要其执行的知识回退到上一步的操作



}

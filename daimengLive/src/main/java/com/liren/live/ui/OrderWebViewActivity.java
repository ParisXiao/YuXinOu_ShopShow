package com.liren.live.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.liren.live.AppConfig;
import com.liren.live.R;
import com.liren.live.base.BaseActivity;
import com.liren.live.ui.customviews.ActivityTitle;

import java.util.Locale;

import butterknife.BindView;

public class OrderWebViewActivity extends BaseActivity {

    @BindView(R.id.order_wv)
    WebView mWebView;

    @BindView(R.id.ll_week)
    LinearLayout mLlWeekView;

    @BindView(R.id.ll_all)
    LinearLayout mLlAllView;

    @BindView(R.id.view_all)
    View mViewAll;

    @BindView(R.id.view_week)
    View mViewWeek;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    private String mUid;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_web_view;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void initView() {

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mLlWeekView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showData(false);
            }
        });

        mLlAllView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               showData(true);
            }
        });
    }

    @Override
    public void initData() {

        mUid = getIntent().getStringExtra("uid");
        showData(false);

    }

    private void showData(boolean b) {

        mViewWeek.setVisibility(b ? View.GONE : View.VISIBLE);
        mViewAll.setVisibility(!b ? View.GONE : View.VISIBLE);
        String url = String.format(Locale.CHINA,
                AppConfig.MAIN_URL + "/index.php?g=appapi&m=Contribute&a=order&type=week&uid="+ mUid +"&type=%s",b ? "all" : "week");
        mWebView.loadUrl(url);
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    public static void startOrderWebView(Context context,String uid){
        Intent intent = new Intent(context,DedicateOrderActivity.class);
        intent.putExtra("uid",uid);
        context.startActivity(intent);
    }
}

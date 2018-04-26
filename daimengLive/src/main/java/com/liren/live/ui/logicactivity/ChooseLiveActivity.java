package com.liren.live.ui.logicactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;

import com.liren.live.R;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.video.videorecord.TCVideoSettingActivity;
import com.liren.live.widget.BlackTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/4/16 0016.
 */

public class ChooseLiveActivity extends MyBaseActivity {


    @BindView(R.id.toolbar_back)
    ImageView toolbarBack;
    @BindView(R.id.toolbar_title)
    BlackTextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.guideline1)
    Guideline guideline1;
    @BindView(R.id.dianshang)
    Button dianshang;
    @BindView(R.id.guideline2)
    Guideline guideline2;
    @BindView(R.id.jiaoyu)
    Button jiaoyu;
    @BindView(R.id.guideline3)
    Guideline guideline3;
    @BindView(R.id.putong)
    Button putong;
    @BindView(R.id.guideline4)
    Guideline guideline4;
    @BindView(R.id.shiping)
    Button shiping;
    @BindView(R.id.guideline5)
    Guideline guideline5;
    @BindView(R.id.linearLayout)
    ConstraintLayout linearLayout;

    @OnClick(R.id.toolbar_back)
    public void back() {
        finish();
    }

    @OnClick({R.id.dianshang, R.id.jiaoyu, R.id.putong, R.id.shiping})
    public void click(Button button) {
        switch (button.getId()) {
            case R.id.dianshang:
                break;
            case R.id.jiaoyu:
                break;
            case R.id.putong:
                break;
            case R.id.shiping:
                Intent intent = new Intent(ChooseLiveActivity.this, TCVideoSettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_kaibo;
    }

    @Override
    protected void initView() {
        toolbarTitle.setText("我要开播");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}

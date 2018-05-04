package com.liren.live.ui.logicactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.liren.live.R;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.video.videorecord.TCVideoSettingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JJC on 2018/4/15.
 */

public class AecommerceDirectBroadcastActivity extends MyBaseActivity {

    @BindView(R.id.titile)
    EditText titile;
    @BindView(R.id.isYideotape)
    CheckBox isYideotape;
    @BindView(R.id.startPlay)
    Button startPlay;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_aecommerce_direct_broadcast;
    }


    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.startPlay})
    public void click(Button button) {
        switch (button.getId()) {
            case R.id.startPlay:
                break;
        }
    }
}

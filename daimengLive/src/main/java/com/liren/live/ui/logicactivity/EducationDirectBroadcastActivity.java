package com.liren.live.ui.logicactivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.liren.live.R;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.widget.BlackTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JJC on 2018/4/15.
 */

public class EducationDirectBroadcastActivity extends MyBaseActivity {

    @BindView(R.id.toolbar_title)
    BlackTextView toolbarTitle;
    @BindView(R.id.className)
    EditText className;
    @BindView(R.id.classPass)
    EditText classPass;
    @BindView(R.id.isYideotape)
    CheckBox isYideotape;
    @BindView(R.id.startPlay)
    Button startPlay;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_education_direct_broadcast;
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
}

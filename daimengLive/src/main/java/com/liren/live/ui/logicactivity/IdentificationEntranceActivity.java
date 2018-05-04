package com.liren.live.ui.logicactivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import com.liren.live.R;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.widget.BlackTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JJC on 2018/4/15.
 */

public class IdentificationEntranceActivity extends MyBaseActivity {

    @BindView(R.id.toolbar_title)
    BlackTextView toolbarTitle;
    @BindView(R.id.identification_btn)
    Button identificationBtn;
    @BindView(R.id.agree)
    CheckBox agree;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_identification_entrance;
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

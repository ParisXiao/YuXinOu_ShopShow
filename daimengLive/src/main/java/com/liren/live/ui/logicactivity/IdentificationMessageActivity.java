package com.liren.live.ui.logicactivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.liren.live.R;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.widget.BlackTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by JJC on 2018/4/15.
 */

public class IdentificationMessageActivity extends MyBaseActivity {

    @BindView(R.id.toolbar_title)
    BlackTextView toolbarTitle;
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.number)
    EditText number;
    @BindView(R.id.carnumber)
    EditText carnumber;
    @BindView(R.id.banktylp)
    EditText banktylp;
    @BindView(R.id.bankAddress)
    EditText bankAddress;
    @BindView(R.id.bankName)
    EditText bankName;
    @BindView(R.id.next_btn)
    Button nextBtn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_identification_message;
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

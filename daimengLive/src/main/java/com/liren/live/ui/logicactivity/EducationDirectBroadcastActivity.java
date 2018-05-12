package com.liren.live.ui.logicactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liren.live.R;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.widget.BlackTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.toolbar_back)
    ImageView toolbarBack;
    @BindView(R.id.toolbar_right)
    TextView toolbarRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.namePate)
    RelativeLayout namePate;
    @BindView(R.id.passPate)
    RelativeLayout passPate;
    @BindView(R.id.centrol_part)
    RelativeLayout centrolPart;
    int LiveType = 0;//0电商 1教育

    @Override
    protected int getLayoutId() {
        return R.layout.activity_education_direct_broadcast;
    }


    @Override
    public void initView() {
        LiveType = getIntent().getIntExtra("LiveType", 0);
        if (LiveType == 0) {
            passPate.setVisibility(View.GONE);
            className.setHint("请填写直播标题");
        }
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
    boolean isStart=false;
    @OnClick(R.id.startPlay)
    public void onViewClicked() {
        if (isStart) {
            return;
        }
        if(LiveType==0){
            if (TextUtils.isEmpty(className.getText().toString().trim())) {
                showToast("请填写直播标题");
                return;
            }
            Intent intent=new Intent(EducationDirectBroadcastActivity.this,ChooseSellerActivity.class);
            intent.putExtra("LiveTitle",className.getText().toString().trim());
            startActivity(intent);

        }else {
            if (TextUtils.isEmpty(className.getText().toString().trim())) {
                showToast("请填写课程名称");
                return;
            }
            if (TextUtils.isEmpty(classPass.getText().toString().trim())) {
                showToast("请填写课程密码");
                return;
            }
        }
    }
}

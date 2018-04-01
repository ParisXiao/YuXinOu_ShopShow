package com.liren.live.ui;

import android.view.View;

import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseActivity;
import com.liren.live.ui.customviews.ActivityTitle;
import com.liren.live.utils.TDevice;
import com.liren.live.widget.BlackEditText;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 密码修改
 */
public class ChangePassActivity extends BaseActivity {

    @BindView(R.id.et_old_pass)
    BlackEditText etOldPass;
    @BindView(R.id.et_new_pass)
    BlackEditText etNewPass;
    @BindView(R.id.et_second_pass)
    BlackEditText etSecondPass;
    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_pass;
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
        setActionBarTitle("修改密码");
    }

    @OnClick(R.id.btn_change_pass)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_change_pass) {
            if (prepareForChangePass()) {
                return;
            }
            String mOldPass    = etOldPass.getText().toString();
            String mNewPass    = etNewPass.getText().toString();
            String mSecondPass = etSecondPass.getText().toString();
            PhoneLiveApi.getChangePass(getUserID(),getUserToken(),mOldPass,mNewPass,mSecondPass,callback);
        }

    }

    //注册回调
    private final StringCallback callback = new StringCallback() {
        @Override
        public void onError(Call call, Exception e,int id) {
            showToast2(getString(R.string.editfail));
        }

        @Override
        public void onResponse(String s,int id) {
            JSONArray res = ApiUtils.checkIsSuccess(s);
            if(res != null) {
                etOldPass.setText("");
                etNewPass.setText("");
                etSecondPass.setText("");
                AppContext.showToast("修改成功",0);
                finish();
            }

        }
    };

    private boolean prepareForChangePass() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tip_no_internet);
            return true;
        }

        if (etOldPass.length() == 0) {
            etOldPass.setError("请输入旧密码");
            etOldPass.requestFocus();
            return true;
        }


        if (etNewPass.length() == 0) {
            etNewPass.setError("请输入新密码");
            etNewPass.requestFocus();
            return true;
        }

        if (!etSecondPass.getText().toString().equals(etNewPass.getText().toString())) {
            etSecondPass.setText("");
            etSecondPass.setError("密码不一致，请重新输入");
            etSecondPass.requestFocus();
            return true;
        }

        return false;
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }


}

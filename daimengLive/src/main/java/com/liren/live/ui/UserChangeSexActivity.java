package com.liren.live.ui;

import android.view.View;
import android.widget.ImageView;

import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseActivity;
import com.liren.live.bean.UserBean;
import com.liren.live.ui.customviews.ActivityTitle;
import com.liren.live.utils.LiveUtils;
import com.lzy.okhttputils.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 修改性别
 */
public class UserChangeSexActivity extends BaseActivity {
    @BindView(R.id.iv_change_sex_male)
    ImageView mIvMale;
    @BindView(R.id.iv_change_sex_female)
    ImageView mIvFemale;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;

    private String sex = "0";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_sex;
    }

    @Override
    public void initData() {
        setActionBarTitle("性别");
        sex = AppContext.getInstance().getLoginUser().sex;

        boolean sexb = changeUI();
        mIvFemale.setImageResource(sexb ? R.drawable.choice_sex_un_femal : R.drawable.choice_sex_femal);
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
    protected boolean hasBackButton() {
        return true;
    }

    @OnClick({R.id.iv_change_sex_male, R.id.iv_change_sex_female})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.iv_change_sex_male:
                sex = "1";
                changeSex();
                break;
            case R.id.iv_change_sex_female:
                sex = "2";
                changeSex();
                break;
        }
    }

    private void changeSex() {
        boolean sexb = changeUI();
        mIvFemale.setImageResource(sexb ? R.drawable.choice_sex_un_femal : R.drawable.choice_sex_femal);
        PhoneLiveApi.saveInfo(LiveUtils.getFiledJson("sex", String.valueOf(sex)), getUserID(), getUserToken(), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (response.isSuccessful()) {
                    UserBean mUser = AppContext.getInstance().getLoginUser();
                    mUser.sex = sex;
                    AppContext.getInstance().saveUserInfo(mUser);
                    finish();
                }
            }

        });
    }

    private boolean changeUI() {
        boolean sexb = sex.equals("1");
        mIvMale.setImageResource(sexb ? R.drawable.choice_sex_male : R.drawable.choice_sex_un_male);
        return sexb;
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    public void onClick(View v) {

    }
}

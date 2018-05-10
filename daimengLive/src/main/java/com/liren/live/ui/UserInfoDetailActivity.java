package com.liren.live.ui;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseActivity;
import com.liren.live.bean.UserBean;
import com.liren.live.em.ChangInfo;
import com.liren.live.ui.customviews.ActivityTitle;
import com.liren.live.utils.DialogHelp;
import com.liren.live.utils.LiveUtils;
import com.liren.live.utils.UIHelper;
import com.liren.live.widget.AvatarView;
import com.liren.live.widget.BlackTextView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 用户信息详情页面
 */
public class UserInfoDetailActivity extends BaseActivity {

    @BindView(R.id.et_info_birthday)
    TextView etInfoBirthday;
    private UserBean mUser;

    @BindView(R.id.rl_userHead)
    RelativeLayout mRlUserHead;

    @BindView(R.id.rl_userNick)
    RelativeLayout mRlUserNick;

    @BindView(R.id.rl_userSign)
    RelativeLayout mRlUserSign;

    @BindView(R.id.rl_userSex)
    RelativeLayout mRlUserSex;

    @BindView(R.id.tv_userNick)
    BlackTextView mUserNick;

    @BindView(R.id.tv_userSign)
    BlackTextView mUserSign;

    @BindView(R.id.av_userHead)
    AvatarView mUserHead;

    @BindView(R.id.iv_info_sex)
    ImageView mUserSex;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_myinfo_detail;
    }

    @Override
    public void initView() {

        mRlUserNick.setOnClickListener(this);
        mRlUserSign.setOnClickListener(this);
        mRlUserHead.setOnClickListener(this);
        mRlUserSex.setOnClickListener(this);
        final Calendar c = Calendar.getInstance();
        etInfoBirthday.setOnClickListener(new View.OnClickListener() { //生日修改
            @Override
            public void onClick(View v) {
                showSelectBirthday(c);
            }
        });

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void initData() {

    }

    private void sendRequiredData() {
        PhoneLiveApi.getMyUserInfo(getUserID(), getUserToken(), callback);
    }

    @Override
    public void onClick(View v) {
        if (mUser != null) {
            switch (v.getId()) {
                case R.id.rl_userNick:
                    UIHelper.showEditInfoActivity(
                            this, "修改昵称",
                            getString(R.string.editnickpromp),
                            mUser.user_nicename,
                            ChangInfo.CHANG_NICK);
                    break;
                case R.id.rl_userSign:
                    UIHelper.showEditInfoActivity(
                            this, "修改签名",
                            getString(R.string.editsignpromp),
                            mUser.signature,
                            ChangInfo.CHANG_SIGN);
                    break;
                case R.id.rl_userHead:
                    UIHelper.showSelectAvatar(this, mUser.avatar);
                    break;
                //修改性别
                case R.id.rl_userSex:
                    changeUserSex();
                    //UIHelper.showChangeSex(this);
                    break;

            }
        }

    }

    //修改性别
    private void changeUserSex() {

        DialogHelp.getSelectDialog(this, "选择性别", new String[]{"男", "女"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final int sex = (i + 1);
                PhoneLiveApi.saveInfo(LiveUtils.getFiledJson("sex", String.valueOf(sex)), getUserID(), getUserToken(), new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        UserBean user = AppContext.getInstance().getLoginUser();
                        user.sex = String.valueOf(sex);
                        AppContext.getInstance().saveUserInfo(user);
                        mUser.sex = String.valueOf(sex);
                        fillUI();
                    }
                });

            }
        }).show();
    }

    //生日选择
    private void showSelectBirthday(final Calendar c) {
        DatePickerDialog dialog = new DatePickerDialog(UserInfoDetailActivity.this, new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                if(c.getTime().getTime()>new Date().getTime())
                {
                    showToast2("请选择正确的日期");
                    return;
                }
                final String birthday= DateFormat.format("yyy-MM-dd", c).toString();
                requestSaveBirthday(birthday);

            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        try {
            dialog.getDatePicker().setMinDate(new SimpleDateFormat("yyyy-MM-dd").parse("1950-01-01").getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //dialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());

        dialog.show();
    }

    //保存生日
    private void requestSaveBirthday(final String birthday) {

        PhoneLiveApi.saveInfo(LiveUtils.getFiledJson("birthday",birthday),
                AppContext.getInstance().getLoginUid(),
                AppContext.getInstance().getToken(),
                new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONArray res = ApiUtils.checkIsSuccess(response.body().toString());
                        if(null != res){
                            AppContext.showToast(getString(R.string.editsuccess));
                            UserBean u =  AppContext.getInstance().getLoginUser();
                            u.birthday = birthday;
                            AppContext.getInstance().updateUserInfo(u);
                            etInfoBirthday.setText(birthday);

                        }else {
                            showToast2(getString(R.string.editfail));
                        }
                    }

                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendRequiredData();
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    private final StringCallback callback = new StringCallback() {
        @Override
        public void onSuccess(String s, Call call, Response response) {
            JSONArray res = ApiUtils.checkIsSuccess(s);
            if (res != null) {
                try {
                    mUser = new Gson().fromJson(res.getString(0), UserBean.class);
                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    };


    private void fillUI() {

        mUserNick.setText(mUser.user_nicename);
        mUserSign.setText(mUser.signature);
        mUserHead.setAvatarUrl(mUser.avatar);
        mUserSex.setImageResource(LiveUtils.getSexRes(mUser.sex));
        etInfoBirthday.setText(mUser.birthday);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getMyUserInfo");
    }
}

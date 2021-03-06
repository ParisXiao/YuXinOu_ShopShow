package com.liren.live.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseFragment;
import com.liren.live.bean.UserBean;
import com.liren.live.ui.customviews.LineControllerView;
import com.liren.live.utils.LiveUtils;
import com.liren.live.utils.TDevice;
import com.liren.live.utils.UIHelper;
import com.liren.live.widget.AvatarView;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 登录用户中心页面
 */
public class UserInformationFragment extends BaseFragment{

    //头像
    @BindView(R.id.iv_avatar)
    AvatarView mIvAvatar;
    //昵称
    @BindView(R.id.tv_name)
    TextView mTvName;

    @BindView(R.id.ll_user_container)
    View mUserContainer;

    //id
    @BindView(R.id.ll_number_id)
    LineControllerView mLcId;

    //签名
    @BindView(R.id.tv_sign)
    TextView mTvSign;

    //粉丝
    @BindView(R.id.ll_fans)
    LineControllerView mLcvFans;
   //排行榜
    @BindView(R.id.ll_contribute)
    LineControllerView mControbite;
    //关注
    @BindView(R.id.ll_follow)
    LineControllerView mLcvFollow;

    //直播记录
    //vip
     @BindView(R.id.ll_vipuser)
     LineControllerView mVipuser;
    @BindView(R.id.iv_sex)
    ImageView mIvSex;

    @BindView(R.id.iv_level)
    ImageView mIvLevel;

    private UserBean mInfo;

    @Override
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_information,
                container, false);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
        mInfo = AppContext.getInstance().getLoginUser();
        fillUI();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView(View view) {

        view.findViewById(R.id.ll_live_record).setOnClickListener(this);
        view.findViewById(R.id.ll_follow).setOnClickListener(this);
        view.findViewById(R.id.ll_fans).setOnClickListener(this);
        view.findViewById(R.id.ll_profit).setOnClickListener(this);
        view.findViewById(R.id.ll_setting).setOnClickListener(this);
        view.findViewById(R.id.ll_level).setOnClickListener(this);
        view.findViewById(R.id.ll_diamonds).setOnClickListener(this);
        view.findViewById(R.id.ll_about).setOnClickListener(this);
        view.findViewById(R.id.ll_authenticate).setOnClickListener(this);
        view.findViewById(R.id.iv_edit_info).setOnClickListener(this);
        view.findViewById(R.id.ll_number_id).setOnClickListener(this);
        view.findViewById(R.id.ll_vipuser).setOnClickListener(this);
        view.findViewById(R.id.ll_contribute).setOnClickListener(this);
        ((LineControllerView)view.findViewById(R.id.ll_diamonds)).setName("我的" + "充值");
    }

    private void fillUI() {

        mTvSign.setText(mInfo.signature);
        mIvAvatar.setAvatarUrl(mInfo.avatar);
        //昵称
        mTvName.setText(mInfo.user_nicename);

        mLcId.setContent(mInfo.id);

        mIvSex.setImageResource(LiveUtils.getSexRes(mInfo.sex));
        mIvLevel.setImageResource(LiveUtils.getLevelRes(mInfo.level));
    }

    protected void requestData(boolean refresh) {
        if (AppContext.getInstance().isLogin()) {

            sendRequestData();
        }

    }

    private void sendRequestData() {

        PhoneLiveApi.getMyUserInfo(AppContext.getInstance().getLoginUid(),
                AppContext.getInstance().getToken(),stringCallback);
    }

    private StringCallback stringCallback = new StringCallback() {
        @Override
        public void onSuccess(String s, Call call, Response response) {
            JSONArray res = ApiUtils.checkIsSuccess(s);
            if(res != null){

                try {
                    JSONObject object = res.getJSONObject(0);
                    mInfo = new Gson().fromJson(object.toString(),UserBean.class);
                    AppContext.getInstance().updateUserInfo(mInfo);

                    //mLiveNum.setText(object.getString("lives"));
                    mLcvFollow.setContent(object.getString("follows"));
                    mLcvFans.setContent(object.getString("fans"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ll_authenticate://申请认证
                UIHelper.showWebView(getActivity(),
                        AppConfig.MAIN_URL + "/index.php?g=Appapi&m=auth&a=index&uid=" +
                                mInfo.id,"");
                break;
            case R.id.ll_contribute:
                UIHelper.showWebView(getActivity(),AppConfig.MAIN_URL+"/index.php?g=appapi&m=Contribute&a=ranking&uid="+
                        mInfo.id,"");
            case R.id.iv_avatar:
                break;
            case R.id.ll_live_record:
                UIHelper.showLiveRecordActivity(getActivity(),mInfo.id);
                break;
            case R.id.ll_follow:
                UIHelper.showAttentionActivity(getActivity(), mInfo.id);
                break;
            case R.id.ll_fans:
                UIHelper.showFansListActivity(getActivity(),mInfo.id);
                    break;
            case R.id.ll_setting:
                UIHelper.showSetting(getActivity());
                break;
            case R.id.ll_diamonds:
                //我的钻石
                UIHelper.showMyDiamonds(getActivity());
                break;
            case R.id.ll_level:
                //我的等级
                UIHelper.showLevel(getActivity());
                break;
            case R.id.ll_vipuser:
                UIHelper.showWebView(getContext(),AppConfig.MAIN_URL+"/index.php?g=Appapi&m=Vip&a=index&uid="+
                        mInfo.id+"&token="+mInfo.token,"");
                break;
            case R.id.ll_profit:
                //收益
                UIHelper.showProfitActivity(getActivity());
                break;
            //编辑资料
            case R.id.iv_edit_info:
                UIHelper.showMyInfoDetailActivity(getContext());
                break;
            case R.id.ll_about:
                UIHelper.showWebView(getContext(),AppConfig.MAIN_URL + "/index.php?g=portal&m=page&a=lists","");
                break;
            //复制id
            case R.id.ll_number_id:
                TDevice.copyTextToBoard(mInfo.id);
                break;

            default:
                break;
        }
    }



    @Override
    public void onResume() {
        super.onResume();

        sendRequestData();
    }


}

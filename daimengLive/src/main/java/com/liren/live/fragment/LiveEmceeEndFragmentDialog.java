package com.liren.live.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.widget.AvatarView;
import com.liren.live.widget.BlackButton;
import com.liren.live.widget.BlackTextView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 主播结束 直播 弹窗
 */
public class LiveEmceeEndFragmentDialog extends DialogFragment {
    @BindView(R.id.rl_livestop)
    protected RelativeLayout mLayoutLiveStop;

    //结束直播收获魅力值数量
    @BindView(R.id.tv_live_end_yp_num)
    BlackTextView mTvLiveEndYpNum;

    @BindView(R.id.tv_live_duration)
    BlackTextView mTvLiveDuration;

    //直播结束房间人数
    @BindView(R.id.tv_live_end_num)
    BlackTextView mTvLiveEndUserNum;

    @BindView(R.id.av_user_head)
    AvatarView mIvUserHead;

    @BindView(R.id.tv_user_nick)
    TextView mTvUserNick;

    @BindView(R.id.btn_back_index)
    BlackButton mBtnBackIndex;

    @BindView(R.id.tv_live_end_yp_name)
    TextView mTvEndYpName;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_live_emcee_end_dialog,null);
        Dialog dialog = new Dialog(getActivity(),R.style.dialog_live_end);
        ButterKnife.bind(this,view);
        dialog.setContentView(view);

        initView(view);
        initData();
        return dialog;
    }

    private void initData() {


        String stream = getArguments().getString("stream");

        PhoneLiveApi.getLiveEndInfo(stream,new StringCallback(){

            @Override
            public void onSuccess(String s, Call call, Response response) {
                JSONArray res = ApiUtils.checkIsSuccess(s);
                if(res != null){
                    try {
                        JSONObject data = res.getJSONObject(0);
                        mTvLiveDuration.setText(data.getString("length"));
                        mTvLiveEndUserNum.setText(data.getString("nums"));
                        mTvLiveEndYpNum.setText(data.getString("votes"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        });


        mIvUserHead.setAvatarUrl(AppContext.getInstance().getLoginUser().avatar_thumb);
        mTvUserNick.setText(AppContext.getInstance().getLoginUser().user_nicename);
    }

    private void initView(View view) {
        mBtnBackIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                dismiss();
            }
        });

        mTvEndYpName.setText("收获" + AppConfig.TICK_NAME);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        OkHttpUtils.getInstance().cancelTag("getLiveEndInfo");
        if(isAdded()){
            getActivity().finish();
        }
    }
}

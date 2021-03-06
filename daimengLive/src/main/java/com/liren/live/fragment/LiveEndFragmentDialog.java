package com.liren.live.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.widget.BlackButton;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by liren on 16/8/18.
 */
public class LiveEndFragmentDialog extends DialogFragment {

    //直播结束关注btn
    @BindView(R.id.btn_follow)
    BlackButton mFollowEmcee;
    @BindView(R.id.btn_back_index)
    BlackButton mBtnBackIndex;
    private String roomnum;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_live_end_dialog,null);
        Dialog dialog = new Dialog(getActivity(),R.style.dialog_live_end);
        ButterKnife.bind(this,view);
        dialog.setContentView(view);

        initView(view);

        initData();

        return dialog;
    }

    private void initData() {
        roomnum = getArguments().getString("roomnum");

        showEndIsFollowEmcee();
    }

    private void initView(View view) {
        mFollowEmcee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFollow(AppContext.getInstance().getLoginUid(), String.valueOf(roomnum));
            }
        });
        mBtnBackIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                dismiss();
            }
        });

    }
    /**
     * @dw 关注
     * @param uid 当前用户id
     * @param touid 关注用户id
     * */
    private void showFollow(String uid,String touid){

        PhoneLiveApi.showFollow(uid,touid, AppContext.getInstance().getToken(),new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                ApiUtils.checkIsSuccess(s);
                if(mFollowEmcee.getText().toString().equals(getResources().getString(R.string.follow))){
                    mFollowEmcee.setText(getResources().getString(R.string.alreadyfollow));
                }else{
                    mFollowEmcee.setText(getResources().getString(R.string.follow));
                }
            }

        });
    }

    //直播结束判断当前主播是否关注过
    private void showEndIsFollowEmcee() {

        //判断当前主播是否已经关注
        PhoneLiveApi.getIsFollow(AppContext.getInstance().getLoginUid(),roomnum,new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                JSONArray res = ApiUtils.checkIsSuccess(s);//0：未关注1:关注
                if(res != null && isAdded()){
                    try {
                        if(res.getJSONObject(0).getInt("isattent") == 0){

                            mFollowEmcee.setText(getString(R.string.follow));
                        }else{

                            mFollowEmcee.setText(getString(R.string.alreadyfollow));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        OkHttpUtils.getInstance().cancelTag("getIsFollow");
        if(isAdded()){
            getActivity().finish();
        }
    }
}

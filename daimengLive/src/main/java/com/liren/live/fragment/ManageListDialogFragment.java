package com.liren.live.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.adapter.ManageListAdapter;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.bean.ManageListBean;
import com.liren.live.widget.BlackTextView;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 管理员列表
 */
public class ManageListDialogFragment extends DialogFragment {
    private List<ManageListBean> mManageList = new ArrayList<>();
    @BindView(R.id.lv_manage_list)
    ListView mListViewManageList;
    @BindView(R.id.tv_manage_title)
    BlackTextView mTvManageTitle;
    @BindView(R.id.iv_close)
    ImageView mIvClose;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_list,null);
        ButterKnife.bind(this,view);
        initView(view);
        initData();
        return view;
    }

    public void initView(View view) {
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void initData() {
        mTvManageTitle.setText("管理员列表");
        PhoneLiveApi.getManageList(AppContext.getInstance().getLoginUid(),callback);
    }
    private StringCallback callback = new StringCallback() {
        @Override
        public void onSuccess(String s, Call call, Response response) {
            JSONArray manageListJsonObject = ApiUtils.checkIsSuccess(response.body().toString());
            if(null != manageListJsonObject){
                try {
                    Gson g = new Gson();
                    if(!(manageListJsonObject.length() > 0))return;
                    for(int i = 0; i<manageListJsonObject.length();i++){
                        mManageList.add(g.fromJson(manageListJsonObject.getString(i),ManageListBean.class));
                    }

                    fillUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                AppContext.showToast("获取管理员列表失败");
            }
        }

    };

    private void fillUI() {
        mListViewManageList.setAdapter(new ManageListAdapter(mManageList));
    }


}

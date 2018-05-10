package com.liren.live.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.adapter.BlackInfoAdapter;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseFragment;
import com.liren.live.bean.UserBean;
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
 * C黑名单
 */
public class BlackListFragment extends BaseFragment {


    @BindView(R.id.lv_black_list)
    ListView mLvBlackList;

    private List<UserBean> mBlackList = new ArrayList<>();

    private BlackInfoAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_black_list,null);
        ButterKnife.bind(this,view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {
        AppContext.showToast("长按可解除拉黑");
        mAdapter = new BlackInfoAdapter(mBlackList);
        mLvBlackList.setAdapter(mAdapter);
        mLvBlackList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                relieveBlack(position);
                return false;
            }
        });
        requestData(false);
    }

    private void relieveBlack(final int position) {
        try {
            EMClient.getInstance().contactManager().removeUserFromBlackList(String.valueOf(mBlackList.get(position).id));

        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        PhoneLiveApi.pullTheBlack(AppContext.getInstance().getLoginUid(),mBlackList.get(position).id,
                AppContext.getInstance().getToken(),
                new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        AppContext.showToast("解除拉黑成功");
                        mBlackList.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }


                });
    }

    @Override
    protected void requestData(boolean refresh) {
        PhoneLiveApi.getBlackList(AppContext.getInstance().getLoginUid(),callback);
    }
    private StringCallback callback = new StringCallback() {
        @Override
        public void onSuccess(String s, Call call, Response response) {

            JSONArray blackJsonArray = ApiUtils.checkIsSuccess(response.body().toString());
            if(null == blackJsonArray)return;

            try {
                if(blackJsonArray.length() > 0){
                    Gson g  = new Gson();
                    for(int i = 0; i< blackJsonArray.length(); i ++){
                        mBlackList.add(g.fromJson(blackJsonArray.getString(i),UserBean.class));
                    }
                    fillUI();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    };

    private void fillUI() {
        mAdapter.notifyDataSetChanged();
    }
}

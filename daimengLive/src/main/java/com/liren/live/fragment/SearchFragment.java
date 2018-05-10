package com.liren.live.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.adapter.UserBaseInfoAdapter;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseFragment;
import com.liren.live.bean.SimpleUserInfo;
import com.liren.live.utils.UIHelper;
import com.liren.live.widget.BlackEditText;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 用户搜索
 */
public class SearchFragment extends BaseFragment {
    @BindView(R.id.et_search_input)
    BlackEditText mSearchKey;

    @BindView(R.id.lv_search)
    ListView mLvSearch;


    private UserBaseInfoAdapter mUserBaseInfoAdapter;

    private List<SimpleUserInfo> mUserList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search_index, null);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {

        mSearchKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    search();
                    return true;
                }
                return false;
            }
        });

        mLvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIHelper.showHomePageActivity(getActivity(), mUserList.get(position).id);
            }
        });

        mLvSearch.setAdapter(mUserBaseInfoAdapter = new UserBaseInfoAdapter(mUserList));


        mLvSearch.setEmptyView(view.findViewById(R.id.iv_empty));
    }

    @Override
    public void initData() {

    }

    @OnClick({R.id.tv_search_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_search_btn:
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    //搜索
    private void search() {


        String screenKey = mSearchKey.getText().toString().trim();
        if (TextUtils.isEmpty(screenKey)) {
            return;
        }
        showWaitDialog();

        PhoneLiveApi.search(screenKey, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                hideWaitDialog();
                JSONArray res = ApiUtils.checkIsSuccess(response.body().toString());

                if (null != res) {
                    mUserList.clear();
                    mUserList.addAll(ApiUtils.formatDataToList(res, SimpleUserInfo.class));
                    fillUI();
                }
            }

        }, AppContext.getInstance().getLoginUid());

    }

    private void fillUI() {
        mUserBaseInfoAdapter.notifyDataSetChanged();
    }

}

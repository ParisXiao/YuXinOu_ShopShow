package com.liren.live.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseFragment;
import com.liren.live.bean.LiveJson;
import com.liren.live.game.adapter.GameListAdapter;
import com.liren.live.utils.UIHelper;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;


//游戏直播分类
public class GameFragment extends BaseFragment {

    @BindView(R.id.rl_list)
    RecyclerView mRecyclerView;

    @BindView(R.id.ll_loading_data_empty)
    LinearLayout mLlLoadingDataEmpty;

    @BindView(R.id.ll_loading_data_error)
    LinearLayout mLlLoadingDataError;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefresh;

    private List<LiveJson> mListLive = new ArrayList<>();
    private GameListAdapter mGameListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        ButterKnife.bind(this,view);
        initView(view);
        initData();

        return view;
    }

    @Override
    public void initData() {

        requestData(false);
    }

    @Override
    public void initView(View view) {


        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mGameListAdapter = new GameListAdapter(mListLive);
        mRecyclerView.setAdapter(mGameListAdapter);

        mGameListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UIHelper.startRtmpPlayerActivity(getContext(),mListLive.get(position));
            }
        });
        mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.global));
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(false);
            }
        });
    }

    @Override
    protected void requestData(boolean refresh) {
        PhoneLiveApi.requestGetGameLive(new StringCallback(){

            @Override
            public void onSuccess(String s, Call call, Response response) {
                mSwipeRefresh.setRefreshing(false);
                JSONArray data = ApiUtils.checkIsSuccess(response.body().toString());
                if(data != null){
                    try {

                        fillUI(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    mSwipeRefresh.setRefreshing(false);
                    mLlLoadingDataEmpty.setVisibility(View.GONE);
                    mLlLoadingDataError.setVisibility(View.VISIBLE);
                }
            }


        });
    }

    private void fillUI(JSONArray data) throws JSONException {

        mListLive.clear();
        mListLive.addAll(ApiUtils.formatDataToList(data,LiveJson.class));

        mGameListAdapter.notifyDataSetChanged();

        if(mListLive.size() > 0){
            mLlLoadingDataEmpty.setVisibility(View.GONE);
        }else{
            mLlLoadingDataEmpty.setVisibility(View.VISIBLE);
        }

        mLlLoadingDataError.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("requestGetGameLive");
    }
}

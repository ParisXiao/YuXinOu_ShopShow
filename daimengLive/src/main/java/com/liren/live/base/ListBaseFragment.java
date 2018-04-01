package com.liren.live.base;

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
import com.liren.live.adapter.BaseListAdapter;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.bean.LiveJson;
import com.liren.live.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ListBaseFragment extends BaseFragment {

    @BindView(R.id.rl_list)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.ll_loading_data_empty)
    protected LinearLayout mLlLoadingDataEmpty;

    @BindView(R.id.ll_loading_data_error)
    protected LinearLayout mLlLoadingDataError;

    @BindView(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout mSwipeRefresh;

    protected List<LiveJson> mListLive = new ArrayList<>();
    protected BaseListAdapter mBaseListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);

        ButterKnife.bind(this,view);
        initView(view);
        initData();

        return view;
    }

    protected int getLayoutId(){
        return R.layout.fragment_list_base;
    }

    @Override
    public void initData() {

        requestData(false);
    }

    @Override
    public void initView(View view) {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mBaseListAdapter = new BaseListAdapter(mListLive);
        mRecyclerView.setAdapter(mBaseListAdapter);

        mBaseListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
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


    protected void fillUI(JSONArray data) throws JSONException {

        mListLive.clear();
        mListLive.addAll(ApiUtils.formatDataToList(data,LiveJson.class));

        mBaseListAdapter.notifyDataSetChanged();

        if(mListLive.size() > 0){
            mLlLoadingDataEmpty.setVisibility(View.GONE);
        }else{
            mLlLoadingDataEmpty.setVisibility(View.VISIBLE);
        }

        mLlLoadingDataError.setVisibility(View.GONE);
    }
}

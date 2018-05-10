package com.liren.live.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.adapter.LiveUserAdapter;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseFragment;
import com.liren.live.bean.LiveJson;
import com.liren.live.utils.UIHelper;
import com.liren.live.widget.WPSwipeRefreshLayout;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 首页左边关注
 */
public class AttentionFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.rv_attentions)
    RecyclerView mRvAttentions;

    @BindView(R.id.mSwipeRefreshLayout)
    WPSwipeRefreshLayout mRefresh;
    //默认提示
    @BindView(R.id.fensi)
    LinearLayout mTvPrompt;
    @BindView(R.id.load)
    LinearLayout mLlFailLoad;

    private List<LiveJson> mUserList = new ArrayList<>();
    //当前加载的页数
    private int pager = 1;

    private View view;
    private LiveUserAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_attention, null);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {


        mAdapter = new LiveUserAdapter(mUserList);
        mRvAttentions.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvAttentions.setAdapter(mAdapter);
        mRefresh.setColorSchemeColors(getResources().getColor(R.color.global));

        //下拉刷新
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pager = 1;
                requestData(false);
            }
        });

        //点击
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UIHelper.startRtmpPlayerActivity(getContext(),mUserList.get(position));
            }
        });

        //上啦加载更多
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {

                if(mUserList.size() >= AppConfig.PAGE_DATA_COUNT){
                    pager ++;
                    requestData(false);
                }else{
                    mAdapter.loadMoreEnd();
                }


            }
        }, mRvAttentions);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();

        mRefresh.setRefreshing(true);
        pager = 1;
        requestData(false);
    }

    @Override
    protected void requestData(boolean refresh) {
        PhoneLiveApi.getAttentionLive(pager,AppContext.getInstance().getLoginUid(), callback);
    }

    private StringCallback callback = new StringCallback() {
        @Override
        public void onSuccess(String s, Call call, Response response) {
            if(mRefresh.isRefreshing()){
                pager = 2;
                mUserList.clear();
            }
            JSONArray res = ApiUtils.checkIsSuccess(s);
            if (null != res) {
                List<LiveJson> data = ApiUtils.formatDataToList(res,LiveJson.class);
                //上拉加载增加页数
                if(!mRefresh.isRefreshing()){

                    if(data.size() != 0){
                        pager ++;
                        mAdapter.loadMoreComplete();
                    }else{
                        mAdapter.loadMoreEnd();
                    }
                }else{
                    mAdapter.setNewData(mUserList);
                }

                mUserList.addAll(data);
            }else{
                if(!mRefresh.isRefreshing()){
                    mAdapter.loadMoreFail();
                }
            }

            mRefresh.setRefreshing(false);

            if (mUserList.size()>0){
                fillUI();
            }else{
                mTvPrompt.setVisibility(View.VISIBLE);
                mLlFailLoad.setVisibility(View.GONE);
                mRvAttentions.setVisibility(View.INVISIBLE);
            }
        }

    };

    private void fillUI() {
        mTvPrompt.setVisibility(View.GONE);
        mLlFailLoad.setVisibility(View.GONE);
        mRvAttentions.setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getAttentionLive");
    }

    @Override
    public void onRefresh() {
        requestData(false);
    }
}
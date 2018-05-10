package com.liren.live.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liren.live.AppConfig;
import com.liren.live.R;
import com.liren.live.adapter.HeaderAndFooterAdapter;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseFragment;
import com.liren.live.bean.LiveJson;
import com.liren.live.utils.LiveUtils;
import com.liren.live.utils.TDevice;
import com.liren.live.utils.UIHelper;
import com.liren.live.widget.SlideshowView;
import com.liren.live.widget.SpaceRecycleView;
import com.liren.live.widget.WPSwipeRefreshLayout;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.nineoldandroids.animation.ObjectAnimator;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @dw 首页热门
 */
public class HotFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.lv_live_room)
    RecyclerView mRecyclerView;

    //默认提示
    @BindView(R.id.ll_loading_data_empty)
    LinearLayout mLlLoadingDataEmpty;

    @BindView(R.id.ll_loading_data_error)
    LinearLayout mLlLoadingDataError;

    @BindView(R.id.mMarqueeView)
    TextView marqueeView;

    //小图切换
    LinearLayout mLlSmallLy;

    //大图切换
    LinearLayout mLlBigLy;

    private SlideshowView mSlideshowView;

    @BindView(R.id.refreshLayout)
    WPSwipeRefreshLayout mSwipeRefreshLayout;

    private List<LiveJson> mUserList = new ArrayList<>();

    private LayoutInflater inflater;

    private HeaderAndFooterAdapter headerAndFooterAdapter;

    private boolean isFirst = true;
    private View headView;

    private int pager = 1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index_hot,null);
        ButterKnife.bind(this,view);
        this.inflater = inflater;

        initView();
        initData();

        return view;
    }
    private void initView(){

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.global));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(gridLayoutManager);

        headView = inflater.inflate(R.layout.view_hot_rollpic,null);
        mSlideshowView = (SlideshowView) headView.findViewById(R.id.slideshowView);

        mRecyclerView.setAdapter(headerAndFooterAdapter);
        mRecyclerView.addItemDecoration(new SpaceRecycleView(1));

        marqueeView.setText(LiveUtils.getConfigFiled(getContext(),"system_notice"));

        initAdapter(R.layout.item_hot_user);

    }

    @Override
    public void initData() {

        marqueeView.setText(LiveUtils.getConfigFiled(getContext(),"system_notice"));
        ObjectAnimator animator = ObjectAnimator.ofFloat(marqueeView,"translationX", TDevice.getScreenWidth(),-marqueeView.getWidth());
        animator.setDuration(10000);
        animator.setRepeatMode(Animation.RESTART);
        animator.setRepeatCount(-1);
        animator.start();
    }


    //初始化适配器
    private void initAdapter(int layout) {

        headerAndFooterAdapter = new HeaderAndFooterAdapter(mUserList,layout);

        //上拉加载更多
        headerAndFooterAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                if(mUserList.size() >= AppConfig.PAGE_DATA_COUNT){
                    selectTermsScreen();
                }else{
                    headerAndFooterAdapter.loadMoreEnd();
                }

            }
        },mRecyclerView);
        headerAndFooterAdapter.openLoadAnimation();
        headerAndFooterAdapter.addHeaderView(headView);

        mLlBigLy = (LinearLayout) headView.findViewById(R.id.ll_big_ly);
        mLlSmallLy = (LinearLayout) headView.findViewById(R.id.ll_small_ly);
        //切换小视图
        mLlSmallLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                headerAndFooterAdapter.removeHeaderView(headView);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
                mRecyclerView.setLayoutManager(gridLayoutManager);

                ((ImageView)mLlSmallLy.getChildAt(0)).setImageResource(R.drawable.explore_hot_small_selected_bingbing);
                ((ImageView)mLlBigLy.getChildAt(0)).setImageResource(R.drawable.explore_hot_big);


                ((TextView)mLlSmallLy.getChildAt(1)).setTextColor(getResources().getColor(R.color.global));
                ((TextView)mLlBigLy.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorGray3));
                initAdapter(R.layout.item_hot_small);
            }
        });

        //切换大视图
        mLlBigLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                headerAndFooterAdapter.removeHeaderView(headView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(linearLayoutManager);

                ((ImageView)mLlSmallLy.getChildAt(0)).setImageResource(R.drawable.explore_hot_small);
                ((ImageView)mLlBigLy.getChildAt(0)).setImageResource(R.drawable.explore_hot_big_selected_bingbing);


                ((TextView)mLlSmallLy.getChildAt(1)).setTextColor(getResources().getColor(R.color.colorGray3));
                ((TextView)mLlBigLy.getChildAt(1)).setTextColor(getResources().getColor(R.color.global));
                initAdapter(R.layout.item_hot_user);
            }
        });
        mRecyclerView.setAdapter(headerAndFooterAdapter);

        headerAndFooterAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UIHelper.startRtmpPlayerActivity(getContext(),mUserList.get(position));
            }
        });
    }

    private StringCallback callback = new StringCallback() {
        @Override
        public void onSuccess(String s, Call call, Response response) {
            JSONArray res = ApiUtils.checkIsSuccess(s);

            try {
                if(res != null){
                    if(mSwipeRefreshLayout.isRefreshing()){
                        pager = 2;
                        mUserList.clear();
                    }

                    //直播数据
                    JSONArray list = res.getJSONObject(0).getJSONArray("list");
                    List<LiveJson> data = ApiUtils.formatDataToList(list,LiveJson.class);
                    if(!mSwipeRefreshLayout.isRefreshing()){

                        if(data.size() != 0){
                            pager ++;
                            headerAndFooterAdapter.loadMoreComplete();
                        }else{
                            headerAndFooterAdapter.loadMoreEnd();
                        }

                    }else{
                        //下拉刷新
                        headerAndFooterAdapter.setNewData(mUserList);
                    }

                    mUserList.addAll(data);
                    //轮播

                    if(isFirst){
                        JSONArray rollPics = res.getJSONObject(0).getJSONArray("slide");
                        mSlideshowView.addDataToUI(rollPics);
                    }

                    isFirst = false;
                    fillUI();

                }else{
                    if(!mSwipeRefreshLayout.isRefreshing()){
                        headerAndFooterAdapter.loadMoreFail();
                    }
                    mLlLoadingDataEmpty.setVisibility(View.VISIBLE);
                    mLlLoadingDataError.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSwipeRefreshLayout.setRefreshing(false);
        }


    };

    private void fillUI() {

        mLlLoadingDataEmpty.setVisibility(View.GONE);
        mLlLoadingDataError.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        headerAndFooterAdapter.notifyDataSetChanged();
    }

    public void selectTermsScreen(){
        PhoneLiveApi.requestHotData(pager,callback);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSwipeRefreshLayout.setRefreshing(true);
        pager = 1;
        selectTermsScreen();
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        pager = 1;
        selectTermsScreen();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("selectTermsScreen");
    }
}

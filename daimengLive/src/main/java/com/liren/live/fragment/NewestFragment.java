package com.liren.live.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.AppManager;
import com.liren.live.R;
import com.liren.live.adapter.NewestAdapter;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseFragment;
import com.liren.live.bean.LiveJson;
import com.liren.live.ui.logicactivity.NewLoginActivity;
import com.liren.live.utils.UIHelper;
import com.liren.live.widget.SpaceRecycleView;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

import static com.liren.live.api.remote.ApiUtils.TOKEN_TIMEOUT;

/**
 * 首页最新直播
 */
public class NewestFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    List<LiveJson> mUserList = new ArrayList<>();

    @BindView(R.id.rv_newest)
    RecyclerView mRecyclerView;

    @BindView(R.id.sl_newest)
    SwipeRefreshLayout mRefresh;
    //默认提示
    @BindView(R.id.ll_loading_data_empty)
    LinearLayout mLlLoadDataEmpty;

    @BindView(R.id.ll_loading_data_error)
    LinearLayout mLlLoadDataError;

    private NewestAdapter newestAdapter;

    //当前加载的页数
    private int pager = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newest, null);

        ButterKnife.bind(this, view);
        initData();
        initView(view);
        return view;
    }

    @Override
    public void initData() {

        newestAdapter = new NewestAdapter(mUserList);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerView.setAdapter(newestAdapter);
    }

    @Override
    public void initView(View view) {

        mRecyclerView.addItemDecoration(new SpaceRecycleView(1));
        newestAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UIHelper.startRtmpPlayerActivity(getContext(),mUserList.get(position));
            }
        });

        newestAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override public void onLoadMoreRequested() {

                if(mUserList.size() >= AppConfig.PAGE_DATA_COUNT){
                    requestData();
                }else{
                    newestAdapter.loadMoreEnd();
                }

            }
        }, mRecyclerView);

        mRefresh.setColorSchemeColors(getResources().getColor(R.color.global));
        mRefresh.setOnRefreshListener(this);
    }
    private   JSONArray checkNewSuccess(String res){
        JSONObject resJson = null;
        String newRes;
        try {
            resJson = new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
            newRes= res.substring(res.lastIndexOf("ret")-2);
            try {
                resJson = new JSONObject(newRes);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        try {
            if(Integer.parseInt(resJson.getString("ret")) == 200){
                JSONObject dataJson =  resJson.getJSONObject("data");
                String code = dataJson.getString("code");
                if(code.equals(TOKEN_TIMEOUT)){

                    AppManager.getAppManager().finishAllActivity();
                    Intent intent = new Intent(AppContext.getInstance(), NewLoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    AppContext.getInstance().startActivity(intent);

                    return null;

                }else if(!code.equals("0")){
                    AppContext.showToast(dataJson.get("msg").toString());
                    //Toast.makeText(AppContext.getInstance(),dataJson.get("msg").toString(),Toast.LENGTH_LONG).show();
                    return null;
                }else {
                    return dataJson.getJSONArray("info");
                }
            }else{
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    //最新主播数据请求
    private void requestData() {

        PhoneLiveApi.getNewestUserList(pager,new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                if(mRefresh.isRefreshing()){
                    pager = 2;
                    mUserList.clear();
                }

                JSONArray res = checkNewSuccess(s);

                if (null != res) {

                    List<LiveJson> data = ApiUtils.formatDataToList(res,LiveJson.class);
                    //上拉加载增加页数
                    if(!mRefresh.isRefreshing()){

                        if(data.size() != 0){
                            pager ++;
                            newestAdapter.loadMoreComplete();
                        }else{
                            newestAdapter.loadMoreEnd();
                        }
                    }else{
                        newestAdapter.setNewData(mUserList);
                    }
                    mUserList.addAll(data);

                }else{
                    mLlLoadDataEmpty.setVisibility(View.VISIBLE);
                    mLlLoadDataError.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                }

                mRefresh.setRefreshing(false);

                if (mUserList.size() > 0){
                    fillUI();
                }else{
                    mLlLoadDataEmpty.setVisibility(View.VISIBLE);
                    mLlLoadDataError.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                }

            }

        });
    }

    private void fillUI() {

        mLlLoadDataEmpty.setVisibility(View.GONE);
        mLlLoadDataError.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        newestAdapter.notifyDataSetChanged();

    }

    @Override
    public void onRefresh() {
        //下拉刷新重置页数
        pager = 1;
        requestData();
    }

    @Override
    public void onResume() {
        super.onResume();

        mRefresh.setRefreshing(true);
        pager = 1;
        requestData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getNewestUserList");
    }



}

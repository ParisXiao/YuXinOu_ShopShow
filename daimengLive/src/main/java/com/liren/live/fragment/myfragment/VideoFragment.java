package com.liren.live.fragment.myfragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.liren.live.R;
import com.liren.live.adapter.myadapter.VideoListAdapter;
import com.liren.live.base.MyBaseFragment;
import com.liren.live.config.UrlConfig;
import com.liren.live.config.UserConfig;
import com.liren.live.entity.VideoListEntity;
import com.liren.live.ui.empty.MyEmptyLayout;
import com.liren.live.utils.GridSpacingItemDecoration;
import com.liren.live.utils.OKHttpUtils;
import com.liren.live.utils.PreferenceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/5/6 0006.
 */

public class VideoFragment extends MyBaseFragment {
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.myEmpty)
    MyEmptyLayout myEmpty;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    Unbinder unbinder;
    private int pageindex = 1;
    private VideoListAdapter adapter;
    private List<VideoListEntity> list = null;

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_livelist;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    protected void initData() {
        list = new ArrayList<>();
        initLoad();
        recycler.setLayoutManager( new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false));
        recycler.addItemDecoration(new GridSpacingItemDecoration(2,20,true));
        recycler.setAdapter(adapter = new VideoListAdapter(getActivity(), list));
        initLoadMoreListener();
        adapter.setOnItemClickListener(new VideoListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onClick(View view, VideoListAdapter.ViewName viewName, int position) {
                if (viewName== VideoListAdapter.ViewName.ITEM){
//                    跳转
                }
            }
        });
    }

    private void initLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData(list);
            }
        }, 1000);
        //绑定
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        myEmpty.bindView(recycler);
        myEmpty.setOnButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新加载数据
                pageindex=1;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData(list);
                    }
                }, 1000);
            }
        });
        refreshLayout.setColorSchemeResources(R.color.pro_start, R.color.pro_center, R.color.pro_end);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageindex=1;
                        getData(list);

                    }
                }, 1000);
            }
        });
    }

    private void getData(final List<VideoListEntity> list) {
        list.clear();
            Observable.create(new Observable.OnSubscribe<Integer>() {
                @Override
                public void call(Subscriber<? super Integer> subscriber) {
                    if (OKHttpUtils.isConllection(getActivity())) {
                        String[] key = new String[]{"pageindex", "pagerows"};
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("pageindex", pageindex + "");
                        map.put("pagerows", "20");
                        String sign = "pageindex" + pageindex + "pagerows" + "20";
                        String token = PreferenceUtils.getInstance(getActivity()).getString(UserConfig.DToken);
                        String result = OKHttpUtils.postData(getActivity(), UrlConfig.SelHotVideo, token, sign, key, map);
                        if (!TextUtils.isEmpty(result)) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(result);
                                String code = jsonObject.getString("code");
                                if (code.equals("0")) {
                                    dismisDialog();
                                    JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                                    JSONArray jsonArray = new JSONArray(jsonObject1.getString("data"));
                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            VideoListEntity listEntity = new VideoListEntity();
                                            listEntity.setID(jsonArray.getJSONObject(i).getString("id"));
                                            listEntity.setClickLikeNum(jsonArray.getJSONObject(i).getString("clicklikenum"));
                                            listEntity.setClicknum(jsonArray.getJSONObject(i).getString("clicknum"));
                                            listEntity.setCommentsNum(jsonArray.getJSONObject(i).getString("commentsnum"));
                                            listEntity.setReleaseaddress(jsonArray.getJSONObject(i).getString("releaseaddress"));
                                            listEntity.setReleaseid(jsonArray.getJSONObject(i).getString("releaseid"));
                                            listEntity.setVideoName(jsonArray.getJSONObject(i).getString("videoname"));
                                            listEntity.setVideoImagePath(jsonArray.getJSONObject(i).getString("videoimagepath"));
                                            listEntity.setVideoPath(jsonArray.getJSONObject(i).getString("videopath"));
                                            listEntity.setWhetherhot(jsonArray.getJSONObject(i).getString("whetherhot"));
                                            listEntity.setReleasetime(jsonArray.getJSONObject(i).getString("releasetime"));
                                            list.add(listEntity);
                                        }
                                    }
                                    subscriber.onNext(0);

//                                成功
                                } else {
                                    dismisDialog();
                                    subscriber.onNext(1);
//                                离线

                                }

                            } catch (JSONException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                                subscriber.onNext(4);
                            }
                        } else {
                            dismisDialog();
                            subscriber.onNext(3);
                        }
                    } else {
                        dismisDialog();
                        subscriber.onNext(2);

                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(Integer integer) {
                    switch (integer) {
                        case 0:
                            adapter.notifyDataSetChanged();
                            refreshLayout.setRefreshing(false);
                            break;
                        case 1:
                            refreshLayout.setRefreshing(false);

                            adapter.notifyDataSetChanged();
                            showToast("获取数据失败");
                            break;
                        case 2:
                            refreshLayout.setRefreshing(false);
                            showToast("网络异常，请检查网络设置");
                            break;
                        case 3:
                            refreshLayout.setRefreshing(false);
                            showToast("服务器异常");
                            break;
                        case 4:
                            refreshLayout.setRefreshing(false);
                            showToast("数据解析异常");
                            break;
                    }
                }
            });
        }

    private void initLoadMoreListener() {
        adapter.changeMoreStatus(adapter.NO_LOAD_MORE);

        recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                adapter.changeMoreStatus(adapter.PULLUP_LOAD_MORE);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {

                    //设置正在加载更多
                    adapter.changeMoreStatus(adapter.LOADING_MORE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            List<VideoListEntity> list = new ArrayList<VideoListEntity>();
                            pageindex += 1;
                            getData(list);
                            adapter.AddFooterItem(list);
                            //设置回到上拉加载更多
                            Toast.makeText(getActivity(), "更新了 " + list.size() + " 条数据", Toast.LENGTH_SHORT).show();
                            adapter.changeMoreStatus(adapter.NO_LOAD_MORE);
                        }
                    }, 1000);


                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

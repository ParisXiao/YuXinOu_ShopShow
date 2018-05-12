package com.liren.live.ui.logicactivity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.adapter.myadapter.ChooseSellerAdapter;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.config.UrlConfig;
import com.liren.live.ui.empty.MyEmptyLayout;
import com.liren.live.utils.OKHttpUtils;
import com.liren.live.widget.BlackTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 商家选择
 * Created by Administrator on 2018/5/5 0005.
 */

public class ChooseSellerActivity extends MyBaseActivity {
    @BindView(R.id.toolbar_back)
    ImageView toolbarBack;
    @BindView(R.id.toolbar_title)
    BlackTextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit_seller)
    EditText editSeller;
    @BindView(R.id.img_search_seller)
    ImageView imgSearchSeller;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.myEmpty)
    MyEmptyLayout myEmpty;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.toolbar_right)
    TextView toolbarRight;
   int pageindex=1;
    ChooseSellerAdapter adapter;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_chooseseller;
    }

    @Override
    protected void initView() {
        toolbarTitle.setText("商家选择");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarRight.setVisibility(View.VISIBLE);
        toolbarRight.setTextColor(0xf06f32);
        toolbarRight.setText("确认");

    }

    @Override
    protected void initData() {
        initLoad();
    }
    @OnClick({R.id.toolbar_back})
    public void chooseClick(View v){
        switch (v.getId()){
            case R.id.toolbar_back:
                finish();
                break;
        }
    }
    private void initLoad() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
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
                myEmpty.showLoading(ChooseSellerActivity.this);
                //重新加载数据
                        getData();
            }
        });
        refreshLayout.setColorSchemeResources(R.color.pro_start, R.color.pro_center,R.color.pro_end);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                        getData();
            }
        });
    }

    private void getData() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (OKHttpUtils.isConllection( ChooseSellerActivity.this)) {
                    String[] key = new String[]{"app_access_token","SortTypeID","pageindex", "pagerows"};
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("app_access_token",AppContext.getInstance().getDToken());
                    map.put("SortTypeID", "1");
                    map.put("pageindex", pageindex + "");
                    map.put("pagerows", "20");
//                        String token = PreferenceUtils.getInstance(getActivity()).getString(UserConfig.DToken);
                    String token = AppContext.getInstance().getToken();
                    String result = OKHttpUtils.postData(ChooseSellerActivity.this, UrlConfig.MemberPushSupplier, token, key, map);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}

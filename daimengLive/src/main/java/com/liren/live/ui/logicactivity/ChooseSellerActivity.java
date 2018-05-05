package com.liren.live.ui.logicactivity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.liren.live.R;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.ui.empty.MyEmptyLayout;
import com.liren.live.widget.BlackTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        myEmpty.showLoading(this);
        myEmpty.bindView(recycler);
        myEmpty.setOnButtonClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEmpty.showLoading(ChooseSellerActivity.this);
                //重新加载数据
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                }, 1000);
            }
        });
        refreshLayout.setColorSchemeResources(R.color.pro_start, R.color.pro_center,R.color.pro_end);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myEmpty.showLoading(ChooseSellerActivity.this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                }, 1000);
            }
        });
    }

    private void getData() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}

package com.liren.live.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.liren.live.R;
import com.liren.live.adapter.OrderAdapter;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseActivity;
import com.liren.live.bean.OrderBean;
import com.liren.live.ui.customviews.ActivityTitle;
import com.liren.live.utils.UIHelper;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;

import java.util.ArrayList;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 贡献排行榜
 */
public class DedicateOrderActivity extends BaseActivity {
    private ArrayList<OrderBean> mOrderList = new ArrayList<>();

    @BindView(R.id.lv_order)
    ListView mOrderListView;

    @BindView(R.id.view_title)
    ActivityTitle mActivityTitle;
    private OrderAdapter mOrderAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order;
    }

    @Override
    public void initView() {
        mOrderAdapter = new OrderAdapter(mOrderList,this);
        mOrderListView.setAdapter(mOrderAdapter);

        mOrderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIHelper.showHomePageActivity(DedicateOrderActivity.this,mOrderList.get(position).getUid());
            }
        });

        mActivityTitle.setReturnListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    public void initData() {

        requestGetData();

    }

    private void requestGetData() {

        PhoneLiveApi.getYpOrder(getIntent().getStringExtra("uid"), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                JSONArray res = ApiUtils.checkIsSuccess(response.body().toString());

                if(null != res){
                    mOrderList.clear();
                    mOrderList.addAll(ApiUtils.formatDataToList(res,OrderBean.class));
                    fillUI();
                }
            }

        });
    }

    private void fillUI() {

       mOrderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getYpOrder");
    }
}

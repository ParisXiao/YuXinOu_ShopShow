package com.liren.live.fragment;

import android.view.View;

import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.ListBaseFragment;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.Call;
import okhttp3.Response;


public class NearbyFragment extends ListBaseFragment {

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
}

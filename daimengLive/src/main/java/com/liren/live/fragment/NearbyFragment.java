package com.liren.live.fragment;

import android.view.View;

import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.ListBaseFragment;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.Call;


public class NearbyFragment extends ListBaseFragment {

    @Override
    protected void requestData(boolean refresh) {
        PhoneLiveApi.requestGetGameLive(new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                mSwipeRefresh.setRefreshing(false);
                mLlLoadingDataEmpty.setVisibility(View.GONE);
                mLlLoadingDataError.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResponse(String response, int id) {
                mSwipeRefresh.setRefreshing(false);
                JSONArray data = ApiUtils.checkIsSuccess(response);
                if(data != null){
                    try {

                        fillUI(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

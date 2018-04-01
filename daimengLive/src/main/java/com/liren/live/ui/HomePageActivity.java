package com.liren.live.ui;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liren.live.utils.SimpleUtils;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.adapter.LiveRecordAdapter;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseActivity;
import com.liren.live.bean.LiveRecordBean;
import com.liren.live.bean.PrivateChatUserBean;
import com.liren.live.bean.UserHomePageBean;
import com.liren.live.utils.LiveUtils;
import com.liren.live.utils.StringUtils;
import com.liren.live.utils.TDevice;
import com.liren.live.utils.UIHelper;
import com.liren.live.widget.AvatarView;
import com.liren.live.widget.BlackTextView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


public class HomePageActivity extends BaseActivity {


    
    @BindView(R.id.tv_home_page_uname)
    BlackTextView mTvUNice;

    @BindView(R.id.iv_home_page_sex)
    ImageView mUSex;

    @BindView(R.id.iv_home_page_level)
    ImageView mULevel;

    
    @BindView(R.id.av_home_page_uhead)
    AvatarView mUHead;

    
    @BindView(R.id.tv_home_page_follow)
    BlackTextView mTvUFollowNum;

    
    @BindView(R.id.tv_home_page_fans)
    BlackTextView mTvUFansNum;

    
    @BindView(R.id.tv_home_page_sign)
    BlackTextView mTvUSign;

    @BindView(R.id.tv_home_page_sign2)
    BlackTextView mTvUSign2;

    @BindView(R.id.tv_home_page_num)
    BlackTextView mTvUNum;

    @BindView(R.id.ll_default_video)
    LinearLayout mDefaultVideoBg;

    @BindView(R.id.ll_home_page_index)
    LinearLayout mHomeIndexPage;

    @BindView(R.id.ll_home_page_video)
    LinearLayout mHomeVideoPage;

    @BindView(R.id.tv_home_page_index_btn)
    BlackTextView mPageIndexBtn;

    @BindView(R.id.tv_home_page_video_btn)
    BlackTextView mPageVideoBtn;

    @BindView(R.id.tv_home_page_menu_follow)
    BlackTextView mFollowState;

    @BindView(R.id.tv_home_page_black_state)
    BlackTextView mTvBlackState;

    @BindView(R.id.ll_home_page_bottom_menu)
    LinearLayout mLLBottomMenu;

    @BindView(R.id.rv_live_record)
    RecyclerView mRvLiveRecord;

    @BindView(R.id.fensi)
    LinearLayout mLoadingDataEmpty;

    @BindView(R.id.load)
    LinearLayout mLoadingDataError;

    @BindView(R.id.view_1)
    View mViewLine1;

    @BindView(R.id.view_2)
    View mViewLine2;

    @BindView(R.id.rl_live_status)
    RelativeLayout mRlLiveStatusView;

    
    @BindView(R.id.iv_home_page_pic)
    ImageView mIvUserPic;

    
    private LiveRecordBean mLiveRecordBean;

    private String uid;

    private AvatarView[] mOrderTopNoThree = new AvatarView[3];

    private UserHomePageBean mUserHomePageBean;

    private ArrayList<LiveRecordBean> mRecordList = new ArrayList<>();

    private LiveRecordAdapter mLiveRecordAdapter;

    
    private int recordPager = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initView()  {

        mOrderTopNoThree [0] = (AvatarView) findViewById(R.id.av_home_page_order1);
        mOrderTopNoThree [1] = (AvatarView) findViewById(R.id.av_home_page_order2);
        mOrderTopNoThree [2] = (AvatarView) findViewById(R.id.av_home_page_order3);
        mLiveRecordAdapter = new LiveRecordAdapter(mRecordList);
        mLiveRecordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mLiveRecordBean = mRecordList.get(position);
                
                showLiveRecord();
            }
        });
        mRvLiveRecord.setLayoutManager(new LinearLayoutManager(this));
        mRvLiveRecord.setAdapter(mLiveRecordAdapter);

        ((TextView)findViewById(R.id.tv_home_tick_order)).setText(AppConfig.TICK_NAME + "排行榜");
    }

    @Override
    public void initData() {

        uid = getIntent().getStringExtra("uid");

        if(uid.equals(getUserID())){
            mLLBottomMenu.setVisibility(View.GONE);
        }

        
        PhoneLiveApi.getHomePageUInfo(getUserID(), uid,new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {
            }

            @Override
            public void onResponse(String response,int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(res != null){

                    try {
                        mUserHomePageBean = new Gson().fromJson(res.getString(0), UserHomePageBean.class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    fillUIUserInfo();
                }
            }
        });
    }


    
    private void fillUIUserInfo() {


        if(mUserHomePageBean.islive.equals("1")){
            mRlLiveStatusView.setVisibility(View.VISIBLE);
            mRlLiveStatusView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UIHelper.startRtmpPlayerActivity(HomePageActivity.this,mUserHomePageBean.liveinfo);
                }
            });
        }else{
            mRlLiveStatusView.setVisibility(View.GONE);
        }

        mUHead.setAvatarUrl(mUserHomePageBean.avatar_thumb);
        mTvUNice.setText(mUserHomePageBean.user_nicename);
        mUSex.setImageResource(LiveUtils.getSexRes(mUserHomePageBean.sex));
        mULevel.setImageResource(LiveUtils.getLevelRes(mUserHomePageBean.level));
        mTvUFansNum.setText(  getString(R.string.fans) + ":" + mUserHomePageBean.fans);
        mTvUFollowNum.setText(getString(R.string.attention) + ":" + mUserHomePageBean.follows);
        mTvUSign.setText(mUserHomePageBean.signature);
        mTvUSign2.setText(mUserHomePageBean.signature);
        mTvUNum.setText(mUserHomePageBean.id);
        mFollowState.setText(StringUtils.toInt(mUserHomePageBean.isattention) == 0 ? getString(R.string.follow2) : getString(R.string.alreadyfollow));
        mTvBlackState.setText(StringUtils.toInt(mUserHomePageBean.isblack) == 0 ? getString(R.string.pullblack):getString(R.string.relieveblack));

        SimpleUtils.loadImageForView(this,mIvUserPic,mUserHomePageBean.avatar,0);

        List<UserHomePageBean.ContributeBean> os =  mUserHomePageBean.contribute;
        for(int i = 0;i<os.size(); i++){
            mOrderTopNoThree[i].setAvatarUrl(os.get(i).getAvatar());
        }


        if(null != mUserHomePageBean.liverecord){
            mRecordList.clear();
            mRecordList.addAll(mUserHomePageBean.liverecord);

            if(mRecordList.size() != 0){
                mRvLiveRecord.setVisibility(View.VISIBLE);
                mLoadingDataEmpty.setVisibility(View.GONE);
                mLoadingDataError.setVisibility(View.GONE);
                mLiveRecordAdapter.notifyDataSetChanged();
            }else{
                mRvLiveRecord.setVisibility(View.INVISIBLE);
                mLoadingDataEmpty.setVisibility(View.VISIBLE);
                mLoadingDataError.setVisibility(View.GONE);
            }


        }else{
            mRvLiveRecord.setVisibility(View.INVISIBLE);
            mLoadingDataEmpty.setVisibility(View.VISIBLE);
            mLoadingDataError.setVisibility(View.GONE);
        }

    }

    @OnClick({R.id.ll_home_page_menu_lahei,R.id.ll_home_page_menu_privatechat,R.id.tv_home_page_menu_follow,R.id.rl_home_pager_yi_order,R.id.tv_home_page_follow,R.id.tv_home_page_index_btn,R.id.tv_home_page_video_btn,R.id.iv_home_page_back,R.id.tv_home_page_fans})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_home_page_menu_privatechat:
                
                openPrivateChat();
                break;
            case R.id.ll_home_page_menu_lahei:
                
                pullTheBlack();
                break;
            case R.id.tv_home_page_menu_follow:
                
                followUserOralready();
                break;
            case R.id.tv_home_page_index_btn:
                
                changeLineStatus(true);
                mHomeIndexPage.setVisibility(View.VISIBLE);
                mHomeVideoPage.setVisibility(View.GONE);
                mPageIndexBtn.setTextColor(getResources().getColor(R.color.global));
                mPageVideoBtn.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.tv_home_page_video_btn:
                
                changeLineStatus(false);
                mHomeIndexPage.setVisibility(View.GONE);
                mHomeVideoPage.setVisibility(View.VISIBLE);
                mPageIndexBtn.setTextColor(getResources().getColor(R.color.black));
                mPageVideoBtn.setTextColor(getResources().getColor(R.color.global));

                break;
            case R.id.iv_home_page_back:
                finish();
                break;
            case R.id.tv_home_page_fans:
                
                UIHelper.showFansListActivity(this,uid);
                break;
            case R.id.tv_home_page_follow:
                
                UIHelper.showAttentionActivity(this, uid);
                break;
            case R.id.rl_home_pager_yi_order:
                
                OrderWebViewActivity.startOrderWebView(this,uid);
                break;
        }

    }

    private void changeLineStatus(boolean status) {
        if(status){
            mViewLine1.setBackgroundResource(R.color.global);
            mViewLine2.setBackgroundColor(Color.parseColor("#E2E2E2"));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewLine1.getLayoutParams();
            params.height = (int) TDevice.dpToPixel(2);
            mViewLine1.setLayoutParams(params);

            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) mViewLine2.getLayoutParams();
            params2.height = 1;
            mViewLine2.setLayoutParams(params2);
        }else{
            mViewLine2.setBackgroundResource(R.color.global);
            mViewLine1.setBackgroundColor(Color.parseColor("#E2E2E2"));

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mViewLine2.getLayoutParams();
            params.height = (int) TDevice.dpToPixel(2);
            mViewLine2.setLayoutParams(params);

            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) mViewLine1.getLayoutParams();
            params2.height = 1;
            mViewLine1.setLayoutParams(params2);
        }

    }


    
    private void showLiveRecord() {

        showWaitDialog("正在获取回放...",false);

        PhoneLiveApi.getLiveRecordById(mLiveRecordBean.getId(),new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {
                hideWaitDialog();
            }

            @Override
            public void onResponse(String response,int id) {
                hideWaitDialog();
                JSONArray res = ApiUtils.checkIsSuccess(response);

                if(res != null){
                    try {
                        mLiveRecordBean.setVideo_url(res.getJSONObject(0).getString("url"));
                        LiveRecordPlayerActivity.startVideoBack(HomePageActivity.this,mLiveRecordBean);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }


    
    private void pullTheBlack() {
        PhoneLiveApi.pullTheBlack(AppContext.getInstance().getLoginUid(),uid,
                AppContext.getInstance().getToken(),
                new StringCallback(){

            @Override
            public void onError(Call call, Exception e,int id) {
                AppContext.showToast("操作失败");
            }

            @Override
            public void onResponse(String response,int id) {
                JSONArray res = ApiUtils.checkIsSuccess(response);
                if(null == res)return;
                if(StringUtils.toInt(mUserHomePageBean.isblack) == 0){
                    
                    try {
                        EMClient.getInstance().contactManager().addUserToBlackList(String.valueOf(mUserHomePageBean.id),true);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        EMClient.getInstance().contactManager().removeUserFromBlackList(String.valueOf(mUserHomePageBean.id));
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }

                int isBlack = StringUtils.toInt(mUserHomePageBean.isblack);

                mUserHomePageBean.isblack = (isBlack == 0 ? "1" : "0");

                mTvBlackState.setText(isBlack == 0 ? getString(R.string.relieveblack) : getString(R.string.pullblack));
                AppContext.showToast( isBlack == 0?"拉黑成功":"解除拉黑",0);

            }
        });
    }

    
    private void openPrivateChat() {

        if(StringUtils.toInt(mUserHomePageBean.isblack2) == 1){
            AppContext.showToast("你已被对方拉黑无法私信");
            return;
        }

        if(null != mUserHomePageBean){

            PhoneLiveApi.getPmUserInfo(getUserID(),mUserHomePageBean.id, new StringCallback() {
                @Override
                public void onError(Call call, Exception e,int id) {

                }

                @Override
                public void onResponse(String response,int id) {
                    JSONArray res = ApiUtils.checkIsSuccess(response);
                    if(null != res)
                        try {
                            UIHelper.showPrivateChatMessage(HomePageActivity.this,
                                    new Gson().fromJson(res.getString(0),PrivateChatUserBean.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                }
            });

        }

    }


    private void followUserOralready() {

        PhoneLiveApi.showFollow(getUserID(),uid,getUserToken(),new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {

            }

            @Override
            public void onResponse(String response,int id) {
                mUserHomePageBean.isattention = (
                        StringUtils.toInt(mUserHomePageBean.isattention) == 0 ? "1" : "0");

                if (StringUtils.toInt(mUserHomePageBean.isattention) == 0 ){
                    mFollowState.setText(getString(R.string.follow2));
                }else{

                    mFollowState.setText(getString(R.string.alreadyfollow));
                    if (StringUtils.toInt(mUserHomePageBean.isblack) != 0){
                        pullTheBlack();
                    }
                }
            }
        });
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag("getHomePageUInfo");
        OkHttpUtils.getInstance().cancelTag("showFollow");
        OkHttpUtils.getInstance().cancelTag("getPmUserInfo");
        OkHttpUtils.getInstance().cancelTag("pullTheBlack");
        OkHttpUtils.getInstance().cancelTag("getHomePageUInfo");
        OkHttpUtils.getInstance().cancelTag("getLiveRecord");
    }
}

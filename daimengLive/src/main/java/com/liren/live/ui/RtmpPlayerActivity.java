package com.liren.live.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.liren.live.AppConfig;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.base.ShowLiveActivityBase;
import com.liren.live.bean.ChatBean;
import com.liren.live.bean.LiveJson;
import com.liren.live.bean.SimpleUserInfo;
import com.liren.live.bean.UserBean;
import com.liren.live.event.Event;
import com.liren.live.fragment.GiftListDialogFragment;
import com.liren.live.ui.dialog.LiveCommon;
import com.liren.live.ui.im.IMControl;
import com.liren.live.ui.customviews.SwipeAnimationController;
import com.liren.live.utils.LiveUtils;
import com.liren.live.utils.SocketMsgUtils;
import com.liren.live.utils.StringUtils;
import com.liren.live.utils.TLog;
import com.liren.live.widget.LoadUrlImageView;
import com.liren.live.widget.VideoSurfaceView;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.bean.SendGiftBean;
import com.liren.live.interf.IMControlInterface;
import com.liren.live.utils.DialogHelp;
import com.liren.live.utils.ShareUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


public class RtmpPlayerActivity extends ShowLiveActivityBase {

    public final static String USER_INFO = "USER_INFO";

    @BindView(R.id.video_view)
    VideoSurfaceView mVideoSurfaceView;

    
    @BindView(R.id.iv_live_look_loading_bg)
    LoadUrlImageView mIvLoadingBg;

    @BindView(R.id.tv_attention)
    TextView mIvAttention;

    
    @BindView(R.id.iv_live_direction)
    ImageView mIvDirectionSwitch;

    @BindView(R.id.tv_live_charging_time)
    TextView mTvChargingTime;

    private KSYMediaPlayer ksyMediaPlayer;

    private Surface mSurface = null;

    
    private int mVideoWidth;

    
    private int mVideoHeight;

    private int speak;
    public LiveJson mEmceeInfo;

    private View mLoadingView;

    private SurfaceHolder mHolder;

    private GiftListDialogFragment mGiftListDialogFragment;

    private SwipeAnimationController mSwipeAnimationController;
    
    private boolean isLit = false;
    
    private int countDownTime = 0;
    
    private int lookTime = 0;

    @Override
    protected int getLayoutId() {
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        return R.layout.activity_live_player;
    }

    @Override
    public void initView() {
        super.initView();

        SurfaceHolder mSurfaceHolder = mVideoSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);

        mVideoSurfaceView.setOnTouchListener(mTouchListener);
        mVideoSurfaceView.setKeepScreenOn(true);

        
        mDanmuControl.show();


        mSwipeAnimationController = new SwipeAnimationController(this);

        mSwipeAnimationController.setAnimationView(mLiveContent);

        
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            mLvChatList.setVisibility(View.GONE);
        }

        
        if(getIntent().getBundleExtra(USER_INFO).getInt("type",0) == 4){
            mIvDirectionSwitch.setVisibility(View.VISIBLE);
        }else{
            mIvDirectionSwitch.setVisibility(View.GONE);
        }

    }


    @Override
    public void initData() {
        super.initData();

        Bundle bundle = getIntent().getBundleExtra(USER_INFO);
        
        mUser = AppContext.getInstance().getLoginUser();
        
        mEmceeInfo = bundle.getParcelable(USER_INFO);

        mStreamName = mEmceeInfo.stream;

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        initLive();
        requestRoomInfo();
        
        initRoomInfo();
    }

    private void initRoomInfo() {

        
        mIvLoadingBg.setVisibility(View.VISIBLE);
        mIvLoadingBg.setImageLoadUrl(mEmceeInfo.avatar);

        mRoomNum = mEmceeInfo.uid;
        mTvLiveNumber.setText(String.format(Locale.CHINA,"房间: %s" ,mEmceeInfo.uid));
        mEmceeHead.setAvatarUrl(mEmceeInfo.avatar);
        



        

        final Bundle bundle = getIntent().getBundleExtra(USER_INFO);
        if(bundle.getInt("type",0) == 3){


            
            if(bundle.getInt("is_first") == 1){
                if(mHandler != null){

                    lookTime ++ ;
                    mTvChargingTime.setText(String.format(Locale.CANADA,"观看第%d分钟",lookTime));
                    mHandler.postDelayed(chargingLiveRunnable,
                            bundle.getInt("time",0) * 60 * 1000);
                }

                return;
            }

            
            new CountDownTimer(15 * 1000,1000){

                @Override
                public void onTick(long l) {

                    countDownTime += 1;
                }

                @Override
                public void onFinish() {

                    if(ksyMediaPlayer != null && !isFinishing()){

                        
                        ksyMediaPlayer.pause();
                        LiveCommon.showConfirmDialog(RtmpPlayerActivity.this, "提示", "试看结束，每分钟" + bundle.getInt("type_val")
                                + AppConfig.CURRENCY_NAME, new com.liren.live.interf.DialogInterface() {
                            @Override
                            public void cancelDialog(View v, Dialog d) {
                                finish();
                            }

                            @Override
                            public void determineDialog(View v, Dialog d) {

                                d.dismiss();
                                if(mHandler != null){

                                    chargeLiveRequest();
                                }
                            }
                        });
                    }


                }
            }.start();
        }

    }

    private void initLive() {

        
        ksyMediaPlayer = new KSYMediaPlayer.Builder(this).build();
        if (ksyMediaPlayer != null && mHolder != null) {
            final Surface newSurface = mHolder.getSurface();
            ksyMediaPlayer.setDisplay(mHolder);
            ksyMediaPlayer.setScreenOnWhilePlaying(true);
            
            ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            if (mSurface != newSurface) {
                mSurface = newSurface;
                ksyMediaPlayer.setSurface(mSurface);
            }
        }

        ksyMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        ksyMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        ksyMediaPlayer.setOnInfoListener(mOnInfoListener);
        ksyMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangeListener);
        ksyMediaPlayer.setOnErrorListener(mOnErrorListener);
        ksyMediaPlayer.setScreenOnWhilePlaying(true);

        ksyMediaPlayer.setBufferTimeMax(5);
        try {
            ksyMediaPlayer.setDataSource(mEmceeInfo.pull);
            ksyMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    private void requestRoomInfo() {

        
        PhoneLiveApi.enterRoom(mUser.id
                , mEmceeInfo.uid
                , mUser.token
                , AppContext.address
                , mEmceeInfo.stream
                , new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String s, int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(s);

                        if (res != null) {

                            try {
                                JSONObject data = res.getJSONObject(0);
                                fillLiveInfo(data);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                });

    }


    
    private void fillLiveInfo(JSONObject data) throws JSONException {

        
        mUserList.addAll(ApiUtils.formatDataToList(data.getJSONArray("userlists")
                ,SimpleUserInfo.class));

        
        IMControl.LIVE_USER_NUMS = data.getInt("nums");

        mTvLiveNum.setText(String.format(Locale.CHINA,"%d人观看",IMControl.LIVE_USER_NUMS));

        speak=data.getInt("speakstate");
        barrageFee = data.getInt("barrage_fee");
        viptype=data.getInt("viptype");
        mTvYpNum.setText(data.getString("votestotal"));
        LiveUtils.sortUserList(mUserList);
        mUserListAdapter.notifyDataSetChanged();

        if(data.getInt("isattention") == 0){
            mIvAttention.setVisibility(View.VISIBLE);
        } else {
            mIvAttention.setVisibility(View.GONE);

        }
        connectToSocketService(data.getString("chatserver"));

    }


    @OnClick({R.id.iv_live_lit,R.id.iv_live_direction,R.id.iv_live_emcee_head, R.id.tglbtn_danmu_setting, R.id.iv_live_shar, R.id.iv_live_privatechat, R.id.iv_live_back, R.id.ll_yp_labe, R.id.ll_live_room_info, R.id.iv_live_chat, R.id.iv_live_look_loading_bg, R.id.bt_send_chat, R.id.iv_live_gift, R.id.tv_attention})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            
            case R.id.iv_live_lit:
                int random = mRandom.nextInt(3);
                if(!isLit && mIMControl != null){
                    isLit = true;
                    mIMControl.doSendLitMsg(mUser,random);
                }
                showLit(random);
                break;
            
            case R.id.iv_live_direction:
                directionSwitch();
                break;
            case R.id.iv_live_emcee_head:
                showUserInfoDialog(LiveUtils.getSimleUserInfo(mEmceeInfo));
                break;
            case R.id.iv_live_shar:
                ShareUtils.showSharePopWindow(this, v);
                break;
            
            case R.id.iv_live_privatechat:
                showPrivateChat();
                break;
            
            case R.id.iv_live_back:
                finish();
                break;
            
            case R.id.ll_yp_labe:
                OrderWebViewActivity.startOrderWebView(this,mEmceeInfo.uid);
                break;
            
            case R.id.iv_live_chat:

                changeEditStatus(true);
                break;
            
            case R.id.tglbtn_danmu_setting:
                openOrCloseDanMu();
                break;
            case R.id.bt_send_chat:
                
                if (mDanMuIsOpen) {
                    sendBarrage();
                } else {
                    if(speak==1||!mUser.level.equals("1")) {
                        sendChat(String.valueOf(viptype));
                    } else{
                        showToast2("您当前等级不能发言");
                    }
                }
                break;
            case R.id.iv_live_look_loading_bg:

                changeEditStatus(false);
                break;
            case R.id.iv_live_gift:

                if (mGiftListDialogFragment == null) {
                    mGiftListDialogFragment = new GiftListDialogFragment();
                }
                mGiftListDialogFragment.show(getSupportFragmentManager(), "GiftListDialogFragment");
                break;
            case R.id.ll_live_room_info:
                showUserInfoDialog(LiveUtils.getSimleUserInfo(mEmceeInfo));
                break;

            case R.id.tv_attention:
                
                PhoneLiveApi.showFollow(mUser.id, mEmceeInfo.uid, mUser.token, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(response);
                        if (null != res) {
                            mIvAttention.setVisibility(View.GONE);
                            showToast2("关注成功");
                        }
                    }
                });
                mIMControl.doSendSystemMessage(mUser.user_nicename + "关注了主播", mUser);
                break;

            default:

                break;
        }
    }

    
    private void directionSwitch() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }else{
            
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    
    public void share(View v) {
        ShareUtils.share(this, v.getId(), LiveUtils.getSimleUserInfo(mEmceeInfo));
    }

    
    private void connectToSocketService(String chatUrl) {

        
        try {
            mIMControl = new IMControl(new ChatListenUIRefresh(), this,chatUrl);
            
            mIMControl.connectSocketServer(mUser,mEmceeInfo.stream, mEmceeInfo.uid);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            TLog.log("connect error");
        }
    }


    
    private class ChatListenUIRefresh implements IMControlInterface {

        @Override
        public void onMessageListen(final SocketMsgUtils socketMsg, final int type, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(StringUtils.toInt(socketMsg.getRetcode()) == 409002){
                        AppContext.showToast("你已经被禁言",0);
                        return;
                    }

                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                        addDanmu(c.getSimpleUserInfo().id,socketMsg.getCt());
                    }else{
                        addChatMessage(c);
                    }
                }
            });
        }

        
        @Override
        public void onDanMuMessage(SocketMsgUtils socketMsg,final ChatBean c) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addPayDanmu(c);
                }
            });

        }

        @Override
        public void onOpenGame(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    
                    if(socketMsg.getParam("type",0) == 2){

                        initGameFruits();
                    }

                }
            });
        }

        @Override
        public void onGameStart(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onGameOpenResult(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onGameBet(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onOpenTimeLive(final SocketMsgUtils msgUtils, ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(ksyMediaPlayer != null){
                        ksyMediaPlayer.pause();
                    }
                    LiveCommon.showConfirmDialog(RtmpPlayerActivity.this, "提示",
                            String.format(Locale.CANADA, "当前房间开启了收费模式,每%s分钟收费%s，是否继续"
                                    , msgUtils.getParam("time", "0"), msgUtils.getParam("money", "0"))
                            , new com.liren.live.interf.DialogInterface() {
                                @Override
                                public void cancelDialog(View v, Dialog d) {
                                    finish();
                                }

                                @Override
                                public void determineDialog(View v, Dialog d) {
                                    if(mHandler != null){

                                        chargeLiveRequest();
                                        d.dismiss();
                                    }
                                }
                            });
                }
            });
        }

        @Override
        public void onGameEnd(SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onAllGiftMessage(SocketMsgUtils socketMsg) {


        }

        @Override
        public void onConnect(final boolean res) {
            
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onConnectRes(res);
                }
            });
        }


        
        @Override
        public void onUserStateChange(SocketMsgUtils socketMsg,final UserBean user, final boolean state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onUserStatusChange(user, state);
                }
            });

        }

        
        @Override
        public void onRoomClose(final int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                        directionSwitch();
                    }

                    showLiveEndDialog(mUser.id,"");
                    videoPlayerEnd();

                }
            });

        }

        
        @Override
        public void onShowSendGift(final SendGiftBean giftInfo, final ChatBean chatBean) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showGiftInit(giftInfo);
                    addChatMessage(chatBean);
                }
            });

        }

        @Override
        public void onKickRoom(final SocketMsgUtils socketMsg,final ChatBean c) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (socketMsg. get2Uid().equals(mUser.id)) {
                        
                        changeEditStatus(false);
                    }
                    addChatMessage(c);
                }
            });

        }

        @Override
        public void onShutUp(final SocketMsgUtils socketMsg,final ChatBean c) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (socketMsg. get2Uid().equals(mUser.id)) {

                        
                        videoPlayerEnd();

                        AlertDialog alertDialog = DialogHelp.getMessageDialog(RtmpPlayerActivity.this, "您已被踢出房间",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                })
                                .create();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);

                        alertDialog.show();
                    }
                    addChatMessage(c);
                }
            });

        }


        
        @Override
        public void onPrivilegeAction(final SocketMsgUtils socketMsg, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(socketMsg.getAction().equals("13") && socketMsg.get2Uid().equals(mUser.id)){

                        DialogHelp.getMessageDialog(RtmpPlayerActivity.this,socketMsg.getCt()).show();
                    }

                    addChatMessage(c);
                }
            });
        }

        
        @Override
        public void onLit(SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    
                }
            });

        }

        
        @Override
        public void onAddZombieFans(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addZombieFans(socketMsg.getCt());
                }
            });
        }

        
        @Override
        public void onError() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AppContext.showToast("服务器连接错误");
                }
            });
        }


    }

    
    private void initGameFruits() {

    }

    
    private Runnable chargingLiveRunnable = new Runnable() {
        @Override
        public void run() {
            chargeLiveRequest();
        }
    };

    private void chargeLiveRequest(){
        PhoneLiveApi.requestCharging(mUser.id,mUser.token,
                mEmceeInfo.uid,mEmceeInfo.stream,new StringCallback(){

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if(ksyMediaPlayer != null){
                            ksyMediaPlayer.pause();

                        }

                        if(isFinishing())return;
                        Dialog dialog = DialogHelp.getMessageDialog(RtmpPlayerActivity.this, "扣费失败，请退出直播间重试", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).create();

                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        dialog.show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JSONArray res = ApiUtils.checkIsSuccess(response);

                        if(res != null){

                            lookTime ++;
                            
                            if(mTvChargingTime != null){
                                mTvChargingTime.setText(String.format(Locale.CANADA,"观看第%d分钟",lookTime));
                            }

                            UserBean userBean = AppContext.getInstance().getLoginUser();
                            try {
                                mUser.coin = res.getJSONObject(0).getString("coin");
                                userBean.coin = mUser.coin;
                                AppContext.getInstance().saveUserInfo(userBean);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if(ksyMediaPlayer != null && !ksyMediaPlayer.isPlaying()){
                                ksyMediaPlayer.start();
                            }

                            if(mHandler != null){
                                mHandler.postDelayed(chargingLiveRunnable, 60 * 1000);
                            }
                        }else{
                            
                            if(ksyMediaPlayer != null){
                                ksyMediaPlayer.pause();
                            }

                            if(isFinishing())return;
                            Dialog dialog = DialogHelp.getMessageDialog(RtmpPlayerActivity.this, "余额不足，请充值", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }).create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                            dialog.show();
                        }
                    }
                });
    }

    private IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            
            if (null != mLoadingView) {
                mRoot.removeView(mLoadingView);
                mLoadingView = null;
            }
            mIvLoadingBg.setVisibility(View.GONE);

            ksyMediaPlayer.start();
        }
    };


    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangeListener = new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
            if (mVideoWidth > 0 && mVideoHeight > 0) {
                if (width != mVideoWidth || height != mVideoHeight) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();

                    if (mVideoSurfaceView != null) {
                        mVideoSurfaceView.setVideoDimension(mVideoWidth, mVideoHeight);
                        mVideoSurfaceView.requestLayout();
                    }
                }
            }
        }
    };

    
    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {

            
            ksyMediaPlayer.reload(mEmceeInfo.pull, true);
        }
    };

    
    private IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            switch (what) {
                case KSYMediaPlayer.MEDIA_ERROR_UNKNOWN:

                    break;
                default:
            }
            return false;
        }
    };

    
    public IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int info, int i1) {
            if(info == IMediaPlayer.MEDIA_INFO_RELOADED)
                
                TLog.log("重连成功");

            if(info == IMediaPlayer.MEDIA_INFO_SUGGEST_RELOAD){
                
                ksyMediaPlayer.reload(mEmceeInfo.pull, true);

            }
            return false;
        }
    };

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            changeEditStatus(false);
            return false;
        }
    };

    private final SurfaceHolder.Callback mSurfaceCallback = new Callback() {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mHolder = holder;
            if (ksyMediaPlayer != null) {
                final Surface newSurface = holder.getSurface();
                ksyMediaPlayer.setDisplay(holder);
                ksyMediaPlayer.setScreenOnWhilePlaying(true);
                
                ksyMediaPlayer.setVideoScalingMode(KSYMediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                if (mSurface != newSurface) {
                    mSurface = newSurface;
                    ksyMediaPlayer.setSurface(mSurface);
                }
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

            if (ksyMediaPlayer != null) {
                mSurface = null;
            }
        }
    };

    
    private void videoPlayerEnd() {

        mShowGiftAnimator.removeAllViews();

        if (mGiftListDialogFragment != null) {
            mGiftListDialogFragment.dismissAllowingStateLoss();
        }

        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.release();
            ksyMediaPlayer = null;
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (mGiftView != null) {
            mRoot.removeView(mGiftView);
        }
        
        mDanmuControl.hide();

    }

    
    public void chatListItemClick(ChatBean chat) {
        if (chat.getType() != 13) {

            showUserInfoDialog(chat.mSimpleUserInfo);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.VideoEvent event) {

        if(event.action == 0 && mIMControl != null){

            mUser = AppContext.getInstance().getLoginUser();
            
            mIMControl.doSendGift(event.data[0], mUser,String.valueOf(viptype), event.data[1]);

        }else if(event.action == 1 && mIMControl != null){

            
            mIMControl.doSendSystemMessage(mUser.user_nicename + "关注了主播", mUser);
            mIvAttention.setVisibility(View.GONE);
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int type = getIntent().getBundleExtra(USER_INFO).getInt("type",0);
        if (ksyMediaPlayer != null) {
            
            if((countDownTime < 15 && type == 3) || (lookTime > 0  && type == 3)){
                ksyMediaPlayer.start();
            }else if(type == 0){
                ksyMediaPlayer.start();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ksyMediaPlayer != null) {
            ksyMediaPlayer.pause();
        }
    }

}

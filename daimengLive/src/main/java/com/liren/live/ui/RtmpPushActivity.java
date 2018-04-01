package com.liren.live.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.liren.live.fragment.LivePlugsDialogFragment;
import com.liren.live.ui.dialog.LiveCommon;
import com.hyphenate.util.NetUtils;
import com.ksyun.media.streamer.encoder.VideoEncodeFormat;
import com.ksyun.media.streamer.filter.imgtex.ImgBeautyProFilter;
import com.ksyun.media.streamer.filter.imgtex.ImgFilterBase;
import com.ksyun.media.streamer.filter.imgtex.ImgTexFilterMgt;
import com.ksyun.media.streamer.kit.KSYStreamer;
import com.ksyun.media.streamer.kit.StreamerConstants;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.base.ShowLiveActivityBase;
import com.liren.live.bean.ChatBean;
import com.liren.live.bean.UserBean;
import com.liren.live.event.Event;
import com.liren.live.fragment.SearchMusicDialogFragment;
import com.liren.live.ui.im.IMControl;
import com.liren.live.utils.SocketMsgUtils;
import com.liren.live.utils.StringUtils;
import com.liren.live.utils.TLog;
import com.liren.live.widget.music.ILrcBuilder;
import com.liren.live.widget.music.LrcRow;
import com.liren.live.widget.music.LrcView;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.bean.SendGiftBean;
import com.liren.live.fragment.MusicPlayerDialogFragment;
import com.liren.live.interf.IMControlInterface;
import com.liren.live.ui.other.LiveStream;
import com.liren.live.utils.DialogHelp;
import com.liren.live.utils.LiveUtils;
import com.liren.live.utils.ShareUtils;
import com.liren.live.widget.music.DefaultLrcBuilder;
import com.tandong.bottomview.view.BottomView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


public class RtmpPushActivity extends ShowLiveActivityBase implements SearchMusicDialogFragment.SearchMusicFragmentInterface{

    
    @BindView(R.id.camera_preview)
    GLSurfaceView mCameraPreview;

    
    @BindView(R.id.lcv_live_start)
    LrcView mLrcView;

    @BindView(R.id.iv_live_camera_control)
    ImageView mIvCameraControl;

    @BindView(R.id.fl_bottom_menu)
    FrameLayout mFlBottomMenu;

    @BindView(R.id.rl_live_music)
    LinearLayout mViewShowLiveMusicLrc;

    private String rtmpPushAddress;
    private Timer mTimer;

    
    private boolean IS_START_LIVE = true;
    public LiveStream mStreamer;
    private boolean mBeauty = true;
    private int mPlayTimerDuration = 1000;
    private MediaPlayer mPlayer;
    
    private boolean flashingLightOn;
    
    private AppCompatSeekBar mGrindSeekBar;
    private AppCompatSeekBar mWhitenSeekBar;
    private AppCompatSeekBar mRuddySeekBar;

    
    private boolean openMoneyTimeLive = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_show;
    }

    @Override
    public void initView() {
        super.initView();


    }

    @Override
    public void initData() {
        super.initData();
        mUser = AppContext.getInstance().getLoginUser();
        mRoomNum = mUser.id;
        mTvLiveNumber.setText(String.format(Locale.CHINA,"房间: %s",mUser.id));
        
        mStreamName = getIntent().getStringExtra("stream");
        
        rtmpPushAddress = getIntent().getStringExtra("push");
        mTvLiveNum.setText(String.format(Locale.CHINA,"%d人观看",IMControl.LIVE_USER_NUMS));
        
        mTvYpNum.setText(getIntent().getStringExtra("votestotal"));

        barrageFee = StringUtils.toInt(getIntent().getStringExtra("barrage_fee"));
        
        String type = getIntent().getStringExtra("type");

        if(type.equals("3")){

            openMoneyTimeLive = true;
        }
        
        mEmceeHead.setAvatarUrl(mUser.avatar);
        
        initChatConnection();
        initLivePlay();
        mDanmuControl.show();

    }

    
    private void initChatConnection() {
        
        try {
            mIMControl = new IMControl(new ChatListenUIRefresh(),this,getIntent().getStringExtra("chaturl"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    
    private void initLivePlay() {

        
        mStreamer = new LiveStream(this);
        mStreamer.setUrl(rtmpPushAddress);
        mStreamer.setPreviewFps(15);

        
        
        

        
        mStreamer.setEncodeMethod(StreamerConstants.ENCODE_METHOD_SOFTWARE);
        mStreamer.setVideoEncodeScene(VideoEncodeFormat.ENCODE_SCENE_DEFAULT);
        mStreamer.setDisplayPreview(mCameraPreview);
        mStreamer.setOnInfoListener(mOnInfoListener);
        mStreamer.setOnErrorListener(mOnErrorListener);

        startAnimation(3);

    }

    
    private void startLiveStream() {

        
        mIMControl.connectSocketServer(mUser,mStreamName, mUser.id);
    }

    @OnClick({R.id.iv_live_open_dialog_menu,R.id.btn_live_sound,R.id.iv_live_emcee_head,R.id.tglbtn_danmu_setting,R.id.ll_live_room_info,R.id.btn_live_end_music,R.id.iv_live_music,R.id.iv_live_meiyan,R.id.iv_live_camera_control,R.id.camera_preview,R.id.iv_live_privatechat,R.id.iv_live_back,R.id.ll_yp_labe,R.id.iv_live_chat,R.id.bt_send_chat})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            
            case R.id.iv_live_open_dialog_menu:

                showDialogMenu();
                break;
            
            case R.id.btn_live_sound:
                showSoundEffectsDialog();
                break;
            
            case R.id.iv_live_emcee_head:
                showUserInfoDialog(mUser);
                break;
            
            case R.id.ll_live_room_info:
                showUserInfoDialog(mUser);
                break;
            
            case R.id.iv_live_music:
                showSearchMusicDialog();
                break;
            
            case R.id.iv_live_meiyan:
                adjustBeauty();
                break;
            
            case R.id.iv_live_camera_control:
                showSettingPopUp(v);
                break;
            
            case R.id.tglbtn_danmu_setting:
                openOrCloseDanMu();
                break;
            case R.id.camera_preview:
                changeEditStatus(false);
                break;
            
            case R.id.iv_live_privatechat:
                showPrivateChat();
                break;
            case R.id.iv_live_back:
                onClickGoBack();
                break;
            
            case R.id.ll_yp_labe:
                showDedicateOrder();
                break;
            
            case R.id.iv_live_chat:
                changeEditStatus(true);
                break;
            case R.id.bt_send_chat:
                if(mDanMuIsOpen) {
                    sendBarrage();
                }else{
                    sendChat("0");
                }
                break;
            case R.id.iv_live_exit:
                finish();
                break;
            case R.id.btn_live_end_music:
                stopMusic();
                break;
        }
    }


    
    private void showDialogMenu() {

        LivePlugsDialogFragment dialogFragment = new LivePlugsDialogFragment();
        dialogFragment.show(getFragmentManager(),"LivePlugsDialogFragment");
    }

    
    private void openGame() {

    }

    
    private void startGame() {


    }

    
    private void showDedicateOrder() {

        OrderWebViewActivity.startOrderWebView(RtmpPushActivity.this,mUser.id);
    }

    
    public void chatListItemClick(ChatBean chat) {

        if (chat.getType()!=13) {
            showUserInfoDialog(chat.mSimpleUserInfo);
        }
    }

    
    private void showSearchMusicDialog(){
        SearchMusicDialogFragment musicFragment = new SearchMusicDialogFragment();

        musicFragment.show(getSupportFragmentManager(),"SearchMusicDialogFragment");
    }

    
    private void showSoundEffectsDialog() {
        MusicPlayerDialogFragment musicPlayerDialogFragment = new MusicPlayerDialogFragment();
        musicPlayerDialogFragment.show(getSupportFragmentManager(),"MusicPlayerDialogFragment");
    }

    
    @Override
    public void onSelectMusic(Intent data) {
        startMusicStream(data);
    }


    
    private class ChatListenUIRefresh implements IMControlInterface {

        @Override
        public void onMessageListen(final SocketMsgUtils socketMsg, final int type, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(StringUtils.toInt(socketMsg) == 409002){
                        AppContext.showToast("你已经被禁言",0);
                        return;
                    }

                    addChatMessage(c);
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
        public void onOpenGame(SocketMsgUtils socketMsg) {

        }

        
        @Override
        public void onGameStart(SocketMsgUtils socketMsg) {

        }

        @Override
        public void onGameOpenResult(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mGameControl != null){
                        try {
                            
                            JSONArray data = new JSONArray(socketMsg.getCt());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }

        @Override
        public void onGameBet(final SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mGameControl != null){

                    }
                }
            });
        }

        @Override
        public void onOpenTimeLive(SocketMsgUtils msgUtils, ChatBean c) {

        }

        @Override
        public void onGameEnd(SocketMsgUtils socketMsg) {

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
        public void onUserStateChange(final SocketMsgUtils socketMsg, final UserBean user, final boolean state) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    viptype=socketMsg.getPushVip();
                    onUserStatusChange(user, state);
                }
            });

        }
        
        @Override
        public void onRoomClose(final int code) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(code == 1){

                        videoPlayerEnd();
                        DialogHelp.getMessageDialog(RtmpPushActivity.this, "直播内容涉嫌违规", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                showLiveEndDialog(mUser.id,mStreamName);
                            }
                        }).show();

                    }
                }
            });

        }
        
        @Override
        public void onShowSendGift(final SendGiftBean giftInfo,final ChatBean chatBean) {
            
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    showGiftInit(giftInfo);
                    addChatMessage(chatBean);
                }
            });

        }

        
        @Override
        public void onPrivilegeAction(final SocketMsgUtils socketMsg, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    addChatMessage(c);
                }
            });
        }
        
        @Override
        public void onLit(SocketMsgUtils socketMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    showLit(mRandom.nextInt(3));
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



                }
            });
        }

        @Override
        public void onKickRoom(final SocketMsgUtils socketMsg, final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    addChatMessage(c);
                }
            });

        }

        @Override
        public void onShutUp(final SocketMsgUtils socketMsg,final ChatBean c) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    addChatMessage(c);
                }
            });

        }
    }


    
    private void startMusicStream(Intent data) {

        
        mStreamer.stopMixMusic();

        mViewShowLiveMusicLrc.setVisibility(View.VISIBLE);


        
        String musicPath = data.getStringExtra("filepath");

        
        String lrcStr = LiveUtils.getFromFile(musicPath.substring(0,musicPath.length() - 3) + "lrc");

        mStreamer.getAudioPlayerCapture().getBgmPlayer().setVolume(1);
        mStreamer.startBgm(musicPath, true);
        mStreamer.setHeadsetPlugged(true);

        
        
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(musicPath);
            mPlayer.setLooping(true);
            mPlayer.setVolume(0,0);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {

                    mp.start();
                    if(mTimer == null){
                        mTimer = new Timer();

                        mTimer.scheduleAtFixedRate(new TimerTask() {
                            long beginTime = -1;

                            @Override
                            public void run() {
                                if(beginTime == -1) {
                                    beginTime = System.currentTimeMillis();
                                }

                                if(null != mPlayer){
                                    final long timePassed = mPlayer.getCurrentPosition();
                                    RtmpPushActivity.this.runOnUiThread(new Runnable() {

                                        public void run() {
                                            mLrcView.seekLrcToTime(timePassed);
                                        }
                                    });
                                }

                            }
                        }, 0, mPlayTimerDuration);
                    }
                }
            });
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    stopLrcPlay();
                }
            });
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(lrcStr);

        
        mLrcView.setLrc(rows);
    }

    
    public void stopLrcPlay(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    
    private void stopMusic() {
        if(mPlayer != null && null != mStreamer){
            mStreamer.stopMixMusic();
            mPlayer.stop();
            mViewShowLiveMusicLrc.setVisibility(View.GONE);
            if(mTimer != null){
                mTimer.cancel();
            }
        }

    }

    
    private void adjustBeauty() {
        if (!mBeauty) {
            mBeauty = true;
            mStreamer.getImgTexFilterMgt().setFilter(
                    mStreamer.getGLRender(), ImgTexFilterMgt.KSY_FILTER_BEAUTY_SOFT);
        } else {
            mStreamer.setFrontCameraMirror(true);
            mBeauty = false;
            mStreamer.getImgTexFilterMgt().setFilter(mStreamer.getGLRender(),
                    ImgTexFilterMgt.KSY_FILTER_BEAUTY_PRO);
            List<ImgFilterBase> filters = mStreamer.getImgTexFilterMgt().getFilter();
            if (filters != null && !filters.isEmpty()) {
                final ImgFilterBase filter = filters.get(0);
                SeekBar.OnSeekBarChangeListener seekBarChangeListener =
                        new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress,
                                                          boolean fromUser) {
                                if (!fromUser) {
                                    return;
                                }
                                float val = progress / 100.f;
                                if (seekBar == mGrindSeekBar) {
                                    filter.setGrindRatio(val);
                                } else if (seekBar == mWhitenSeekBar) {
                                    filter.setWhitenRatio(val);
                                } else if (seekBar == mRuddySeekBar) {
                                    if (filter instanceof ImgBeautyProFilter) {
                                        val = progress / 50.f - 1.0f;
                                    }
                                    filter.setRuddyRatio(val);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                            }
                        };
                BottomView mManageMenu = new BottomView(this, R.style.BottomViewTheme_Transparent,R.layout.view_adjust_beauty);


                mRuddySeekBar = (AppCompatSeekBar) mManageMenu.getView().findViewById(R.id.ruddy_seek_bar);
                mGrindSeekBar = (AppCompatSeekBar) mManageMenu.getView().findViewById(R.id.grind_seek_bar);
                mWhitenSeekBar = (AppCompatSeekBar) mManageMenu.getView().findViewById(R.id.whiten_seek_bar);
                mManageMenu.setAnimation(R.style.BottomToTopAnim);
                mManageMenu.showBottomView(false);


                mGrindSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
                mWhitenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
                mRuddySeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

                mGrindSeekBar.setProgress((int)(filter.getGrindRatio() * 100));
                mWhitenSeekBar.setProgress((int)(filter.getWhitenRatio() * 100));
                int ruddyVal = (int)(filter.getRuddyRatio() * 100);
                if (filter instanceof ImgBeautyProFilter) {
                    ruddyVal = (int)(filter.getRuddyRatio() * 50 + 50);
                }
                mRuddySeekBar.setProgress(ruddyVal);
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event.CommonEvent event) {

        if(event.action == 1) {

            EventBus.getDefault().unregister(this);
            if(!NetUtils.hasNetwork(RtmpPushActivity.this)){

                videoPlayerEnd();
                new AlertDialog.Builder(RtmpPushActivity.this)
                        .setTitle("提示")
                        .setMessage("网络断开连接,请检查网络后重新开始直播")
                        .setNegativeButton("确定", new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialogInterface, int i) {

                                showLiveEndDialog(mUser.id,mStreamName);
                            }
                        })
                        .create()
                        .show();

            }
        }else if(event.action == 2){
            
            openGame();

        }else if(event.action == 3){
            
            if(openMoneyTimeLive){
                AppContext.showToast("计时房间已打开");
                return;
            }

            LiveCommon.showInputContentDialog(RtmpPushActivity.this, "设置扣费金额", new com.liren.live.interf.DialogInterface() {
                @Override
                public void cancelDialog(View v, Dialog d) {

                    d.dismiss();
                }

                @Override
                public void determineDialog(View v,final Dialog d) {

                    String coin = ((EditText)d.findViewById(R.id.et_input)).getText().toString();

                    if(StringUtils.toInt(coin) > 0){
                        
                        PhoneLiveApi.requestSetRoomType(mUser.id,mUser.token,mStreamName,coin,new StringCallback(){

                            @Override
                            public void onError(Call call, Exception e, int id) {
                                d.dismiss();
                                AppContext.showToast("修改房间状态失败");
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                d.dismiss();
                                JSONArray res = ApiUtils.checkIsSuccess(response);

                                if(res != null){
                                    AppContext.showToast("房间已修改为付费计时房间");
                                    try {
                                        openMoneyTimeLive = true;
                                        JSONObject data = res.getJSONObject(0);
                                        if(mIMControl != null){
                                            mIMControl.doSendOpenTimeLive(data.getString("time"),data.getString("money"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                    }
                }
            });


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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){

            onClickGoBack();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10: {
                
                if((grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                    showSoundEffectsDialog();
                }else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AppContext.showToast("缺少权限,请到设置中修改",0);
                }else if(grantResults.length > 0 && grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    AppContext.showToast("缺少权限,请到设置中修改",0);
                }
                break;

            }
        }
    }

    
    private void onClickGoBack(){

        DialogHelp.getConfirmDialog(this, getString(R.string.iscloselive), new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                videoPlayerEnd();
                showLiveEndDialog(mUser.id,mStreamName);
            }
        }).show();
    }

    
    private void videoPlayerEnd() {
        IS_START_LIVE = false;
        
        stopMusic();
        

        mStreamer.stopCameraPreview();
        mStreamer.stopStream();

        
        PhoneLiveApi.closeLive(mUser.id, mUser.token,mStreamName, new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {
                
            }

            @Override
            public void onResponse(String response,int id) {

            }
        });

        mGiftShowQueue.clear();
        mLuxuryGiftShowQueue.clear();
        mListChats.clear();

        if(mGiftView != null){
            mLiveContent.removeView(mGiftView);
        }
        mShowGiftAnimator.removeAllViews();

        mIMControl.closeLive();

        if(null != mHandler){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        
        mDanmuControl.hide();
    }

    private KSYStreamer.OnInfoListener mOnInfoListener =  new KSYStreamer.OnInfoListener() {
        public void onInfo(int what, int msg1, int msg2) {
            switch (what) {
                case StreamerConstants.KSY_STREAMER_CAMERA_INIT_DONE:
                    mStreamer.startStream();
                    break;
                case StreamerConstants.KSY_STREAMER_OPEN_STREAM_SUCCESS:
                    PhoneLiveApi.changeLiveState(mUser.id,mUser.token,mStreamName,"1",null);
                    break;
                case StreamerConstants.KSY_STREAMER_FRAME_SEND_SLOW:

                    break;
                case StreamerConstants.KSY_STREAMER_EST_BW_RAISE:

                    break;
                case StreamerConstants.KSY_STREAMER_EST_BW_DROP:

                    break;
                default:
                    TLog.log("OnInfo: " + what + " msg1: " + msg1 + " msg2: " + msg2);
                    break;
            }
        }
    };
    private KSYStreamer.OnErrorListener mOnErrorListener = new KSYStreamer.OnErrorListener() {
        
        @Override
        public void onError(int what, int msg1, int msg2) {
            switch (what) {
                case StreamerConstants.KSY_STREAMER_ERROR_DNS_PARSE_FAILED:

                    break;
                case StreamerConstants.KSY_STREAMER_ERROR_CONNECT_FAILED:

                    break;
                case StreamerConstants.KSY_STREAMER_ERROR_PUBLISH_FAILED:


                    break;
                case StreamerConstants.KSY_STREAMER_ERROR_CONNECT_BREAKED:


                    break;
                case StreamerConstants.KSY_STREAMER_ERROR_AV_ASYNC:

                    break;
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNSUPPORTED:

                    break;
                case StreamerConstants.KSY_STREAMER_VIDEO_ENCODER_ERROR_UNKNOWN:

                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNSUPPORTED:

                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_ENCODER_ERROR_UNKNOWN:

                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:

                    break;
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:

                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:

                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:

                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:

                    break;
                default:

                    break;
            }

            switch (what) {
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_UNKNOWN:
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_START_FAILED:
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_START_FAILED:
                case StreamerConstants.KSY_STREAMER_AUDIO_RECORDER_ERROR_UNKNOWN:
                    break;
                case StreamerConstants.KSY_STREAMER_CAMERA_ERROR_SERVER_DIED:
                    mStreamer.stopCameraPreview();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStreamer.startCameraPreview();
                        }
                    }, 5000);
                    break;
                
                default:
                    if(mStreamer != null && IS_START_LIVE){
                        mStreamer.startStream();
                    }

                    if(mHandler != null){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mStreamer.startStream();
                            }
                        }, 3000);
                    }

                    break;
            }

        }
    };

    @Override
    public void onPause() {
        super.onPause();

        mStreamer.onPause();
        if(IS_START_LIVE){
            mStreamer.stopCameraPreview();
        }
        mStreamer.stopStream();

    }
    @Override
    public void onResume() {
        super.onResume();
        mStreamer.startCameraPreview();
        mStreamer.onResume();
    }

    
    private void startAnimation(final int num){
        final TextView tvNum = new TextView(this);
        tvNum.setTextColor(getResources().getColor(R.color.white));
        tvNum.setText(String.valueOf(num));
        tvNum.setTextSize(30);
        mRoot.addView(tvNum);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvNum.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        tvNum.setLayoutParams(params);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvNum,"scaleX",5f,1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvNum,"scaleY",5f,1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX,scaleY);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mRoot == null)return;
                mRoot.removeView(tvNum);
                if(num == 1){
                    startLiveStream();
                    return;
                }
                startAnimation(num == 3?2:1);
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animatorSet.setDuration(1000);
        animatorSet.start();

    }

    
    private void showSettingPopUp(View v) {

        View popView  =   getLayoutInflater().inflate(R.layout.pop_view_camera_control,null);
        LinearLayout llLiveCameraControl  =    (LinearLayout)popView.findViewById(R.id.ll_live_camera_control);
        llLiveCameraControl.measure(0,0);
        int height = llLiveCameraControl.getMeasuredHeight();
        popView.findViewById(R.id.iv_live_flashing_light).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashingLightOn = !flashingLightOn;
                mStreamer.toggleTorch(flashingLightOn);
            }
        });
        popView.findViewById(R.id.iv_live_switch_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStreamer.switchCamera();
            }
        });
        popView.findViewById(R.id.iv_live_shar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.showSharePopWindow(RtmpPushActivity.this,mIvCameraControl);
            }
        });

        PopupWindow popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-height);
    }

    public void share(View v){
        ShareUtils.share(this,v.getId(),mUser);
    }

    public static void RtmpPushActivity(Context context,String stream,String barrage_fee,String votestotal,String push,String chaturl,String type,boolean isFrontCameraMirro){

        Intent intent = new Intent(context,RtmpPushActivity.class);
        intent.putExtra("stream",stream);
        intent.putExtra("barrage_fee",barrage_fee);
        intent.putExtra("votestotal",votestotal);
        intent.putExtra("push",push);
        intent.putExtra("chaturl",chaturl);
        intent.putExtra("type",type);
        intent.putExtra("isFrontCameraMirro",isFrontCameraMirro);
        context.startActivity(intent);

    }
}

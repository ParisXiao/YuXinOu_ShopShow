package com.liren.live.base;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liren.live.game.base.GameControlInterface;
import com.liren.live.ui.customviews.HeartLayout;
import com.google.gson.Gson;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.liren.live.AppConfig;
import com.liren.live.R;
import com.liren.live.adapter.ChatListAdapter;
import com.liren.live.adapter.UserListAdapter;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.bean.ChatBean;
import com.liren.live.bean.SendGiftBean;
import com.liren.live.bean.SimpleUserInfo;
import com.liren.live.bean.UserBean;
import com.liren.live.fragment.LiveEmceeEndFragmentDialog;
import com.liren.live.fragment.LiveEndFragmentDialog;
import com.liren.live.fragment.UserInfoDialogFragment;
import com.liren.live.interf.DialogInterface;
import com.liren.live.model.Danmu;
import com.liren.live.ui.im.IMControl;
import com.liren.live.ui.im.PhoneLivePrivateChat;
import com.liren.live.utils.InputMethodUtils;
import com.liren.live.utils.LiveUtils;
import com.liren.live.utils.StringUtils;
import com.liren.live.utils.TDevice;
import com.liren.live.utils.TLog;
import com.liren.live.utils.ThreadManager;
import com.liren.live.viewpagerfragment.PrivateChatCorePagerDialogFragment;
import com.liren.live.widget.AvatarView;
import com.liren.live.widget.BlackButton;
import com.liren.live.widget.BlackEditText;
import com.liren.live.widget.BlackTextView;
import com.liren.live.widget.SpaceRecycleView;
import com.liren.live.widget.VerticalImageSpan;
import com.liren.live.widget.danmu.DanmuControl;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.lang.ref.SoftReference;

import butterknife.BindView;
import master.flame.danmaku.controller.IDanmakuView;
import okhttp3.Call;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;



public class ShowLiveActivityBase extends BaseActivity {

    @BindView(R.id.ll_live_game_content)
    protected LinearLayout mRlGameContent;

    @BindView(R.id.rl_live_root)
    protected RelativeLayout mRoot;

    @BindView(R.id.heart_layout)
    protected HeartLayout mHeartLayout;

    
    @BindView(R.id.ll_show_gift_animator)
    protected LinearLayout mShowGiftAnimator;

    @BindView(R.id.view_live_content)
    protected RelativeLayout mLiveContent;

    
    @BindView(R.id.tv_live_num)
    protected BlackTextView mTvLiveNum;

    @BindView(R.id.tv_yingpiao_num)
    protected BlackTextView mTvYpNum;

    
    @BindView(R.id.lv_live_room)
    protected ListView mLvChatList;

    
    @BindView(R.id.iv_live_chat)
    protected ImageView mLiveChat;

    
    @BindView(R.id.iv_live_emcee_head)
    protected AvatarView mEmceeHead;

    
    @BindView(R.id.ll_bottom_menu)
    protected RelativeLayout mButtonMenu;

    @BindView(R.id.fl_bottom_menu)
    protected FrameLayout mButtonMenuFrame;

    
    @BindView(R.id.ll_live_chat_edit)
    protected LinearLayout mLiveChatEdit;

    @BindView(R.id.ll_yp_labe)
    protected LinearLayout mLiveLade;

    @BindView(R.id.et_live_chat_input)
    protected BlackEditText mChatInput;

    @BindView(R.id.tv_live_number)
    protected BlackTextView mTvLiveNumber;

    
    @BindView(R.id.hl_room_user_list)
    protected RecyclerView mRvUserList;

    @BindView(R.id.iv_live_new_message)
    protected ImageView mIvNewPrivateChat;

    @BindView(R.id.tglbtn_danmu_setting)
    protected BlackButton mBtnDanMu;

    @BindView(R.id.tv_live_join_room_animation)
    TextView mTvJoinRoomAnimation;

    @BindView(R.id.danmakuView)
    protected IDanmakuView mDanmakuView;

    @BindView(R.id.mMarqueeView)
    TextView mMarqueeView;

    protected Gson mGson = new Gson();

    
    private Map<Integer,View> mGiftShowNow = new HashMap<>();
    
    protected Map<Integer,SendGiftBean> mGiftShowQueue = new HashMap();
    
    private List<UserBean> mJoinRoomAnimationQueue = new ArrayList<>();
    
    protected List<SendGiftBean> mLuxuryGiftShowQueue = new ArrayList<>();
    
    protected boolean giftAnimationPlayEnd = true;

    protected int mShowGiftFirstUid = 0,mShowGiftSecondUid = 0;
    
    public boolean mConnectionState = false;

    protected  int viptype=0;
    protected ChatListAdapter mChatListAdapter;
    
    protected UserListAdapter mUserListAdapter;
    
    protected List<ChatBean> mListChats = new ArrayList<>();
    
    protected List<SimpleUserInfo> mUserList = new ArrayList<>();

    
    protected DanmuControl mDanmuControl;
    
    public IMControl mIMControl;

    protected UserBean mUser;
    protected Handler mHandler;
    
    protected int mScreenWidth,mScreenHeight;
    protected Random mRandom = new Random();
    
    protected String mRoomNum,mStreamName;
    
    protected boolean mDanMuIsOpen = false;
    protected BroadcastReceiver broadCastReceiver;
    
    protected View mGiftView;
    
    protected int barrageFee;
    
    protected GameControlInterface mGameControl;


    @Override
    public void initData() {
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mHandler = new Handler();

        registerPrivateBroadcast();
    }

    
    protected void registerPrivateBroadcast(){

        IntentFilter cmdFilter = new IntentFilter("com.liren.live");
        if(broadCastReceiver == null){
            broadCastReceiver = new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    
                    mIvNewPrivateChat.setVisibility(View.VISIBLE);
                }
            };
        }
        registerReceiver(broadCastReceiver,cmdFilter);
    }


    @Override
    public void initView() {

        mChatListAdapter = new ChatListAdapter(this);
        mChatListAdapter.setChats(mListChats);
        mLvChatList.setAdapter(mChatListAdapter);

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(OrientationHelper.HORIZONTAL);
        mRvUserList.setLayoutManager(manager);
        
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space_5);
        mRvUserList.addItemDecoration(new SpaceRecycleView(spacingInPixels));
        mRvUserList.setAdapter(mUserListAdapter = new UserListAdapter(mUserList));
        mScreenWidth = (int) TDevice.getScreenWidth();
        mScreenHeight = (int) TDevice.getScreenHeight();

        ((TextView)findViewById(R.id.tv_live_tick_name)).setText(AppConfig.TICK_NAME);

        mTvJoinRoomAnimation.setX(mScreenWidth);
        
        mLvChatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    changeEditStatus(false);
                }
                return false;
            }
        });


        
        mUserListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                
                showUserInfoDialog(mUserList.get(position));
            }
        });


        
        mDanmuControl = new DanmuControl(this);
        mDanmuControl.setDanmakuView(mDanmakuView);

        mLvChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chatListItemClick(mListChats.get(position));
            }
        });

        mMarqueeView.setText(LiveUtils.getConfigFiled(this,"system_notice"));
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMarqueeView,"translationX",mScreenWidth,-mMarqueeView.getWidth());
        animator.setDuration(10000);
        animator.setRepeatMode(Animation.RESTART);
        animator.setRepeatCount(-1);
        animator.start();
        
        
    }

    
    protected void openOrCloseDanMu() {

        mDanMuIsOpen = !mDanMuIsOpen;
        if (mDanMuIsOpen) {
            mDanmuControl.show();
            if (mChatInput.getText().toString().equals("")) {
                mChatInput.setHint("弹幕，" + barrageFee + AppConfig.CURRENCY_NAME + "/条");
            }

        } else {
            mDanmuControl.hide();
            mChatInput.setHint("");
        }
        mBtnDanMu.setBackgroundResource(mDanMuIsOpen ? R.drawable.tuanmubutton1 : R.drawable.tanmubutton);
    }

    
    protected void sendChat(String viptype) {
        String sendMsg = mChatInput.getText().toString();
        sendMsg = sendMsg.trim();
        if(mConnectionState && !sendMsg.equals("")){
            mChatInput.setText("");

             mIMControl.doSendMsg(sendMsg,viptype, mUser, 0);


         }


    }

    
    protected View initShowEvenSentSendGift(SendGiftBean mSendGiftInfo){
        View view = getLayoutInflater().inflate(R.layout.item_show_gift_animator, null);
        if(mShowGiftFirstUid == 0){
            mShowGiftFirstUid = mSendGiftInfo.getUid();
        }else{
            mShowGiftSecondUid = mSendGiftInfo.getUid();
        }
        mGiftShowNow.put(mSendGiftInfo.getUid(), view);
        return view;
    }


    
    protected boolean timingDelGiftAnimation(int index){

        int id = index == 1 ? mShowGiftFirstUid:mShowGiftSecondUid;

        SendGiftBean mSendGiftInfo = mGiftShowQueue.get(id);

        if(mSendGiftInfo != null){

            long time = System.currentTimeMillis() - mSendGiftInfo.getSendTime();
            if((time > 4000) && (mShowGiftAnimator != null)){
                
                mShowGiftAnimator.removeView(mGiftShowNow.get(id));

                mGiftShowQueue.remove(id);

                mGiftShowNow.remove(id);
                if(index == 1){
                    mShowGiftFirstUid = 0;
                }else{
                    mShowGiftSecondUid = 0;
                }
                
                if(mGiftShowQueue.size() != 0){

                    Iterator iterator = mGiftShowQueue.entrySet().iterator();
                    while (iterator.hasNext()){
                        Map.Entry entry = (Map.Entry) iterator.next();
                        SendGiftBean sendGift = (SendGiftBean) entry.getValue();

                        if(mShowGiftFirstUid != sendGift.getUid() && mShowGiftSecondUid != sendGift.getUid()){
                            showEvenSentGiftAnimation(initShowEvenSentSendGift(sendGift),sendGift,1);
                            break;
                        }
                    }
                }
                return false;
            }else{
                return true;
            }
        }
        return true;
    }


    
    protected int showLit(final int index) {

        mHeartLayout.addFavor();
        return index;

    }
    protected void switchPlayAnimation(SendGiftBean mSendGiftBean){
        switch (mSendGiftBean.getGiftid()){
            case 22:
                
                showFireworksAnimation();
                break;
            case 21:
                
                showCruisesAnimation(mSendGiftBean);
                break;
            case 9:
                
                showRedCarAnimation(mSendGiftBean);
                break;
            case 19:
                
                showPlainAnimation(mSendGiftBean);
                break;
            default:
                
                showOrdinaryGiftInit(mSendGiftBean);
                break;
        }
    }
    
    protected void showPlainAnimation(final SendGiftBean mSendGiftBean){
        if(!giftAnimationPlayEnd){
            return;
        }
        
        giftAnimationPlayEnd = false;
        
        final int[] colorArr = new int[]{R.color.red,R.color.yellow,R.color.blue,R.color.green,R.color.orange,R.color.pink};

        mGiftView = getLayoutInflater().inflate(R.layout.view_live_gift_animation_plain,null);
        
        AvatarView uHead = (AvatarView) mGiftView.findViewById(R.id.iv_animation_head);
        TextView mname = (TextView) mGiftView.findViewById(R.id.tv_liwu_name);
        mname.setText(mSendGiftBean.getNicename());
        uHead.setAvatarUrl(mSendGiftBean.getAvatar());
        mLiveContent.addView(mGiftView);
        final RelativeLayout mRootAnimation = (RelativeLayout) mGiftView.findViewById(R.id.rl_animation_flower);

        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {

                ObjectAnimator plainAnimator = ObjectAnimator.ofFloat(mGiftView,"translationX",mScreenWidth,0);
                plainAnimator.setDuration(3000);
                plainAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        subscriber.onNext("");
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                plainAnimator.start();
            }
        });

        observable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Random random = new Random();
                int num = random.nextInt(50) + 10;
                int width = mRootAnimation.getWidth();
                int height = mRootAnimation.getHeight();
                

                for(int i  = 0; i<num; i ++){
                    int color = random.nextInt(5);
                    int x  = random.nextInt(50);
                    final int tx = width==0?0:random.nextInt(width);
                    final int ty = height==0?0:random.nextInt(height);
                    TextView flower = new TextView(ShowLiveActivityBase.this);
                    flower.setX(x);
                    flower.setText("❀");
                    flower.setTextColor(getResources().getColor(colorArr[color]));
                    flower.setTextSize(50);
                    
                    mRootAnimation.addView(flower);
                    flower.animate().alpha(0f).translationX(tx).translationY(ty).setDuration(5000).start();

                }
                if(mHandler == null) return;
                
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ObjectAnimator plainAnimator = ObjectAnimator.ofFloat(mGiftView,"translationX",-mGiftView.getWidth());
                        plainAnimator.setDuration(2000);
                        plainAnimator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if(null != mGiftView){
                                    if(null != mLiveContent){
                                        mLiveContent.removeView(mGiftView);
                                    }
                                    if(mLuxuryGiftShowQueue.size() > 0){
                                        mLuxuryGiftShowQueue.remove(0);
                                    }
                                    giftAnimationPlayEnd = true;
                                    if(mLuxuryGiftShowQueue.size() > 0 && mHandler != null){
                                        switchPlayAnimation(mLuxuryGiftShowQueue.get(0));
                                    }
                                }

                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        plainAnimator.start();
                    }
                },4000);
            }
        });

    }
    
    protected void showRedCarAnimation(SendGiftBean sendGiftBean){
        if(!giftAnimationPlayEnd){
            return;
        }

        giftAnimationPlayEnd = false;
        
        mGiftView = getLayoutInflater().inflate(R.layout.view_live_gift_animation_car_general,null);
        AvatarView uHead = (AvatarView) mGiftView.findViewById(R.id.iv_animation_red_head);
        TextView mname = (TextView) mGiftView.findViewById(R.id.tv_liwu_name);
        mname.setText(sendGiftBean.getNicename());
        uHead.setAvatarUrl(sendGiftBean.getAvatar());
        
        final ImageView redCar = (ImageView) mGiftView.findViewById(R.id.iv_animation_red_car);
        
        mLiveContent.addView(mGiftView);

        final int vw = redCar.getLayoutParams().width;
        
        final Runnable carRunnable = new Runnable() {
            @Override
            public void run() {
                
                redCar.setImageResource(R.drawable.car_red1);
                mGiftView.animate().translationX(~vw)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                
                                redCar.setImageResource(R.drawable.car_10001);
                                ObjectAnimator oX = ObjectAnimator.ofFloat(mGiftView,"translationX",mScreenWidth,mScreenWidth/2-vw/2);
                                ObjectAnimator oY = ObjectAnimator.ofFloat(mGiftView,"translationY",mScreenHeight/2,mScreenHeight >> 2);

                                
                                AnimatorSet animatorSet = new AnimatorSet();
                                animatorSet.playTogether(oX,oY);
                                animatorSet.setDuration(2000);
                                animatorSet.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        
                                        redCar.setImageResource(R.drawable.backcar1);
                                        mGiftView.animate().translationX(-vw).translationY(0)
                                                .setDuration(1000)
                                                .withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        
                                                        if(mGiftView == null || mLiveContent == null)return;
                                                        mLiveContent.removeView(mGiftView);
                                                        if(mLuxuryGiftShowQueue.size() > 0){
                                                            mLuxuryGiftShowQueue.remove(0);
                                                        }

                                                        giftAnimationPlayEnd = true;
                                                        if(mLuxuryGiftShowQueue.size() > 0 && mHandler != null){
                                                            switchPlayAnimation(mLuxuryGiftShowQueue.get(0));
                                                        }
                                                    }
                                                })
                                                .start();

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });
                                animatorSet.start();

                            }
                        })
                        .setDuration(1000).start();
            }
        };

        
        ObjectAnimator ox = ObjectAnimator.ofFloat(mGiftView,"translationX",mScreenWidth + vw,mScreenWidth/2-vw/2);
        ox.setDuration(1500);
        ObjectAnimator oy = ObjectAnimator.ofFloat(mGiftView,"translationY",mScreenHeight >> 2);
        
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ox,oy);
        animatorSet.setDuration(1500);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                
                redCar.setImageResource(R.drawable.car_red6);
                if(mHandler == null) return;
                mHandler.postDelayed(carRunnable,500);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();

    }
    
    protected void showCruisesAnimation(SendGiftBean mSendGiftBean){
        if(!giftAnimationPlayEnd){
            return;
        }
        giftAnimationPlayEnd = false;
        mGiftView = getLayoutInflater().inflate(R.layout.view_live_gift_animation_cruises,null);

        
        AvatarView mUhead = (AvatarView) mGiftView.findViewById(R.id.live_cruises_uhead);
        ((TextView)mGiftView.findViewById(R.id.tv_live_cruises_uname)).setText(mSendGiftBean.getNicename());
        mUhead.setAvatarUrl(mSendGiftBean.getAvatar());

        mLiveContent.addView(mGiftView);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mGiftView.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mGiftView.setLayoutParams(params);
        final RelativeLayout cruises = (RelativeLayout) mGiftView.findViewById(R.id.rl_live_cruises);

        
        cruises.animate().translationX(mScreenWidth/2 + mScreenWidth/3).translationY(120f).withEndAction(new Runnable() {
            @Override
            public void run() {
                if(mHandler == null) return;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        
                        cruises.animate().translationX(mScreenWidth*2).translationY(200f).setDuration(3000)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        
                                        if(mLiveContent == null)return;
                                        mLiveContent.removeView(mGiftView);
                                        if(mLuxuryGiftShowQueue.size() > 0){
                                            mLuxuryGiftShowQueue.remove(0);
                                        }
                                        giftAnimationPlayEnd = true;
                                        if(mLuxuryGiftShowQueue.size() > 0 && mHandler != null){
                                            switchPlayAnimation(mLuxuryGiftShowQueue.get(0));
                                        }
                                    }
                                }).start();
                    }
                },2000);

            }
        }).setDuration(3000).start();

        
        ImageView seaOne = (ImageView) mGiftView.findViewById(R.id.iv_live_water_one);
        ImageView seaTwo = (ImageView) mGiftView.findViewById(R.id.iv_live_water_one2);
        ObjectAnimator obj = ObjectAnimator.ofFloat(seaOne,"translationX",-50,50);
        obj.setDuration(1000);
        obj.setRepeatCount(-1);
        obj.setRepeatMode(ObjectAnimator.REVERSE);
        obj.start();
        ObjectAnimator obj2 = ObjectAnimator.ofFloat(seaTwo,"translationX",50,-50);
        obj2.setDuration(1000);
        obj2.setRepeatCount(-1);
        obj2.setRepeatMode(ObjectAnimator.REVERSE);
        obj2.start();
    }
    
    protected void showFireworksAnimation(){
        if(!giftAnimationPlayEnd){
            return;
        }
        giftAnimationPlayEnd = false;
        
        final ImageView animation = new ImageView(this);
        RelativeLayout.LayoutParams pa = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pa.addRule(RelativeLayout.CENTER_IN_PARENT);
        animation.setLayoutParams(pa);
        animation.setImageResource(R.drawable.gift_fireworks_heart_animation);
        final AnimationDrawable an = (AnimationDrawable) animation.getDrawable();
        mLiveContent.addView(animation);
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                int duration = 0;
                for(int i = 0; i< an.getNumberOfFrames(); i++){
                    duration += an.getDuration(i);
                }
                an.start();
                if(mHandler == null) return;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        
                        if(mLiveContent == null)return;
                        mLiveContent.removeView(animation);
                        if(mLuxuryGiftShowQueue.size() > 0){
                            mLuxuryGiftShowQueue.remove(0);
                        }
                        giftAnimationPlayEnd = true;
                        if(mLuxuryGiftShowQueue.size() > 0 && mHandler != null){
                            switchPlayAnimation(mLuxuryGiftShowQueue.get(0));
                        }

                    }
                },duration);
            }
        });

    }
    
    protected void showEvenSentGiftAnimation(final View mShowGiftLayout,final SendGiftBean gitInfo,int num) {
        final AvatarView mGiftIcon = (AvatarView) mShowGiftLayout.findViewById(R.id.av_gift_icon);
        final TextView mGiftNum = (TextView) mShowGiftLayout.findViewById(R.id.tv_show_gift_num);
        ((AvatarView) mShowGiftLayout.findViewById(R.id.av_gift_uhead)).setAvatarUrl(gitInfo.getAvatar());
        ((TextView) mShowGiftLayout.findViewById(R.id.tv_gift_uname)).setText(gitInfo.getNicename());
        ((TextView) mShowGiftLayout.findViewById(R.id.tv_gift_gname)).setText(gitInfo.getGiftname());
        mGiftIcon.setAvatarUrl(gitInfo.getGifticon());

        if(mShowGiftAnimator != null){
            mShowGiftAnimator.addView(mShowGiftLayout);
        }
        
        ObjectAnimator oa1 = ObjectAnimator.ofFloat(mShowGiftLayout,"translationX",-340f,0f);
        oa1.setDuration(300);
        oa1.start();
        oa1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                showGiftNumAnimation(mGiftNum, gitInfo.getUid());
                
                ObjectAnimator giftIconAnimator = ObjectAnimator.ofFloat(mGiftIcon, "translationX", -40f, TDevice.dpToPixel(190));
                giftIconAnimator.setDuration(500);
                giftIconAnimator.start();
                
                final int index = mShowGiftFirstUid == gitInfo.getUid() ? 1 : 2;
                if (mHandler != null) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (timingDelGiftAnimation(index)) {
                                if (mHandler != null) {
                                    mHandler.postDelayed(this, 1000);
                                }
                            } else {
                                mHandler.removeCallbacks(this);
                            }

                        }
                    }, 1000);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }


    
    protected void showGiftNumAnimation(TextView tv,int uid){
        tv.setText("X" + mGiftShowQueue.get(uid).getGiftcount());
        PropertyValuesHolder p1 = PropertyValuesHolder.ofFloat("scaleX",2.0f,0.2f,1f);
        PropertyValuesHolder p2 = PropertyValuesHolder.ofFloat("scaleY",2.0f,0.2f,1f);
        ObjectAnimator.ofPropertyValuesHolder(tv,p1,p2).setDuration(200).start();
        mGiftShowQueue.get(uid).setSendTime(System.currentTimeMillis());
    }

    
    protected void showOrdinaryGiftInit(final SendGiftBean mSendGiftInfo){
        
        View mShowGiftLayout = mGiftShowNow.get(mSendGiftInfo.getUid());
        
        mSendGiftInfo.setSendTime(System.currentTimeMillis());
        boolean isShow = false;
        boolean isFirst = false;
        
        if(mGiftShowQueue.get(mSendGiftInfo.getUid()) == null){
            mGiftShowQueue.put(mSendGiftInfo.getUid(),mSendGiftInfo);
            
            isFirst = true;
        }
        
        boolean isNewGift = !(mSendGiftInfo.getGiftid() == mGiftShowQueue.get(mSendGiftInfo.getUid()).getGiftid());
        
        if((mGiftShowNow.size() < 2) && (mShowGiftLayout == null)){
            
            mShowGiftLayout = initShowEvenSentSendGift(mSendGiftInfo);
            isShow = true;
        }
        
        if(mShowGiftLayout != null){
            isShow = true;
        }
        
        if(isNewGift && mShowGiftLayout != null){
            ((AvatarView)mShowGiftLayout.findViewById(R.id.av_gift_icon)).setAvatarUrl(mSendGiftInfo.getGifticon());

            ((TextView)mShowGiftLayout.findViewById(R.id.tv_show_gift_num)).setText("X1");
            ((TextView)mShowGiftLayout.findViewById(R.id.tv_gift_gname)).setText(mSendGiftInfo.getGiftname());
            
            mGiftShowQueue.put(mSendGiftInfo.getUid(), mSendGiftInfo);
        }
        
        if(mSendGiftInfo.getEvensend().equals("y")&&(!isFirst)&&(!isNewGift)){
            
            mGiftShowQueue.get(mSendGiftInfo.getUid()).setGiftcount(mGiftShowQueue.get(mSendGiftInfo.getUid()).getGiftcount() + 1);
        }
        
        if(isShow && isFirst){
            showEvenSentGiftAnimation(mShowGiftLayout,mSendGiftInfo,1);
        }else if(isShow && (!isNewGift)){
            showGiftNumAnimation((TextView) mShowGiftLayout.findViewById(R.id.tv_show_gift_num), mSendGiftInfo.getUid());
        }


    }
    
    protected void showGiftInit(SendGiftBean mSendGiftInfo){
        
        if(null!=mTvYpNum &&null!=mSendGiftInfo){
            mTvYpNum.setText(String.valueOf(StringUtils.toInt(mTvYpNum.getText().toString()) + mSendGiftInfo.getTotalcoin()));
        }
        
        int gId = mSendGiftInfo.getGiftid();
        if(gId == 19 || gId == 21 || gId == 22 || gId == 9 || gId == 19  ){
            mLuxuryGiftShowQueue.add(mSendGiftInfo);
        }
        switchPlayAnimation(mSendGiftInfo);

    }

    public void chatListItemClick(ChatBean chat){

    }

    
    protected void changeEditStatus(boolean status) {
        if(status){
            mChatInput.setFocusable(true);
            mChatInput.setFocusableInTouchMode(true);
            mChatInput.requestFocus();
            InputMethodUtils.toggleSoftKeyboardState(this);
            mLiveChatEdit.setVisibility(View.VISIBLE);
            mButtonMenu.setVisibility(View.GONE);
        }else{
            if(mLiveChatEdit.getVisibility() != View.GONE && InputMethodUtils.isShowSoft(this)){
                InputMethodUtils.closeSoftKeyboard(this);
                mButtonMenu.setVisibility(View.VISIBLE);
                mLiveChatEdit.setVisibility(View.GONE);
            }
        }

    }

    
    protected void addChatMessage(ChatBean c){
        if(mListChats.size()>30)mListChats.remove(0);
        mListChats.add(c);
        mChatListAdapter.notifyDataSetChanged();

        if(mLvChatList!=null){

            mLvChatList.setSelection(mListChats.size() - 1);
        }

    }


    

    protected void addPayDanmu(final ChatBean c)  {

        final SoftReference<ChatBean>  refChatBean= new SoftReference<ChatBean>(c);

        Glide.with(this).load(c.getSimpleUserInfo().avatar).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Danmu danmu = new Danmu(0, c.getSimpleUserInfo().id, "Comment", resource, c.getContent(),refChatBean);
                mDanmuControl.addDanmu(danmu,c.getSimpleUserInfo().user_nicename,0);

            }
            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                SoftReference<ChatBean>  refChatBean= new SoftReference<ChatBean>(c);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chat_room_user);
                Danmu danmu = new Danmu(0, c.getSimpleUserInfo().id, "Comment", bitmap,  c.getContent(),refChatBean);
                mDanmuControl.addDanmu(danmu,c.getSimpleUserInfo().user_nicename,0);
            }
        }); 
    }

    protected void addDanmu(String userId,String body)  {

        Danmu danmu = new Danmu(0,userId, "Comment",null,body,null);
        mDanmuControl.addDanmu2(danmu,body,0);

    }

    
    protected void showUserInfoDialog(SimpleUserInfo toUserInfo){

        UserInfoDialogFragment u = new UserInfoDialogFragment();
        Bundle b = new Bundle();
        b.putParcelable("MYUSERINFO",mUser);
        b.putParcelable("TOUSERINFO",toUserInfo);
        b.putString("ROOMNUM",mRoomNum);
        u.setArguments(b);
        u.show(getSupportFragmentManager(),"UserInfoDialogFragment");
    }

    
    protected void showPrivateChat() {
        
        try {
            unregisterReceiver(broadCastReceiver);
        }catch (Exception e){

        }
        
        mIvNewPrivateChat.setVisibility(View.GONE);
        PrivateChatCorePagerDialogFragment privateChatFragment = new PrivateChatCorePagerDialogFragment();

        privateChatFragment.show(getSupportFragmentManager(),"PrivateChatCorePagerDialogFragment");
        privateChatFragment.setDialogInterface(new DialogInterface() {
            @Override
            public void cancelDialog(View v, Dialog d) {
                
                registerPrivateBroadcast();

                

                if(PhoneLivePrivateChat.getUnreadMsgsCount() > 0){
                    mIvNewPrivateChat.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void determineDialog(View v, Dialog d) {

            }
        });

    }

    
    public void onConnectRes(boolean res){
        if(res){
            mConnectionState = true;

            
            mIMControl.getZombieFans();
        }
    }


    
    protected void addZombieFans(String zombies){

        JSONArray fans = ApiUtils.checkIsSuccess(zombies);
        if(null != fans){
            try {
                
                JSONObject jsonInfoObj = fans.getJSONObject(0);
                JSONArray fansJsonArray = jsonInfoObj.getJSONArray("list");

                if(!(mUserList.size() >= 20) && fansJsonArray.length() > 0){
                    for(int i = 0; i < fansJsonArray.length() ; i++){
                        UserBean u = mGson.fromJson(fansJsonArray.getString(i),UserBean.class);
                        mUserList.add(u);
                    }
                    LiveUtils.sortUserList(mUserList);
                    mUserListAdapter.notifyDataSetChanged();
                }
                
                if(fansJsonArray.length() > 0){
                    IMControl.LIVE_USER_NUMS = StringUtils.toInt(jsonInfoObj.getString("nums"),0);
                    mTvLiveNum.setText(String.format(Locale.CANADA,"%d人观看",IMControl.LIVE_USER_NUMS));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    
    protected void onUserStatusChange(UserBean user,boolean state){
        
        mTvLiveNum.setText(String.format(Locale.CANADA,"%d人观看",IMControl.LIVE_USER_NUMS));

        for(int i = 0; i< mUserList.size(); i++){
            if(user.id.equals(mUserList.get(i).id)){
                mUserList.remove(i);
                break;
            }
        }
        if(state && !mUserList.contains(user)){
            
            mUserList.add(user);
            TLog.log("加入" +user.id);

            if(StringUtils.toInt(user.level) >= AppConfig.JOIN_ROOM_ANIMATION_LEVEL){
                joinRoomAnimation(user);
            }

        }else{

            TLog.log("离开" +user.id);
        }

        
        LiveUtils.sortUserList(mUserList);
        mUserListAdapter.notifyDataSetChanged();
    }


    
    private void joinRoomAnimation(UserBean user) {

        mJoinRoomAnimationQueue.add(user);

        if(mJoinRoomAnimationQueue.size() == 1){
            startJoinRoomAnimation();
        }
    }

    
    private void startJoinRoomAnimation() {

        UserBean user = mJoinRoomAnimationQueue.get(0);

        SpannableStringBuilder name = new SpannableStringBuilder("  " + user.user_nicename);
        
        Drawable levelDrawable = getResources().getDrawable(LiveUtils.getLevelRes(user.level));
        levelDrawable.setBounds(0, 0, (int) TDevice.dpToPixel(35), (int) TDevice.dpToPixel(15));
        VerticalImageSpan levelImage = new VerticalImageSpan(levelDrawable);
         if(viptype!=0) {
             Drawable vipDrawable = getResources().getDrawable(LiveUtils.getVipRes(viptype));
             vipDrawable.setBounds(0, 0, (int) TDevice.dpToPixel(20), (int) TDevice.dpToPixel(20));
             VerticalImageSpan vipImage = new VerticalImageSpan(vipDrawable);//vip标记
             name.setSpan(new ForegroundColorSpan(Color.parseColor("#00FAF6")), 1, name.length(),
                     Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
             name.setSpan(vipImage, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
             name.setSpan(levelImage, 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
              if(viptype==2)  showFireworksAnimation();
           //  if(viptype==2)    showRedCarAnimation(mLuxuryGiftShowQueue.get(0))  ;
         }else {
             name.setSpan(levelImage, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         }
        name.append("进入房间");


        mTvJoinRoomAnimation.setText(name);
        ObjectAnimator animation = ObjectAnimator.ofFloat(mTvJoinRoomAnimation,"translationX",-mTvJoinRoomAnimation.getWidth(),0);
        animation.setDuration(1500);
        animation.start();
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if(mHandler == null)return;

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mTvJoinRoomAnimation.setX(mScreenWidth);
                        mJoinRoomAnimationQueue.remove(0);

                        if(mJoinRoomAnimationQueue.size() != 0){

                            startJoinRoomAnimation();

                        }

                    }
                },1500);


            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }


    
    protected void showLiveEndDialog(String uid,String stream){

        if(!uid.equals(mRoomNum)){
            LiveEndFragmentDialog liveEndFragmentDialog = new LiveEndFragmentDialog();
            Bundle bundle = new Bundle();
            bundle.putString("roomnum",mRoomNum);
            liveEndFragmentDialog.setArguments(bundle);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(liveEndFragmentDialog, "liveEndFragmentDialog");
            transaction.commitAllowingStateLoss();
        }else{
            LiveEmceeEndFragmentDialog dialog = new LiveEmceeEndFragmentDialog();
            Bundle bundle = new Bundle();
            bundle.putString("stream",stream);
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(),"LiveEmceeEndFragmentDialog");
        }

    }

    
    @Override
    protected void onPause() {
        super.onPause();
        mDanmuControl.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDanmuControl.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDanmuControl.destroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        
        try {
            unregisterReceiver(broadCastReceiver);
        }catch (Exception e){

        }
    }

    @Override
    public void onClick(View view) {

    }

    
    protected void sendBarrage() {

        if(mChatInput.getText().toString().trim().length() == 0 || (!mConnectionState))return;

        PhoneLiveApi.sendBarrage(mUser,mChatInput.getText().toString(),mRoomNum,mStreamName,new StringCallback() {
            @Override
            public void onError(Call call, Exception e,int id) {

            }
            @Override
            public void onResponse(String response,int id) {

                JSONArray data = ApiUtils.checkIsSuccess(response);
                if (data != null) {
                    try {
                        JSONObject tokenJson = data.getJSONObject(0);

                        mUser.coin = tokenJson.getString("coin");
                        mUser.level = tokenJson.getString("level");
                        mIMControl.doSendBarrage(tokenJson.getString("barragetoken"), mUser);
                        mChatInput.setText("");
                        mChatInput.setHint("弹幕，" + barrageFee + AppConfig.CURRENCY_NAME + "/条");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }



    @Override
    protected boolean hasActionBar() {
        return false;
    }

}

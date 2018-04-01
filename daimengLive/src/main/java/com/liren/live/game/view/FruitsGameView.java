package com.liren.live.game.view;


import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liren.live.R;
import com.liren.live.game.adapter.FruitsGameAdapter;
import com.liren.live.game.base.GameOnClickStakeInterface;
import com.liren.live.game.bean.FruitsGameBean;
import com.liren.live.utils.TDevice;
import com.liren.live.utils.UIHelper;
import com.liren.live.widget.SpaceRecycleView;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.List;

public class FruitsGameView extends RelativeLayout {

    private RecyclerView mRvFruits,mRvOrder;
    private Context mContext;
    private List<FruitsGameBean> mListFruits = new ArrayList<>();
    private FruitsGameAdapter fruitsGameAdapter;
    //倒计时View
    private TextView mTvCountDown;
    //中间
    private ImageView mIvCenter;
    //显示游戏结果
    private LinearLayout mLlShowGameResult;
    //提示信息
    private TextView mTvGameMessage;
    //用户余额
    private TextView mTvCoin;
    private LinearLayout mLlGameSelectNumGroup;
    //点击充值
    private LinearLayout mLlRecharge;

    private Button mBtnStartGame,mBtnShowGoneGame,mBtnGameOver;

    private LinearLayout mLlGameContentView;

    private boolean gameStatus;

    public FruitsGameView(Context context) {
        super(context);
        
        init(context);
    }

    public FruitsGameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public FruitsGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    public void setListFruits(List<FruitsGameBean> mListFruits) {
        this.mListFruits.clear();
        this.mListFruits.addAll(mListFruits);
    }

    private void init(Context context) {

        View view = inflate(context, R.layout.view_game_fruits,this);
        mRvFruits = (RecyclerView) view.findViewById(R.id.rv_game_fruits);
        mTvCountDown = (TextView) view.findViewById(R.id.tv_game_count_down);
        mIvCenter = (ImageView) view.findViewById(R.id.iv_game_card);
        mLlShowGameResult = (LinearLayout) view.findViewById(R.id.ll_game_fruits_result);
        mTvGameMessage = (TextView) view.findViewById(R.id.tv_game_message);
        mTvCoin = (TextView) view.findViewById(R.id.tv_game_my_coin);
        mLlGameSelectNumGroup = (LinearLayout) view.findViewById(R.id.ll_game_select_num_group);
        mLlRecharge = (LinearLayout) view.findViewById(R.id.ll_game_recharge);
        mBtnStartGame = (Button) view.findViewById(R.id.btn_live_start_game);
        mBtnShowGoneGame = (Button) view.findViewById(R.id.btn_live_show_gone_game);
        mLlGameContentView = (LinearLayout) view.findViewById(R.id.ll_live_game_content_view);
        mBtnGameOver = (Button) view.findViewById(R.id.btn_live_game_over);
        //点击充值
        mLlRecharge.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UIHelper.showMyDiamonds(mContext);
            }
        });

        fruitsGameAdapter = new FruitsGameAdapter(mListFruits);
        mContext = context;
        mRvFruits.setLayoutManager(new GridLayoutManager(mContext,3));
        mRvFruits.addItemDecoration(new SpaceRecycleView(5));



        //显示隐藏游戏内容布局
        mBtnShowGoneGame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mLlGameContentView.getVisibility() == VISIBLE){
                    mLlGameContentView.setVisibility(GONE);
                }else{
                    mLlGameContentView.setVisibility(VISIBLE);
                }
            }
        });
    }

    //开始游戏按钮时间设置
    public void setStartGameListener(OnClickListener onClickListener){

        mBtnStartGame.setOnClickListener(onClickListener);
    }
    //结束游戏事件设置
    public void setGameOverListener(OnClickListener onClickListener){

        mBtnGameOver.setOnClickListener(onClickListener);
    }

    //设置每次点击押注事件
    public void setStakeSelectNumItemClick(final GameOnClickStakeInterface stakeItemClick){
        //选择每次压住的价格
        for(int i = 0; i < mLlGameSelectNumGroup.getChildCount(); i++){
            final int num = i;
            mLlGameSelectNumGroup.getChildAt(i).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(stakeItemClick != null){
                        stakeItemClick.onClickStake(num + 1);
                    }

                    for(int j = 0; j < mLlGameSelectNumGroup.getChildCount(); j++){
                        if(j == num){
                            mLlGameSelectNumGroup.getChildAt(j).setBackgroundResource(R.drawable.bg_game_fruits_select_num_2);
                        }else{
                            mLlGameSelectNumGroup.getChildAt(j).setBackgroundResource(R.drawable.bg_game_fruits_select_num);
                        }
                    }
                }
            });
        }
    }

    //下注选项点击事件
    public void setOnItemClick(BaseQuickAdapter.OnItemClickListener listener){

        fruitsGameAdapter.setOnItemClickListener(listener);
    }

    //开始游戏动画开始
    public void startGameAnimation(){

        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvCenter,"rotation",0f,360f);
        animator.setDuration(500);
        animator.start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                //隐藏扑克
                mIvCenter.setVisibility(GONE);
                //显示倒计时UI
                mTvCountDown.setVisibility(VISIBLE);
                mRvFruits.setAdapter(fruitsGameAdapter);
                fruitsGameAdapter.openLoadAnimation(BaseQuickAdapter.HEADER_VIEW);

                showGameStatus("开始竞猜");

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    //揭晓结果
    public void openGameResult(int... id){

        mLlShowGameResult.removeAllViews();
        mLlShowGameResult.setVisibility(VISIBLE);
        Handler handler = new Handler();
        for(int i = 0; i < id.length; i ++){
            
            ImageButton img = new ImageButton(mContext);
            img.setBackgroundResource(R.mipmap.card_bg3);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams((int)TDevice.dpToPixel(60),(int)TDevice.dpToPixel(60));
            param.setMargins(10,10,10,10);

            img.setLayoutParams(param);

            mLlShowGameResult.addView(img);
            showItemPoker(handler,img,id[i],i);
        }
        //ObjectAnimator animatorX = ObjectAnimator.ofFloat(mLlShowGameResult,"translationX", TDevice.dpToPixel(10));
        //ObjectAnimator animatorY = ObjectAnimator.ofFloat(mLlShowGameResult,"translationY", TDevice.dpToPixel(100));

    }

    //延时显示每一个卡片的结果
    private void showItemPoker(Handler handler, final ImageView img, final int imgPic, int i) {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                img.setBackgroundResource(R.drawable.bg_game_fruits);
                img.setImageResource(imgPic);
            }
        },i * 1000);

    }

    //更新某一个Item数据
    public void refreshItemData(){

        fruitsGameAdapter.notifyDataSetChanged();
    }

    //更新用户余额
    public void refreshCoin(String coin){
        mTvCoin.setText(coin);
    }

    //删除显示的游戏结果
    public void removeGameResultView(){
        mLlShowGameResult.setVisibility(INVISIBLE);
    }

    //倒计时
    public void refreshCountDownTv(int time){

        mTvCountDown.setText(String.valueOf(time));
    }

    //显示游戏状态
    public void showGameStatus(String msg) {

        mTvGameMessage.setText(msg);
        mTvGameMessage.setVisibility(VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mTvGameMessage,"alpha",1f,0f);
        animator.setDuration(3000);
        animator.start();
    }

    //游戏重新初始化UI
    public void refreshGameUI(){

        mListFruits.clear();
        fruitsGameAdapter.notifyDataSetChanged();
        mTvCountDown.setVisibility(INVISIBLE);
        mIvCenter.setVisibility(VISIBLE);
    }

    //修改游戏状态
    public void changeStartGameBtnStatus(boolean change){
        gameStatus = change;
        if(change){
            mBtnStartGame.setVisibility(GONE);
        }else{
            mBtnStartGame.setVisibility(VISIBLE);
        }
    }

    public void initGameMenu(boolean isEmcee){
        if(isEmcee){
            mBtnGameOver.setVisibility(VISIBLE);
            mBtnStartGame.setVisibility(VISIBLE);
        }
    }

    public boolean getGameStatus(){
        return gameStatus;
    }

}

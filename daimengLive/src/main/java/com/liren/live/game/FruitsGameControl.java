package com.liren.live.game;

import android.content.Context;
import android.widget.LinearLayout;

import com.liren.live.R;
import com.liren.live.game.base.GameControlInterface;
import com.liren.live.game.base.GameEventInterface;
import com.liren.live.game.base.GameOnClickStakeInterface;
import com.liren.live.game.bean.FruitsGameBean;
import com.liren.live.game.view.FruitsGameView;

import java.util.ArrayList;
import java.util.List;

public class FruitsGameControl implements GameControlInterface {

    private FruitsGameView fruitsGameView;
    private LinearLayout gameViewContent;
    private GameEventInterface gameEventInterface;

    private String gameId,fruitsToken;
    private int selectStakeNum = 10;
    public int time;
    private List<FruitsGameBean> mListFruits = new ArrayList<>();
    private List<Integer> mListBetIndex = new ArrayList<>();
    private Context mContext;


    public FruitsGameControl(Context context,LinearLayout gameViewContent,boolean isEmcee) {
        mContext = context;
        fruitsGameView = new FruitsGameView(context);

        this.gameViewContent = gameViewContent;
        mListFruits.add(new FruitsGameBean(R.mipmap.card_1,1,"0"));
        mListFruits.add(new FruitsGameBean(R.mipmap.card_2,2,"0"));
        mListFruits.add(new FruitsGameBean(R.mipmap.card_3,3,"0"));
        mListFruits.add(new FruitsGameBean(R.mipmap.card_4,4,"0"));
        mListFruits.add(new FruitsGameBean(R.mipmap.card_5,5,"0"));
        mListFruits.add(new FruitsGameBean(R.mipmap.card_6,6,"0"));

        fruitsGameView.setStakeSelectNumItemClick(new GameOnClickStakeInterface() {
            @Override
            public void onClickStake(int num) {
                selectStakeNum = (int) Math.pow(10,num);

            }
        });

        if(isEmcee){
            fruitsGameView.initGameMenu(isEmcee);
        }
    }

}

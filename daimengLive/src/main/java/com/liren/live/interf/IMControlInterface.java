package com.liren.live.interf;

import com.liren.live.bean.ChatBean;
import com.liren.live.bean.SendGiftBean;
import com.liren.live.bean.UserBean;
import com.liren.live.utils.SocketMsgUtils;

/**
 * Created by Administrator on 2016/3/17.
 */
public interface IMControlInterface {
    //发送消息
    void onMessageListen(SocketMsgUtils socketMsg,int type,ChatBean chatBean);
    //链接结果
    void onConnect(boolean res);
    //用户进出房间状态
    void onUserStateChange(SocketMsgUtils socketMsg,UserBean user, boolean upordown);
    //关闭房间
    void onRoomClose(int code);
    //发送礼物
    void onShowSendGift(SendGiftBean contentJson, ChatBean chatBean);
    //特权操作
    void onPrivilegeAction(SocketMsgUtils socketMsg,ChatBean c);
    //点亮
    void onLit(SocketMsgUtils socketMsg);
    //僵尸粉丝
    void onAddZombieFans(SocketMsgUtils socketMsg);
    void onError();
    //被踢出房间
    void onKickRoom(SocketMsgUtils socketMsg,ChatBean c);
    //被禁言
    void onShutUp(SocketMsgUtils socketMsg,ChatBean c);
    //弹幕消息
    void onDanMuMessage(SocketMsgUtils socketMsg,ChatBean c);
    //打开游戏
    void onOpenGame(SocketMsgUtils socketMsg);
    //游戏开始
    void onGameStart(SocketMsgUtils socketMsg);
    //游戏开奖
    void onGameOpenResult(SocketMsgUtils socketMsg);
    //游戏下注
    void onGameBet(SocketMsgUtils socketMsg);
    //开启计时房间
    void onOpenTimeLive(SocketMsgUtils msgUtils, ChatBean c);
    //结束游戏
    void onGameEnd(SocketMsgUtils socketMsg);
    //全站礼物广播通知
    void onAllGiftMessage(SocketMsgUtils socketMsg);
}

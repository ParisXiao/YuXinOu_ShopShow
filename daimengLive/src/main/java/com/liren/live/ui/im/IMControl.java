package com.liren.live.ui.im;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.google.gson.Gson;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.bean.ChatBean;
import com.liren.live.bean.SendGiftBean;
import com.liren.live.bean.SimpleUserInfo;
import com.liren.live.bean.UserBean;
import com.liren.live.interf.IMControlInterface;
import com.liren.live.utils.LiveUtils;
import com.liren.live.utils.SocketMsgUtils;
import com.liren.live.utils.StringUtils;
import com.liren.live.utils.TDevice;
import com.liren.live.utils.TLog;
import com.liren.live.widget.VerticalImageSpan;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class IMControl {

    public static final int[] heartImg = new int[]{R.drawable.plane_heart_cyan, R.drawable.plane_heart_pink, R.drawable.plane_heart_red, R.drawable.plane_heart_yellow,R.drawable.plane_heart_cyan};

    public static final String EVENT_NAME = "broadcast";

    private static final int SEND_CHAT    = 2;
    private static final int SYSTEM_NOT   = 1;
    private static final int NOTICE       = 0;
    private static final int PRIVELEGE    = 4;
    private static final int GAME_MESSAGE = 5;
    public static int LIVE_USER_NUMS = 0;

    private Socket mSocket;

    private Context context;

    private IMControlInterface mIMControl;

    private Gson mGson;

    
    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            TLog.log("socket断开连接");
        }
    };

    
    private Emitter.Listener onError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mIMControl.onError();
            TLog.log("socket连接Error");
        }
    };

    
    private Emitter.Listener onMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                String res = args[0].toString();

                SocketMsgUtils socketMsg = SocketMsgUtils.getFormatJsonMode(res);

                int action = StringUtils.toInt(socketMsg.getAction());

                
                switch (StringUtils.toInt(socketMsg.getMsgtype())) {
                    case GAME_MESSAGE:
                        
                        if(action == 1){

                            mIMControl.onGameStart(socketMsg);
                        }else if(action == 2){
                            
                            mIMControl.onGameOpenResult(socketMsg);
                        }else if(action == 0){

                            mIMControl.onOpenGame(socketMsg);
                        }else if(action == 3){
                            
                            mIMControl.onGameBet(socketMsg);
                        }else if(action == 4){
                            
                            mIMControl.onGameEnd(socketMsg);
                        }

                        break;
                    case SEND_CHAT:
                        
                        if (action == 0) {
                            onMessage(socketMsg);
                        }
                        break;
                    case SYSTEM_NOT:
                        if (action == 0) {
                            
                            onSendGift(socketMsg);

                        } else if (action == 18) {
                            
                            mIMControl.onRoomClose(0);

                        } else if(action == 7){
                            
                            onDanmuMessage(socketMsg);
                        } else if(action == 19){
                            
                            mIMControl.onRoomClose(1);
                        }else if(action == 2){
                            
                            mIMControl.onAllGiftMessage(socketMsg);
                        }
                        break;
                    case NOTICE:

                        if (action == 0) {
                            
                            IMControl.LIVE_USER_NUMS += 1;
                            mIMControl.onUserStateChange(socketMsg,mGson.fromJson(socketMsg.getCt(), UserBean.class), true);
                        } else if (action == 1) {
                            IMControl.LIVE_USER_NUMS -= 1;
                            mIMControl.onUserStateChange(socketMsg,mGson.fromJson(socketMsg.getCt(), UserBean.class), false);
                        } else if (action == 2) {
                            
                            mIMControl.onLit(socketMsg);
                        } else if (action == 3) {
                            
                            mIMControl.onAddZombieFans(socketMsg);
                        }
                        break;
                    case PRIVELEGE:
                        onPrivateAction(socketMsg);
                        break;
                    default:
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    
    private void onPrivateAction(SocketMsgUtils msgUtils) throws JSONException {

        SpannableStringBuilder msg = new SpannableStringBuilder(msgUtils.getCt());
        SpannableStringBuilder name = new SpannableStringBuilder("系统消息 : ");
        name.setSpan(new ForegroundColorSpan(Color.parseColor("#1EC659")), 0, name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msg.setSpan(new ForegroundColorSpan(Color.parseColor("#1EC659")), 0, msg.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ChatBean c = new ChatBean();
        c.setType(13);
        c.setSendChatMsg(msg);
        c.setUserNick(name);
        if(msgUtils.getAction().equals("1")){

            mIMControl.onShutUp(msgUtils,c);
        }else if(msgUtils.getAction().equals("2")){

            mIMControl.onKickRoom(msgUtils,c);

        }else if(msgUtils.getAction().equals("5")){

            mIMControl.onOpenTimeLive(msgUtils,c);
        }else{

            mIMControl.onPrivilegeAction(msgUtils,c);
        }

    }

    private void onDanmuMessage(SocketMsgUtils msgUtils) throws JSONException {

        String ct = msgUtils.getCt();
        ChatBean c = new ChatBean();
        SimpleUserInfo userInfo = new SimpleUserInfo();
        userInfo.id = msgUtils.getUid();
        userInfo.level = msgUtils.getLevel();
        userInfo.user_nicename = msgUtils.getUname();
        userInfo.avatar = msgUtils.getUHead();
        c.setSimpleUserInfo(userInfo);

        JSONObject jsonObject = new JSONObject(ct);

        c.setContent(jsonObject.getString("content"));
        mIMControl.onDanMuMessage(msgUtils,c);
    }


    
    private void onSendGift(SocketMsgUtils msgUtils) throws JSONException {
        ChatBean c = new ChatBean();
        SimpleUserInfo userInfo = new SimpleUserInfo();
        userInfo.id = msgUtils.getUid();
        userInfo.level = msgUtils.getLevel();
        userInfo.user_nicename = msgUtils.getUname();
        userInfo.avatar = msgUtils.getUHead();
        c.setSimpleUserInfo(userInfo);

        SendGiftBean mSendGiftInfo = mGson.fromJson(msgUtils.getCt(), SendGiftBean.class);

        mSendGiftInfo.setAvatar(userInfo.avatar);
        mSendGiftInfo.setEvensend(msgUtils.getParam("evensend","n"));
        mSendGiftInfo.setNicename(userInfo.user_nicename);
        String uname = "_ " + userInfo.user_nicename + " : ";
        SpannableStringBuilder msg = new SpannableStringBuilder("我送了" + mSendGiftInfo.getGiftcount() + "个" + mSendGiftInfo.getGiftname());
        SpannableStringBuilder name = new SpannableStringBuilder(uname);

        Drawable d = context.getResources().getDrawable(LiveUtils.getLevelRes(userInfo.level));
        d.setBounds(0, 0, (int) TDevice.dpToPixel(35), (int) TDevice.dpToPixel(15));
        VerticalImageSpan is = new VerticalImageSpan(d);
        name.setSpan(new ForegroundColorSpan(Color.parseColor("#F551B8")), 1, name.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if(msgUtils.getVipType()>0) {
            Drawable vipDrawable = context.getResources().getDrawable(LiveUtils.getVipRes(msgUtils.getVipType()-1));
            vipDrawable.setBounds(0, 0, (int) TDevice.dpToPixel(20), (int) TDevice.dpToPixel(20));
            VerticalImageSpan vipImage = new VerticalImageSpan(vipDrawable);//vip标记
            name.setSpan(new ForegroundColorSpan(Color.parseColor("#2DBCFB")), 1, name.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            name.setSpan(vipImage, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            name.setSpan(is, 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            name.setSpan(is, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        msg.setSpan(new ForegroundColorSpan(Color.parseColor("#F551B8")), 0, msg.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        c.setSendChatMsg(msg);
        c.setUserNick(name);


        mIMControl.onShowSendGift(mSendGiftInfo, c);
    }

    
    private void onMessage(SocketMsgUtils msgUtils) throws JSONException {


        ChatBean c = new ChatBean();
        SimpleUserInfo userInfo = new SimpleUserInfo();
        userInfo.id = msgUtils.getUid();
        userInfo.level = msgUtils.getLevel();
        userInfo.user_nicename = msgUtils.getUname();
        userInfo.avatar = msgUtils.getUHead();
        c.setSimpleUserInfo(userInfo);
        String uname = "_ " + userInfo.user_nicename + " : ";
        SpannableStringBuilder msg = new SpannableStringBuilder(msgUtils.getCt());
        SpannableStringBuilder name = new SpannableStringBuilder(uname);
        Drawable levelDrawable = context.getResources().getDrawable(LiveUtils.getLevelRes(userInfo.level));
        levelDrawable.setBounds(0, 0, (int) TDevice.dpToPixel(35), (int) TDevice.dpToPixel(15));
        VerticalImageSpan levelImage = new VerticalImageSpan(levelDrawable);

         if(msgUtils.getVipType()>0) {



             Drawable vipDrawable = context.getResources().getDrawable(LiveUtils.getVipRes(msgUtils.getVipType()-1));
             vipDrawable.setBounds(0, 0, (int) TDevice.dpToPixel(20), (int) TDevice.dpToPixel(20));
             VerticalImageSpan vipImage = new VerticalImageSpan(vipDrawable);//vip标记
             name.setSpan(new ForegroundColorSpan(Color.parseColor("#2DBCFB")), 1, name.length(),
                     Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
             name.setSpan(vipImage, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
             name.setSpan(levelImage, 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         }else{
             name.setSpan(levelImage, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         }

        String touid = String.valueOf(msgUtils.get2Uid());
        
        if ((!touid.equals("0") && (touid.equals(AppContext.getInstance().getLoginUid())))) {
            msg.setSpan(new ForegroundColorSpan(Color.rgb(232, 109, 130)), 0, msg.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        if (msgUtils.getParam("heart",0) > 0) {

            int index = msgUtils.getParam("heart",0);
            msg.append("❤");
            
            Drawable hearDrawable = context.getResources().getDrawable(heartImg[index]);
            hearDrawable.setBounds(0,0, (int) TDevice.dpToPixel(20), (int) TDevice.dpToPixel(20));
            VerticalImageSpan hearImage = new VerticalImageSpan(hearDrawable);
            msg.setSpan(hearImage, msg.length() - 1, msg.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        c.setSendChatMsg(msg);
        c.setUserNick(name);

        mIMControl.onMessageListen(msgUtils,2,c);
    }


    
    private Emitter.Listener onConn = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args[0].toString().equals("ok")) {
                mIMControl.onConnect(true);
            } else {
                mIMControl.onConnect(false);
            }
        }
    };

    public IMControl(IMControlInterface IMControlInterface, Context context,String chaturl) throws URISyntaxException {
        this.mIMControl = IMControlInterface;
        this.context = context;
        mGson = new Gson();
        try {

            IO.Options option = new IO.Options();
            option.forceNew = true;
            option.reconnection = true;
            option.reconnectionDelay = 2000;
            mSocket  = IO.socket(chaturl,option);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    
    public void connectSocketServer(UserBean u,final String stream,String liveuid) {
        publicSocketInitAction(u,stream,liveuid);
    }

    
    public void publicSocketInitAction(final UserBean u,final String stream,final String liveuid){

        if (null == mSocket) return;
        try {
            mSocket.connect();
            JSONObject dataJson = new JSONObject();
            dataJson.put("uid", u.id);
            dataJson.put("token", u.token);
            dataJson.put("roomnum", liveuid);
            dataJson.put("stream",stream);
            mSocket.emit("conn", dataJson);

            mSocket.on("conn", onConn);
            mSocket.on("broadcastingListen", onMessage);
            mSocket.on(mSocket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(mSocket.EVENT_ERROR, onError);
            mSocket.on(mSocket.EVENT_RECONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                    TLog.log("重连");
                    try {
                        JSONObject dataJson = new JSONObject();
                        dataJson.put("uid", u.id);
                        dataJson.put("token", u.token);
                        dataJson.put("roomnum", stream);
                        dataJson.put("liveuid",liveuid);
                        mSocket.emit("conn", dataJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    
    public void closeLive() {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("StartEndLive")
                .setAction("18")
                .setMsgtype(String.valueOf(SYSTEM_NOT))
                .setCt(context.getString(R.string.livestart))
                .build()
                .sendMessage(mSocket);
    }

    
    public void doSetCloseLive() {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("stopLive")
                .setAction("19")
                .setMsgtype(String.valueOf(SYSTEM_NOT))
                .setCt(context.getString(R.string.livestart))
                .build()
                .sendMessage(mSocket);

    }

    
    public void doSendGift(String token, UserBean mUser,String viptype, String evensend) {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SendGift")
                .setAction("0")
                .setMsgtype(String.valueOf(SYSTEM_NOT))
                .setMyUserInfo(mUser)
                .setViptype(viptype)//viptype
                .addParamToJson1("uhead",mUser.avatar_thumb)
                .setCt(token)
                .addParamToJson1("evensend",evensend)
                .build()
                .sendMessage(mSocket);

    }

    
    public void doSendBarrage(String token, UserBean mUser) {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SendBarrage")
                .setAction("7")
                .setMsgtype(String.valueOf(SYSTEM_NOT))
                .setMyUserInfo(mUser)
                .addParamToJson1("uhead",mUser.avatar_thumb)
                .setCt(token)
                .build()
                .sendMessage(mSocket);

    }

    
    public void doSetShutUp(UserBean mUser, SimpleUserInfo mToUser) {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("ShutUpUser")
                .setAction("1")
                .setMsgtype(String.valueOf(PRIVELEGE))
                .setMyUserInfo(mUser)
                .set2UserInfo(mToUser)
                .setCt(mToUser.user_nicename + "被禁言5分钟")
                .build()
                .sendMessage(mSocket);

    }

    
    public void doSetKick(UserBean mUser, SimpleUserInfo mToUser) {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("KickUser")
                .setAction("2")
                .setMsgtype(String.valueOf(PRIVELEGE))
                .setMyUserInfo(mUser)
                .set2UserInfo(mToUser)
                .setCt(mToUser.user_nicename + "被踢出房间")
                .build()
                .sendMessage(mSocket);
    }

    
    public void doSetOrRemoveManage(UserBean user, SimpleUserInfo touser, String content) {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("SystemNot")
                .setAction("13")
                .setMsgtype(String.valueOf(PRIVELEGE))
                .setMyUserInfo(user)
                .set2UserInfo(touser)
                .setCt(content)
                .build()
                .sendMessage(mSocket);


    }
    
    public void doSendSystemMessage(String msg,UserBean user){

        SocketMsgUtils.getNewJsonMode()
                .set_method_("SystemNot")
                .setAction("13")
                .setMsgtype(String.valueOf(PRIVELEGE))
                .setCt(msg)
                .build()
                .sendMessage(mSocket);
    }

    
    public void doSendMsg(String sendMsg, String viptype,UserBean user, int reply) {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("SendMsg")
                .setAction("0")
                .setMsgtype(String.valueOf(SEND_CHAT))
                .setViptype(viptype)//viptype
                .setMyUserInfo(user)//标记001
                .setCt(sendMsg)
                .build()
                .sendMessage(mSocket);
    }



    
    public void doSendLitMsg(UserBean user, int index) {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("SendMsg")
                .setAction("0")
                .setMsgtype("2")
                .setMyUserInfo(user)
                .setCt("我点亮了")
                .addParamToJson1("heart", String.valueOf(index + 1))
                .build()
                .sendMessage(mSocket);

    }

    
    public void getZombieFans() {

        SocketMsgUtils.getNewJsonMode()
                .set_method_("requestFans")
                .setAction("")
                .setMsgtype("")
                .build()
                .sendMessage(mSocket);

    }

    
    public void doSendOpenGame(int type){

        SocketMsgUtils.getNewJsonMode()
                .set_method_("gameMessage")
                .setAction("0")
                .addParamToJson1("type", String.valueOf(type))
                .setMsgtype(String.valueOf(GAME_MESSAGE))
                .build()
                .sendMessage(mSocket);

    }

    
    public void doSendStartFruitsGame(String gameId,String gameToken,String time){
        SocketMsgUtils.getNewJsonMode()
                .set_method_("startFruits")
                .setAction("1")
                .setMsgtype(String.valueOf(GAME_MESSAGE))
                .addParamToJson1("gameid",gameId)
                .addParamToJson1("gametoken",gameToken)
                .addParamToJson1("time",time)
                .build()
                .sendMessage(mSocket);
    }

    
    public void doSendBetMessage(String grade,String coin){
        SocketMsgUtils.getNewJsonMode()
                .set_method_("gameMessage")
                .setAction("6")
                .setMsgtype(String.valueOf(GAME_MESSAGE))
                .addParamToJson1("grade",grade)
                .addParamToJson1("coin",coin)
                .build()
                .sendMessage(mSocket);
    }
    
    public void doSendEndGame(){
        SocketMsgUtils.getNewJsonMode()
                .set_method_("gameMessage")
                .setAction("8")
                .setMsgtype(String.valueOf(GAME_MESSAGE))
                .build()
                .sendMessage(mSocket);
    }
    
    public void doSendOpenTimeLive(String time, String money) {
        SocketMsgUtils.getNewJsonMode()
                .set_method_("SystemNot")
                .setAction("5")
                .addParamToJson1("time",time)
                .addParamToJson1("money",money)
                .setMsgtype(String.valueOf(PRIVELEGE))
                .build()
                .sendMessage(mSocket);
    }
}

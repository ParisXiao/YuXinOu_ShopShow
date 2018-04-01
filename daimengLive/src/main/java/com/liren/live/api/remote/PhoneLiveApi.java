package com.liren.live.api.remote;
import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.bean.GiftBean;
import com.liren.live.bean.UserBean;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cn.sharesdk.framework.PlatformDb;

/**
 * 接口获取
 */
public class PhoneLiveApi {

    /**
    * 登陆
    * @param phone
    * @param code
    * */
    public static void login(String phone,String password,StringCallback callback){
        String url = AppConfig.MAIN_URL_API;
        try {
            OkHttpUtils.get()
                    .url(url)
                    .addParams("service","Login.userLogin")
                    .addParams("user_login",phone)
                    .addParams("user_pass",URLEncoder.encode(password,"UTF-8"))
                    .build()
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    public static void reg(String user_login,String user_pass,String user_pass2,String code,StringCallback callback){
        String url = AppConfig.MAIN_URL_API;
        try {
            OkHttpUtils.get()
                    .url(url)
                    .addParams("service","Login.userReg")
                    .addParams("user_login",user_login)
                    .addParams("user_pass",URLEncoder.encode(user_pass,"UTF-8"))
                    .addParams("user_pass2",URLEncoder.encode(user_pass2,"UTF-8"))
                    .addParams("code",code)
                    .build()
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void findPass(String user_login,String user_pass,String user_pass2,String code,StringCallback callback){
        String url = AppConfig.MAIN_URL_API;
        try {
            OkHttpUtils.get()
                    .url(url)
                    .addParams("service","Login.userFindPass")
                    .addParams("user_login",user_login)
                    .addParams("user_pass",URLEncoder.encode(user_pass,"UTF-8"))
                    .addParams("user_pass2",URLEncoder.encode(user_pass2,"UTF-8"))
                    .addParams("code",code)
                    .tag("findPass")
                    .build()
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取用户信息
     * @param token appkey
     * @param uid 用户id
     * @param callback 回调
     * */
    public static void getMyUserInfo(String uid, String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getBaseInfo")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("getMyUserInfo")
                .build()
                .execute(callback);

    }
    /**
     * 获取其他用户信息
     * @param uid 用户id
     * @param callback 回调
     * */
    public static void getOtherUserInfo(int uid,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getUserInfo")
                .addParams("uid", String.valueOf(uid))
                .tag("getOtherUserInfo")
                .build()
                .execute(callback);

    }
    /**
     * @dw 修改用户信息

     * */
    public static void saveInfo(String fields, String uid, String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.updateFields")
                .addParams("fields", fields)
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("saveInfo")
                .build()
                .execute(callback);
    }

    /**
     * @dw 进入直播间初始化信息
     * @param uid 当前用户id
     * @param showId 主播id
     * @param token  token
     * */
    public static void enterRoom(String uid,String showId,String token,String address,String stream,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.enterRoom")
                .addParams("uid", uid)
                .addParams("liveuid",showId)
                .addParams("token", token)
                .addParams("city", address)
                .addParams("stream",stream)
                    .tag("initRoomInfo")
                .build()
                .execute(callback);
    }


    /**
    * @dw 开始直播
     * @param uid 主播id
     * @param title 开始直播标题
     * @param token
     * */
    public static void createLive(String uid,String a1,String a2, String title, String token,String name, File file,String type,String type_val,StringCallback callback) {
        try {
            PostFormBuilder postFormBuilder = OkHttpUtils.post()
                    .url(AppConfig.MAIN_URL_API)
                    .addParams("service", "Live.createRoom")
                    .addParams("uid", String.valueOf(uid))
                    .addParams("title",URLEncoder.encode(title,"UTF-8"))
                    .addParams("user_nicename",name)
                    .addParams("avatar",a1)
                    .addParams("avatar_thumb",a2)
                    .addParams("city", AppContext.address)
                    .addParams("province",AppContext.province)
                    .addParams("lat", AppContext.lat)
                    .addParams("lng",AppContext.lng)
                    .addParams("token",token)
                    .addParams("type",type)
                    .addParams("type_val",type_val);
            if(file != null){
                postFormBuilder.addFile("file",file.getName(),file)
                        .tag("createLive")
                        .build()
                        .execute(callback);
            }else{
                postFormBuilder
                        .tag("createLive")
                        .build()
                        .execute(callback);
            }
        } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
        }

    }
    /**
    * @dw 关闭直播
    * @param token 用户的token
    * */
    public static void closeLive(String id,String token,String stream,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.stopRoom")
                .addParams("uid",id)
                .addParams("token",token)
                .addParams("stream",stream)
                .tag("closeLive")
                .build()
                .execute(callback);
    }

    /**
    * @dw 获取礼物列表
    * @param callback
    * */
    public static void getGiftList(String uid,String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.getGiftList")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("getGiftList")
                .build()
                .execute(callback);
    }

    /**
    * @dw 赠送礼物
    * @param g 赠送礼物信息
    * @param mUser 用户信息
    * @param mNowRoomNum 房间号(主播id)
    * */
    public static void sendGift(UserBean mUser, GiftBean g, String mNowRoomNum,String stream, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.sendGift")
                .addParams("token",mUser.token)
                .addParams("uid",String.valueOf(mUser.id))
                .addParams("liveuid", String.valueOf(mNowRoomNum))
                .addParams("giftid", String.valueOf(g.getId()))
                .addParams("giftcount","1")
                .addParams("stream",stream)
                .tag("sendGift")
                .build()
                .execute(callback);
    }
     /**
     * @dw 发送弹幕
     * @param content  弹幕信息
     * @param mUser 用户信息
     * @param mNowRoomNum 房间号(主播id)
     * */
    public static void sendBarrage(UserBean mUser, String content, String mNowRoomNum,String stream, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.sendBarrage")
                .addParams("token",mUser.token)
                .addParams("uid",mUser.id)
                .addParams("liveuid",mNowRoomNum)
                .addParams("content",content)
                .addParams("stream",stream)
                .addParams("giftid","1")
                .addParams("giftcount","1")
                .tag("sendBarrage")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取其他用户信息
    * @param uid 其他用户id
    * @param ucuid 当前用户自己的id
    * */
    public static void getUserInfo(String uid,String ucuid,String liveuid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.getPop")
                .addParams("uid",uid)
                .addParams("touid",ucuid)
                .addParams("liveuid",liveuid)
                .tag("getUserInfo")
                .build()
                .execute(callback);
    }
    /**
    * @dw 判断是否关注
    * @param touid 当前主播id\
    * @param uid 当前用户uid
    * */
    public static void getIsFollow(String uid, String touid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.isAttent")
                .addParams("uid",uid)
                .addParams("touid",touid)
                .tag("getIsFollow")
                .build()
                .execute(callback);

    }
    /**
     * @dw 关注
     * @param uid 当前用户id
     * @param touid 关注用户id
     */
    public static void showFollow(String uid, String touid,String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.setAttent")
                .addParams("uid",uid)
                .addParams("touid",touid)
                .addParams("token",token)
                .tag("showFollow")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取homepage中的用户信息
    * @param uid 查询用户id
    * */
    public static void getHomePageUInfo(String uid,String touid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.getUserHome")
                .addParams("uid",uid)
                .addParams("touid",touid)
                .tag("getHomePageUInfo")
                .build()
                .execute(callback);
    }

    /**
     * @dw 获取homepage用户的fans
     * @param ucid 自己的id
     * @param uid 查询用户id  */
    public static void getFansList(String uid, String touid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.getFansList")
                .addParams("uid",uid)
                .addParams("touid",touid)
                .tag("getFansList")
                .build()
                .execute(callback);
    }

    /**
     * @dw 获取homepage用户的关注用户列表get
     * @param ucid 自己的id
     * @param uid 查询用户id
     * */
    public static void getAttentionList(String uid, String ucid,int pager,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.getFollowsList")
                .addParams("uid",uid)
                .addParams("touid",ucid)
                .addParams("p", String.valueOf(pager))
                .tag("getAttentionList")
                .build()
                .execute(callback);
    }

    /**
     * @dw 获取魅力值排行
     * @param uid 查询用户id
     * */
    public static void getYpOrder(String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getContributeList")
                .addParams("touid",uid)
                .tag("getYpOrder")
                .build()
                .execute(callback);

    }

    /**
     * @dw 获取收益信息
     * @param uid 查询用户id
     * @param token token
     * */
    public static void getWithdraw(String uid, String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "User.getProfit")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("getWithdraw")
                .build()
                .execute(callback);

    }
    /**
     * @dw 获取最新
     * */
    public static void getNewestUserList(int pager,StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service", "Home.getNew")
                .addParams("lng", AppContext.lng)
                .addParams("lat", AppContext.lat)
                .addParams("p", String.valueOf(pager))
                .tag("getNewestUserList")
                .build()
                .execute(callback);

    }
    /**
    * @dw 更新头像
    * @param uid 用户id
    * @param token
    * @param protraitFile 图片文件
    *
    * */
    public static void updatePortrait(String uid,String token, File protraitFile, StringCallback callback) {
        OkHttpUtils.post()
                .url(AppConfig.MAIN_URL_API+"appapi/")
                .addParams("service","User.updateAvatar")
                .addFile("file", "wp.png", protraitFile)
                .addParams("uid",uid)
                .addParams("token", token)
                //.url(AppConfig.MAIN_URL_API + "appapi/?service=User.upload")
                .tag("phonelive")
                .build()
                .execute(callback);

    }

    /**
    * @dw 提现
    * @param uid 用户id
    * @param token
    * */
    public static void requestCash(String uid, String token,String money, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.setCash")
                .addParams("uid",String.valueOf(uid))
                .addParams("token", token)
                .addParams("money", money)
                .tag("requestCash")
                .build()
                .execute(callback);
    }
    /**
     * @dw 直播记录
     * @param uid 用户id
     * */
    public static void getLiveRecord(int page,String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getLiverecord")
                .addParams("uid",uid)
                .addParams("touid",uid)
                .addParams("p", String.valueOf(page))
                .tag("getLiveRecord")
                .build()
                .execute(callback);
    }
    /**
    * @dw 支付宝下订单
    * @param uid 用户id
    * */
    public static void getAliPayOrderNum(String uid,String orderid,String changeid, String num,String money,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Charge.getAliOrder")
                .addParams("uid",uid)
                .addParams("orderid",orderid)
                .addParams("money",money)
                .addParams("changeid",changeid)
                .addParams("coin",num)
                .tag("getAliPayOrderNum")
                .build()
                .execute(callback);
    }
    //定位
    public static void getAddress(StringCallback callback) {
        OkHttpUtils.get()
                .url("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json")
                .tag("phonelive")
                .build()
                .execute(callback);
    }

    /**
    *@dw 搜索
    *@param screenKey 搜索关键词
    *@param uid 用户id
    * */
    public static void search(String screenKey, StringCallback callback,String uid) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Home.search")
                .addParams("key",screenKey)
                .addParams("uid",uid)
                .tag("search")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取地区列表
    *
    * */
    public static void getAreaList(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getArea")
                .tag("getAreaList")
                .build()
                .execute(callback);
    }
    /**
    * @dw 地区检索
    * @param sex 性别
    * @param area 地区
    * */

    public static void selectTermsScreen(int sex, String area, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.searchArea")
                .addParams("sex",String.valueOf(sex))
                .addParams("key",area)
                .tag("selectTermsScreen")
                .build()
                .execute(callback);
    }
    /**
    * @dw 批量获取用户信息
    * @param uidList 用户id字符串 以逗号分割
    *
    * */
    public static void getMultiBaseInfo(int action,String uid,String uidList, StringCallback callback) {

            OkHttpUtils.get()
                    .url(AppConfig.MAIN_URL_API)
                    .addParams("service","User.getMultiInfo")
                    .addParams("uids",uidList)
                    .addParams("type","2")
                    .addParams("uid",String.valueOf(uid))
                    .tag("getMultiBaseInfo")
                    .build()
                    .execute(callback);

    }

    /**
    * @dw 获取已关注正在直播的用户
    * @param uid 用户id
    * */
    public static void getAttentionLive(int pager,String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Home.getFollow")
                .addParams("uid",String.valueOf(uid))
                .addParams("p", String.valueOf(pager))
                .tag("getAttentionLive")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取用户信息私聊专用
    * @param uid  当前用户id
    * @param ucuid  to uid
    * */

    public static void getPmUserInfo(String uid, String ucuid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getPmUserInfo")
                .addParams("uid",uid)
                .addParams("touid",ucuid)
                .tag("getPmUserInfo")
                .build()
                .execute(callback);

    }

    /**
     * @dw 微信支付
     * @param uid 用户id
     * @param price 价格
     * */
    public static void wxPay(String uid,String orderid,String changeid, String price,String num ,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Charge.getWxOrder")
                .addParams("uid",uid)
                .addParams("changeid",changeid)
                .addParams("orderid",orderid)
                .addParams("coin",num)
                .addParams("money",price)
                .build()
                .execute(callback);

    }
    /**
    * @dw 第三方登录
    * @param platDB  用户信息
    * @param type 平台
    * */
    public static void otherLogin(String type,PlatformDb platDB, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Login.userLoginByThird")
                .addParams("openid",platDB.getUserId())
                .addParams("nicename",platDB.getUserName())
                .addParams("type",type)
                .addParams("avatar",platDB.getUserIcon())
                .tag("otherLogin")
                .build()
                .execute(callback);
    }
    /**
    * @dw 设为管理员
    * @param roomnum 房间号码
    * @param touid 操作id
    * @param token 用户登录token
    * */
    public static void setManage(String roomnum, String touid,String token,String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.setAdmin")
                .addParams("liveuid",roomnum)
                .addParams("touid",touid)
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("setManage")
                .build()
                .execute(callback);

    }
    /**
    * @dw 判断是否为管理员
    * @param uid 用户id
    * @param showid 房间号码
    * */

    public static void isManage(String showid, String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getIsAdmin")
                .addParams("liveuid",showid)
                .addParams("uid",uid)
                .tag("isManage")
                .build()
                .execute(callback);

    }
    /**
    * @dw 禁言
    * @param showid 房间id
    * @param touid 被禁言用户id
    * @param token 用户登录token
    * */
    public static void setShutUp(String showid, String touid, String uid,String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.setShutUp")
                .addParams("liveuid",showid)
                .addParams("touid",touid)
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("setShutUp")
                .build()
                .execute(callback);
    }
    //是否禁言解除
    public static void isShutUp(int uid, int showid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.isShutUp")
                .addParams("liveuid",String.valueOf(showid))
                .addParams("uid",String.valueOf(uid))
                .tag("isShutUp")
                .build()
                .execute(callback);
    }
    //token是否过期
    public static void tokenIsOutTime(String uid, String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.iftoken")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("tokenIsOutTime")
                .build()
                .execute(callback);
    }
    /**
    * @dw 拉黑
    * */
    public static void pullTheBlack(String uid, String touid, String token,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.setBlack")
                .addParams("uid",uid)
                .addParams("touid", touid)
                .addParams("token",token)
                .tag("pullTheBlack")
                .build()
                .execute(callback);

    }
    /**
    * @dw 黑名单列表
    * */
    public static void getBlackList(String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getBlackList")
                .addParams("uid", uid)
                .addParams("touid", uid)
                .tag("getBlackList")
                .build()
                .execute(callback);
    }

    public static void getMessageCode(String phoneNum,String methodName,StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service",methodName)
                .addParams("mobile", phoneNum)
                .tag("getMessageCode")
                .build()
                .execute(callback);
    }
    /**
    * @dw 获取用户余额
    * @param uid 用户id
    * */
    public static void getUserDiamondsNum(String uid,String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getUserPrivateInfo")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("getUserDiamondsNum")
                .build()
                .execute(callback);
    }
    /**
    * @dw 点亮
    * @param uid 用户id
    * @param token 用户登录token
    * @param showid 房间号
    * */
    public static void showLit(String uid,String token,String showid) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.setLight")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("showid",showid)
                .tag("showLit")
                .build()
                .execute(null);
    }
    /**
    * @dw 百度接口搜索音乐
    * @param keyword 歌曲关键词
    * */
    public static void searchMusic(String keyword, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","live.searchMusic")
                .addParams("key",keyword)
                .tag("searchMusic")
                .build()
                .execute(callback);

    }
    /**
    * @dw 获取music信息
    * @param songid 歌曲id
    * */
    public static void getMusicFileUrl(String songid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.getDownurl")
                .addParams("audio_id",songid)
                .tag("getMusicFileUrl")
                .build()
                .execute(callback);
    }

    /**
     * @param musicUrl 下载地址
     * @dw 下载音乐文件
     */
    public static void downloadMusic(String musicUrl, FileCallBack fileCallBack) {
        OkHttpUtils.get()
                .url(musicUrl)
                .tag("downloadMusic")
                .build()
                .execute(fileCallBack);
    }
    /**
     * @dw 开播等级限制
     * */
    public static void getLevelLimit(String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getLevelLimit")
                .addParams("uid",String.valueOf(uid))
                .tag("phonelive")
                .build()
                .execute(callback);
    }
    /**
     * @dw 检查新版本
     * */
    public static void checkUpdate(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getVersion")
                .tag("checkUpdate")
                .build()
                .execute(callback);
    }

    public static void downloadLrc(String musicLrc, FileCallBack fileCallBack) {
        OkHttpUtils.get()
                .url(musicLrc)
                .tag("downloadLrc")
                .build()
                .execute(fileCallBack);
    }
    /**
    * @dw 下载最新apk
    * @param apkUrl  下载地址
    * */
    public static void getNewVersionApk(String apkUrl,FileCallBack fileCallBack) {
        OkHttpUtils.get()
                .url(apkUrl)
                .tag("getNewVersionApk")
                .build()
                .execute(fileCallBack);
    }
    /**
    * @dw 获取管理员列表
    * @param uid  用户id
    * */
    public static void getManageList(String uid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.getAdminList")
                .addParams("liveuid",String.valueOf(uid))
                .tag("getManageList")
                .build()
                .execute(callback);
    }

    //举报
    public static void report(String uid, String token,String touid) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.setReport")
                .addParams("uid",uid)
                .addParams("touid",touid)
                .addParams("token",token)
                .addParams("content","涉嫌违规")
                .tag("report")
                .build()
                .execute(null);

    }


    //获取直播记录
    public static void getLiveRecordById(String showid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getAliCdnRecord")
                .addParams("id",showid)
                .tag("getLiveRecordById")
                .build()
                .execute(callback);
    }


    /**
     * @dw 判断是否购买vip
     * @param uid 用户id
     * @param viplevel vip登记
     *
     * */
    public static void isBuyVip(int uid, String viplevel, StringCallback isBuyVipCallback) {
        OkHttpUtils.post()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.isBuyVip")
                .addParams("uid",String.valueOf(uid))
                .addParams("viplevel",viplevel)
                .build()
                .execute(isBuyVipCallback);
    }

    public static void getConfig(StringCallback buyVipCallback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Home.getConfig")
                .tag("getConfig")
                .build()
                .execute(buyVipCallback);
    }

    public static void getChangePass(String uid,String token,String oldpass,String pass1st,String pass2nd, StringCallback getChangePassCallback) {
        try {
            OkHttpUtils.get()
                    .url(AppConfig.MAIN_URL_API)
                    .addParams("service","User.updatePass")
                    .addParams("uid",uid)
                    .addParams("token",token)
                    .addParams("oldpass",URLEncoder.encode(oldpass,"UTF-8"))
                    .addParams("pass",URLEncoder.encode(pass1st,"UTF-8"))
                    .addParams("pass2",URLEncoder.encode(pass2nd,"UTF-8"))
                    .tag("getChangePass")
                    .build()
                    .execute(getChangePassCallback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void getPubMsg(StringCallback getPubMsgCallback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.pub_msg")
                .tag("getPubMsg")
                .build()
                .execute(getPubMsgCallback);
    }

    //修改直播状态
    public static void changeLiveState(String uid,String token,String stream,String status,StringCallback callback){
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.changeLive")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("stream",stream)
                .addParams("status",status)
                .build()
                .execute(callback);
    }

    //检查房间状态
    public static void checkoutRoom(String uid, String token, String stream, String liveuid, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.checkLive")
                .addParams("uid",uid)
                .addParams("token",token)
                .addParams("stream",stream)
                .addParams("liveuid",liveuid)
                .build()
                .execute(callback);
    }

    //热门数据
    public static void requestHotData(int pager,StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Home.getHot")
                .addParams("p", String.valueOf(pager))
                .tag("requestHotData")
                .build()
                .execute(callback);
    }

    //我的钻石
    public static void requestBalance(String userID, String userToken, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getBalance")
                .addParams("uid",userID)
                .addParams("token",userToken)
                .tag("requestBalance")
                .build()
                .execute(callback);
    }

    //三方登录开启状态
    public static void requestOtherLoginStatus(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Home.getLogin")
                .tag("requestOtherLoginStatus")
                .build()
                .execute(callback);
    }

    //踢人
    public static void setKick(String showid, String touid, String uid,String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.kicking")
                .addParams("liveuid",showid)
                .addParams("touid",touid)
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("setKick")
                .build()
                .execute(callback);
    }

    //超管关闭直播
    public static void setCloseLive(String id, String token, String liveuid, String type, StringCallback callback) {

        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.superStopRoom")
                .addParams("liveuid",liveuid)
                .addParams("token",token)
                .addParams("uid",id)
                .addParams("type",type)
                .tag("setCloseLive")
                .build()
                .execute(callback);
    }

    //房间扣费
    public static void requestCharging(String uid, String token,String liveuid,String stream, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.roomCharge")
                .addParams("liveuid",liveuid)
                .addParams("token",token)
                .addParams("uid",uid)
                .addParams("stream",stream)
                .tag("requestCharging")
                .build()
                .execute(callback);
    }

    public static void getLiveEndInfo(String stream, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.stopInfo")
                .addParams("stream",stream)
                .tag("getLiveEndInfo")
                .build()
                .execute(callback);
    }

    //请求首游戏主播
    public static void requestGetGameLive(StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Home.getGame")
                .tag("requestGetGameLive")
                .build()
                .execute(callback);
    }

    /**
    * @dw 请求开始游戏
    * @param liveuid 主播ID
    * @param stream 流地址
    * @param token 主播token
    * */
    public static void requestStartGame(String liveuid, String stream, String token, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Game.fruitsGameStart")
                .addParams("liveuid",liveuid)
                .addParams("stream",stream)
                .addParams("token",token)
                .tag("requestStartGame")
                .build()
                .execute(callback);
    }


    /**
    * @dw 下注
    * @param uid 用户id
    * @param coin 下注金额
    * @param token
    * @param gameId 游戏id
    * @param grade 下注的哪一个
    * */
    public static void requestStakeGame(String uid, String token, String coin,String gameId,String grade, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Game.fruitsBet")
                .addParams("uid",uid)
                .addParams("coin",coin)
                .addParams("token",token)
                .addParams("gameid",gameId)
                .addParams("grade",grade)
                .tag("requestStakeGame")
                .build()
                .execute(callback);
    }

    /**
    * @dw 获取用户余额
    * @param uid 用户id
    * @param token 用户token
    * */
    public static void requestGetUserCoin(String uid,String token,StringCallback callback){
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","User.getUserCoin")
                .addParams("uid",uid)
                .addParams("token",token)
                .tag("requestGetUserCoin")
                .build()
                .execute(callback);
    }

    /**
    * @dw 修改房间类型
    * @param uid
    * @param token 用户token
    * @param stream 流明
    * @param coin 扣费金额
    * */
    public static void requestSetRoomType(String uid, String token, String stream,String coin, StringCallback callback) {
        OkHttpUtils.get()
                .url(AppConfig.MAIN_URL_API)
                .addParams("service","Live.openChargingRoom")
                .addParams("liveuid",uid)
                .addParams("token",token)
                .addParams("stream",stream)
                .addParams("coin",coin)
                .tag("requestSetRoomType")
                .build()
                .execute(callback);
    }
}

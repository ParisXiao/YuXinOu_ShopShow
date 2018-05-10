package com.liren.live.api.remote;

import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.bean.GiftBean;
import com.liren.live.bean.UserBean;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.FileCallback;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.PostRequest;

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
     *
     * @param phone
     */
    public static void login(String phone, String password, StringCallback callback) {
        String url = AppConfig.MAIN_URL_API;
        try {
            OkHttpUtils.get(url)
                    .url(url)
                    .params("service", "Login.userLogin")
                    .params("user_login", phone)
                    .params("user_pass", URLEncoder.encode(password, "UTF-8"))
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void reg(String user_login, String user_pass, String user_pass2, String code, StringCallback callback) {
        String url = AppConfig.MAIN_URL_API;
        try {
            OkHttpUtils.get(url)
                    .url(url)
                    .params("service", "Login.userReg")
                    .params("user_login", user_login)
                    .params("user_pass", URLEncoder.encode(user_pass, "UTF-8"))
                    .params("user_pass2", URLEncoder.encode(user_pass2, "UTF-8"))
                    .params("code", code)
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void findPass(String user_login, String user_pass, String user_pass2, String code, StringCallback callback) {
        String url = AppConfig.MAIN_URL_API;
        try {
            OkHttpUtils.get(url)
                    .url(url)
                    .params("service", "Login.userFindPass")
                    .params("user_login", user_login)
                    .params("user_pass", URLEncoder.encode(user_pass, "UTF-8"))
                    .params("user_pass2", URLEncoder.encode(user_pass2, "UTF-8"))
                    .params("code", code)
                    .tag("findPass")
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取用户信息
     *
     * @param token    appkey
     * @param uid      用户id
     * @param callback 回调
     */
    public static void getMyUserInfo(String uid, String token, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getBaseInfo")
                .params("uid", uid)
                .params("token", token)
                .tag("getMyUserInfo")
                .execute(callback);

    }

    /**
     * 获取其他用户信息
     *
     * @param uid      用户id
     * @param callback 回调
     */
    public static void getOtherUserInfo(int uid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getUserInfo")
                .params("uid", String.valueOf(uid))
                .tag("getOtherUserInfo")
                .execute(callback);

    }

    /**
     * @dw 修改用户信息
     */
    public static void saveInfo(String fields, String uid, String token, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.updateFields")
                .params("fields", fields)
                .params("uid", uid)
                .params("token", token)
                .tag("saveInfo")
                .execute(callback);
    }

    /**
     * @param uid    当前用户id
     * @param showId 主播id
     * @param token  token
     * @dw 进入直播间初始化信息
     */
    public static void enterRoom(String uid, String showId, String token, String address, String stream, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.enterRoom")
                .params("uid", uid)
                .params("liveuid", showId)
                .params("token", token)
                .params("city", address)
                .params("stream", stream)
                .tag("initRoomInfo")
                .execute(callback);
    }


    /**
     * @param uid   主播id
     * @param title 开始直播标题
     * @param token
     * @dw 开始直播
     */
    public static void createLive(String uid, String a1, String a2, String title, String token, String name, File file, String type, String type_val, StringCallback callback) {
        try {
            PostRequest postFormBuilder = OkHttpUtils.post(AppConfig.MAIN_URL_API)
                    .url(AppConfig.MAIN_URL_API)
                    .params("service", "Live.createRoom")
                    .params("uid", String.valueOf(uid))
                    .params("title", URLEncoder.encode(title, "UTF-8"))
                    .params("user_nicename", name)
                    .params("avatar", a1)
                    .params("avatar_thumb", a2)
                    .params("city", AppContext.address)
                    .params("province", AppContext.province)
                    .params("lat", AppContext.lat)
                    .params("lng", AppContext.lng)
                    .params("token", token)
                    .params("type", type)
                    .params("type_val", type_val);
            postFormBuilder
                    .tag("createLive")
                    .execute(callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param token 用户的token
     * @dw 关闭直播
     */
    public static void closeLive(String id, String token, String stream, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.stopRoom")
                .params("uid", id)
                .params("token", token)
                .params("stream", stream)
                .tag("closeLive")
                .execute(callback);
    }

    /**
     * @param callback
     * @dw 获取礼物列表
     */
    public static void getGiftList(String uid, String token, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.getGiftList")
                .params("uid", uid)
                .params("token", token)
                .tag("getGiftList")
                .execute(callback);
    }

    /**
     * @param g           赠送礼物信息
     * @param mUser       用户信息
     * @param mNowRoomNum 房间号(主播id)
     * @dw 赠送礼物
     */
    public static void sendGift(UserBean mUser, GiftBean g, String mNowRoomNum, String stream, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.sendGift")
                .params("token", mUser.token)
                .params("uid", String.valueOf(mUser.id))
                .params("liveuid", String.valueOf(mNowRoomNum))
                .params("giftid", String.valueOf(g.getId()))
                .params("giftcount", "1")
                .params("stream", stream)
                .tag("sendGift")
                .execute(callback);
    }

    /**
     * @param content     弹幕信息
     * @param mUser       用户信息
     * @param mNowRoomNum 房间号(主播id)
     * @dw 发送弹幕
     */
    public static void sendBarrage(UserBean mUser, String content, String mNowRoomNum, String stream, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.sendBarrage")
                .params("token", mUser.token)
                .params("uid", mUser.id)
                .params("liveuid", mNowRoomNum)
                .params("content", content)
                .params("stream", stream)
                .params("giftid", "1")
                .params("giftcount", "1")
                .tag("sendBarrage")
                .execute(callback);
    }

    /**
     * @param uid   其他用户id
     * @param ucuid 当前用户自己的id
     * @dw 获取其他用户信息
     */
    public static void getUserInfo(String uid, String ucuid, String liveuid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.getPop")
                .params("uid", uid)
                .params("touid", ucuid)
                .params("liveuid", liveuid)
                .tag("getUserInfo")
                .execute(callback);
    }

    /**
     * @param touid 当前主播id\
     * @param uid   当前用户uid
     * @dw 判断是否关注
     */
    public static void getIsFollow(String uid, String touid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.isAttent")
                .params("uid", uid)
                .params("touid", touid)
                .tag("getIsFollow")
                .execute(callback);

    }

    /**
     * @param uid   当前用户id
     * @param touid 关注用户id
     * @dw 关注
     */
    public static void showFollow(String uid, String touid, String token, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.setAttent")
                .params("uid", uid)
                .params("touid", touid)
                .params("token", token)
                .tag("showFollow")
                .execute(callback);
    }

    /**
     * @param uid 查询用户id
     * @dw 获取homepage中的用户信息
     */
    public static void getHomePageUInfo(String uid, String touid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getUserHome")
                .params("uid", uid)
                .params("touid", touid)
                .tag("getHomePageUInfo")
                .execute(callback);
    }

    /**
     * @param uid  查询用户id
     * @dw 获取homepage用户的fans
     */
    public static void getFansList(String uid, String touid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getFansList")
                .params("uid", uid)
                .params("touid", touid)
                .tag("getFansList")
                .execute(callback);
    }

    /**
     * @param ucid 自己的id
     * @param uid  查询用户id
     * @dw 获取homepage用户的关注用户列表get
     */
    public static void getAttentionList(String uid, String ucid, int pager, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getFollowsList")
                .params("uid", uid)
                .params("touid", ucid)
                .params("p", String.valueOf(pager))
                .tag("getAttentionList")
                .execute(callback);
    }

    /**
     * @param uid 查询用户id
     * @dw 获取魅力值排行
     */
    public static void getYpOrder(String uid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getContributeList")
                .params("touid", uid)
                .tag("getYpOrder")
                .execute(callback);

    }

    /**
     * @param uid   查询用户id
     * @param token token
     * @dw 获取收益信息
     */
    public static void getWithdraw(String uid, String token, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getProfit")
                .params("uid", uid)
                .params("token", token)
                .tag("getWithdraw")
                .execute(callback);

    }

    /**
     * @dw 获取最新
     */
    public static void getNewestUserList(int pager, StringCallback callback) {

        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Home.getNew")
                .params("lng", AppContext.lng)
                .params("lat", AppContext.lat)
                .params("p", String.valueOf(pager))
                .tag("getNewestUserList")
                .execute(callback);

    }

    /**
     * @param uid          用户id
     * @param token
     * @param protraitFile 图片文件
     * @dw 更新头像
     */
    public static void updatePortrait(String uid, String token, File protraitFile, StringCallback callback) {
        OkHttpUtils.post(AppConfig.MAIN_URL_API + "appapi/")
                .url(AppConfig.MAIN_URL_API + "appapi/")
                .params("service", "User.updateAvatar")
                .params("file",  protraitFile)
                .params("uid", uid)
                .params("token", token)
                //.url(AppConfig.MAIN_URL_API + "appapi/?service=User.upload")
                .tag("phonelive")
                .execute(callback);

    }

    /**
     * @param uid   用户id
     * @param token
     * @dw 提现
     */
    public static void requestCash(String uid, String token, String money, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.setCash")
                .params("uid", String.valueOf(uid))
                .params("token", token)
                .params("money", money)
                .tag("requestCash")
                .execute(callback);
    }

    /**
     * @param uid 用户id
     * @dw 直播记录
     */
    public static void getLiveRecord(int page, String uid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getLiverecord")
                .params("uid", uid)
                .params("touid", uid)
                .params("p", String.valueOf(page))
                .tag("getLiveRecord")
                .execute(callback);
    }

    /**
     * @param uid 用户id
     * @dw 支付宝下订单
     */
    public static void getAliPayOrderNum(String uid, String orderid, String changeid, String num, String money, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Charge.getAliOrder")
                .params("uid", uid)
                .params("orderid", orderid)
                .params("money", money)
                .params("changeid", changeid)
                .params("coin", num)
                .tag("getAliPayOrderNum")
                .execute(callback);
    }

    //定位
    public static void getAddress(StringCallback callback) {
        OkHttpUtils.get("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json")
                .url("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json")
                .tag("phonelive")
                .execute(callback);
    }

    /**
     * @param screenKey 搜索关键词
     * @param uid       用户id
     * @dw 搜索
     */
    public static void search(String screenKey, StringCallback callback, String uid) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Home.search")
                .params("key", screenKey)
                .params("uid", uid)
                .tag("search")
                .execute(callback);
    }

    /**
     * @dw 获取地区列表
     */
    public static void getAreaList(StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getArea")
                .tag("getAreaList")
                .execute(callback);
    }

    /**
     * @param sex  性别
     * @param area 地区
     * @dw 地区检索
     */

    public static void selectTermsScreen(int sex, String area, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.searchArea")
                .params("sex", String.valueOf(sex))
                .params("key", area)
                .tag("selectTermsScreen")
                .execute(callback);
    }

    /**
     * @param uidList 用户id字符串 以逗号分割
     * @dw 批量获取用户信息
     */
    public static void getMultiBaseInfo(int action, String uid, String uidList, StringCallback callback) {

        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getMultiInfo")
                .params("uids", uidList)
                .params("type", "2")
                .params("uid", String.valueOf(uid))
                .tag("getMultiBaseInfo")
                .execute(callback);

    }

    /**
     * @param uid 用户id
     * @dw 获取已关注正在直播的用户
     */
    public static void getAttentionLive(int pager, String uid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Home.getFollow")
                .params("uid", String.valueOf(uid))
                .params("p", String.valueOf(pager))
                .tag("getAttentionLive")
                .execute(callback);
    }

    /**
     * @param uid   当前用户id
     * @param ucuid to uid
     * @dw 获取用户信息私聊专用
     */

    public static void getPmUserInfo(String uid, String ucuid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getPmUserInfo")
                .params("uid", uid)
                .params("touid", ucuid)
                .tag("getPmUserInfo")
                .execute(callback);

    }

    /**
     * @param uid   用户id
     * @param price 价格
     * @dw 微信支付
     */
    public static void wxPay(String uid, String orderid, String changeid, String price, String num, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Charge.getWxOrder")
                .params("uid", uid)
                .params("changeid", changeid)
                .params("orderid", orderid)
                .params("coin", num)
                .params("money", price)
                .execute(callback);

    }

    /**
     * @param platDB 用户信息
     * @param type   平台
     * @dw 第三方登录
     */
    public static void otherLogin(String type, PlatformDb platDB, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Login.userLoginByThird")
                .params("openid", platDB.getUserId())
                .params("nicename", platDB.getUserName())
                .params("type", type)
                .params("avatar", platDB.getUserIcon())
                .tag("otherLogin")
                .execute(callback);
    }

    /**
     * @param roomnum 房间号码
     * @param touid   操作id
     * @param token   用户登录token
     * @dw 设为管理员
     */
    public static void setManage(String roomnum, String touid, String token, String uid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.setAdmin")
                .params("liveuid", roomnum)
                .params("touid", touid)
                .params("uid", uid)
                .params("token", token)
                .tag("setManage")
                .execute(callback);

    }

    /**
     * @param uid    用户id
     * @param showid 房间号码
     * @dw 判断是否为管理员
     */

    public static void isManage(String showid, String uid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getIsAdmin")
                .params("liveuid", showid)
                .params("uid", uid)
                .tag("isManage")
                .execute(callback);

    }

    /**
     * @param showid 房间id
     * @param touid  被禁言用户id
     * @param token  用户登录token
     * @dw 禁言
     */
    public static void setShutUp(String showid, String touid, String uid, String token, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.setShutUp")
                .params("liveuid", showid)
                .params("touid", touid)
                .params("uid", uid)
                .params("token", token)
                .tag("setShutUp")
                .execute(callback);
    }

    //是否禁言解除
    public static void isShutUp(int uid, int showid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.isShutUp")
                .params("liveuid", String.valueOf(showid))
                .params("uid", String.valueOf(uid))
                .tag("isShutUp")
                .execute(callback);
    }

    //token是否过期
    public static void tokenIsOutTime(String uid, String token, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.iftoken")
                .params("uid", uid)
                .params("token", token)
                .tag("tokenIsOutTime")
                .execute(callback);
    }

    /**
     * @dw 拉黑
     */
    public static void pullTheBlack(String uid, String touid, String token, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.setBlack")
                .params("uid", uid)
                .params("touid", touid)
                .params("token", token)
                .tag("pullTheBlack")
                .execute(callback);

    }

    /**
     * @dw 黑名单列表
     */
    public static void getBlackList(String uid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getBlackList")
                .params("uid", uid)
                .params("touid", uid)
                .tag("getBlackList")
                .execute(callback);
    }

    public static void getMessageCode(String phoneNum, String methodName, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", methodName)
                .params("mobile", phoneNum)
                .tag("getMessageCode")
                .execute(callback);
    }

    /**
     * @param uid 用户id
     * @dw 获取用户余额
     */
    public static void getUserDiamondsNum(String uid, String token, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getUserPrivateInfo")
                .params("uid", uid)
                .params("token", token)
                .tag("getUserDiamondsNum")
                .execute(callback);
    }

    /**
     * @param uid    用户id
     * @param token  用户登录token
     * @param showid 房间号
     * @dw 点亮
     */
    public static void showLit(String uid, String token, String showid) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.setLight")
                .params("uid", uid)
                .params("token", token)
                .params("showid", showid)
                .tag("showLit")
                .execute(null);
    }

    /**
     * @param keyword 歌曲关键词
     * @dw 百度接口搜索音乐
     */
    public static void searchMusic(String keyword, StringCallback callback) {

        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "live.searchMusic")
                .params("key", keyword)
                .tag("searchMusic")
                .execute(callback);

    }

    /**
     * @param songid 歌曲id
     * @dw 获取music信息
     */
    public static void getMusicFileUrl(String songid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.getDownurl")
                .params("audio_id", songid)
                .tag("getMusicFileUrl")
                .execute(callback);
    }

    /**
     * @param musicUrl 下载地址
     * @dw 下载音乐文件
     */
    public static void downloadMusic(String musicUrl, FileCallback fileCallBack) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(musicUrl)
                .tag("downloadMusic")
                .execute(fileCallBack);
    }

    /**
     * @dw 开播等级限制
     */
    public static void getLevelLimit(String uid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getLevelLimit")
                .params("uid", String.valueOf(uid))
                .tag("phonelive")
                .execute(callback);
    }

    /**
     * @dw 检查新版本
     */
    public static void checkUpdate(StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getVersion")
                .tag("checkUpdate")
                .execute(callback);
    }

    public static void downloadLrc(String musicLrc, FileCallback fileCallBack) {
        OkHttpUtils.get(musicLrc)
                .url(musicLrc)
                .tag("downloadLrc")
                .execute(fileCallBack);
    }

    /**
     * @param apkUrl 下载地址
     * @dw 下载最新apk
     */
    public static void getNewVersionApk(String apkUrl, FileCallback fileCallBack) {
        OkHttpUtils.get(apkUrl)
                .url(apkUrl)
                .tag("getNewVersionApk")
                .execute(fileCallBack);
    }

    /**
     * @param uid 用户id
     * @dw 获取管理员列表
     */
    public static void getManageList(String uid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.getAdminList")
                .params("liveuid", String.valueOf(uid))
                .tag("getManageList")
                .execute(callback);
    }

    //举报
    public static void report(String uid, String token, String touid) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.setReport")
                .params("uid", uid)
                .params("touid", touid)
                .params("token", token)
                .params("content", "涉嫌违规")
                .tag("report")
                .execute(null);

    }


    //获取直播记录
    public static void getLiveRecordById(String showid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getAliCdnRecord")
                .params("id", showid)
                .tag("getLiveRecordById")
                .execute(callback);
    }


    /**
     * @param uid      用户id
     * @param viplevel vip登记
     * @dw 判断是否购买vip
     */
    public static void isBuyVip(int uid, String viplevel, StringCallback isBuyVipCallback) {
        OkHttpUtils.post(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.isBuyVip")
                .params("uid", String.valueOf(uid))
                .params("viplevel", viplevel)
                .execute(isBuyVipCallback);
    }

    public static void getConfig(StringCallback buyVipCallback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Home.getConfig")
                .tag("getConfig")
                .execute(buyVipCallback);
    }

    public static void getChangePass(String uid, String token, String oldpass, String pass1st, String pass2nd, StringCallback getChangePassCallback) {
        try {
            OkHttpUtils.get(AppConfig.MAIN_URL_API)
                    .url(AppConfig.MAIN_URL_API)
                    .params("service", "User.updatePass")
                    .params("uid", uid)
                    .params("token", token)
                    .params("oldpass", URLEncoder.encode(oldpass, "UTF-8"))
                    .params("pass", URLEncoder.encode(pass1st, "UTF-8"))
                    .params("pass2", URLEncoder.encode(pass2nd, "UTF-8"))
                    .tag("getChangePass")
                    .execute(getChangePassCallback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void getPubMsg(StringCallback getPubMsgCallback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.pub_msg")
                .tag("getPubMsg")
                .execute(getPubMsgCallback);
    }

    //修改直播状态
    public static void changeLiveState(String uid, String token, String stream, String status, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.changeLive")
                .params("uid", uid)
                .params("token", token)
                .params("stream", stream)
                .params("status", status)
                .execute(callback);
    }

    //检查房间状态
    public static void checkoutRoom(String uid, String token, String stream, String liveuid, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.checkLive")
                .params("uid", uid)
                .params("token", token)
                .params("stream", stream)
                .params("liveuid", liveuid)
                .execute(callback);
    }

    //热门数据
    public static void requestHotData(int pager, StringCallback callback) {

        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Home.getHot")
                .params("p", String.valueOf(pager))
                .tag("requestHotData")
                .execute(callback);
    }

    //我的钻石
    public static void requestBalance(String userID, String userToken, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getBalance")
                .params("uid", userID)
                .params("token", userToken)
                .tag("requestBalance")
                .execute(callback);
    }

    //三方登录开启状态
    public static void requestOtherLoginStatus(StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Home.getLogin")
                .tag("requestOtherLoginStatus")
                .execute(callback);
    }

    //踢人
    public static void setKick(String showid, String touid, String uid, String token, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.kicking")
                .params("liveuid", showid)
                .params("touid", touid)
                .params("uid", uid)
                .params("token", token)
                .tag("setKick")
                .execute(callback);
    }

    //超管关闭直播
    public static void setCloseLive(String id, String token, String liveuid, String type, StringCallback callback) {

        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.superStopRoom")
                .params("liveuid", liveuid)
                .params("token", token)
                .params("uid", id)
                .params("type", type)
                .tag("setCloseLive")
                .execute(callback);
    }

    //房间扣费
    public static void requestCharging(String uid, String token, String liveuid, String stream, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.roomCharge")
                .params("liveuid", liveuid)
                .params("token", token)
                .params("uid", uid)
                .params("stream", stream)
                .tag("requestCharging")
                .execute(callback);
    }

    public static void getLiveEndInfo(String stream, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.stopInfo")
                .params("stream", stream)
                .tag("getLiveEndInfo")
                .execute(callback);
    }

    //请求首游戏主播
    public static void requestGetGameLive(StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Home.getGame")
                .tag("requestGetGameLive")
                .execute(callback);
    }

    /**
     * @param liveuid 主播ID
     * @param stream  流地址
     * @param token   主播token
     * @dw 请求开始游戏
     */
    public static void requestStartGame(String liveuid, String stream, String token, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Game.fruitsGameStart")
                .params("liveuid", liveuid)
                .params("stream", stream)
                .params("token", token)
                .tag("requestStartGame")
                .execute(callback);
    }


    /**
     * @param uid    用户id
     * @param coin   下注金额
     * @param token
     * @param gameId 游戏id
     * @param grade  下注的哪一个
     * @dw 下注
     */
    public static void requestStakeGame(String uid, String token, String coin, String gameId, String grade, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Game.fruitsBet")
                .params("uid", uid)
                .params("coin", coin)
                .params("token", token)
                .params("gameid", gameId)
                .params("grade", grade)
                .tag("requestStakeGame")
                .execute(callback);
    }

    /**
     * @param uid   用户id
     * @param token 用户token
     * @dw 获取用户余额
     */
    public static void requestGetUserCoin(String uid, String token, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "User.getUserCoin")
                .params("uid", uid)
                .params("token", token)
                .tag("requestGetUserCoin")
                .execute(callback);
    }

    /**
     * @param uid
     * @param token  用户token
     * @param stream 流明
     * @param coin   扣费金额
     * @dw 修改房间类型
     */
    public static void requestSetRoomType(String uid, String token, String stream, String coin, StringCallback callback) {
        OkHttpUtils.get(AppConfig.MAIN_URL_API)
                .url(AppConfig.MAIN_URL_API)
                .params("service", "Live.openChargingRoom")
                .params("liveuid", uid)
                .params("token", token)
                .params("stream", stream)
                .params("coin", coin)
                .tag("requestSetRoomType")
                .execute(callback);
    }

}

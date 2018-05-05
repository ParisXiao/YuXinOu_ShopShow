package com.liren.live.config;

/**
 * Created by Administrator on 2018/5/2 0002.
 */

public class UrlConfig {
    public static String POST="http://183.230.180.239:58085/";
    //获取上传小视频的code
    public static String GetSignature=POST+"api/TencentSDK/GetSignature";
    //登录
    public static String MLogin=POST+"api/Member/MLogin";
    //注册
    public static String MRegister=POST+"api/Member/MRegister";
    //获取电商H5地址
    public static String SelPagesForPC=POST+"api/Member/SelPagesForPC";
     //获取热门直播地址
    public static String SelHotRoom=POST+"api/LiveRoom/SelHotRoom";





}

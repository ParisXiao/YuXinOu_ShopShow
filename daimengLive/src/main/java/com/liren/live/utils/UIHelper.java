package com.liren.live.utils;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.bean.LiveJson;
import com.liren.live.bean.PrivateChatUserBean;
import com.liren.live.bean.SimpleBackPage;
import com.liren.live.bean.UserBean;
import com.liren.live.em.ChangInfo;
import com.liren.live.fragment.ManageListDialogFragment;
import com.liren.live.interf.DialogInterface;
import com.liren.live.ui.ActionBarSimpleBackActivity;
import com.liren.live.ui.ChangePassActivity;
import com.liren.live.ui.DedicateOrderActivity;
import com.liren.live.ui.EditInfoActivity;
import com.liren.live.ui.HomePageActivity;
import com.liren.live.ui.LiveRecordActivity;
import com.liren.live.ui.MobileFindPassActivity;
import com.liren.live.ui.MobileRegActivity;
import com.liren.live.ui.RtmpPlayerActivity;
import com.liren.live.ui.SettingActivity;
import com.liren.live.ui.SettingPushLiveActivity;
import com.liren.live.ui.SimpleBackActivity;
import com.liren.live.ui.SimpleUserInfoListActivity;
import com.liren.live.ui.UserDiamondsActivity;
import com.liren.live.ui.UserInfoDetailActivity;
import com.liren.live.ui.UserLevelActivity;
import com.liren.live.ui.UserProfitActivity;
import com.liren.live.ui.UserSelectAvatarActivity;
import com.liren.live.ui.WebViewActivity;
import com.liren.live.ui.dialog.LiveCommon;
import com.liren.live.ui.logicactivity.MyMainActivity;
import com.liren.live.ui.logicactivity.MyVoiceActivity;
import com.liren.live.ui.logicactivity.NewLoginActivity;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 界面帮助类
 */

public class UIHelper {
    /**
     * 发送通知广播
     *
     * @param context
     */
    public static void sendBroadcastForNotice(Context context) {
        /*Intent intent = new Intent(NoticeService.INTENT_ACTION_BROADCAST);
        context.sendBroadcast(intent);*/
    }
    /**
     * 手机登录
     *
     * @param context
     */

    public static void showMobilLogin(Context context) {
        Intent intent = new Intent(context, NewLoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * 手机密码注册
     *
     * @param context
     */

    public static void showMobileRegLogin(Context context) {
        Intent intent = new Intent(context, MobileRegActivity.class);
        context.startActivity(intent);
    }

    /**
     * 手机密码注册
     *
     * @param context
     */

    public static void showUserFindPass(Context context) {
        Intent intent = new Intent(context, MobileFindPassActivity.class);
        context.startActivity(intent);
    }
    /**
     * 登陆选择
     *
     * @param context
     */
    public static void showLoginSelectActivity(Context context) {
        /*Intent intent = new Intent(context, SelectLoginActivity.class);
        context.startActivity(intent);*/
        showMobilLogin(context);

    }

    /**
     * 首页
     *
     * @param context
     */
    public static void showMainActivity(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(context, MyMainActivity.class);
        //Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);

    }
    /**
     * 我的详细资料
     *
     * @param context
     */
    public static void showMyInfoDetailActivity(Context context) {
        Intent intent = new Intent(context, UserInfoDetailActivity.class);
        context.startActivity(intent);
    }
    /**
     * 编辑资料
     *
     * @param context
     */
    public static void showEditInfoActivity(UserInfoDetailActivity context, String action,
                                            String prompt, String defaultStr, ChangInfo changInfo) {
        Intent intent = new Intent(context, EditInfoActivity.class);
        intent.putExtra(EditInfoActivity.EDITACTION,action);
        intent.putExtra(EditInfoActivity.EDITDEFAULT,defaultStr);
        intent.putExtra(EditInfoActivity.EDITPROMP,prompt);
        intent.putExtra(EditInfoActivity.EDITKEY, changInfo.getAction());
        context.startActivity(intent);
        //context.overridePendingTransition(R.anim.activity_open_start, 0);
    }

    public static void showSelectAvatar(UserInfoDetailActivity context, String avatar) {
        Intent intent = new Intent(context, UserSelectAvatarActivity.class);
        intent.putExtra("uhead",avatar);
        context.startActivity(intent);
        //context.overridePendingTransition(R.anim.activity_open_start, 0);
    }

    /**
     * 获取webviewClient对象
     *
     * @return
     */
    public static WebViewClient getWebViewClient() {

        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //showUrlRedirect(view.getContext(), url);
                return true;
            }
        };
    }

    /**
     * 我的等级
     *
     * @return
     */
    public static void showLevel(Context context) {
        Intent intent = new Intent(context, UserLevelActivity.class);
        context.startActivity(intent);
    }
    /**
     * 我的钻石
     *
     * @return
     */
    public static void showMyDiamonds(Context context) {
        Intent intent = new Intent(context, UserDiamondsActivity.class);
        context.startActivity(intent);
    }
    /**
     * 会员充值
     *
     * @return
     */

    /**
     * 我的收益
     *
     * @return
     */
    public static void showProfitActivity(Context context) {
        Intent intent = new Intent(context, UserProfitActivity.class);
        context.startActivity(intent);
    }
    /**
     * 设置
     *
     * @return
     */
    public static void showSetting(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }
    /**
     * 设置
     *
     * @return
     */
    public static void showMyVoice(Context context) {
        Intent intent = new Intent(context, MyVoiceActivity.class);
        context.startActivity(intent);
    }
    /**
     * 看直播
     *
     * @return
     */
    public static void showLookLiveActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, RtmpPlayerActivity.class);
        intent.putExtra(RtmpPlayerActivity.USER_INFO,bundle);
        context.startActivity(intent);
    }
    /**
     * 直播
     */
    public static void showRtmpPushActivity(Context context,int type) {
        Intent intent = new Intent(context, SettingPushLiveActivity.class);
        intent.putExtra("type",type);
        context.startActivity(intent);
    }
    /*
    * 其他用户个人信息
    * */
    public static void showHomePageActivity(Context context,String id) {
        Intent intent = new Intent(context, HomePageActivity.class);
        intent.putExtra("uid",id);
        context.startActivity(intent);
    }
    /*
    * 粉丝列表
    * */
    public static void showFansListActivity(Context context, String uid) {
        Intent intent = new Intent(context, SimpleUserInfoListActivity.class);
        intent.putExtra("uid",uid);
        intent.putExtra("type",1);
        context.startActivity(intent);
    }
    /*
    * 关注列表
    * */
    public static void showAttentionActivity(Context context, String uid) {
        Intent intent = new Intent(context, SimpleUserInfoListActivity.class);
        intent.putExtra("uid",uid);
        intent.putExtra("type",0);
        context.startActivity(intent);
    }
    //魅力值贡献榜
    public static void showDedicateOrderActivity(Context context, String uid) {

        Intent intent = new Intent(context, DedicateOrderActivity.class);
        intent.putExtra("uid",uid);
        context.startActivity(intent);
    }
    //直播记录
    public static void showLiveRecordActivity(Context context, String uid) {
        Intent intent = new Intent(context, LiveRecordActivity.class);
        intent.putExtra("uid",uid);
        context.startActivity(intent);
    }
    //私信页面
    public static void showPrivateChatSimple(Context context, String uid) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra("uid",uid);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, SimpleBackPage.USER_PRIVATECORE.getValue());
        context.startActivity(intent);
    }
    //私信详情
    public static void showPrivateChatMessage(Context context, PrivateChatUserBean user) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra("user",user);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, SimpleBackPage.USER_PRIVATECORE_MESSAGE.getValue());
        context.startActivity(intent);

    }
    //地区选择
    public static void showSelectArea(Context context) {
        Intent intent = new Intent(context,SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.AREA_SELECT.getValue());
        context.startActivity(intent);
    }
    //搜索
    public static void showScreen(Context context) {
        Intent intent = new Intent(context,SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.INDEX_SECREEN.getValue());
        context.startActivity(intent);
    }
    //打开网页
    public static void showWebView(Context context,String url, String title) {

        Intent intent = new Intent(context, WebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        bundle.putString("title",title);
        intent.putExtra("URL_INFO",bundle);
        context.startActivity(intent);
    }
    //黑名单
    public static void showBlackList(Context context) {
        Intent intent = new Intent(context,ActionBarSimpleBackActivity.class);
        intent.putExtra(ActionBarSimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.USER_BLACK_LIST.getValue());
        context.startActivity(intent);
    }
    //推送管理
    public static void showPushManage(Context context) {
        Intent intent = new Intent(context,ActionBarSimpleBackActivity.class);
        intent.putExtra(ActionBarSimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.USER_PUSH_MANAGE.getValue());
        context.startActivity(intent);
    }
    //搜索歌曲
    public static void showSearchMusic(Activity context) {
        Intent intent = new Intent(context,SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE,SimpleBackPage.LIVE_START_MUSIC.getValue());
        context.startActivityForResult(intent,1);
    }
    //管理员列表
    public static void shoManageListActivity(Context context) {
        Intent intent = new Intent(context,ManageListDialogFragment.class);
        context.startActivity(intent);
    }

    public static void showChangePassActivity(Context context) {
        Intent intent = new Intent(context, ChangePassActivity.class);
        context.startActivity(intent);
    }


    //进入直播间
    public static void startRtmpPlayerActivity(final Context context,final LiveJson live){

        PhoneLiveApi.checkoutRoom(AppContext.getInstance().getLoginUid()
                ,AppContext.getInstance().getToken(),live.stream,live.uid, new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONArray res = ApiUtils.checkIsSuccess(s);
                        if (res != null) {
                            try {
                                final JSONObject data = res.getJSONObject(0);
                                final int roomType = data.getInt("type");
                                if(roomType == 2){

                                    LiveCommon.showConfirmDialog(context, "提示", data.getString("type_msg"), new DialogInterface() {
                                        @Override
                                        public void cancelDialog(View v, Dialog d) {
                                            d.dismiss();
                                        }

                                        @Override
                                        public void determineDialog(View v, Dialog d) {
                                            PhoneLiveApi.requestCharging(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),
                                                    live.uid,live.stream,new StringCallback(){

                                                        @Override
                                                        public void onSuccess(String s, Call call, Response response) {
                                                            JSONArray res = ApiUtils.checkIsSuccess(s);

                                                            if(res != null){

                                                                UserBean userBean = AppContext.getInstance().getLoginUser();
                                                                try {
                                                                    userBean.coin = res.getJSONObject(0).getString("coin");
                                                                    AppContext.getInstance().saveUserInfo(userBean);
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                Bundle bundle = new Bundle();
                                                                bundle.putInt("type",roomType);
                                                                bundle.putParcelable("USER_INFO", live);
                                                                UIHelper.showLookLiveActivity(context, bundle);
                                                            }
                                                        }

                                                    });
                                            d.dismiss();
                                        }
                                    });

                                }else if(roomType == 3){

                                    //是否是第一次既进入
                                    if(data.getInt("is_first") != 0){
                                        LiveCommon.showConfirmDialog(context, "提示", data.getString("type_msg"), new DialogInterface() {

                                            @Override
                                            public void cancelDialog(View v, Dialog d) {
                                                d.dismiss();
                                            }

                                            @Override
                                            public void determineDialog(View v, Dialog d) {
                                                PhoneLiveApi.requestCharging(AppContext.getInstance().getLoginUid(),AppContext.getInstance().getToken(),
                                                        live.uid,live.stream,new StringCallback(){

                                                            @Override
                                                            public void onSuccess(String s, Call call, Response response) {
                                                                JSONArray res = ApiUtils.checkIsSuccess(s);

                                                                if(res != null){

                                                                    UserBean userBean = AppContext.getInstance().getLoginUser();
                                                                    try {
                                                                        userBean.coin = res.getJSONObject(0).getString("coin");
                                                                        AppContext.getInstance().saveUserInfo(userBean);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }

                                                                    try {
                                                                        Bundle bundle = new Bundle();
                                                                        bundle.putInt("type",roomType);
                                                                        bundle.putParcelable("USER_INFO", live);

                                                                        bundle.putInt("is_first",data.getInt("is_first"));
                                                                        bundle.putInt("time",data.getInt("type_time"));
                                                                        bundle.putInt("type_val",data.getInt("type_val"));
                                                                        UIHelper.showLookLiveActivity(context, bundle);
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            }


                                                        });
                                                d.dismiss();
                                            }
                                        });
                                    }else{
                                        //第一次进入
                                        try {
                                            Bundle bundle = new Bundle();
                                            bundle.putInt("type",roomType);
                                            bundle.putParcelable("USER_INFO", live);

                                            bundle.putInt("isfirst",data.getInt("is_first"));
                                            bundle.putInt("time",data.getInt("type_time"));
                                            bundle.putInt("type_val",data.getInt("type_val"));
                                            UIHelper.showLookLiveActivity(context, bundle);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }else if(roomType == 1){

                                    LiveCommon.showInputContentDialog(context, "请输入房间密码", new DialogInterface() {
                                        @Override
                                        public void cancelDialog(View v, Dialog d) {
                                            d.dismiss();
                                        }

                                        @Override
                                        public void determineDialog(View v, Dialog d) {
                                            try {
                                                EditText et = (EditText) d.findViewById(R.id.et_input);
                                                if(!data.getString("type_msg").equals(MD5.getMD5(et.getText().toString()))
                                                        && !data.getString("type_msg").contains(MD5.getMD5(et.getText().toString()))){
                                                    Toast.makeText(context,"密码错误",Toast.LENGTH_SHORT).show();
                                                    return;
                                                }

                                                Bundle bundle = new Bundle();

                                                bundle.putParcelable("USER_INFO", live);
                                                UIHelper.showLookLiveActivity(context, bundle);
                                                d.dismiss();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

                                }else{
                                    Bundle bundle = new Bundle();

                                    bundle.putParcelable("USER_INFO", live);
                                    UIHelper.showLookLiveActivity(context, bundle);
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }


                });
    }

}

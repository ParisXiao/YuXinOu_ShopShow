package com.liren.live.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.base.BaseActivity;
import com.liren.live.bean.UserBean;
import com.liren.live.interf.DialogInterface;
import com.liren.live.ui.dialog.LiveCommon;
import com.liren.live.utils.DialogHelp;
import com.liren.live.utils.FileUtil;
import com.liren.live.utils.ImageUtils;
import com.liren.live.utils.InputMethodUtils;
import com.liren.live.utils.ShareUtils;
import com.liren.live.utils.StringUtils;
import com.liren.live.widget.BlackEditText;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class SettingPushLiveActivity extends BaseActivity {
    //填写直播标题
    @BindView(R.id.et_start_live_title)
    BlackEditText mStartLiveTitle;

    //开始直播遮罩层
    @BindView(R.id.rl_start_live_bg)
    RelativeLayout mStartLiveBg;

    //开始直播btn
    @BindView(R.id.btn_start_live)
    Button mBtnStartLive;

    @BindView(R.id.cb_set_pass)
    CheckBox mCbSetPass;

    @BindView(R.id.cb_set_charge)
    CheckBox mCbCharge;

    @BindView(R.id.cb_set_charge_time)
    CheckBox mCbChargeTime;

    @BindView(R.id.iv_live_select_pic)
    ImageView mIvLivePic;

    @BindView(R.id.ll_select_live_mode)
    LinearLayout mLlSelectLiveMode;

    //分享模式 7为不分享任何平台
    private int shareType = 7;

    private UserBean mUser;

    private boolean isFrontCameraMirro = false;

    private boolean isClickStartLive = false;
    private String type = "0";
    private String type_val = "";

    private final static int CROP = 400;

    private final static String FILE_SAVEPATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/PhoneLive/Portrait/";

    private Uri origUri;
    private Uri cropUri;
    private File protraitFile;
    private String protraitPath;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ready_start_live;
    }

    @Override
    public void initView() {

        findViewById(R.id.iv_live_share_weibo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,0);
                shareType = 0 == shareType?7:0;
            }
        });
        findViewById(R.id.iv_live_share_timeline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,2);
                shareType = 2 == shareType?7:2;
            }
        });
        findViewById(R.id.iv_live_share_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,1);
                shareType = 1 == shareType?7:1;
            }
        });

        findViewById(R.id.iv_live_share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,3);
                shareType = 3 == shareType?7:3;
            }
        });
        findViewById(R.id.iv_live_share_qqzone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLiveShare(v,4);
                shareType = 4 == shareType?7:4;
            }
        });

        mCbSetPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                type = b ? "1" : "0";

                if(!b)return;
                LiveCommon.showInputContentDialog(SettingPushLiveActivity.this, "设置密码"
                        , new DialogInterface() {
                            @Override
                            public void cancelDialog(View v, Dialog d) {
                                d.dismiss();
                                mCbSetPass.setChecked(false);
                            }

                            @Override
                            public void determineDialog(View v, Dialog d) {

                                EditText e = (EditText) d.findViewById(R.id.et_input);
                                if(TextUtils.isEmpty(e.getText().toString())){
                                    AppContext.showToast("密码不能为空",0);
                                    return;
                                }
                                type_val = e.getText().toString();
                                d.dismiss();
                            }
                        });
            }
        });

        mCbCharge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                type = b ? "2" : "0";

                if(!b)return;
                LiveCommon.showInputContentDialog(SettingPushLiveActivity.this, "设置收费金额"
                        , new DialogInterface() {
                            @Override
                            public void cancelDialog(View v, Dialog d) {
                                d.dismiss();
                                mCbCharge.setChecked(false);
                            }

                            @Override
                            public void determineDialog(View v, Dialog d) {
                                EditText e = (EditText) d.findViewById(R.id.et_input);
                                if(TextUtils.isEmpty(e.getText().toString())){
                                    AppContext.showToast("金额不能为空",0);
                                    return;
                                }
                                type_val = e.getText().toString();
                                d.dismiss();
                            }
                        });
            }
        });

        mCbChargeTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                type = b ? "3" : "0";

                if(!b)return;
                LiveCommon.showInputContentDialog(SettingPushLiveActivity.this, "设置收费金额(每分钟)"
                        , new DialogInterface() {
                            @Override
                            public void cancelDialog(View v, Dialog d) {
                                d.dismiss();
                                mCbCharge.setChecked(false);
                            }

                            @Override
                            public void determineDialog(View v, Dialog d) {
                                EditText e = (EditText) d.findViewById(R.id.et_input);
                                if(TextUtils.isEmpty(e.getText().toString())){
                                    AppContext.showToast("金额不能为空",0);
                                    return;
                                }
                                type_val = e.getText().toString();
                                d.dismiss();
                            }
                        });
            }
        });

        mIvLivePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogHelp.getSelectDialog(SettingPushLiveActivity.this, new String[]{"摄像头", "相册"}
                        , new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialogInterface, int i) {
                                if(android.os.Build.VERSION.SDK_INT >= 23) {
                                    //摄像头权限检测
                                    if (ContextCompat.checkSelfPermission(SettingPushLiveActivity.this, Manifest.permission.CAMERA)
                                            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(SettingPushLiveActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        //进行权限请求
                                        ActivityCompat.requestPermissions(SettingPushLiveActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE},
                                                5);
                                    }else{
                                        if(i == 0){
                                            startTakePhoto();
                                        }else{
                                            startImagePick();
                                        }

                                    }
                                }else{
                                    if(i == 0){
                                        startTakePhoto();
                                    }else{
                                        startImagePick();
                                    }
                                }
                            }
                        }).create().show();
            }
        });



        if(getIntent().getIntExtra("type",0) == 1){
            mLlSelectLiveMode.setVisibility(View.GONE);
        }


    }

    @Override
    public void initData() {

        mUser = AppContext.getInstance().getLoginUser();
    }

    @OnClick({R.id.iv_live_exit,R.id.btn_start_live})
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start_live://创建房间
                //请求服务端存储记录
                createRoom();
                break;
            case R.id.iv_live_exit:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * @dw 创建直播房间
     * 请求服务端添加直播记录,分享直播
     * */
    private void createRoom() {
        if(shareType != 7){

            ShareUtils.share(SettingPushLiveActivity.this, shareType, mUser,null);
        }else {
            readyStart();
        }
        isClickStartLive = true;
        InputMethodUtils.closeSoftKeyboard(this);
        mBtnStartLive.setTextColor(getResources().getColor(R.color.white));
    }
    /**
     * @dw 准备直播
     * */
    private void readyStart() {

        if(getIntent().getIntExtra("type",0) == 1){
            type = "4";
        }
        //请求服务端
        PhoneLiveApi.createLive(mUser.id,mUser.avatar,mUser.avatar_thumb,
                StringUtils.getNewString(mStartLiveTitle.getText().toString()),mUser.token ,
                mUser.user_nicename,
                protraitFile,
                type,
                type_val,
                new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONArray res = ApiUtils.checkIsSuccess(s);
                        if(res != null){
                            try {
                                JSONObject data = res.getJSONObject(0);
                                RtmpPushActivity.RtmpPushActivity(SettingPushLiveActivity.this,
                                        data.getString("stream"),
                                        data.getString("barrage_fee"),
                                        data.getString("votestotal"),
                                        data.getString("push"),
                                        data.getString("chatserver"),
                                        type,
                                        isFrontCameraMirro);

                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else {
                            AppContext.showToast("开启直播失败,请退出重试");
                        }
                    }

                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isClickStartLive && shareType != 7){
            readyStart();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 5: {
                // 判断权限请求是否通过
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    startTakePhoto();
                    return;
                }
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AppContext.showToast("您已拒绝使用摄像头权限,无法使用摄像头拍照,请去设置中修改",0);
                }else if(grantResults.length > 0 && grantResults[1] != PackageManager.PERMISSION_GRANTED){
                    AppContext.showToast("您拒绝读取文件权限,无法上传图片,请到设置中修改",0);
                }
            }
        }
    }

    /**
     * 选择图片裁剪
     */
    private void startImagePick() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"),
                    ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
        }
    }
    // 裁剪头像的绝对路径
    private Uri getUploadTempFile(Uri uri) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            File savedir = new File(FILE_SAVEPATH);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        } else {
            AppContext.showToast("无法保存上传的头像，请检查SD卡是否挂载");
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(uri);

        // 如果是标准Uri
        if (StringUtils.isEmpty(thePath)) {
            thePath = ImageUtils.getAbsoluteImagePath(this, uri);
        }
        String ext = FileUtil.getFileFormat(thePath);
        ext = StringUtils.isEmpty(ext) ? "jpg" : ext;
        // 照片命名
        String cropFileName = "phonelive_crop_" + timeStamp + "." + ext;
        // 裁剪头像的绝对路径
        protraitPath = FILE_SAVEPATH + cropFileName;
        protraitFile = new File(protraitPath);

        cropUri = Uri.fromFile(protraitFile);
        return this.cropUri;
    }

    private void startTakePhoto() {
        Intent intent;
        // 判断是否挂载了SD卡
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/phonelive/Camera/";
            File savedir = new File(savePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (StringUtils.isEmpty(savePath)) {
            AppContext.showToastShort("无法保存照片，请检查SD卡是否挂载");
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String fileName = "phonelive_" + timeStamp + ".jpg";// 照片命名
        File out = new File(savePath, fileName);
        Uri uri = Uri.fromFile(out);
        origUri = uri;

        //theLarge = savePath + fileName;// 该照片的绝对路径

        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
    }
    /**
     * 拍照后裁剪
     *
     * @param data 原始图片
     */
    private void startActionCrop(Uri data) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(data, "image/*");
        intent.putExtra("output",this.getUploadTempFile(data));
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", CROP);// 输出图片大小
        intent.putExtra("outputY", CROP);
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent,
                ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnIntent) {
        if (resultCode != Activity.RESULT_OK)
            return;

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
                startActionCrop(origUri);// 拍照后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
                startActionCrop(imageReturnIntent.getData());// 选图后裁剪
                break;
            case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:

                if(protraitFile != null){
                    mIvLivePic.setImageBitmap(BitmapFactory.decodeFile(protraitPath));
                }
                break;
        }
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    /**
     * @dw 开始直播分享
     * @param v 点击按钮
     * @param type 分享平台
     * */
    private void startLiveShare(View v,int type){
        String titleStr = "";
        if(type == shareType){
            String titlesClose[] = getResources().getStringArray(R.array.live_start_share_close);
            titleStr = titlesClose[type];
        }else {
            String titlesOpen[] = getResources().getStringArray(R.array.live_start_share_open);
            titleStr = titlesOpen[type];
        }

        View popView  =  getLayoutInflater().inflate(R.layout.pop_view_share_start_live,null);
        TextView title = (TextView) popView.findViewById(R.id.tv_pop_share_start_live_prompt);
        title.setText(titleStr);
        PopupWindow pop = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popView.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setOutsideTouchable(true);
        int location[] = new int[2];
        v.getLocationOnScreen(location);
        pop.setFocusable(false);

        pop.showAtLocation(v, Gravity.NO_GRAVITY,location[0] + v.getWidth()/2 - popView.getMeasuredWidth()/2,location[1]- popView.getMeasuredHeight());

    }


}

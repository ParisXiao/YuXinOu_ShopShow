package com.liren.live.video.common.activity.videopreview;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.config.UrlConfig;
import com.liren.live.config.UserConfig;
import com.liren.live.utils.OKHttpUtils;
import com.liren.live.utils.PreferenceUtils;
import com.liren.live.video.common.utils.FileUtils;
import com.liren.live.video.common.utils.TCConstants;
import com.liren.live.video.common.widget.CustomProgressDialog;
import com.liren.live.video.videoupload.TXUGCPublish;
import com.liren.live.video.videoupload.TXUGCPublishTypeDef;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXLog;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 录制完成后的预览界面
 * Created by carolsuo on 2017/3/21.
 */

public class TCVideoPreviewActivity extends FragmentActivity implements View.OnClickListener, ITXLivePlayListener {
    public static final String TAG = "TCVideoPreviewActivity";

    private int mVideoSource; // 视频来源

    ImageView mStartPreview;
    boolean mVideoPlay = false;
    boolean mVideoPause = false;
    boolean mAutoPause = false;

    private ImageView mIvPublish;
    private ImageView mIvToEdit;
    private String mVideoPath;
    private String mCoverImagePath;
    ImageView mImageViewBg;
    private TXLivePlayer mTXLivePlayer = null;
    private TXLivePlayConfig mTXPlayConfig = null;
    private TXCloudVideoView mTXCloudVideoView;
    private SeekBar mSeekBar;
    private TextView mProgressTime;
    private long mTrackingTouchTS = 0;
    private boolean mStartSeek = false;
    //错误消息弹窗
    private ErrorDialogFragment mErrDlgFragment;
    //视频时长（ms）
    private long mVideoDuration;
    //录制界面传过来的视频分辨率
    private int mVideoResolution;
    private String Signature = "";
    private CustomProgressDialog mCustomProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mErrDlgFragment = new ErrorDialogFragment();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_video_preview);

        mStartPreview = (ImageView) findViewById(R.id.record_preview);
        mIvToEdit = (ImageView) findViewById(R.id.record_to_edit);
        mIvToEdit.setOnClickListener(this);

        mIvPublish = (ImageView) findViewById(R.id.video_publish);

        mVideoSource = getIntent().getIntExtra(TCConstants.VIDEO_RECORD_TYPE, TCConstants.VIDEO_RECORD_TYPE_EDIT);
        mVideoPath = getIntent().getStringExtra(TCConstants.VIDEO_RECORD_VIDEPATH);
        mCoverImagePath = getIntent().getStringExtra(TCConstants.VIDEO_RECORD_COVERPATH);
        mVideoDuration = getIntent().getLongExtra(TCConstants.VIDEO_RECORD_DURATION, 0);
        mVideoResolution = getIntent().getIntExtra(TCConstants.VIDEO_RECORD_RESOLUTION, -1);
        Log.i(TAG, "onCreate: CouverImagePath = " + mCoverImagePath);
        mImageViewBg = (ImageView) findViewById(R.id.cover);
        if (mCoverImagePath != null && !mCoverImagePath.isEmpty()) {
            Glide.with(this).load(Uri.fromFile(new File(mCoverImagePath)))
                    .into(mImageViewBg);
        }

        mCustomProgressDialog = new CustomProgressDialog();
        mCustomProgressDialog.createLoadingDialog(this, "");
        mCustomProgressDialog.setCancelable(false); // 设置是否可以通过点击Back键取消
        mCustomProgressDialog.setCanceledOnTouchOutside(false); // 设置在点击Dialog外是否取消Dialog进度条

        mTXLivePlayer = new TXLivePlayer(this);
        mTXPlayConfig = new TXLivePlayConfig();
        mTXCloudVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTXCloudVideoView.disableLog(true);

        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean bFromUser) {
                if (mProgressTime != null) {
                    mProgressTime.setText(String.format(Locale.CHINA, "%02d:%02d/%02d:%02d", (progress) / 60, (progress) % 60, (seekBar.getMax()) / 60, (seekBar.getMax()) % 60));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mStartSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mTXLivePlayer != null) {
                    mTXLivePlayer.seek(seekBar.getProgress());
                }
                mTrackingTouchTS = System.currentTimeMillis();
                mStartSeek = false;
            }
        });
        mProgressTime = (TextView) findViewById(R.id.progress_time);

//        mIvPublish.setVisibility(View.GONE);

        if (mVideoSource == TCConstants.VIDEO_RECORD_TYPE_UGC_RECORD) {
            mIvToEdit.setVisibility(View.VISIBLE);
        }
        //获取sign
        getSignCode();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_delete:
                deleteVideo();
                FileUtils.deleteFile(mCoverImagePath);
                break;
            case R.id.record_download:
                downloadRecord();
                break;
            case R.id.record_preview:
                if (mVideoPlay) {
                    if (mVideoPause) {
                        mTXLivePlayer.resume();
                        mStartPreview.setBackgroundResource(R.drawable.icon_record_pause);
                        mVideoPause = false;
                    } else {
                        mTXLivePlayer.pause();
                        mStartPreview.setBackgroundResource(R.drawable.icon_record_start);
                        mVideoPause = true;
                    }
                } else {
                    startPlay();
                }
                break;
            case R.id.video_publish:
                showPush();
                break;
            case R.id.record_to_edit:
                startEditVideo();
                break;
            default:
                break;
        }

    }

    private void startEditVideo() {
        // 播放器版本没有此activity
//        Intent intent = new Intent(this, TCVideoEditerActivity.class);
        Class editActivityClass = null;
        try {
            editActivityClass = Class.forName("com.tencent.liteav.demo.shortvideo.editor.TCVideoPreprocessActivity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (editActivityClass != null) {
            Intent intent = new Intent(this, editActivityClass);
            intent.putExtra(TCConstants.VIDEO_EDITER_PATH, mVideoPath);
            intent.putExtra(TCConstants.VIDEO_RECORD_COVERPATH, mCoverImagePath);
            intent.putExtra(TCConstants.VIDEO_RECORD_TYPE, mVideoSource);
            intent.putExtra(TCConstants.VIDEO_RECORD_RESOLUTION, mVideoResolution);
            startActivity(intent);
            finish();
        }
    }

    private void getSignCode() {
        Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (OKHttpUtils.isConllection(TCVideoPreviewActivity.this)) {
                    String[] key = new String[]{};
                    Map<String, String> map = new HashMap<String, String>();
//                    String token = PreferenceUtils.getInstance(TCVideoPreviewActivity.this).getString(UserConfig.DToken);
                    String token = AppContext.getInstance().getToken();
//                    String token ="0210b22418b65984d44462a6165dd9d3" ;
                    String result = OKHttpUtils.postData(TCVideoPreviewActivity.this, UrlConfig.GetSignature, token, key, map);
                    if (!TextUtils.isEmpty(result)) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(result);
                            String code = jsonObject.getString("code");
                            if (code.equals("0")) {
                                Signature = jsonObject.getString("result");
                            } else {
                                subscriber.onNext(0);
                            }

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                            subscriber.onNext(0);
                        }
                    } else {
                        subscriber.onNext(0);
                    }
                } else {
                    subscriber.onNext(0);

                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                switch (integer) {
                    case 0:
                        mIvPublish.setVisibility(View.INVISIBLE);
                        break;


                }
            }
        });


    }

    //    private void publish() {
//        stopPlay(false);
//        Intent intent = new Intent(getApplicationContext(), TCVideoPublisherActivity.class);
//        intent.putExtra(TCConstants.VIDEO_RECORD_TYPE, TCConstants.VIDEO_RECORD_TYPE_PLAY);
//        intent.putExtra(TCConstants.VIDEO_RECORD_VIDEPATH,  mVideoPath);
//        intent.putExtra(TCConstants.VIDEO_RECORD_COVERPATH, mCoverImagePath);
//        startActivity(intent);
//        finish();
//    }
    private String title="德玛西亚";

    private void showPush() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_video_push, null);
        final EditText edit_title = (EditText) v.findViewById(R.id.edit_title);
        LinearLayout video_push = (LinearLayout) v.findViewById(R.id.video_push);
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
        //dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
        //只用下面这一行弹出对话框时需要点击输入框才能弹出软键盘
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//加上下面这一行弹出对话框时软键盘随之弹出
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        video_push.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                title = edit_title.getText().toString().trim();
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(TCVideoPreviewActivity.this, "请编辑视频标题", Toast.LENGTH_SHORT).show();
                    return;
                }
                mCustomProgressDialog.show();
                publish();
                dialog.dismiss();
            }
        });


    }

    private void publish() {
        stopPlay(false);
        TXUGCPublish txugcPublish = new TXUGCPublish(this.getApplicationContext(), PreferenceUtils.getInstance(TCVideoPreviewActivity.this).getString(UserConfig.UserPhone));
        txugcPublish.setListener(new TXUGCPublishTypeDef.ITXVideoPublishListener() {
            @Override
            public void onPublishProgress(long uploadBytes, long totalBytes) {
                TXLog.d(TAG, "onPublishProgress [" + uploadBytes + "/" + totalBytes + "]");
            }

            @Override
            public void onPublishComplete(final TXUGCPublishTypeDef.TXPublishResult result) {
                TXLog.d(TAG, "onPublishComplete [" + result.retCode + "/" + (result.retCode == 0 ? result.videoURL : result.descMsg) + "]");
                if (result.retCode == 0) {

                    final String[] msg = new String[1];
                    Observable.create(new Observable.OnSubscribe<Integer>() {

                        @Override
                        public void call(Subscriber<? super Integer> subscriber) {
                            if (OKHttpUtils.isConllection(TCVideoPreviewActivity.this)) {
                                File file = new File(mCoverImagePath);
                                FileInputStream inputFile = null;
                                String img = "";
                                try {
                                    inputFile = new FileInputStream(file);
                                    byte[] buffer = new byte[(int) file.length()];
                                    inputFile.read(buffer);
                                    inputFile.close();
                                    img = Base64.encodeToString(buffer, Base64.DEFAULT);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String[] key = new String[]{"ReleaseID", "VideoName","WhetherHot","Lng","Lat","ReleaseAddress","VideoPath","VideoImagePath"};
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("ReleaseID", "15273");
                                map.put("VideoName", title);
                                map.put("WhetherHot", "");
                                map.put("Lng", "");
                                map.put("Lat", "");
                                map.put("ReleaseAddress", "");
                                map.put("VideoPath", result.videoURL);
                                map.put("VideoImagePath", img);
                                String sign = "Lat"+""+"Lng"+""+"ReleaseAddress"+""+"ReleaseID" + "15273" + "VideoName" + title + "VideoPath" + result.videoURL + "VideoImagePath" + img+"WhetherHot"+"";
//                                String token = PreferenceUtils.getInstance(getActivity()).getString(UserConfig.DToken);
//                                String token ="0210b22418b65984d44462a6165dd9d3" ;
                                String token = AppContext.getInstance().getToken();
                                String result = OKHttpUtils.postData(TCVideoPreviewActivity.this, UrlConfig.SaveSmallVideoInformation, token, key, map);

                                if (!TextUtils.isEmpty(result)) {
                                    JSONObject jsonObject;
                                    try {
                                        jsonObject = new JSONObject(result);
                                        String code = jsonObject.getString("code");
                                        msg[0] = jsonObject.getString("desc");
                                        if (code.equals("0")) {
                                            subscriber.onNext(0);
//                                成功

                                        } else {
                                            subscriber.onNext(1);
//                                离线

                                        }

                                    } catch (JSONException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                    }
                                } else {
//                                    mWorkLoadingProgress.dismiss();
//                                    mWorkLoadingProgress.setProgress(0);
                                    subscriber.onNext(3);
                                }
                            } else {
//                                mWorkLoadingProgress.dismiss();
//                                mWorkLoadingProgress.setProgress(0);
                                subscriber.onNext(2);
                            }
                        }
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Integer integer) {
                            switch (integer) {
                                case 0:
                                    mCustomProgressDialog.dismiss();
                                    finish();
                                    Toast.makeText(TCVideoPreviewActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                    break;
                                case 1:
                                    mCustomProgressDialog.dismiss();
                                    Toast.makeText(TCVideoPreviewActivity.this, msg[0], Toast.LENGTH_SHORT).show();
                                    break;
                                case 2:
                                    mCustomProgressDialog.dismiss();
                                    Toast.makeText(TCVideoPreviewActivity.this, "网络异常，请检查网络设置", Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    mCustomProgressDialog.dismiss();
                                    Toast.makeText(TCVideoPreviewActivity.this, "服务器异常", Toast.LENGTH_SHORT).show();

                                    break;
                            }
                        }
                    });
                }


            }
        });

        TXUGCPublishTypeDef.TXPublishParam param = new TXUGCPublishTypeDef.TXPublishParam();
        // signature计算规则可参考 https://www.qcloud.com/document/product/266/9221
        param.signature = Signature;
        param.videoPath = mVideoPath;
        param.coverPath = mCoverImagePath;
        txugcPublish.publishVideo(param);

    }

    private boolean startPlay() {
        mStartPreview.setBackgroundResource(R.drawable.icon_record_pause);
        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setPlayListener(this);

        mTXLivePlayer.enableHardwareDecode(false);
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);

        mTXLivePlayer.setConfig(mTXPlayConfig);

        int result = mTXLivePlayer.startPlay(mVideoPath, TXLivePlayer.PLAY_TYPE_LOCAL_VIDEO); // result返回值：0 success;  -1 empty url; -2 invalid url; -3 invalid playType;
        if (result != 0) {
            mStartPreview.setBackgroundResource(R.drawable.icon_record_start);
            return false;
        }

        mVideoPlay = true;
        return true;
    }

    private static ContentValues initCommonContentValues(File saveFile) {
        ContentValues values = new ContentValues();
        long currentTimeInSeconds = System.currentTimeMillis();
        values.put(MediaStore.MediaColumns.TITLE, saveFile.getName());
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, saveFile.getName());
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, currentTimeInSeconds);
        values.put(MediaStore.MediaColumns.DATE_ADDED, currentTimeInSeconds);
        values.put(MediaStore.MediaColumns.DATA, saveFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.SIZE, saveFile.length());

        return values;
    }

    private void downloadRecord() {
        File file = new File(mVideoPath);
        File newFile = null;
        if (file.exists()) {
            try {
                newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + file.getName());
//                if (!newFile.exists()) {
//                    newFile = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() + File.separator + file.getName());
//                }

                file.renameTo(newFile);
                mVideoPath = newFile.getAbsolutePath();

                ContentValues values = initCommonContentValues(newFile);
                values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
                values.put(MediaStore.Video.VideoColumns.DURATION, mVideoDuration);//时长
                this.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

                insertVideoThumb(newFile.getPath(), mCoverImagePath);

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            Toast.makeText(TCVideoPreviewActivity.this, "已保存在" + newFile.getPath(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 插入视频缩略图
     *
     * @param videoPath
     * @param coverPath
     */
    private void insertVideoThumb(String videoPath, String coverPath) {
        //以下是查询上面插入的数据库Video的id（用于绑定缩略图）
        //根据路径查询
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Thumbnails._ID},//返回id列表
                String.format("%s = ?", MediaStore.Video.Thumbnails.DATA), //根据路径查询数据库
                new String[]{videoPath}, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String videoId = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails._ID));
                //查询到了Video的id
                ContentValues thumbValues = new ContentValues();
                thumbValues.put(MediaStore.Video.Thumbnails.DATA, coverPath);//缩略图路径
                thumbValues.put(MediaStore.Video.Thumbnails.VIDEO_ID, videoId);//video的id 用于绑定
                //Video的kind一般为1
                thumbValues.put(MediaStore.Video.Thumbnails.KIND,
                        MediaStore.Video.Thumbnails.MINI_KIND);
                //只返回图片大小信息，不返回图片具体内容
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                Bitmap bitmap = BitmapFactory.decodeFile(coverPath, options);
                if (bitmap != null) {
                    thumbValues.put(MediaStore.Video.Thumbnails.WIDTH, bitmap.getWidth());//缩略图宽度
                    thumbValues.put(MediaStore.Video.Thumbnails.HEIGHT, bitmap.getHeight());//缩略图高度
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                }
                this.getContentResolver().insert(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, //缩略图数据库
                        thumbValues);
            }
            cursor.close();
        }
    }

    private void deleteVideo() {
        stopPlay(true);
        //删除文件
        FileUtils.deleteFile(mVideoPath);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTXCloudVideoView.onResume();
        if (mVideoPlay && mAutoPause) {
            mTXLivePlayer.resume();
            mAutoPause = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTXCloudVideoView.onPause();
        if (mVideoPlay && !mVideoPause) {
            mTXLivePlayer.pause();
            mAutoPause = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTXCloudVideoView.onDestroy();
        stopPlay(true);
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXLivePlayer != null) {
            mTXLivePlayer.setPlayListener(null);
            mTXLivePlayer.stopPlay(clearLastFrame);
            mVideoPlay = false;
        }
    }

    @Override
    public void onPlayEvent(int event, Bundle param) {
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.setLogText(null, param, event);
        }
        if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            if (mStartSeek) {
                return;
            }
            if (mImageViewBg.isShown()) {
                mImageViewBg.setVisibility(View.GONE);
            }
            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS);
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION);//单位为s
            long curTS = System.currentTimeMillis();
            // 避免滑动进度条松开的瞬间可能出现滑动条瞬间跳到上一个位置
            if (Math.abs(curTS - mTrackingTouchTS) < 500) {
                return;
            }
            mTrackingTouchTS = curTS;

            if (mSeekBar != null) {
                mSeekBar.setProgress(progress);
            }
            if (mProgressTime != null) {
                mProgressTime.setText(String.format(Locale.CHINA, "%02d:%02d/%02d:%02d", (progress) / 60, progress % 60, (duration) / 60, duration % 60));
            }

            if (mSeekBar != null) {
                mSeekBar.setMax(duration);
            }
        } else if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {

            showErrorAndQuit(TCConstants.ERROR_MSG_NET_DISCONNECTED);

        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            if (mProgressTime != null) {
                mProgressTime.setText(String.format(Locale.CHINA, "%s", "00:00/00:00"));
            }
            if (mSeekBar != null) {
                mSeekBar.setProgress(0);
            }
            stopPlay(false);
            mImageViewBg.setVisibility(View.VISIBLE);
            startPlay();
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    public static class ErrorDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.ConfirmDialogStyle)
                    .setCancelable(true)
                    .setTitle(getArguments().getString("errorMsg"))
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            getActivity().finish();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            return alertDialog;
        }
    }

    protected void showErrorAndQuit(String errorMsg) {

        if (!mErrDlgFragment.isAdded() && !this.isFinishing()) {
            Bundle args = new Bundle();
            args.putString("errorMsg", errorMsg);
            mErrDlgFragment.setArguments(args);
            mErrDlgFragment.setCancelable(false);

            //此处不使用用.show(...)的方式加载dialogfragment，避免IllegalStateException
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(mErrDlgFragment, "loading");
            transaction.commitAllowingStateLoss();
        }
    }
}

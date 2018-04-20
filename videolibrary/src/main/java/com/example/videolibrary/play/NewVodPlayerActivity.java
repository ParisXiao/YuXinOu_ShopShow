package com.example.videolibrary.play;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.videolibrary.R;
import com.example.videolibrary.common.activity.QRCodeScanActivity;
import com.example.videolibrary.play.wkvideoplayer.model.Video;
import com.example.videolibrary.play.wkvideoplayer.model.VideoUrl;
import com.example.videolibrary.play.wkvideoplayer.util.DensityUtil;
import com.example.videolibrary.play.wkvideoplayer.view.MediaController;
import com.example.videolibrary.play.wkvideoplayer.view.SuperVideoPlayer;
import com.example.videolibrary.play.wkvideoplayer.view.VodRspData;
import com.tencent.rtmp.TXPlayerAuthBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by annidy on 2017/12/27.
 */

public class NewVodPlayerActivity extends Activity implements View.OnClickListener {
    public static final String TAG = "NewVodPlayerActivity";
    private SuperVideoPlayer mSuperVideoPlayer;
    private ImageView mPlayBtnView;
    private RecyclerView mRecyclerView;
    private NewVodListAdapter mAdapter;
    private ImageView mIvAdd;
    private Button mBtnScan;
    private ArrayList<TXPlayerAuthParam> mVodList;
    private TXPlayerAuthParam mCurrentFileIDParam;
    private LinearLayout mLyTop;
    private ImageView mIvBack;
    private NewVodListAdapter.OnItemClickLitener mOnItemClickListener = new NewVodListAdapter.OnItemClickLitener() {
        @Override
        public void onItemClick(int position,String title) {
            mCurrentFileIDParam = mVodList.get(position);
            Log.d(TAG,"currentFileParam:" + mCurrentFileIDParam);
            if (mSuperVideoPlayer != null) {
                mSuperVideoPlayer.updateUI(title);
            }
            playVideoWithFileId(mCurrentFileIDParam);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vod);
        initView();
        checkPermission();
        getWindow().addFlags(WindowManager.LayoutParams.
                FLAG_KEEP_SCREEN_ON);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 100);
            }
        }
    }

    private void initView() {
        mLyTop = (LinearLayout) findViewById(R.id.layout_top);
        mSuperVideoPlayer = (SuperVideoPlayer) findViewById(R.id.video_player_item_1);
        mSuperVideoPlayer.setVideoPlayCallback(mVideoPlayCallback);

        mPlayBtnView = (ImageView) findViewById(R.id.play_btn);
        mPlayBtnView.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new NewVodListAdapter(this);
        mAdapter.setOnItemClickLitener(mOnItemClickListener);
        mRecyclerView.setAdapter(mAdapter);

        mIvBack = (ImageView)findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);

        mIvAdd = (ImageView) findViewById(R.id.iv_add);
        mIvAdd.setOnClickListener(this);

        mBtnScan = (Button) findViewById(R.id.btnScan);
        mBtnScan.setOnClickListener(this);

        playVideo();
        initData();
    }

    private void playVideoWithFileId(TXPlayerAuthParam param) {
        TXPlayerAuthBuilder authBuilder = new TXPlayerAuthBuilder();
        try {
            if (!TextUtils.isEmpty(param.appId)) {
                authBuilder.setAppId(Integer.parseInt(param.appId));
            }
            String fileId = param.fileId;
            authBuilder.setFileId(fileId);

            if (mSuperVideoPlayer != null) {
                mSuperVideoPlayer.playFileID(authBuilder);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "请输入正确的AppId和FileId", Toast.LENGTH_SHORT).show();
        }
    }

    private void initData() {
        mVodList = new ArrayList<TXPlayerAuthParam>();
        TXPlayerAuthParam param1 = new TXPlayerAuthParam();
        param1.appId = "1252463788";
        param1.fileId = "4564972819220421305";
        mVodList.add(param1);

        TXPlayerAuthParam param2 = new TXPlayerAuthParam();
        param2.appId = "1252463788";
        param2.fileId = "4564972819219071568";
        mVodList.add(param2);

        TXPlayerAuthParam param3 = new TXPlayerAuthParam();
        param3.appId = "1252463788";
        param3.fileId = "4564972819219071668";
        mVodList.add(param3);

        TXPlayerAuthParam param4 = new TXPlayerAuthParam();
        param4.appId = "1252463788";
        param4.fileId = "4564972819219071679";
        mVodList.add(param4);

        TXPlayerAuthParam param5 = new TXPlayerAuthParam();
        param5.appId = "1252463788";
        param5.fileId = "4564972819219081699";
        mVodList.add(param5);
        mSuperVideoPlayer.setVideoListInfo(mVodList);
        mSuperVideoPlayer.getNextInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSuperVideoPlayer != null) {
            mSuperVideoPlayer.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSuperVideoPlayer != null) {
            mSuperVideoPlayer.onPause();
        }
    }

    private SuperVideoPlayer.VideoPlayCallbackImpl mVideoPlayCallback = new SuperVideoPlayer.VideoPlayCallbackImpl() {
        @Override
        public void onCloseVideo() {
            mSuperVideoPlayer.onDestroy();
            mPlayBtnView.setVisibility(View.VISIBLE);
            mSuperVideoPlayer.setVisibility(View.GONE);
            resetPageToPortrait();
        }

        @Override
        public void onSwitchPageType() {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mLyTop.setVisibility(View.VISIBLE);
                mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                mLyTop.setVisibility(View.GONE);
                mSuperVideoPlayer.setPageType(MediaController.PageType.EXPAND);
            }
        }

        @Override
        public void onPlayFinish() {
            mPlayBtnView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onBack() {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                mLyTop.setVisibility(View.VISIBLE);
                mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
            } else {
                finish();
            }
        }

        @Override
        public void onLoadVideoInfo(VodRspData data) {
            mAdapter.addVideoInfo(data);
        }
    };

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_add) {
            showAddDialog();

        } else if (i == R.id.btnScan) {
            scan();

        } else if (i == R.id.play_btn) {
            if (mCurrentFileIDParam != null) {
                playVideoWithFileId(mCurrentFileIDParam);
            }

        } else if (i == R.id.iv_back) {
            finish();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || data.getExtras() == null || TextUtils.isEmpty(data.getExtras().getString("result"))) {
            return;
        }
        String result = data.getExtras().getString("result");
        if (requestCode == 200) {
            EditText editText = (EditText) findViewById(R.id.editText);
            editText.setText(result);
        } else if (requestCode == 100) {
            if (mSuperVideoPlayer != null) {
                mSuperVideoPlayer.updateUI("新播放的视频");
                mSuperVideoPlayer.setPlayUrl(result);
            }
        }
    }

    private void scan() {
        Intent intent = new Intent(this, QRCodeScanActivity.class);
        startActivityForResult(intent, 100);
    }

    private void showAddDialog() {
        AlertDialog.Builder customizeDialog = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_vod_player_fileid, null);
        customizeDialog.setTitle("请设置AppID和FileID");
        customizeDialog.setView(dialogView);

        final EditText etAppId = (EditText) dialogView.findViewById(R.id.et_appid);
        final EditText etFileId = (EditText) dialogView.findViewById(R.id.et_fileid);

        customizeDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customizeDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TXPlayerAuthParam param = new TXPlayerAuthParam();
                        param.appId = etAppId.getText().toString();
                        param.fileId = etFileId.getText().toString();

                        try {
                            if (TextUtils.isEmpty(param.appId)) {
                                Toast.makeText(NewVodPlayerActivity.this, "请输入正确的AppId和FileId", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int appid = Integer.parseInt(param.appId);
                            String fileId = param.fileId;
                            if(TextUtils.isEmpty(fileId)){
                                Toast.makeText(NewVodPlayerActivity.this, "请输入正确的AppId和FileId", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(NewVodPlayerActivity.this, "请输入正确的AppId和FileId", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mVodList.add(param);

                        if (mSuperVideoPlayer != null) {
                            mSuperVideoPlayer.setVideoPlayInfoCallback(mPlayInfoCallback);
                            mSuperVideoPlayer.addVodInfo(param);
                        }
                    }
                });
        customizeDialog.show();
    }

    private SuperVideoPlayer.OnPlayInfoCallback mPlayInfoCallback = new SuperVideoPlayer.OnPlayInfoCallback() {
        @Override
        public void onPlayInfoCallback(int ret) {
            if (ret < 0) {
                int index = mVodList.size() - 1;
                if (index >= 0) {
                    mVodList.remove(index);
                }
            }
        }
    };

    private void playVideo() {
        mPlayBtnView.setVisibility(View.GONE);
        mSuperVideoPlayer.setVisibility(View.VISIBLE);
        mSuperVideoPlayer.setAutoHideController(false);

        Video video = new Video();
        VideoUrl videoUrl1 = new VideoUrl();
        videoUrl1.setFormatName("720P");
        videoUrl1.setFormatUrl("http://1252463788.vod2.myqcloud.com/95576ef5vodtransgzp1252463788/68e3febf4564972819220421305/master_playlist.m3u8");

        ArrayList<VideoUrl> arrayList1 = new ArrayList<>();
        arrayList1.add(videoUrl1);
        video.setVideoName("测试视频一");
        video.setVideoUrl(arrayList1);
        ArrayList<Video> videoArrayList = new ArrayList<>();
        videoArrayList.add(video);
        mSuperVideoPlayer.loadMultipleVideo(videoArrayList, 0, 0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSuperVideoPlayer != null) {
            mSuperVideoPlayer.onDestroy();
        }
    }

    /***
     * 旋转屏幕之后回调
     *
     * @param newConfig newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (null == mSuperVideoPlayer) return;
        /***
         * 根据屏幕方向重新设置播放器的大小
         */
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().invalidate();
            float height = DensityUtil.getWidthInPx(this);
            float width = DensityUtil.getHeightInPx(this);
            mSuperVideoPlayer.getLayoutParams().height = (int) width;
            mSuperVideoPlayer.getLayoutParams().width = (int) height;
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            float width = DensityUtil.getWidthInPx(this);
            float height = DensityUtil.dip2px(this, 200.f);
            mSuperVideoPlayer.getLayoutParams().height = (int) height;
            mSuperVideoPlayer.getLayoutParams().width = (int) width;
        }
    }

    /***
     * 恢复屏幕至竖屏
     */
    private void resetPageToPortrait() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mSuperVideoPlayer.setPageType(MediaController.PageType.SHRINK);
        }
    }

}

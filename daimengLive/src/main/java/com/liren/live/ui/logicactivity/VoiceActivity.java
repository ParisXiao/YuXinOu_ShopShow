package com.liren.live.ui.logicactivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.liren.live.R;
import com.liren.live.base.MyBaseActivity;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.superplayer.library.SuperPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VoiceActivity extends MyBaseActivity implements SuperPlayer.OnNetChangeListener {

    @BindView(R.id.movie_super_player)
    SuperPlayer movieSuperPlayer;
    @BindView(R.id.movie_refresh)
    SwipyRefreshLayout movieRefresh;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_voice;
    }

    @Override
    protected void initView() {
        initEvent();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
    private void initEvent() {
        movieRefresh.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(direction == SwipyRefreshLayoutDirection.TOP){
                    movieRefresh.setRefreshing(false);
                    startPlay();
                }else {

                }
            }
        });
        movieSuperPlayer.setNetChangeListener(true)//设置监听手机网络的变化
                .setOnNetChangeListener(this)//实现网络变化的回调
                .onPrepared(new SuperPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared() {
                        /**
                         * 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
                         */
                        Log.d("JYD","监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）");
                    }
                }).onComplete(new Runnable() {
            @Override
            public void run() {
                /**
                 * 监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
                 */

                startPlay();

            }
        }).onInfo(new SuperPlayer.OnInfoListener() {
            @Override
            public void onInfo(int what, int extra) {
                /**
                 * 监听视频的相关信息。
                 */
                Log.d("JYD","监听视频的相关信息");
            }
        }).onError(new SuperPlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {
                /**
                 * 监听视频播放失败的回调
                 */
                Log.d("JYD","监听视频播放失败的回调");
            }
        });//开始播放视频
        movieSuperPlayer.setScaleType(SuperPlayer.SCALETYPE_FITXY);
        movieSuperPlayer.setPlayerWH(0, movieSuperPlayer.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
    }

    private void startPlay(){
        movieSuperPlayer.play("http://ws.stream.qqmusic.qq.com/1913719.m4a?fromtag=46");//开始播放视频
    }
    /**
     * 网络链接监听类
     */
    @Override
    public void onWifi() {
        Toast.makeText(this, "当前网络环境是WIFI", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMobile() {
        Toast.makeText(this, "当前网络环境是手机网络", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisConnect() {
        Toast.makeText(this, "网络链接断开", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNoAvailable() {
        Toast.makeText(this, "无网络链接", Toast.LENGTH_SHORT).show();
    }


    /**
     * 下面的这几个Activity的生命状态很重要
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (movieSuperPlayer != null) {
            movieSuperPlayer.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (movieSuperPlayer != null) {
            movieSuperPlayer.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (movieSuperPlayer != null) {
            movieSuperPlayer.onDestroy();
        }
    }
}

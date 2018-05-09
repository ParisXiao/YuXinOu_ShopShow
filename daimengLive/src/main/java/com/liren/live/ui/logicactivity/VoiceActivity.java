package com.liren.live.ui.logicactivity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liren.live.R;
import com.liren.live.adapter.CommentAdapter;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.bean.AreaBean;
import com.liren.live.config.UrlConfig;
import com.liren.live.config.UserConfig;
import com.liren.live.entity.VideoListEntity;
import com.liren.live.utils.OKHttpUtils;
import com.liren.live.utils.PreferenceUtils;
import com.liren.live.widget.CircleImageView;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.superplayer.library.SuperPlayer;
import com.superplayer.library.mediaplayer.ControlbarShowHideInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VoiceActivity extends MyBaseActivity implements SuperPlayer.OnNetChangeListener, ControlbarShowHideInterface {

    @BindView(R.id.movie_super_player)
    SuperPlayer movieSuperPlayer;
    @BindView(R.id.movie_refresh)
    SwipyRefreshLayout movieRefresh;
    @BindView(R.id.activity_voice)
    RelativeLayout activityVoice;
    List<VideoListEntity> list = new ArrayList<>();
    int index = 0;
    int pageindex = 1;
    @BindView(R.id.bottomTolk)
    LinearLayout bottomTolk;
    @BindView(R.id.open_pinlun)
    LinearLayout openPinlun;
    @BindView(R.id.po)
    ImageView po;
    @BindView(R.id.num_pinlun)
    TextView numPinlun;
    @BindView(R.id.pinlun)
    LinearLayout pinlun;
    @BindView(R.id.num_dianzan)
    TextView numDianzan;
    @BindView(R.id.dianzan)
    LinearLayout dianzan;
    @BindView(R.id.num_zhuanfa)
    TextView numZhuanfa;
    @BindView(R.id.zhuanfa)
    LinearLayout zhuanfa;
    @BindView(R.id.author_icon)
    CircleImageView authorIcon;
    @BindView(R.id.author_name)
    TextView authorName;
    @BindView(R.id.num_dianji)
    TextView numDianji;
    @BindView(R.id.stop_exit)
    ImageView stopExit;
    @BindView(R.id.close_comment)
    ImageView closeComment;
    @BindView(R.id.commentRecycler)
    RecyclerView commentRecycler;
    @BindView(R.id.commentEdit)
    EditText commentEdit;
    @BindView(R.id.commentSend)
    Button commentSend;
    @BindView(R.id.comment)
    RelativeLayout comment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_voice;
    }

    @Override
    protected void initView() {
        Bundle bundle = getIntent().getExtras();
        list = bundle.getParcelableArrayList("VideoList");
        index = bundle.getInt("index");
    }

    @Override
    protected void initData() {

        initEvent();
        startPlay();
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
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    movieRefresh.setRefreshing(false);
                    index -= 1;
                    if (index == 0) {
                        index = 0;
                        pageindex = 1;
                        getData(list);
                    } else {
                        startPlay();
                    }
                } else {
                    movieRefresh.setRefreshing(false);
                    index += 1;
                    if (index > list.size() - 1) {
                        index = list.size() - 1;
                        pageindex += 1;
                        getData(list);
                    } else {
                        startPlay();
                    }

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
                        Log.d("JYD", "监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）");
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
                Log.d("JYD", "监听视频的相关信息");
            }
        }).onError(new SuperPlayer.OnErrorListener() {
            @Override
            public void onError(int what, int extra) {
                /**
                 * 监听视频播放失败的回调
                 */
                Log.d("JYD", "监听视频播放失败的回调");
            }
        });//开始播放视频
        movieSuperPlayer.setScaleType(SuperPlayer.SCALETYPE_16_9);
        movieSuperPlayer.setPlayerWH(0, movieSuperPlayer.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
        movieSuperPlayer.setControlbarInterface(this);
    }

    private void startPlay() {
        movieSuperPlayer.play(list.get(index).getVideoPath());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(list.get(index).getCommentsNum())) {
                    if (Integer.valueOf(list.get(index).getCommentsNum()) > 10000) {
                        numPinlun.setText(Integer.valueOf(list.get(index).getCommentsNum()) / 10000 + "万");
                    } else {
                        numPinlun.setText(list.get(index).getCommentsNum());
                    }
                } else {
                    numPinlun.setText("0");
                }
                if (!TextUtils.isEmpty(list.get(index).getClickLikeNum())) {
                    if (Integer.valueOf(list.get(index).getClickLikeNum()) > 10000) {
                        numDianzan.setText(Integer.valueOf(list.get(index).getClickLikeNum()) / 10000 + "万");
                    } else {
                        numDianzan.setText(list.get(index).getClickLikeNum());
                    }
                } else {
                    numDianzan.setText("0");
                }
                if (!TextUtils.isEmpty(list.get(index).getForwardingnum())) {
                    if (Integer.valueOf(list.get(index).getForwardingnum()) > 10000) {
                        numZhuanfa.setText(Integer.valueOf(list.get(index).getForwardingnum()) / 10000 + "万");
                    } else {
                        numZhuanfa.setText(list.get(index).getForwardingnum());
                    }
                } else {
                    numZhuanfa.setText("0");
                }
                if (!TextUtils.isEmpty(list.get(index).getClicknum())) {
                    if (Integer.valueOf(list.get(index).getClicknum()) > 10000) {
                        numDianji.setText("播放量:" + Integer.valueOf(list.get(index).getClicknum()) / 10000 + "万");
                    } else {
                        numDianji.setText("播放量:" + list.get(index).getClicknum());
                    }
                } else {
                    numDianji.setText("播放量:" + "1");
                }
                if (!TextUtils.isEmpty(list.get(index).getReleaseaddress())) {
                    authorName.setText(list.get(index).getReleaseaddress());
                } else {
                    authorName.setText("未知");
                }
            }
        });
//        movieSuperPlayer.play("http://ws.stream.qqmusic.qq.com/1913719.m4a?fromtag=46");//开始播放视频
    }

    private void getData(final List<VideoListEntity> list) {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (OKHttpUtils.isConllection(VoiceActivity.this)) {
                    String[] key = new String[]{"pageindex", "pagerows"};
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("pageindex", pageindex + "");
                    map.put("pagerows", "20");
                    String sign = "pageindex" + pageindex + "pagerows" + "20";
                    String token = PreferenceUtils.getInstance(VoiceActivity.this).getString(UserConfig.DToken);
                    String result = OKHttpUtils.postData(VoiceActivity.this, UrlConfig.SelHotVideo, token, sign, key, map);
                    if (!TextUtils.isEmpty(result)) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(result);
                            String code = jsonObject.getString("code");
                            if (code.equals("0")) {
                                dismisDialog();
                                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                                JSONArray jsonArray = new JSONArray(jsonObject1.getString("data"));
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        VideoListEntity listEntity = new VideoListEntity();
                                        listEntity.setID(jsonArray.getJSONObject(i).getString("id"));
                                        listEntity.setClickLikeNum(jsonArray.getJSONObject(i).getString("clicklikenum"));
                                        listEntity.setClicknum(jsonArray.getJSONObject(i).getString("clicknum"));
                                        listEntity.setCommentsNum(jsonArray.getJSONObject(i).getString("commentsnum"));
                                        listEntity.setReleaseaddress(jsonArray.getJSONObject(i).getString("releaseaddress"));
                                        listEntity.setReleaseid(jsonArray.getJSONObject(i).getString("releaseid"));
                                        listEntity.setVideoName(jsonArray.getJSONObject(i).getString("videoname"));
                                        listEntity.setVideoImagePath(jsonArray.getJSONObject(i).getString("videoimagepath"));
                                        listEntity.setVideoPath(jsonArray.getJSONObject(i).getString("videopath"));
                                        listEntity.setWhetherhot(jsonArray.getJSONObject(i).getString("whetherhot"));
                                        listEntity.setReleasetime(jsonArray.getJSONObject(i).getString("releasetime"));
                                        list.add(listEntity);
                                    }
                                }
                                subscriber.onNext(0);

//                                成功
                            } else {
                                dismisDialog();
                                subscriber.onNext(1);
//                                离线

                            }

                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                            subscriber.onNext(4);
                        }
                    } else {
                        dismisDialog();
                        subscriber.onNext(3);
                    }
                } else {
                    dismisDialog();
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
                        startPlay();
                        break;
                    case 1:
//                        refreshLayout.setRefreshing(false);
//
//                        adapter.notifyDataSetChanged();
                        showToast("获取数据失败");
                        break;
                    case 2:
//                        refreshLayout.setRefreshing(false);
                        showToast("网络异常，请检查网络设置");
                        break;
                    case 3:
//                        refreshLayout.setRefreshing(false);
                        showToast("服务器异常");
                        break;
                    case 4:
//                        refreshLayout.setRefreshing(false);
                        showToast("数据解析异常");
                        break;
                }
            }
        });
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

    @Override
    public void controlbarShowHide(boolean isShow) {
    }

    @OnClick({R.id.open_pinlun, R.id.pinlun, R.id.dianzan, R.id.zhuanfa,R.id.close_comment,R.id.commentSend})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.open_pinlun:
                comment.setVisibility(View.VISIBLE);
                List<AreaBean> list = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    list.add(new AreaBean());
                }
                commentRecycler.setLayoutManager(new GridLayoutManager(this, 1));
                commentRecycler.setAdapter(new CommentAdapter(list));
                break;
            case R.id.pinlun:
                break;
            case R.id.dianzan:
                break;
            case R.id.zhuanfa:
                break;
            case R.id.close_comment:
                comment.setVisibility(View.GONE);
                break;
            case R.id.commentSend:
                comment.setVisibility(View.GONE);
                break;
        }
    }

    @OnClick(R.id.stop_exit)
    public void onViewClicked() {
        movieSuperPlayer.stop();
        finish();
    }
}

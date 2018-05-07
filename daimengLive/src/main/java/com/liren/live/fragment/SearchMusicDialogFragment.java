package com.liren.live.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.gson.Gson;
import com.liren.live.AppConfig;
import com.liren.live.AppContext;
import com.liren.live.R;
import com.liren.live.adapter.SearchMusicAdapter;
import com.liren.live.api.remote.ApiUtils;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.bean.MusicBean;
import com.liren.live.utils.DBManager;
import com.liren.live.utils.TLog;
import com.liren.live.widget.BlackEditText;
import com.liren.live.widget.BlackTextView;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 直播间音乐搜索弹窗
 */
public class SearchMusicDialogFragment extends DialogFragment {
    @BindView(R.id.iv_search_music_back)
    ImageView mSearchBack;
    
    @BindView(R.id.tv_search_btn)
    BlackTextView mSearchBtn;

    @BindView(R.id.lv_search_music)
    ListView mSearchListView;

    @BindView(R.id.et_search_input)
    BlackEditText mInputEdit;

    @BindView(R.id.iv_close)
    ImageView mIvClose;
    private List<MusicBean> mMusicList = new ArrayList<>();
    private SearchMusicAdapter mAdapter;
    private DBManager mDbManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_search_music,null);

        ButterKnife.bind(this,view);
        Dialog dialog = new Dialog(getActivity(),R.style.dialog_live_end);
        dialog.setContentView(view);

        initView(view);
        initData();
        return dialog;
    }

    public void initView(View view) {

        //返回
        mSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //搜索
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMusic();
            }
        });
        //选中音乐下载
        mSearchListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                File file = new File(AppConfig.DEFAULT_SAVE_MUSIC_PATH + mMusicList.get(position).audio_name + ".mp3");
                if(file.exists()){
                    file.delete();
                    mDbManager.delete(mMusicList.get(position));
                    mMusicList.remove(position);
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    //所有音乐
    private void searchMusic() {
        String keyword = mInputEdit.getText().toString().trim();
        if(keyword.equals("")){
            AppContext.showToast("请输入有效的关键词~");
            return;
        }
        PhoneLiveApi.searchMusic(keyword,new StringCallback(){

            @Override
            public void onError(Call call, Exception e,int id) {
                AppContext.showToast("查询失败,请换首歌试试~");
            }

            @Override
            public void onResponse(String response,int id) {

                JSONArray resJson = ApiUtils.checkIsSuccess(response);

                if(resJson != null){
                    mMusicList.clear();

                    if(resJson.length() > 0){
                        Gson g = new Gson();
                        for(int i = 0; i <resJson.length(); i++){
                            try {
                                mMusicList.add(g.fromJson(resJson.getString(i),MusicBean.class));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        fillUI();
                    }

                }

            }

        });
    }

    private void fillUI() {
        mAdapter.notifyDataSetChangedMusicList(mMusicList);
    }


    public void initData() {
        mDbManager = new DBManager(getActivity());
        mMusicList =  mDbManager.query();

        mAdapter = new SearchMusicAdapter(mMusicList,this, mDbManager);
        mSearchListView.setAdapter(mAdapter);
    }

    /**
     * @dw 获取歌曲信息
     * */
    public void downloadMusic(final MusicBean music,final CircularProgressButton mBtnDownload) {

        //获取歌曲信息
        PhoneLiveApi.getMusicFileUrl(music.audio_id,new StringCallback(){

            @Override
            public void onError(Call call, Exception e,int id) {
                AppContext.showToast("获取歌曲失败");
            }

            @Override
            public void onResponse(String response,int id) {

                JSONArray musicFileInfoJson = ApiUtils.checkIsSuccess(response);
                if(musicFileInfoJson != null){

                    //歌曲下载地址
                    try {
                        String musicUrl = musicFileInfoJson.getJSONObject(0).getString("audio_link");
                        //歌词下载地址
                        String musicLrc = musicFileInfoJson.getJSONObject(0).getString("lrcLink");

                        //判断歌曲地址是否存在
                        if(!TextUtils.isEmpty(musicUrl)){
                            downloadMusicAndLrc(musicUrl, musicLrc, music, mBtnDownload);

                        }else{
                            Toast.makeText(getActivity(),"歌曲无法下载,请换首歌试试",Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
    }

    //下载歌词和歌曲
    private void downloadMusicAndLrc(String musicUrl, String musicLrc, final MusicBean music, final CircularProgressButton mBtnDownload) {
        //下载歌曲
        PhoneLiveApi.downloadMusic(musicUrl,new FileCallBack(AppConfig.DEFAULT_SAVE_MUSIC_PATH,music.audio_name + ".mp3"){

            @Override
            public void onError(Call call, Exception e,int id) {

                mBtnDownload.setErrorText("下载失败");
            }

            @Override
            public void onResponse(File response,int id) {
                List<MusicBean> list = new ArrayList<MusicBean>();
                list.add(music);
                mDbManager.add(list);

            }

            @Override
            public void inProgress(float progress, long total,int id) {
                mBtnDownload.setProgress((int) (progress*100));
            }
        });

        try {
            //下载歌词s
            PhoneLiveApi.downloadLrc(musicLrc,new FileCallBack(AppConfig.DEFAULT_SAVE_MUSIC_PATH,music.audio_name + ".lrc"){

                @Override
                public void onError(Call call, Exception e,int id) {
                    AppContext.showToast("没有找到相应的歌词~");
                }

                @Override
                public void onResponse(File response,int id) {
                    TLog.log(response.getPath());
                }

                @Override
                public void inProgress(float progress, long total,int id) {

                }
            });
        }catch (Exception e){
            AppContext.showToast("歌词下载失败");
        }
    }


    //歌曲选中回调接口
    public interface SearchMusicFragmentInterface{
        void onSelectMusic(Intent intent);
    }
}

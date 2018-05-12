package com.liren.live.fragment.myfragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Guideline;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.liren.live.R;
import com.liren.live.base.MyBaseFragment;
import com.liren.live.ui.logicactivity.ChooseSellerActivity;
import com.liren.live.utils.UIHelper;
import com.liren.live.video.shortvideo.choose.TCVideoChooseActivity;
import com.liren.live.video.videorecord.TCVideoSettingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2018/5/5 0005.
 */

public class KaiBoFragment extends MyBaseFragment {

    Unbinder unbinder;
    @BindView(R.id.guideline1)
    Guideline guideline1;
    @BindView(R.id.dianshang)
    Button dianshang;
    @BindView(R.id.guideline2)
    Guideline guideline2;
    @BindView(R.id.jiaoyu)
    Button jiaoyu;
    @BindView(R.id.guideline3)
    Guideline guideline3;
    @BindView(R.id.putong)
    Button putong;
    @BindView(R.id.guideline4)
    Guideline guideline4;
    @BindView(R.id.shiping)
    Button shiping;
    @BindView(R.id.guideline5)
    Guideline guideline5;
    @BindView(R.id.linearLayout)
    ConstraintLayout linearLayout;
    private SweetAlertDialog mDialog;
    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_kaibo;
    }

    @Override
    protected void initView(View view) {


    }

    @Override
    protected void initData() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.dianshang, R.id.jiaoyu, R.id.putong, R.id.shiping})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dianshang:
                Intent intent=new Intent(getActivity(), ChooseSellerActivity.class);
                intent.putExtra("LiveType",0);
                startActivity(intent);
                break;
            case R.id.jiaoyu:
                break;
            case R.id.putong:
                UIHelper.showRtmpPushActivity(getActivity(),0);
                break;
            case R.id.shiping:
                showVideo();
                break;
        }
    }
    private void  showVideo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.dialog_video_choose, null);
        LinearLayout lz = (LinearLayout) v.findViewById(R.id.video_lz);
        LinearLayout pj = (LinearLayout) v.findViewById(R.id.video_pj);
        LinearLayout bd = (LinearLayout) v.findViewById(R.id.video_bd);
        final Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);//自定义布局应该在这里添加，要在dialog.show()的后面
        //dialog.getWindow().setGravity(Gravity.CENTER);//可以设置显示的位置
        lz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), TCVideoSettingActivity.class);
                startActivity(intent);

            }
        });

        pj.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), TCVideoChooseActivity.class);
                intent.putExtra("TITLE","短视频拼接");
                intent.putExtra("CHOOSE_TYPE", TCVideoChooseActivity.TYPE_MULTI_CHOOSE);
                startActivity(intent);
            }
        });
        bd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent intent = new Intent(getActivity(), TCVideoChooseActivity.class);
                intent.putExtra("TITLE","短视频特效");
                intent.putExtra("CHOOSE_TYPE", TCVideoChooseActivity.TYPE_SINGLE_CHOOSE);
                startActivity(intent);
            }
        });

    }
}

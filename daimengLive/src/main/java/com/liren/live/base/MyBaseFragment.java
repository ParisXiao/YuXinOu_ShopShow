package com.liren.live.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by xiaoli on 2018/4/11.
 */

public abstract class MyBaseFragment extends Fragment {
    private SweetAlertDialog mDialog;
    Unbinder unbinder;
    /**
     * 初始化layout
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化布局
     */
    protected abstract void initView(View view);

    /**
     * 参数设置
     */
    protected abstract void initData();

    private MyBaseActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MyBaseActivity) getActivity();
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    public void showLoadDialog(String text) {
        if (mDialog == null) {
            mDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
            mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            mDialog.setTitleText(text);
            mDialog.setCancelable(false);
        }
        if (!mDialog.isShowing()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDialog.show();
                }
            });
            mDialog.setCanceledOnTouchOutside(false);
        }


    }

    public void dismisDialog() {
        if (mDialog != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDialog.dismiss();
                }
            });

        }
    }
    public void showToast(String s){
        if (!TextUtils.isEmpty(s))
            Toast.makeText(activity,s,Toast.LENGTH_SHORT).show();
    }
    /**
     * 沉浸式适配
     */
    private void TranslucentFlag() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4 全透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);//calculateStatusColor(Color.WHITE, (int) alphaValue)

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0以上 全透明实现
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

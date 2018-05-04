package com.liren.live.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by xiaoli on 2018/4/11.
 */

public abstract class MyBaseActivity extends AppCompatActivity {
    private SweetAlertDialog mDialog;
    /**
     * 初始化layout
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * 初始化布局
     */
    protected abstract void initView();

    /**
     * 参数设置
     */
    protected abstract void initData();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        TranslucentFlag();
        initView();
        initData();
    }
    public void showLoadDialog(String text){
        if(mDialog==null){
            mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            mDialog.setTitleText(text);
            mDialog.setCancelable(false);
        }
        if(!mDialog.isShowing()){
            mDialog.show();
            mDialog.setCanceledOnTouchOutside(true);
        }


    }

    public void dismisDialog(){
        if(mDialog!=null){
            mDialog.dismiss();
        }

    }
    /**
     * 沉浸式适配
     */
    private void TranslucentFlag(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);//calculateStatusColor(Color.WHITE, (int) alphaValue)

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0以上 全透明实现
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }
    public void showToast(String s){
        if (!TextUtils.isEmpty(s))
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
}

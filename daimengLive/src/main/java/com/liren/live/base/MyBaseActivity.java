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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.liren.live.AppContext;
import com.liren.live.api.remote.PhoneLiveApi;
import com.liren.live.utils.LiveUtils;
import com.liren.live.utils.TLog;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by xiaoli on 2018/4/11.
 */

public abstract class MyBaseActivity extends AppCompatActivity {
    private SweetAlertDialog mDialog;

    /**
     * 初始化layout
     *
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
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {

            stopLocation();
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息

                    AppContext.lng = String.valueOf(amapLocation.getLongitude());
                    AppContext.lat = String.valueOf(amapLocation.getLatitude());
                    AppContext.province = amapLocation.getProvince();
                    AppContext.address =  amapLocation.getCity();
                    if(AppContext.address.equals("泰安市")){
                        AppContext.address = "外太空";
                    }
                    PhoneLiveApi.saveInfo(LiveUtils.getFiledJson("city",AppContext.address),getUserID(),AppContext.getInstance().getToken(),null);

                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    TLog.log("location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }

            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        TranslucentFlag();
        initView();
        initData();
    }
    public void initAMap() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }
    //停止定位
    public void stopLocation(){
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();//销毁定位客户端。

    }
    public void showLoadDialog(String text) {
        if (mDialog == null) {
            mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            mDialog.setTitleText(text);
            mDialog.setCancelable(false);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
            mDialog.setCanceledOnTouchOutside(false);
        }


    }

    public void dismisDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * 沉浸式适配
     */
    private void TranslucentFlag() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);//calculateStatusColor(Color.WHITE, (int) alphaValue)

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0以上 全透明实现
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    public void showToast(String s) {
        if (!TextUtils.isEmpty(s))
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
    protected String getUserID()
    {
        return AppContext.getInstance().getLoginUid();
    }


    protected String getUserToken()
    {
        return AppContext.getInstance().getToken();
    }
}

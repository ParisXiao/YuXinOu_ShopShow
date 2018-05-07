package com.liren.live.ui.logicactivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.liren.live.R;
import com.liren.live.base.BackHandledInterface;
import com.liren.live.base.MyBaseActivity;
import com.liren.live.base.MyBaseFragment;
import com.liren.live.fragment.myfragment.HomeFragment;
import com.liren.live.fragment.myfragment.LiveFragment;
import com.liren.live.fragment.myfragment.ShopFragment;
import com.liren.live.fragment.myfragment.TeachFragment;
import com.liren.live.fragment.myfragment.TravelFragment;
import com.liren.live.fragment.myfragment.VideoFragment;
import com.liren.live.widget.BlackTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/5 0005.
 */

public class MyMainActivity extends MyBaseActivity implements BackHandledInterface {
    @BindView(R.id.toolbar_back)
    ImageView toolbarBack;
    @BindView(R.id.toolbar_title)
    BlackTextView toolbarTitle;
    @BindView(R.id.toolbar_right)
    TextView toolbarRight;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.container_fragment)
    FrameLayout containerFragment;
    @BindView(R.id.home)
    RadioButton home;
    @BindView(R.id.live)
    RadioButton live;
    @BindView(R.id.shop)
    RadioButton shop;
    @BindView(R.id.teach)
    RadioButton teach;
    @BindView(R.id.travel)
    RadioButton travel;
    @BindView(R.id.main_daohang)
    public RadioGroup mainDaohang;
    Fragment currentFragment;
    @BindView(R.id.video)
    RadioButton video;
    private MyBaseFragment mBackHandedFragment;
    private boolean hadIntercept;
    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;
    @Override
    public void setSelectedFragment(MyBaseFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    public void onBackPressed() {
        if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//                if ((System.currentTimeMillis() - exitTime) > 2000) {
//                    //弹出提示，可以有多种方式
//                    Toast.makeText(getApplicationContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
//                    exitTime = System.currentTimeMillis();
//                } else {
                    super.onBackPressed(); //退出
//                }

            } else {
                getSupportFragmentManager().popBackStack(); //fragment 出栈
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mymain;
    }

    @Override
    protected void initView() {
        toolbarBack.setVisibility(View.GONE);
        toolbarTitle.setText("首页");
        mainDaohang.check(R.id.home);
        home.setTextColor(getResources().getColor(R.color.tab_red));
    }

    @Override
    protected void initData() {
        add(new HomeFragment(), R.id.container_fragment, "HOME");
        mainDaohang.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.home:
                        add(new HomeFragment(), R.id.container_fragment, "HOME");
                        toolbarTitle.setText("首页");
                        home.setTextColor(getResources().getColor(R.color.tab_red));
                        live.setTextColor(getResources().getColor(R.color.text_gray));
                        video.setTextColor(getResources().getColor(R.color.text_gray));
                        shop.setTextColor(getResources().getColor(R.color.text_gray));
                        teach.setTextColor(getResources().getColor(R.color.text_gray));
                        travel.setTextColor(getResources().getColor(R.color.text_gray));
                        break;

                    case R.id.live:
                        add(new LiveFragment(), R.id.container_fragment, "LIVE");
                        toolbar.setVisibility(View.VISIBLE);
                        mainDaohang.setVisibility(View.VISIBLE);
                        toolbarTitle.setText("直播特卖");
                        home.setTextColor(getResources().getColor(R.color.text_gray));
                        live.setTextColor(getResources().getColor(R.color.tab_red));
                        video.setTextColor(getResources().getColor(R.color.text_gray));
                        shop.setTextColor(getResources().getColor(R.color.text_gray));
                        teach.setTextColor(getResources().getColor(R.color.text_gray));
                        travel.setTextColor(getResources().getColor(R.color.text_gray));
                        break;
                    case R.id.video:
                        toolbar.setVisibility(View.VISIBLE);
                        mainDaohang.setVisibility(View.VISIBLE);
                        add(new VideoFragment(), R.id.container_fragment, "VIDEO");
                        toolbarTitle.setText("小视频");
                        home.setTextColor(getResources().getColor(R.color.text_gray));
                        live.setTextColor(getResources().getColor(R.color.text_gray));
                        video.setTextColor(getResources().getColor(R.color.tab_red));
                        shop.setTextColor(getResources().getColor(R.color.text_gray));
                        teach.setTextColor(getResources().getColor(R.color.text_gray));
                        travel.setTextColor(getResources().getColor(R.color.text_gray));
                        break;
                    case R.id.shop:
                        add(new ShopFragment(), R.id.container_fragment, "SHOP");
                        toolbarTitle.setText("跨境商城");
                        home.setTextColor(getResources().getColor(R.color.text_gray));
                        live.setTextColor(getResources().getColor(R.color.text_gray));
                        video.setTextColor(getResources().getColor(R.color.text_gray));
                        shop.setTextColor(getResources().getColor(R.color.tab_red));
                        teach.setTextColor(getResources().getColor(R.color.text_gray));
                        travel.setTextColor(getResources().getColor(R.color.text_gray));
                        break;
                    case R.id.teach:
                        add(new TeachFragment(), R.id.container_fragment, "TEACH");
                        toolbarTitle.setText("课程教育");
                        home.setTextColor(getResources().getColor(R.color.text_gray));
                        live.setTextColor(getResources().getColor(R.color.text_gray));
                        video.setTextColor(getResources().getColor(R.color.text_gray));
                        shop.setTextColor(getResources().getColor(R.color.text_gray));
                        teach.setTextColor(getResources().getColor(R.color.tab_red));
                        travel.setTextColor(getResources().getColor(R.color.text_gray));
                        break;
                    case R.id.travel:
                        add(new TravelFragment(), R.id.container_fragment, "TRAVEL");
                        toolbarTitle.setText("旅游路线");
                        home.setTextColor(getResources().getColor(R.color.text_gray));
                        live.setTextColor(getResources().getColor(R.color.text_gray));
                        video.setTextColor(getResources().getColor(R.color.text_gray));
                        shop.setTextColor(getResources().getColor(R.color.text_gray));
                        teach.setTextColor(getResources().getColor(R.color.text_gray));
                        travel.setTextColor(getResources().getColor(R.color.login_text));
                        break;

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    public void add(Fragment fragment, int id, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //优先检查，fragment是否存在，避免重叠
        Fragment tempFragment = (Fragment) fragmentManager.findFragmentByTag(tag);
        if (tempFragment != null) {
            fragment = tempFragment;
        }
        if (fragment.isAdded()) {
            addOrShowFragment(fragmentTransaction, fragment, id, tag);
        } else {
            if (currentFragment != null && currentFragment.isAdded()) {
                fragmentTransaction.hide(currentFragment).add(id, fragment, tag).commit();
            } else {
                fragmentTransaction.add(id, fragment, tag).commit();
            }
            currentFragment = fragment;
        }
    }

    /**
     * 添加或者显示 fragment
     *
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction, Fragment fragment, int id, String tag) {
        if (currentFragment == fragment)
            return;
        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragment).add(id, fragment, tag).commit();
        } else {
            transaction.hide(currentFragment).show(fragment).commit();
        }
        currentFragment.setUserVisibleHint(false);
        currentFragment = fragment;
        currentFragment.setUserVisibleHint(true);
    }

}

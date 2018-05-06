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
import com.liren.live.base.MyBaseActivity;
import com.liren.live.fragment.myfragment.HomeFragment;
import com.liren.live.fragment.myfragment.LiveFragment;
import com.liren.live.fragment.myfragment.ShopFragment;
import com.liren.live.fragment.myfragment.TeachFragment;
import com.liren.live.fragment.myfragment.TravelFragment;
import com.liren.live.widget.BlackTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/5/5 0005.
 */

public class MyMainActivity extends MyBaseActivity {
    @BindView(R.id.toolbar_back)
    ImageView toolbarBack;
    @BindView(R.id.toolbar_title)
    BlackTextView toolbarTitle;
    @BindView(R.id.toolbar_right)
    TextView toolbarRight;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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
    RadioGroup mainDaohang;
    Fragment currentFragment;
    RadioButton[] radioButtons=new RadioButton[]{home,live,shop,teach,travel};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mymain;
    }

    @Override
    protected void initView() {
        toolbarBack.setVisibility(View.GONE);
        toolbarTitle.setText("首页");
        mainDaohang.check(R.id.home);
        home. setTextColor(getResources().getColor(R.color.login_text));
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
                       home. setTextColor(getResources().getColor(R.color.login_text));
                       live. setTextColor(getResources().getColor(R.color.text_gray));
                       shop. setTextColor(getResources().getColor(R.color.text_gray));
                       teach. setTextColor(getResources().getColor(R.color.text_gray));
                       travel. setTextColor(getResources().getColor(R.color.text_gray));
                        break;
                    case R.id.live:
                        add(new LiveFragment(), R.id.container_fragment, "LIVE");
                        toolbarTitle.setText("直播特卖");
                        home. setTextColor(getResources().getColor(R.color.text_gray));
                        live. setTextColor(getResources().getColor(R.color.login_text));
                        shop. setTextColor(getResources().getColor(R.color.text_gray));
                        teach. setTextColor(getResources().getColor(R.color.text_gray));
                        travel. setTextColor(getResources().getColor(R.color.text_gray));
                        break;
                    case R.id.shop:
                        add(new ShopFragment(), R.id.container_fragment, "SHOP");
                        toolbarTitle.setText("跨境商城");
                        home. setTextColor(getResources().getColor(R.color.text_gray));
                        live. setTextColor(getResources().getColor(R.color.text_gray));
                        shop. setTextColor(getResources().getColor(R.color.login_text));
                        teach. setTextColor(getResources().getColor(R.color.text_gray));
                        travel. setTextColor(getResources().getColor(R.color.text_gray));
                        break;
                    case R.id.teach:
                        add(new TeachFragment(), R.id.container_fragment, "TEACH");
                        toolbarTitle.setText("课程教育");
                        home. setTextColor(getResources().getColor(R.color.text_gray));
                        live. setTextColor(getResources().getColor(R.color.text_gray));
                        shop. setTextColor(getResources().getColor(R.color.text_gray));
                        teach. setTextColor(getResources().getColor(R.color.login_text));
                        travel. setTextColor(getResources().getColor(R.color.text_gray));
                        break;
                    case R.id.travel:
                        add(new TravelFragment(), R.id.container_fragment, "TRAVEL");
                        toolbarTitle.setText("旅游路线");
                        home. setTextColor(getResources().getColor(R.color.text_gray));
                        live. setTextColor(getResources().getColor(R.color.text_gray));
                        shop. setTextColor(getResources().getColor(R.color.text_gray));
                        teach. setTextColor(getResources().getColor(R.color.text_gray));
                        travel. setTextColor(getResources().getColor(R.color.login_text));
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

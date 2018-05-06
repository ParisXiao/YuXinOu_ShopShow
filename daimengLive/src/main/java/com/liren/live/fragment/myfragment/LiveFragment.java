package com.liren.live.fragment.myfragment;

import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liren.live.R;
import com.liren.live.adapter.myadapter.TabFragmentPagerAdapter;
import com.liren.live.base.MyBaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/5/5 0005.
 */

public class LiveFragment extends MyBaseFragment {
    @BindView(R.id.live_hot)
    TextView liveHot;
    @BindView(R.id.live_new)
    TextView liveNew;
    @BindView(R.id.live_like)
    TextView liveLike;
    @BindView(R.id.live_choose)
    LinearLayout liveChoose;
    @BindView(R.id.viewpager_live)
    ViewPager viewpagerLive;
    Unbinder unbinder;
    @BindView(R.id.cursorImage)
    ImageView cursorImage;
    private List<Fragment> list;
    private TabFragmentPagerAdapter adapter;
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_live;
    }

    @Override
    protected void initView(View view) {
        InitImageView(view);
    }

    /**
     * 初始化动画
     */
    private void InitImageView(View view) {
        cursorImage=view.findViewById(R.id.cursorImage);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 3 - cursorImage.getWidth()) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        cursorImage.setImageMatrix(matrix);// 设置动画初始位置
    }

    @Override
    protected void initData() {
        viewpagerLive.setOnPageChangeListener(new MyPagerChangeListener());
        //把Fragment添加到List集合里面
        list = new ArrayList<>();
        list.add(LiveListFragment.newInstance("0"));//热门
        list.add(LiveListFragment.newInstance("1"));//最新
        list.add(LiveListFragment.newInstance("2"));//关注
        adapter = new TabFragmentPagerAdapter(getActivity().getSupportFragmentManager(), list);
        viewpagerLive.setAdapter(adapter);
        viewpagerLive.setCurrentItem(0);  //初始化显示第一个页面
        liveHot.setTextColor(getActivity().getResources().getColor(R.color.login_text));
        liveNew.setTextColor(getActivity().getResources().getColor(R.color.text_gray));
        liveLike.setTextColor(getActivity().getResources().getColor(R.color.text_gray));
    }

    @OnClick({R.id.live_hot, R.id.live_new, R.id.live_like})
    public void tabOnclick(View view) {
        switch (view.getId()) {
            case R.id.live_hot:
                viewpagerLive.setCurrentItem(0);
                break;
            case R.id.live_new:
                viewpagerLive.setCurrentItem(1);
                break;
            case R.id.live_like:
                viewpagerLive.setCurrentItem(2);
                break;
        }
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

    /**
     * 设置一个ViewPager的侦听事件，当左右滑动ViewPager时菜单栏被选中状态跟着改变
     */
    public class MyPagerChangeListener implements ViewPager.OnPageChangeListener {
        int one, two;

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            one = cursorImage.getWidth();// 页卡1 -> 页卡2 偏移量
            two = one * 2;// 页卡1 -> 页卡3 偏移量
            Animation animation = null;

            switch (arg0) {
                case 0:
                    if (currIndex == 1) {
                        animation = new TranslateAnimation(one, 0, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, 0, 0, 0);
                    }
                    liveHot.setTextColor(getActivity().getResources().getColor(R.color.login_text));
                    liveNew.setTextColor(getActivity().getResources().getColor(R.color.text_gray));
                    liveLike.setTextColor(getActivity().getResources().getColor(R.color.text_gray));
                    break;
                case 1:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, one, 0, 0);
                    } else if (currIndex == 2) {
                        animation = new TranslateAnimation(two, one, 0, 0);
                    }
                    liveNew.setTextColor(getActivity().getResources().getColor(R.color.login_text));
                    liveHot.setTextColor(getActivity().getResources().getColor(R.color.text_gray));
                    liveLike.setTextColor(getActivity().getResources().getColor(R.color.text_gray));
                    break;
                case 2:
                    if (currIndex == 0) {
                        animation = new TranslateAnimation(offset, two, 0, 0);
                    } else if (currIndex == 1) {
                        animation = new TranslateAnimation(one, two, 0, 0);
                    }
                    liveLike.setTextColor(getActivity().getResources().getColor(R.color.login_text));
                    liveHot.setTextColor(getActivity().getResources().getColor(R.color.text_gray));
                    liveNew.setTextColor(getActivity().getResources().getColor(R.color.text_gray));
                    break;
            }
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            cursorImage.startAnimation(animation);
        }
    }
}

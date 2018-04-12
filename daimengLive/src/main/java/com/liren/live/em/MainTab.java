package com.liren.live.em;

import com.liren.live.R;
import com.liren.live.fragment.UserInformationFragment;
import com.liren.live.ui.RtmpPushActivity;
import com.liren.live.viewpagerfragment.IndexPagerFragment;

/**
 * Created by Administrator on 2016/3/9.
 */
public enum  MainTab {
    INDEX(0, R.drawable.btn_tab_hot_background,0, IndexPagerFragment.class),
    LIVE(1, R.drawable.btn_tab_live_background,1, RtmpPushActivity.class),
    HOME(2, R.drawable.btn_tab_home_background,2, UserInformationFragment.class);
    private int idx;
    private int resName;
    private int resIcon;
    private Class<?> clz;

    private MainTab(int idx, int resIcon,int resName, Class<?> clz) {
        this.idx = idx;
        this.resIcon = resIcon;
        this.resName = resName;
        this.clz = clz;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getResName() {
        return resName;
    }

    public void setResName(int resName) {
        this.resName = resName;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public Class<?> getClz() {
        return clz;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }
}
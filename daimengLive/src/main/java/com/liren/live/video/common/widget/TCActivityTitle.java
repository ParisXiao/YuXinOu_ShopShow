package com.liren.live.video.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liren.live.R;


/**
 * 标题控件
 */
public class TCActivityTitle extends RelativeLayout {

    private String titleTexts;
    private boolean canBacks;
    private String backTexts;
    private String moreTexts;

    private LinearLayout llReturn;
    private TextView tvTitle;
    private TextView tvMore;
    private TextView tvBack;


    public TCActivityTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_title_video, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TCActivityTitle, 0, 0);
        try {
            titleTexts = ta.getString(R.styleable.TCActivityTitle_titleTexts);
            canBacks = ta.getBoolean(R.styleable.TCActivityTitle_canBacks, true);
            backTexts = ta.getString(R.styleable.TCActivityTitle_backTexts);
            moreTexts = ta.getString(R.styleable.TCActivityTitle_moreTexts);
            setUpView();
        } finally {
            ta.recycle();
        }
    }

    private void setUpView(){
        llReturn = (LinearLayout)findViewById(R.id.menu_return);
        tvTitle = (TextView)findViewById(R.id.title);
        tvMore = (TextView)findViewById(R.id.menu_more);
        tvBack = (TextView) findViewById(R.id.back_tv);


        if (!canBacks){
            llReturn.setVisibility(View.GONE);
        }

        tvBack.setText(backTexts);

        tvMore.setText(moreTexts);
        tvTitle.setText(titleTexts);
    }

    /**
     * 设置标题
     * @param title 标题
     */
    public void setTitle(String title){
        titleTexts = title;
        tvTitle.setText(title);
    }

    /**
     * 设置扩展消息
     * @param title 扩展消息
     */
    public void setMoreText(String title){
        moreTexts = title;
        tvMore.setText(title);
    }

    /**
     * 设置返回文案
     * @param strReturn 返回文案
     */
    public void setReturnText(String strReturn){
        backTexts = strReturn;
        tvBack.setText(strReturn);
    }

    /**
     * 设置返回消息事件
     * @param listener 返回消息listener
     */
    public void setReturnListener(OnClickListener listener){
        llReturn.setOnClickListener(listener);
    }

    /**
     * 设置扩展事件
     * @param listener 扩展事件listener
     */
    public void setMoreListener(OnClickListener listener){
        if (!TextUtils.isEmpty(moreTexts)) {
            tvMore.setOnClickListener(listener);
        }
    }
}

package com.liren.live.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.liren.live.R;
import com.liren.live.event.Event;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class LivePlugsDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity(), R.style.dialog_gift);
        dialog.setContentView(R.layout.dialog_live_plugs);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);

        ButterKnife.bind(this,dialog);
        initView();
        initData();
        return dialog;
    }

    @OnClick({R.id.iv_game_1,R.id.iv_time_live})
    public void OnClick(View v){

        Event.CommonEvent e = new Event.CommonEvent();
        switch (v.getId()){

            case R.id.iv_game_1:

                e.action = 2;
                break;
            case R.id.iv_time_live:

                e.action = 3;
                break;

            default:
                break;
        }

        EventBus.getDefault().post(e);

        dismiss();

    }

    private void initView() {

    }

    private void initData() {

    }


}

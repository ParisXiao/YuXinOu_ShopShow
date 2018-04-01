package com.liren.live.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.liren.live.R;
import com.liren.live.interf.DialogInterface;

/**
 * UI公共类
 */
public class LiveCommon {



    //填写内容弹窗
    public static void showInputContentDialog(Context context,String title,final DialogInterface dialogInterface){

        final Dialog dialog = new Dialog(context, R.style.dialog_setting);
        dialog.setContentView(R.layout.dialog_set_room_pass);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        ((TextView)dialog.findViewById(R.id.tv_title)).setText(title);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.cancelDialog(view,dialog);
            }
        });
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.determineDialog(view,dialog);
            }
        });


    }

    //提示确认框
    public static void showConfirmDialog(Context context,String title,String content,final DialogInterface dialogInterface){

        final Dialog dialog = new Dialog(context, R.style.dialog_setting);
        dialog.setContentView(R.layout.dialog_confim);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        ((TextView)dialog.findViewById(R.id.tv_title)).setText(title);
        ((TextView)dialog.findViewById(R.id.tv_message_text)).setText(content);
        dialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.cancelDialog(view,dialog);
            }
        });
        dialog.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogInterface.determineDialog(view,dialog);
            }
        });


    }

}

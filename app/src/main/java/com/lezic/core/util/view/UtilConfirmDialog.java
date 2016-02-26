package com.lezic.core.util.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 确认消息对话框
 * Created by cielo on 2016/2/19 0019.
 */
public class UtilConfirmDialog {

    /**
     * 确认消息
     *
     * @param context
     * @param title
     * @param message
     * @param ok      确认回调
     * @param cancel  取消回调
     */
    public static void showDialog(Context context, int title, CharSequence message,
                                  android.content.DialogInterface.OnClickListener ok, android.content.DialogInterface.OnClickListener cancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        if (ok != null) {
            builder.setPositiveButton("确认", ok);
        }
        if (cancel != null) {
            builder.setNegativeButton("取消", cancel);
        }
        builder.show();
    }

    /**
     * 确认消息
     *
     * @param context
     * @param title
     * @param message
     * @param ok      确认回调
     */
    public static void showDialog(Context context, int title, CharSequence message,
                                  android.content.DialogInterface.OnClickListener ok) {

        UtilConfirmDialog.showDialog(context, title, message, ok, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

    }


}

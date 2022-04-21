package com.droidlogic.FileBrower;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

public class UsbBroadCastReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        FloatWindowHelper floatWindowHelper = FloatWindowHelper.getInstance();
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            if (!floatWindowHelper.isWindowShowing()) {
                floatWindowHelper.showFloatWindow(context);
            }
        } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED) || action.equals(Intent.ACTION_MEDIA_EJECT)) {
            if (floatWindowHelper.isWindowShowing()) {
                floatWindowHelper.dismissFloatWindow();
            }
        }
    }
}
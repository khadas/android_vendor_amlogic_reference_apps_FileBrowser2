package com.droidlogic.FileBrower;

import android.app.ActivityManager;
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

import java.util.List;

public class UsbBroadCastReceiver extends BroadcastReceiver {

    private final static String TOP_ACTIVITY = "com.droidlogic.FileBrower.FileBrower";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (isAppRunning(context)) {
            return;
        }
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

    private boolean isAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(1);
        if (list.size() <= 0) {
            return false;
        }
        if ( list.get(0).topActivity.getClassName().equals(TOP_ACTIVITY) ) {
            return true;
        } else {
            return false;
        }
    }
}
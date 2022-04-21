package com.droidlogic.FileBrower;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class FloatWindowHelper {

    @SuppressLint("StaticFieldLeak")
    private static volatile FloatWindowHelper mInstance;
    private WindowManager windowManager;
    private View windowView;

    private FloatWindowHelper() {
    }

    public static FloatWindowHelper getInstance() {
        if (mInstance == null) {
            synchronized (FloatWindowHelper.class) {
                if (mInstance == null) {
                    mInstance = new FloatWindowHelper();
                }
            }
        }
        return mInstance;
    }

    public void showFloatWindow(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        windowView = LayoutInflater.from(context).inflate(R.layout.usb_mount_hint, null);
        windowManager.addView(windowView, params);
        initView();
    }

    public void dismissFloatWindow() {
        windowManager.removeViewImmediate(windowView);
        windowView = null;
        windowManager = null;
    }

    public boolean isWindowShowing() {
        return windowManager != null;
    }

    private final View.OnKeyListener onBackKeyListener = (v, keyCode, event) -> {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            dismissFloatWindow();
            return true;
        }
        return false;
    };

    private void initView() {
        View viewUsb = windowView.findViewById(R.id.bt_open_usb);
        viewUsb.setOnClickListener(v -> setIntent(v.getContext(), 0));

        View viewVideo = windowView.findViewById(R.id.bt_open_video);
        viewVideo.setOnClickListener(v -> setIntent(v.getContext(), 1));

        View viewPicture = windowView.findViewById(R.id.bt_open_picture);
        viewPicture.setOnClickListener(v -> setIntent(v.getContext(), 3));

        View viewMusic = windowView.findViewById(R.id.bt_open_music);
        viewMusic.setOnClickListener(v -> setIntent(v.getContext(), 2));

        View viewFile = windowView.findViewById(R.id.bt_file);
        viewFile.setOnClickListener(v -> setIntent(v.getContext(), 5));

        viewUsb.setOnKeyListener(onBackKeyListener);
        viewVideo.setOnKeyListener(onBackKeyListener);
        viewPicture.setOnKeyListener(onBackKeyListener);
        viewMusic.setOnKeyListener(onBackKeyListener);
        viewFile.setOnKeyListener(onBackKeyListener);
    }

    private void setIntent(Context context, int tag) {
        Intent intent2 = new Intent(context, FileBrower.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.putExtra("show_by", tag);
        context.startActivity(intent2);
        dismissFloatWindow();
    }

}

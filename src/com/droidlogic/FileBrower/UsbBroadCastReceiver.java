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
    private WindowManager windowManager;
    private LinearLayout bt_open_usb;
    private LinearLayout bt_open_video;
    private LinearLayout bt_open_picture;
    private LinearLayout bt_open_music;
    private LinearLayout bt_file;
    private Context mContext;
    private boolean isMount = false;
    private boolean unMount = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        windowManager  = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        bt_open_usb = SingletonView.getInstanceView(context).findViewById(R.id.bt_open_usb);
        bt_open_video = SingletonView.getInstanceView(context).findViewById(R.id.bt_open_video);
        bt_open_picture = SingletonView.getInstanceView(context).findViewById(R.id.bt_open_picture);
        bt_open_music = SingletonView.getInstanceView(context).findViewById(R.id.bt_open_music);
        bt_file = SingletonView.getInstanceView(context).findViewById(R.id.bt_file);
        bt_open_usb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntent(0);
            }
        });
        bt_open_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntent(1);
            }
        });
        bt_open_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntent(3);
            }
        });
        bt_open_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntent(2);
            }
        });
        bt_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntent(5);
            }
        });
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            if (SingletonView.getInstance().getIsMount()) {
                return;
            }
            SingletonView.getInstance().setIsMount(true);
            SingletonView.getInstance().setUnMount(false);

            setWindowDialog(context);
            bt_open_usb.requestFocus();
        } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED) ||
                action.equals(Intent.ACTION_MEDIA_EJECT)) {
            if (SingletonView.getInstance().getUnMount()) {
                return;
            }
            SingletonView.getInstance().setIsMount(false);
            SingletonView.getInstance().setUnMount(true);
            if (SingletonView.getInstanceView(context) != null && SingletonView.getInstanceView(context).isShown()) {
                windowManager.removeView(SingletonView.getInstanceView(context));
            }

        }

    }


    private void setWindowDialog(Context context){
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.format = PixelFormat.TRANSLUCENT;
        params.flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        windowManager.addView(SingletonView.getInstanceView(context), params);


    }

    private void setIntent(int tag) {
        Intent intent2 = new Intent(mContext, FileBrower.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.putExtra("show_by",tag);
        mContext.startActivity(intent2);
        if (SingletonView.getInstanceView(mContext) != null && SingletonView.getInstanceView(mContext).isShown()) {
            windowManager.removeView(SingletonView.getInstanceView(mContext));
        }
    }



}

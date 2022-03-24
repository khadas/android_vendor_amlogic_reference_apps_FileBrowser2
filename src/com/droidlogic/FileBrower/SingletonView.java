package com.droidlogic.FileBrower;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class SingletonView {
    private static Context mContext;
    private static SingletonView singletonView;
    private static View view;
    private  boolean isMount = false;
    private  boolean unMount = false;

    public SingletonView() {
    }


    public static  synchronized SingletonView getInstance() {
            if (singletonView == null) {
                    singletonView = new SingletonView();
            }
            return singletonView;
    }

    public static  synchronized View getInstanceView(Context context) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.usb_mount_hint, null);
        }
        return view;
    }

   public  void setIsMount(Boolean isMounted) {
       isMount = isMounted;
   }
    public  boolean getIsMount() {
        return isMount;
    }
    public  void setUnMount(Boolean unMounted) {
        unMount = unMounted;
    }
    public  boolean getUnMount() {
        return unMount;
    }
}

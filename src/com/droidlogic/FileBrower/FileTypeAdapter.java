package com.droidlogic.FileBrower;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.zip.Inflater;

public class FileTypeAdapter extends BaseAdapter {
    private Context mContext;
    private String[] typeName;
    private int[] typeIconNormal;
    private int[] typeIconFocused;
    private int mCurrentItem = 0;
    private boolean isClick = false;

    public FileTypeAdapter(Context context, String[] typeName, int[] typeIconNormal, int[] typeIconFocused) {
        this.mContext = context;
        this.typeName = typeName;
        this.typeIconNormal = typeIconNormal;
        this.typeIconFocused = typeIconFocused;
    }

    @Override
    public int getCount() {
        return typeName.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.type_item, null);
            holder.typeIcon = view.findViewById(R.id.type_icon);
            holder.typeName = view.findViewById(R.id.type_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (mCurrentItem == i && isClick) {
            holder.typeName.setTextColor(Color.WHITE);
            holder.typeIcon.setImageDrawable(mContext.getDrawable(typeIconNormal[i]));
        } else {
            holder.typeIcon.setImageDrawable(mContext.getDrawable(typeIconFocused[i]));
            holder.typeName.setTextColor(Color.BLACK);
        }
        holder.typeName.setText(typeName[i]);
        return view;
    }

    class ViewHolder {
        public TextView typeName;
        public ImageView typeIcon;
    }

    public void setCurrentItem(int currentItem) {
        this.mCurrentItem = currentItem;
    }

    public void setItemClick(boolean click) {
        this.isClick = click;
    }
}

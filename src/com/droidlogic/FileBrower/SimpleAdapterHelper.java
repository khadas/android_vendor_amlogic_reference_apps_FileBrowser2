package com.droidlogic.FileBrower;

import android.content.Context;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

public class SimpleAdapterHelper {
    public static final String KEY_NAME = "key_name";
    public static final String KEY_PATH = "key_path";
    public static final String KEY_TYPE = "key_type";
    public static final String KEY_DATE = "key_date";
    public static final String KEY_SIZE = "key_size";
    public static final String KEY_SELE = "key_sele";
    public static final String KEY_RDWR = "key_rdwr";
    private static SimpleAdapter simpleAdapter;

    public static SimpleAdapter setFlieListAdapter(Context context, List<Map<String, Object>> mList, boolean isThumb, GridView lv) {
        if (!isThumb) {
            simpleAdapter = new SimpleAdapter(context,
                    mList,
                    R.layout.filelist_item,
                    new String[]{
                            KEY_TYPE,
                            KEY_NAME,
                            KEY_SELE,
                            KEY_SIZE,
                            KEY_DATE,
                            KEY_RDWR},
                    new int[]{
                            R.id.item_type,
                            R.id.item_name,
                            R.id.item_sel,
                            R.id.item_size,
                            R.id.item_date,
                            R.id.item_rw}
            );
            lv.setNumColumns(1);
        } else {
            simpleAdapter = new ThumbnailAdapter1(context,
                    mList,
                    R.layout.gridview_item,
                    new String[]{
                            KEY_TYPE,
                            KEY_SELE,
                            KEY_NAME},
                    new int[]{
                            R.id.itemImage,
                            R.id.itemMark,
                            R.id.itemText});
            lv.setNumColumns(6);
        }
        return simpleAdapter;
    }

    public static SimpleAdapter setSdcardList(Context context, List<Map<String, Object>> mList, boolean isThumb, GridView lv) {
        if (!isThumb) {
            ((GridView) lv).setNumColumns(1);
            return new SimpleAdapter(context, mList,
                    R.layout.device_item,
                    new String[]{
                            KEY_TYPE,
                            KEY_NAME,
                            KEY_RDWR
                    },
                    new int[]{
                            R.id.device_type,
                            R.id.device_name,
                            R.id.device_rw}
            );
        } else {
            ((GridView) lv).setNumColumns(6);
            return new ThumbnailAdapter1(context,
                    mList,
                    R.layout.gridview_item,
                    new String[]{
                            KEY_TYPE,
                            KEY_SELE,
                            KEY_NAME},
                    new int[]{
                            R.id.itemImage,
                            R.id.itemMark,
                            R.id.itemText});
        }
    }
}

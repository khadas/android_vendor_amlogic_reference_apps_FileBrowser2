/******************************************************************
*
*Copyright (C) 2012  Amlogic, Inc.
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
******************************************************************/
package com.droidlogic.FileBrower;

import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.os.storage.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.Gravity;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.PowerManager;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.storage.StorageVolume;
//import android.os.storage.VolumeInfo;
import android.content.BroadcastReceiver;
import android.media.MediaScannerConnection;



import android.Manifest;
import android.content.pm.PackageManager;

import java.util.Iterator;

import com.droidlogic.FileBrower.FileBrowerDatabase.FileMarkCursor;
import com.droidlogic.FileBrower.FileBrowerDatabase.ThumbnailCursor;
import com.droidlogic.FileBrower.FileOp.FileOpReturn;
import com.droidlogic.FileBrower.FileOp.FileOpTodo;
import com.droidlogic.app.FileListManager;

import android.bluetooth.BluetoothAdapter;
import java.lang.System;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import android.os.StrictMode;

public class FileBrower extends Activity {
    public static final String TAG = "FileBrowser";

    private List<Map<String, Object>> mList;
    private List<Map<String, Object>> sList;
    private SimpleAdapter simpleAdapter2;
    private boolean mListLoaded = false;
    private static final int LOAD_DIALOG_ID = 4;
    private ProgressDialog load_dialog;
    private boolean mLoadCancel = false;
    private boolean isPause = false;

    private PowerManager.WakeLock mWakeLock;
    private static final String SD_PATH_EQUAL = "/storage/sdcard1";

    public static final String KEY_NAME = "key_name";
    public static final String KEY_PATH = "key_path";
    public static final String KEY_TYPE = "key_type";
    public static final String KEY_DATE = "key_date";
    public static final String KEY_SIZE = "key_size";
    public static final String KEY_SELE = "key_sele";
    public static final String KEY_RDWR = "key_rdwr";

    public static String cur_path = FileListManager.STORAGE;
    private static final int SORT_DIALOG_ID = 0;
    private static final int EDIT_DIALOG_ID = 1;
    private static final int CLICK_DIALOG_ID = 2;
    private static final int HELP_DIALOG_ID = 3;
    private static String exit_path = FileListManager.STORAGE;
    private AlertDialog sort_dialog;
    private AlertDialog edit_dialog;
    private AlertDialog click_dialog;
    private AlertDialog help_dialog;
    private ProgressBar mLoadingProgress;
    private ListView sort_lv;
    private ListView edit_lv;
    private ListView click_lv;
    private ListView help_lv;
    private ListView lv_type;
    private boolean local_mode;
    public static FileBrowerDatabase db;
    public static FileMarkCursor myCursor;
    public static ThumbnailCursor myThumbCursor;
    public static Handler mProgressHandler;
    private AbsListView lv;
    private TextView tv;
    private ToggleButton btn_mode;
    private List<String> devList = new ArrayList<String>();
    private int request_code = 1550;
    private String lv_sort_flag = "by_name";
    private String show_by = "";

    private int item_position_selected, item_position_first, item_position_last;
    private int fromtop_piexl;
    private boolean isInFileBrowserView=false;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 2;

    String open_mode[] = {"movie","music","photo","packageInstall"};

    private static FileListManager mFileListManager;

    private static final int RET_OK = 0;

    private String[] typeName;
    private int[] typeIconNormal = {R.mipmap.main_all,R.mipmap.main_video,
            R.mipmap.main_music,R.mipmap.main_picture,R.mipmap.main_apk,
            R.mipmap.main_file};
    private int[] typeIconFocused = {R.mipmap.main_all,R.mipmap.main_video,
            R.mipmap.main_music,R.mipmap.main_picture,R.mipmap.main_apk,
            R.mipmap.main_file};
    private FileTypeAdapter fileTypeAdapter;
    private List<Map<String, Object>> readList = new ArrayList<>();
    private GridLayout gridLayout;
    private String[] mStrings = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N",
            "O","P","Q","R","S","D","U","V","W","X","Y","Z","0","1",
            "2", "3","4","5","6","7","8","9"};
    private TextView tv_search;
    private Button btClear;
    private Button btDel;
    private LinearLayout search_input;
    private StringBuilder setText = new StringBuilder("");
    private boolean isMount = false;
    private boolean isSearch = false;
    private boolean isSearchItemClick = false;
    private boolean isBtnHome = false;
    private ExecutorService executorService = Executors.newFixedThreadPool(8);
    private SimpleAdapter mSimpleAdapter;
    private List typeList = new ArrayList<Map<String, Object>>();
    private UsbBroadCastReceiver usbBroadCastReceiver;
    private boolean isThumb = false;
    private int selectedItem = 0;
    Comparator  mFileComparator = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            if (o1.isDirectory() && o2.isFile())
                return -1;
            if (o1.isFile() && o2.isDirectory())
                return 1;
            return o1.getName().compareTo(o2.getName());
        }
    };

    private BroadcastReceiver mMountReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Uri uri = intent.getData();
            String path = uri.getPath();

            if (action == null || path == null)
                return;

            if (path.startsWith(SD_PATH_EQUAL)) {
                path = path.replace(SD_PATH_EQUAL, FileListManager.NAND);
            }

            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                isMount = false;
                cur_path = FileListManager.STORAGE;
                DeviceScan();
                if (FileOp.IsBusy) {
                    if (isOperateInDirectory(path, FileOp.source_path) ||
                        isOperateInDirectory(path, FileOp.target_path)) {
                        FileOp.copy_cancel = true;
                    }
                }
            }
            else if ((action.equals ("com.droidvold.action.MEDIA_UNMOUNTED")
                || action.equals ("com.droidvold.action.MEDIA_EJECT")) && !path.equals("/dev/null")) {
                isMount = false;
                cur_path = FileListManager.STORAGE;
                DeviceScan();
            }
            else if (action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals ("com.droidvold.action.MEDIA_MOUNTED")) {
                if (isMount) {
                    return;
                }
                DeviceScan();
                isMount = true;
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                isMount = false;
                cur_path = FileListManager.STORAGE;
                DeviceScan();
            }
            else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                if (sort_dialog != null)
                    sort_dialog.dismiss();
                if (click_dialog != null)
                    click_dialog.dismiss();
                if (help_dialog != null)
                    help_dialog.dismiss();
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        //ignore orientation change
    }

    /** Called when the activity is first created or resumed. */
    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
        //StorageManager m_storagemgr = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        //m_storagemgr.registerListener(mListener);

        /** edit process bar handler
        *  mProgressHandler.sendMessage(Message.obtain(mProgressHandler, msg.what, msg.arg1, msg.arg2));
        */
        mProgressHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (!isInFileBrowserView)
                    return;

                ProgressBar pb = null;
                TextView tvForPaste = null;
                if (edit_dialog != null) {
                    pb = (ProgressBar) edit_dialog.findViewById(R.id.edit_progress_bar);
                    tvForPaste=(TextView)edit_dialog.findViewById(R.id.text_view_paste);
                }

                switch (msg.what) {
                    case 0: 	//set invisible
                        if ((edit_dialog != null) && (pb != null) && (tvForPaste != null)) {
                            pb.setVisibility(View.INVISIBLE);
                            tvForPaste.setVisibility(View.GONE);
                        }
                    break;
                    case 1:		//set progress_bar1
                        if ((edit_dialog != null) && (pb != null)) {
                            pb.setProgress(msg.arg1);
                        }
                    break;
                    case 2:		//set progress_bar2
                        if ((edit_dialog != null) && (pb != null)) {
                            pb.setSecondaryProgress(msg.arg1);
                        }
                    break;
                    case 3:		//set visible
                        if ((edit_dialog != null) && (pb != null) && (tvForPaste != null)) {
                            pb.setProgress(0);
                            pb.setSecondaryProgress(0);
                            pb.setVisibility(View.VISIBLE);

                            tvForPaste.setVisibility(View.VISIBLE);
                            tvForPaste.setText(getText(R.string.edit_dialog_paste_file)+"\n"+FileOp.getMarkFileName("list"));
                        }
                    break;
                    case 4:		//file paste ok
                        scanAll();

                        db.deleteAllFileMark();
                        lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                        ThumbnailOpUtils.updateThumbnailsForDir(getBaseContext(), cur_path);
                        Toast.makeText(FileBrower.this,
                            getText(R.string.Toast_msg_paste_ok),
                            Toast.LENGTH_SHORT).show();
                        FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                        if (edit_dialog != null)
                            edit_dialog.dismiss();
                        if (mWakeLock.isHeld())
                            mWakeLock.release();

                        if (tvForPaste != null) {
                            tvForPaste.setText("");
                            tvForPaste.setVisibility(View.GONE);
                        }
                    break;
                    case 5:		//file paste err
                        Toast.makeText(FileBrower.this,
                            getText(R.string.Toast_msg_paste_nofile),
                            Toast.LENGTH_SHORT).show();
                        FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                        if (edit_dialog != null)
                            edit_dialog.dismiss();
                        if (mWakeLock.isHeld())
                            mWakeLock.release();

                        if (tvForPaste != null) {
                            tvForPaste.setText("");
                            tvForPaste.setVisibility(View.GONE);
                        }
                    break;
                    case 7:		//dir cannot write
                        Toast.makeText(FileBrower.this,
                            getText(R.string.Toast_msg_paste_writeable),
                            Toast.LENGTH_SHORT).show();
                        //FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                        if (edit_dialog != null)
                            edit_dialog.dismiss();
                        if (mWakeLock.isHeld())
                            mWakeLock.release();

                        if (tvForPaste != null) {
                            tvForPaste.setText("");
                            tvForPaste.setVisibility(View.GONE);
                        }
                    break;
                    case 8:		//no free space
                        db.deleteAllFileMark();
                        lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                        ThumbnailOpUtils.updateThumbnailsForDir(getBaseContext(), cur_path);
                        Toast.makeText(FileBrower.this,
                            getText(R.string.Toast_msg_paste_nospace),
                            Toast.LENGTH_SHORT).show();
                        FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                        if (edit_dialog != null)
                            edit_dialog.dismiss();
                        if (mWakeLock.isHeld())
                            mWakeLock.release();

                        if (tvForPaste != null) {
                            tvForPaste.setText("");
                            tvForPaste.setVisibility(View.GONE);
                        }
                    break;
                    case 9:		//file copy cancel
                        if ((FileOp.copying_file != null) && (FileOp.copying_file.exists())) {
                            try {
                                if (FileOp.copying_file.isDirectory())
                                    FileUtils.deleteDirectory(FileOp.copying_file);
                                else
                                    FileOp.copying_file.delete();
                            }
                            catch (Exception e) {
                                Log.e("Exception when delete",e.toString());
                            }
                        }

                        Toast.makeText(FileBrower.this,
                            getText(R.string.Toast_copy_fail),
                            Toast.LENGTH_SHORT).show();
                        FileOp.copy_cancel = false;
                        FileOp.copying_file = null;
                        db.deleteAllFileMark();
                        lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                        FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                        if (edit_dialog != null)
                            edit_dialog.dismiss();
                        if (mWakeLock.isHeld())
                            mWakeLock.release();
                        scanAll();
                        if (tvForPaste != null) {
                            tvForPaste.setText("");
                            tvForPaste.setVisibility(View.GONE);
                        }
                    break;
                    case 10:    //update list
                        //((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                        if (!mListLoaded) {
                            break;
                        }
                        lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                        mListLoaded = false;
                        if (load_dialog != null)
                            load_dialog.dismiss();
                    break;
                    case 11:	//destination dir is sub folder of src dir
                    Toast.makeText(FileBrower.this,
                        getText(R.string.Toast_msg_paste_sub_folder),
                        Toast.LENGTH_SHORT).show();
                    //FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                    if (edit_dialog != null)
                        edit_dialog.dismiss();
                    if (mWakeLock.isHeld())
                        mWakeLock.release();

                    if (tvForPaste != null) {
                        tvForPaste.setText("");
                        tvForPaste.setVisibility(View.GONE);
                    }
                    break;
                    case 12:
                        if (load_dialog != null) {
                            load_dialog.dismiss();
                        }
                        if (!show_by.equals("")) {
                            typeList.clear();
                            typeList.addAll(sortType(Integer.parseInt(show_by)));
                            mSimpleAdapter.notifyDataSetChanged();

                            fileTypeAdapter.setCurrentItem(Integer.parseInt(show_by));
                            fileTypeAdapter.notifyDataSetChanged();
                            show_by = "";
                        }
                        break;
                }
            }
        };

        // install an intent filter to receive SD card related events.
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction ("com.droidvold.action.MEDIA_UNMOUNTED");
        intentFilter.addAction ("com.droidvold.action.MEDIA_MOUNTED");
        intentFilter.addAction ("com.droidvold.action.MEDIA_EJECT");
        intentFilter.addDataScheme("file");
        registerReceiver(mMountReceiver, intentFilter);
        registerReceiver(usbBroadCastReceiver, intentFilter);

        if (mListLoaded) {
            mListLoaded = false;
        }
        if (isSearch) {
            return;
        }
        /*File file = new File(cur_path);
        if (!(file.exists())) {
            cur_path = FileListManager.STORAGE;
        }*/
        if (cur_path.equals(FileListManager.STORAGE)) {
            DeviceScan();
        } else if (cur_path.equals("")) {

        } else {
            getFileListAdapterSorted(cur_path, lv_sort_flag);
        }
        lv.setSelectionFromTop(item_position_selected, fromtop_piexl);
        isInFileBrowserView=true;
    }

    @Override
    public void onPause() {
        super.onPause();

        //StorageManager m_storagemgr = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        //m_storagemgr.unregisterListener(mListener);

        unregisterReceiver(mMountReceiver);

        mLoadCancel = true;
        isPause = true;
        //update sharedPref
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("cur_path", cur_path);
        editor.putBoolean("isChecked", btn_mode.isChecked());
        editor.commit();

        if (load_dialog != null)
            load_dialog.dismiss();

        if (mListLoaded)
            mListLoaded = false;

        if (!local_mode) {
            db.deleteAllFileMark();
        }
        db.close();
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        show_by = String.valueOf(intent.getIntExtra("show_by",0));
        if (show_by.equals("0")) {
            show_by = "";
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //for file uri, close the strict mode check
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
         //end
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
            return;
        }
        usbBroadCastReceiver = new UsbBroadCastReceiver();
        mFileListManager = new FileListManager(this);
        typeName = getResources().getStringArray(R.array.sort);
        try {
            Bundle bundle = this.getIntent().getExtras();
            if (!bundle.getString("sort_flag").equals("")) {
                lv_sort_flag = bundle.getString("sort_flag");
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Do not set sort flag");
        }

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        //mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, TAG);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);

        /* setup database */
        db = new FileBrowerDatabase(this);
        SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);

        /* btn_mode default checked */
        btn_mode = (ToggleButton) findViewById(R.id.btn_mode);
        btn_mode.setChecked(settings.getBoolean("isChecked", false));

        /* setup file list */
        lv = (AbsListView) findViewById(R.id.listview);
        lv.setEmptyView(findViewById(R.id.empty_view));
        gridLayout = findViewById(R.id.gl);
        gridLayout.setColumnCount(6);
        gridLayout.setRowCount(6);
        search_input = findViewById(R.id.search_input);
        tv_search = findViewById(R.id.tv_search);
        btClear = findViewById(R.id.bt_clear);
        btDel = findViewById(R.id.bt_del);
        fileTypeAdapter = new FileTypeAdapter(this, typeName, typeIconNormal, typeIconFocused);
        lv_type = findViewById(R.id.lv_type);
        lv_type.setAdapter(fileTypeAdapter);
        local_mode = false;
        // cur_path = settings.getString("cur_path", FileListManager.STORAGE);
        try {
            Bundle bundle = this.getIntent().getExtras();
            if (!bundle.getString("cur_path").equals("")) {
                cur_path=bundle.getString("cur_path");
            }
        }
        catch (Exception e) {
            Log.e(TAG, " Do not set cur_path");
        }

        if (cur_path != null) {
            File file = new File(cur_path);
            if (!file.exists()) {
                cur_path = FileListManager.STORAGE;
            }
        } else {
            cur_path = FileListManager.STORAGE;
        }

        mList = new ArrayList<Map<String, Object>>();

        /* lv OnItemClickListener */
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                isSearchItemClick = true;
                Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);
                String file_path = (String) item.get(KEY_PATH);
                File file = new File(file_path);
                if (!file.exists()) {
                    //finish();
                    return;
                }

                if (Intent.ACTION_GET_CONTENT.equalsIgnoreCase(FileBrower.this.getIntent().getAction())) {
                    if (file.isDirectory()) {
                        cur_path = file_path;
                        lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                    }
                    else {
                        FileBrower.this.setResult(Activity.RESULT_OK,new Intent(null, Uri.fromFile(file)));
                        FileBrower.this.finish();
                    }
                }
                else {
                    ToggleButton btn_mode = (ToggleButton) findViewById(R.id.btn_mode);
                    if (!btn_mode.isChecked()) {
                        if (file.isDirectory()) {
                            cur_path = file_path;
                            getFileListAdapterSorted(cur_path, lv_sort_flag);
                        }
                        else {
                            openFile(file);
                            //showDialog(CLICK_DIALOG_ID);
                        }

                    }
                    else {
                        if (!cur_path.equals(FileListManager.STORAGE)) {
                            if (item.get(KEY_SELE).equals(R.drawable.item_img_unsel)) {
                                FileOp.updateFileStatus(file_path, 1,"list");
                                item.put(KEY_SELE, R.drawable.item_img_sel);
                            }
                            else if (item.get(KEY_SELE).equals(R.drawable.item_img_sel)) {
                                FileOp.updateFileStatus(file_path, 0,"list");
                                item.put(KEY_SELE, R.drawable.item_img_unsel);
                            }
                            ((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
                        }
                        else {
                            cur_path = file_path;
                            lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                        }
                    }

                    item_position_selected = lv.getSelectedItemPosition();
                    item_position_first = lv.getFirstVisiblePosition();
                    item_position_last = lv.getLastVisiblePosition();
                    View cv = lv.getChildAt(item_position_selected - item_position_first);
                    if (cv != null) {
                        fromtop_piexl = cv.getTop();
                    }
                }
            }
        });
        lv_type.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (fileTypeAdapter.getCurrentItem() == 0) {
                    return false;
                }
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    lv.setAdapter(getFileListAdapterSorted(FileListManager.STORAGE, lv_sort_flag));
                    fileTypeAdapter.setCurrentItem(0);
                    lv_type.setSelection(0);
                    return true;
                }
                return false;
            }
        });
        lv_type.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fileTypeAdapter.setCurrentItem(i);
                SimpleAdapter simpleAdapter;
                if (i == 0) {
                    cur_path = FileListManager.STORAGE;
                    lv.setAdapter(getFileListAdapterSorted(FileListManager.STORAGE, lv_sort_flag));
                    return;
                }
                cur_path = "";
                selectedItem = i;
                if (!isThumb) {
                    simpleAdapter = SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                            sortType(i), false, (GridView) lv);
                } else {
                    simpleAdapter = SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                            sortType(i), true, (GridView) lv);
                }
                lv.setAdapter(simpleAdapter);
            }
        });

        /* btn_parent listener */
        Button btn_parent = (Button) findViewById(R.id.btn_parent);
        btn_parent.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!cur_path.equals(FileListManager.STORAGE)) {
                    File file = new File(cur_path);
                    String parent_path = file.getParent();
                    if (cur_path.equals(FileListManager.NAND) || parent_path.equals(FileListManager.MEDIA_RW)) {
                        cur_path = FileListManager.STORAGE;
                        DeviceScan();
                    }
                    else {
                        cur_path = parent_path;
                        lv.setAdapter(getFileListAdapterSorted(parent_path, lv_sort_flag));
                    }
                }
            }
        });

        /* btn_home listener */
        ImageButton btn_home = (ImageButton) findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                isBtnHome = true;
                isSearch = false;
                search_input.setVisibility(View.GONE);
                lv_type.setVisibility(View.VISIBLE);
                cur_path = FileListManager.STORAGE;
                DeviceScan();
            }
        });

        /* btn_edit_listener */
        ImageButton btn_edit = (ImageButton) findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!cur_path.equals(FileListManager.STORAGE))
                    showDialog(EDIT_DIALOG_ID);
                else {
                    Toast.makeText(FileBrower.this,
                    getText(R.string.Toast_msg_edit_noopen),
                    Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* btn_sort_listener */
        ImageButton btn_sort = (ImageButton) findViewById(R.id.btn_sort);
        btn_sort.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!cur_path.equals(FileListManager.STORAGE))
                    showDialog(SORT_DIALOG_ID);
                else {
                    Toast.makeText(FileBrower.this,
                    getText(R.string.Toast_msg_sort_noopen),
                    Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* btn_help_listener */
        Button btn_help = (Button) findViewById(R.id.btn_help);
        btn_help.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showDialog(HELP_DIALOG_ID);
            }
        });

        ImageButton btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearch = true;
                initKeyBroad();
                sList = new ArrayList<>();
                if (!isThumb) {
                    simpleAdapter2 = SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                            sList, false, (GridView) lv);
                } else {
                    simpleAdapter2 = SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                            sList, true, (GridView) lv);
                }

                lv.setAdapter(simpleAdapter2);
            }
        });

        /* btn_istswitch_listener */
        ImageButton btn_listswitch = (ImageButton) findViewById(R.id.btn_listswitch);
        btn_listswitch.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (Intent.ACTION_GET_CONTENT.equalsIgnoreCase(FileBrower.this.getIntent().getAction())) {
                    Toast.makeText(FileBrower.this,
                    getText(R.string. Thumbnail_unsupport),
                    Toast.LENGTH_SHORT).show();
                }
                else {
                    SimpleAdapter simpleAdapter;
                    if (isThumb) {
                        isThumb = false;
                        if (cur_path.equals("")) {
                            simpleAdapter = SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                                    sortType(selectedItem), false, (GridView) lv);
                        } else if (cur_path.equals(FileListManager.STORAGE)) {
                            List<Map<String, Object>> list = getDeviceListData();
                            simpleAdapter = SimpleAdapterHelper.setSdcardList(FileBrower.this,
                                    list, false, (GridView) lv);
                        } else {
                            simpleAdapter = SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                                    getFileListDataSortedAsync(cur_path, lv_sort_flag), false, (GridView) lv);
                        }
                    } else {
                        isThumb = true;
                        if (cur_path.equals("")) {
                            simpleAdapter = SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                                    sortType(selectedItem), true, (GridView) lv);
                        } else if (cur_path.equals(FileListManager.STORAGE)) {
                            List<Map<String, Object>> list = getDeviceListData();
                            simpleAdapter = SimpleAdapterHelper.setSdcardList(FileBrower.this,
                                    list, true, (GridView) lv);
                        } else {
                            simpleAdapter = SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                                    getFileListDataSortedAsync(cur_path, lv_sort_flag), true, (GridView) lv);
                        }
                    }

                    lv.setAdapter(simpleAdapter);
                }
            }
        });
    }

    private void initKeyBroad() {
        search_input.setVisibility(View.VISIBLE);
        lv_type.setVisibility(View.GONE);
        gridLayout.removeAllViews();
        for (int i = 0; i < mStrings.length; i++) {
            Button textView = new Button(this);
            textView.setPadding(1, 1, 1, 1);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width =34;
            params.height =50;

            textView.setBackgroundColor(Color.TRANSPARENT);
            textView.setTextColor(Color.WHITE);
            textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        textView.setBackgroundColor(Color.WHITE);
                        textView.setTextColor(Color.BLACK);
                    } else {
                        textView.setBackgroundColor(Color.TRANSPARENT);
                        textView.setTextColor(Color.WHITE);
                    }
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setText.append(textView.getText());
                    tv_search.setText(setText);
                }
            });
            textView.setGravity(Gravity.CENTER);
            textView.setText(mStrings[i]);
            gridLayout.addView(textView,params);
        }

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setText.delete(0, setText.length());
                tv_search.setText(setText);
            }
        });

        btDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setText.length() < 1) {
                    return;
                } else if (setText.length() == 1) {
                    setText = new StringBuilder("");
                } else {
                    setText = new StringBuilder(setText.substring(0, setText.length()-1));
                }
                tv_search.setText(setText);
            }
        });

        tv_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<Map<String, Object>> list = GetTargetList.listsearch(readList, charSequence.toString());
                sList.clear();
                sList.addAll(list);
                Collections.sort(sList, new Comparator<Map<String, Object>>() {
                    public int compare(Map<String, Object> object1, Map<String, Object> object2) {
                        File file1 = new File((String) object1.get(KEY_PATH));
                        File file2 = new File((String) object2.get(KEY_PATH));

                        if ((file1.isFile() && file2.isFile()) || (file1.isDirectory() && file2.isDirectory())) {
                            return ((String) object1.get(KEY_NAME)).toLowerCase()
                                    .compareTo(((String) object2.get(KEY_NAME)).toLowerCase());
                        } else {
                            return file2.isFile() ? -1 : 1;
                        }
                    }
                });
                simpleAdapter2.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    /** onDestory() */
    @Override
    public void onDestroy() {
        super.onDestroy();
        isInFileBrowserView = false;
        if (!local_mode) {
            db.deleteAllFileMark();
        }
        db.close();
        db = null;
      if (usbBroadCastReceiver != null) {
            unregisterReceiver(usbBroadCastReceiver);
            usbBroadCastReceiver = null;
        }
        executorService.shutdownNow();
        System.exit(0);
    }
    private final class ScannPathTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            // We don't want to show the spinner every time we load images, because that would be
            // annoying; instead, only start showing the spinner if loading the image has taken
            // longer than 1 sec (ie 1000 ms)
            if (mLoadingProgress == null) {
                FrameLayout rootFrameLayout=(FrameLayout)findViewById(android.R.id.content);
                FrameLayout.LayoutParams layoutParams=
                    new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.gravity=Gravity.CENTER;
                mLoadingProgress=new ProgressBar(FileBrower.this);
                mLoadingProgress.setLayoutParams(layoutParams);
                rootFrameLayout.addView(mLoadingProgress);
                mLoadingProgress.setVisibility(View.GONE);
            }

            mLoadingProgress.postDelayed(() -> {
                if (getStatus() != AsyncTask.Status.FINISHED && mLoadingProgress.getVisibility() == View.GONE) {
                    mLoadingProgress.setVisibility(View.VISIBLE);
                }
            }, 500);
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.d(TAG, "doInBackground show image by image player service");
            String filePath = params[0];
            Log.i(TAG,"filePath = " + filePath);
            MediaScanner mediaScanner = new MediaScanner(getBaseContext());
            //String fileMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg");
            String[] filePaths = new String[]{filePath};
            //String[] mimeTypes = new String[]{fileMimeType};
            mediaScanner.scanFiles(filePaths, null);
            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            if (mLoadingProgress.getVisibility() == View.VISIBLE) {
                mLoadingProgress.setVisibility(View.GONE);
            }


        }

        @Override
        protected void onCancelled() {
            if (mLoadingProgress.getVisibility() == View.VISIBLE) {
                mLoadingProgress.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission is granted");
                } else {
                    Log.d(TAG, "permission is denied");
                    return;
                }
                break;
            }
        }
    }

    public class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

        /**
         *
         */
        private MediaScannerConnection mediaScanConn = null;

        public MediaScanner(Context context) {

            mediaScanConn = new MediaScannerConnection(context, this);
        }

        private int scanTimes = 0;
        private String[] filePaths;
        private String[] mimeTypes;


        public void scanFiles(String[] filePaths, String[] mimeTypes) {
            this.filePaths = filePaths;
            this.mimeTypes = mimeTypes;
            mediaScanConn.connect();
        }


        @Override
        public void onMediaScannerConnected() {
            for (int i = 0; i < filePaths.length; i++) {
                Log.i(TAG,"onMediaScannerConnected " + filePaths[i]);
                mediaScanConn.scanFile(filePaths[i], null);
            }
        }



        @Override
        public void onScanCompleted(String path, Uri uri) {
            if (uri == null) return;
            scanTimes++;
            if (scanTimes == filePaths.length) {
                mediaScanConn.disconnect();
                scanTimes = 0;
            }

            // TODO Auto-generated method stub
            Intent intent = new Intent();

            String type = "*/*";
            File f = new File(path);
            type = mFileListManager.CheckMediaType(f);
            Log.i(TAG,"onScanCompleted file path = " + path);
            Log.i(TAG,"onScanCompleted file path = " + uri.toString());

            if (FileUtils.isVideo(f.getName()) ) {
                intent.setData(uri);
                intent.setClassName("com.droidlogic.exoplayer2.demo", "com.droidlogic.videoplayer.MoviePlayer");
            } else if (FileUtils.isMusic(f.getName())) {
                intent.setData(uri);
                intent.setClassName("com.droidlogic.musicplayer", "com.droidlogic.musicplayer.PlaybackActivity");
            } else if (FileUtils.isPhoto(f.getName()) && Build.VERSION.SDK_INT < 34) {
                intent.setData(uri);
                intent.setClassName("com.droidlogic.imageplayer", "com.droidlogic.imageplayer.FullImageActivity");
            } else {
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(uri,type);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
                Intent intent2 = new Intent();
                intent2.setAction(android.content.Intent.ACTION_VIEW);
                intent2.setDataAndType(uri,type);
                try {
                    startActivity(intent2);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(FileBrower.this,
                            getText(R.string.Toast_msg_no_applicaton),
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
            startActivity(intent);

            filePaths = null;
            mimeTypes = null;
        }

    }


    public void install_apk (String apk_filepath) {
        Intent installintent = new Intent();
        File apkFile = new File(apk_filepath);
        installintent.setAction (Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(this,
            "com.droidlogic.filebrowser.fileprovider",
            apkFile);
        installintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        installintent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivityForResult(installintent,RET_OK);
    }

    protected void openFile(File f) {
        String filePath = f.getAbsolutePath();
        if (filePath.endsWith(".apk")) {
            try {
                install_apk(filePath);
                return;
            }
            catch (ActivityNotFoundException e) {
                Toast.makeText(FileBrower.this,
                getText(R.string.Toast_msg_no_applicaton),
                Toast.LENGTH_SHORT).show();
            }
        }
        Intent intent = new Intent();
        String type = "*/*";
        Uri uri = FileProvider.getUriForFile(this,
                "com.droidlogic.filebrowser.fileprovider",
                f);
        if (FileUtils.isVideo(f.getName()) ) {
            intent.setDataAndType(uri, "video/*");
            intent.setClassName("com.droidlogic.exoplayer2.demo", "com.droidlogic.videoplayer.MoviePlayer");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else if (FileUtils.isMusic(f.getName())) {
            intent.setAction(android.content.Intent.ACTION_VIEW);
            type = mFileListManager.CheckMediaType(f);
            intent.setDataAndType(Uri.fromFile(f),type);
            startActivity(intent);
            return;
        } else if (FileUtils.isPhoto(f.getName()) && Build.VERSION.SDK_INT < 34) {
            intent.setDataAndType(uri, "image/*");
            intent.setClassName("com.droidlogic.imageplayer", "com.droidlogic.imageplayer.FullImageActivity");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            type = mFileListManager.CheckMediaType(f);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(uri,type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            type = mFileListManager.CheckMediaType(f);
            Intent intent2 = new Intent();
            intent2.setAction(android.content.Intent.ACTION_VIEW);
            intent2.setDataAndType(uri,type);
            try {
                startActivity(intent2);
            } catch (ActivityNotFoundException e) {
            }
            return;
        }
        startActivity(intent);
        /*ScannPathTask task = new ScannPathTask();
        task.execute(filePath);*/
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                //Intent intent = this.getIntent();
                Bundle bundle = data.getExtras();
                cur_path = bundle.getString("cur_path");
            break;
            default:
            break;
        }
    }

    private void DeviceScan() {
        // TODO Auto-generated method stub
        devList.clear();
        String internal = getString(R.string.memory_device_str);
        String sdcard = getString(R.string.sdcard_device_str);
        String usb = getString(R.string.usb_device_str);
        String cdrom = getString(R.string.cdrom_device_str);
        String sdcardExt = getString(R.string.ext_sdcard_device_str);
        String DeviceArray[] = {internal,sdcard,usb,cdrom,sdcardExt};

        int length = 0;
        length = DeviceArray.length;

        for (int i = 0; i < length; i++) {
            if (FileOp.deviceExist(DeviceArray[i])) {
                devList.add(DeviceArray[i]);
            }
        }
        if (!show_by.equals("")) {
            List<Map<String, Object>> list = getDeviceListData();
            if (!isBtnHome) {
                readList.clear();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i=0;i<list.size();i++) {
                            read(((String) list.get(i).get(KEY_PATH)));
                        }

                    }
                }).start();
            } else {
                isBtnHome = false;
            }
            if (!isThumb) {
                mSimpleAdapter = SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                        typeList, false, (GridView) lv);
            } else {
                mSimpleAdapter = SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                        typeList, true, (GridView) lv);
            }
            if (!isPause) {
                lv.setAdapter(mSimpleAdapter);
            }
        } else {
            if (!isPause) {
                lv.setAdapter(getDeviceListAdapter());
                lv.requestFocus();
            }
        }
    }

    private ListAdapter
    getDeviceListAdapter() {
        List<Map<String, Object>> list = getDeviceListData();
        if (!isBtnHome) {
            readList.clear();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i=0;i<list.size();i++) {
                        read(((String) list.get(i).get(KEY_PATH)));
                    }
                }
            }).start();
        } else {
            isBtnHome = false;
        }
        // TODO Auto-generated method stub
        return SimpleAdapterHelper.setSdcardList(FileBrower.this, list, isThumb, (GridView) lv);
    }

    private List<Map<String, Object>> getDeviceListData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return list;
        }

        list = mFileListManager.getDevices();
        int fileCnt = list.size();
        for (int i = 0; i < fileCnt; i++) {
            Map<String, Object> fMap = list.get(i);
            String sType = (String)fMap.get(KEY_TYPE);
            if (sType.equals("type_nand")) {
                fMap.put(KEY_TYPE, R.drawable.sd_card_icon);
            } else if (sType.equals("type_udisk")) {
                fMap.put(KEY_TYPE, R.drawable.usb_card_icon);
            } else {
                fMap.put(KEY_TYPE, R.drawable.sd_card_icon);
            }
        }
        FileOp.updatePathShow(this, mFileListManager, FileListManager.STORAGE, false);
        if (!list.isEmpty()) {
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> object1, Map<String, Object> object2) {
                    return ((Integer) object1.get(KEY_SIZE)).compareTo(((Integer) object2.get(KEY_SIZE)));
                }
            });
        }
        return list;
    }

    /** Dialog */
    @Override
    protected Dialog onCreateDialog(int id) {
        LayoutInflater inflater = (LayoutInflater) FileBrower.this.getSystemService(LAYOUT_INFLATER_SERVICE);

        switch (id) {
            case SORT_DIALOG_ID:
                View layout_sort = inflater.inflate(R.layout.sort_dialog_layout, (ViewGroup) findViewById(R.id.layout_root_sort));
                sort_dialog = new AlertDialog.Builder(FileBrower.this)
                    .setView(layout_sort)
                    .setTitle(R.string.btn_sort_str)
                    .create();
            return sort_dialog;

            case EDIT_DIALOG_ID:
                View layout_edit = inflater.inflate(R.layout.edit_dialog_layout, (ViewGroup) findViewById(R.id.layout_root_edit));
                edit_dialog = new AlertDialog.Builder(FileBrower.this)
                    .setView(layout_edit)
                    .setTitle(R.string.btn_edit_str)
                    .create();
            return edit_dialog;

            case CLICK_DIALOG_ID:
                View layout_click = inflater.inflate(R.layout.click_dialog_layout, (ViewGroup) findViewById(R.id.layout_root_click));
                click_dialog = new AlertDialog.Builder(FileBrower.this)
                    .setView(layout_click)
                    .create();
            return click_dialog;

            case HELP_DIALOG_ID:
                View layout_help = inflater.inflate(R.layout.help_dialog_layout, (ViewGroup) findViewById(R.id.layout_root_help));
                help_dialog = new AlertDialog.Builder(FileBrower.this)
                    .setView(layout_help)
                    .setTitle(R.string.btn_help_str)
                    .create();
            return help_dialog;

            case LOAD_DIALOG_ID:
                if (load_dialog == null) {
                    load_dialog = new ProgressDialog(this);
                    load_dialog.setMessage(getText(R.string.load_dialog_msg_str));
                    load_dialog.setIndeterminate(true);
                    load_dialog.setCancelable(true);
                }
            return load_dialog;
        }

        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        WindowManager wm = getWindowManager();
        Display display = wm.getDefaultDisplay();
        LayoutParams lp = dialog.getWindow().getAttributes();

        switch (id) {
            case SORT_DIALOG_ID:
                if (display.getHeight() > display.getWidth()) {
                    lp.width = (int) (display.getWidth() * 1.0);
                } else {
                    lp.width = (int) (display.getWidth() * 0.5);
                }
                dialog.getWindow().setAttributes(lp);

                sort_lv = (ListView) sort_dialog.getWindow().findViewById(R.id.sort_listview);
                sort_lv.setAdapter(getDialogListAdapter(SORT_DIALOG_ID));
                sort_lv.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        if (!cur_path.equals(FileListManager.STORAGE)) {
                            if (pos == 0) {
                                lv_sort_flag = "by_name";
                                lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                            }
                            else if (pos == 1) {
                                lv_sort_flag = "by_date";
                                lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                            }
                            else if (pos == 2) {
                                lv_sort_flag = "by_size";
                                lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                            }
                        }
                        sort_dialog.dismiss();

                        /*//todo
                        Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(pos);
                        if (item.get("item_sel").equals(R.drawable.dialog_item_img_unsel))
                            item.put("item_sel", R.drawable.dialog_item_img_sel);
                        else if (item.get("item_sel").equals(R.drawable.dialog_item_img_sel))
                            item.put("item_sel", R.drawable.dialog_item_img_unsel);
                        ((BaseAdapter) sort_lv.getAdapter()).notifyDataSetChanged();
                        */
                    }
                });
            break;

            case EDIT_DIALOG_ID:
                if (display.getHeight() > display.getWidth()) {
                    lp.width = (int) (display.getWidth() * 1.0);
                } else {
                    lp.width = (int) (display.getWidth() * 0.5);
                }
                dialog.getWindow().setAttributes(lp);

                if (mProgressHandler == null) return;
                mProgressHandler.sendMessage(Message.obtain(mProgressHandler, 0));
                edit_lv = (ListView) edit_dialog.getWindow().findViewById(R.id.edit_listview);
                if (edit_lv == null) return;
                edit_lv.setAdapter(getDialogListAdapter(EDIT_DIALOG_ID));
                //edit_dialog.setCanceledOnTouchOutside(false);
                edit_dialog.setOnDismissListener(new OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        FileOp.copy_cancel = true;
                    }
                });

                edit_lv.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        if (!cur_path.equals(FileListManager.STORAGE)) {
                            if (FileOp.IsBusy) {
                                return;
                            }
                            File wFile = new File(cur_path);
                            if (!wFile.canWrite()) {
                                Toast.makeText(FileBrower.this,
                                    getText(R.string.Toast_msg_no_write),
                                    Toast.LENGTH_SHORT).show();
                            } else {
                                if (pos == 0) {
                                    try {
                                        myCursor = db.getFileMark();
                                        if (myCursor.getCount() > 0) {
                                            Toast.makeText(FileBrower.this,
                                                getText(R.string.Toast_msg_cut_todo),
                                                Toast.LENGTH_SHORT).show();
                                            FileOp.file_op_todo = FileOpTodo.TODO_CUT;
                                        } else {
                                            Toast.makeText(FileBrower.this,
                                                getText(R.string.Toast_msg_cut_nofile),
                                                Toast.LENGTH_SHORT).show();
                                            FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                                        }
                                    } finally {
                                        myCursor.close();
                                    }

                                edit_dialog.dismiss();
                            }
                            else if (pos == 1) {
                                try {
                                    myCursor = db.getFileMark();
                                    if (myCursor.getCount() > 0) {
                                        Toast.makeText(FileBrower.this,
                                            getText(R.string.Toast_msg_cpy_todo),
                                            Toast.LENGTH_SHORT).show();
                                        FileOp.file_op_todo = FileOpTodo.TODO_CPY;
                                    } else {
                                        Toast.makeText(FileBrower.this,
                                            getText(R.string.Toast_msg_cpy_nofile),
                                            Toast.LENGTH_SHORT).show();
                                        FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                                    }
                                } finally {
                                    myCursor.close();
                                }
                                edit_dialog.dismiss();
                            }
                            else if (pos == 2) {
                                if (!mWakeLock.isHeld())
                                    mWakeLock.acquire();

                                    if (cur_path.startsWith(FileListManager.NAND)) {
                                        Log.d(TAG,"==== Environment.MEDIA_MOUNTED:"+Environment.MEDIA_MOUNTED);
                                        //if(Environment.getExternalStorage2State().equals(Environment.MEDIA_MOUNTED))
                                        //if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))  //from thumbnailView1
                                        if (Environment.getStorageState(new File(FileListManager.NAND)).equals(Environment.MEDIA_MOUNTED)) {
                                            new Thread () {
                                                public void run () {
                                                    try {
                                                        FileOp.pasteSelectedFile("list");
                                                    } catch(Exception e) {
                                                        Log.e("Exception when paste file", e.toString());
                                                    }
                                                }
                                            }.start();
                                        }
                                        else {
                                            Toast.makeText(FileBrower.this,
                                                getText(R.string.Toast_no_sdcard),
                                                Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        new Thread () {
                                            public void run () {
                                                try {
                                                    FileOp.pasteSelectedFile("list");
                                                } catch(Exception e) {
                                                    Log.e("Exception when paste file", e.toString());
                                                }
                                            }
                                        }.start();
                                    }
                                }
                                else if (pos == 3) {
                                    FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                                    FileOpReturn delStatus = FileOp.deleteSelectedFile("list");
                                    if (FileOpReturn.SUCCESS == delStatus) {
                                        db.deleteAllFileMark();
                                        lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                                        scanAll();
                                        Toast.makeText(FileBrower.this,
                                            getText(R.string.Toast_msg_del_ok),
                                            Toast.LENGTH_SHORT).show();
                                    }
                                    else if (FileOpReturn.ERR_DEL_FAIL == delStatus) {
                                        Toast.makeText(FileBrower.this,
                                            getText(R.string.Toast_msg_del_fail),
                                            Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(FileBrower.this,
                                            getText(R.string.Toast_msg_del_nofile),
                                            Toast.LENGTH_SHORT).show();
                                    }
                                    edit_dialog.dismiss();
                                }
                                else if (pos == 4) {
                                    FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                                    myCursor = db.getFileMark();
                                    if (myCursor.getCount() > 0) {
                                        if (myCursor.getCount() > 1) {
                                            String fullPath = FileOp.getMarkFilePath("list");
                                            Toast.makeText(FileBrower.this,
                                                getText(R.string.Toast_msg_rename_morefile)+"\n"+fullPath,
                                                Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            String fullPath=FileOp.getSingleMarkFilePath("list");
                                            if (null != fullPath) {
                                                String dirPath = fullPath.substring(0, fullPath.lastIndexOf('/'));
                                                if (cur_path.equals(dirPath)) {
                                                    if (!fileRename()) {
                                                        Toast.makeText(FileBrower.this,
                                                            getText(R.string.Toast_msg_rename_error),
                                                            Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                else {
                                                    Toast.makeText(FileBrower.this,
                                                        getText(R.string.Toast_msg_rename_diffpath)+"\n"+dirPath,
                                                        Toast.LENGTH_LONG).show();
                                                }
                                            }
                                            else if (!fileRename()) {
                                                Toast.makeText(FileBrower.this,
                                                    getText(R.string.Toast_msg_rename_error),
                                                    Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(FileBrower.this,
                                            getText(R.string.Toast_msg_rename_nofile),
                                            Toast.LENGTH_SHORT).show();
                                    }
                                    edit_dialog.dismiss();
                                }
                                else if (pos == 5) {
                                    FileOp.file_op_todo = FileOpTodo.TODO_NOTHING;
                                    myCursor = db.getFileMark();
                                    if (myCursor.getCount() > 0) {
                                        int ret = shareFile();
                                        if (ret <= 0) {
                                            Toast.makeText(FileBrower.this,
                                                getText(R.string.Toast_msg_share_nofile),
                                                Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        Toast.makeText(FileBrower.this,
                                            getText(R.string.Toast_msg_share_nofile),
                                            Toast.LENGTH_SHORT).show();
                                    }
                                    edit_dialog.dismiss();
                                }
                            }
                        } else {
                            Toast.makeText(FileBrower.this,
                                getText(R.string.Toast_msg_paste_wrongpath),
                                Toast.LENGTH_SHORT).show();
                            edit_dialog.dismiss();
                        }
                    }
                });
            break;

            case CLICK_DIALOG_ID:
                if (display.getHeight() > display.getWidth()) {
                    lp.width = (int) (display.getWidth() * 1.0);
                } else {
                    lp.width = (int) (display.getWidth() * 0.5);
                }
                dialog.getWindow().setAttributes(lp);

                click_lv = (ListView) click_dialog.getWindow().findViewById(R.id.click_listview);
                click_lv.setAdapter(getDialogListAdapter(CLICK_DIALOG_ID));
            break;

            case HELP_DIALOG_ID:
                if (display.getHeight() > display.getWidth()) {
                    lp.width = (int) (display.getWidth() * 1.0);
                }
                else {
                    lp.width = (int) (display.getWidth() * 0.5);
                }
                dialog.getWindow().setAttributes(lp);

                help_lv = (ListView) help_dialog.getWindow().findViewById(R.id.help_listview);
                help_lv.setAdapter(getDialogListAdapter(HELP_DIALOG_ID));
                help_lv.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                        help_dialog.dismiss();
                    }
                });
            break;

            case LOAD_DIALOG_ID:
                if (display.getHeight() > display.getWidth()) {
                    lp.width = (int) (display.getWidth() * 1.0);
                }
                else {
                    lp.width = (int) (display.getWidth() * 0.5);
                }
                dialog.getWindow().setAttributes(lp);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        mLoadCancel = true;
                    }
                });

                mLoadCancel = false;
            break;
        }
    }

    private Dialog mRenameDialog;
    private String name = null;
    private String path = null;
    private boolean fileRename() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.file_rename, null);

        path = FileOp.getSingleMarkFilePath("list");
        if (null != path) {
            int index = -1;
            index = path.lastIndexOf("/");
            if (index >= 0) {
                name=path.substring(index + 1);
                if (null == name) {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }

        final EditText mRenameEdit = (EditText) v.findViewById(R.id.editTextRename);
        final File mRenameFile = new File(path);
        mRenameEdit.setText(name);

        Button buttonOK = (Button) v.findViewById(R.id.buttonOK);
        Button buttonCancel = (Button) v.findViewById(R.id.buttonCancel);
        buttonOK.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (null != mRenameDialog ) {
                    mRenameDialog.dismiss();
                    mRenameDialog = null;
                }

                String newFileName = String.valueOf(mRenameEdit.getText());
                if (!name.equals(newFileName)) {
                    newFileName = path.substring(0, path.lastIndexOf('/') + 1) + newFileName;
                    if (mRenameFile.renameTo(new File(newFileName))) {
                        db.deleteAllFileMark();
                        lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                        scanAll();
                    }
                    else {
                        Toast.makeText(FileBrower.this,
                            getText(R.string.Toast_msg_rename_error),
                            Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (null != mRenameDialog ) {
                    mRenameDialog.dismiss();
                    mRenameDialog = null;
                }
            }
        });

        mRenameDialog = new AlertDialog.Builder(FileBrower.this)
            .setView(v)
            .show();
        return true;
    }

    private int shareFile() {
        ArrayList<Uri> uris = new ArrayList<Uri>();
        Intent intent = new Intent();
        String type = "*/*";

        uris = FileOp.getMarkFilePathUri("list");
        final int size = uris.size();

        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        if (ba == null) {
            Toast.makeText(FileBrower.this,
                getText(R.string.Toast_msg_share_nodev),
                Toast.LENGTH_SHORT).show();
            return 0xff;
        }

        if (size > 0) {
            if (size > 1) {
                intent.setAction(Intent.ACTION_SEND_MULTIPLE).setType(type);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
            }
            else {
                intent.setAction(Intent.ACTION_SEND).setType(type);
                intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
            }
            intent.setType(type);
            startActivity(intent);
        }
        return size;
    }

    /** getFileListAdapter */
    private SimpleAdapter getFileListAdapter(String path) {
        if (!isThumb) {
            return SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                    getFileListData(path), false, (GridView) lv);
        } else {
            return SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                    getFileListData(path), true, (GridView) lv);
        }
    }

    /** getFileListData */
    private List<Map<String, Object>> getFileListData(String path) {
        List<Map<String, Object>> list = FileUtils.getFiles(path);
        int fileCnt = list.size();
        String tmpPath = null;
        for (int i = 0; i < fileCnt; i++) {
            Map<String, Object> fMap = list.get(i);
            tmpPath = (String)fMap.get(KEY_PATH);
            if (tmpPath !=null) {
                File file = new File(tmpPath);
                long file_date = file.lastModified();
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm")
                        .format(new Date(file_date));
                fMap.put(KEY_DATE, date + " | ");
                long file_size = file.length();
                if (FileOp.isFileSelected(tmpPath,"list")) {
                    fMap.put(KEY_SELE, R.drawable.item_img_sel);
                } else {
                    fMap.put(KEY_SELE, R.drawable.item_img_unsel);
                }
                if (file.isDirectory()) {
                    fMap.put(KEY_TYPE, R.drawable.item_type_dir);
                    fMap.put(KEY_SIZE, " | ");
                } else {
                    fMap.put(KEY_TYPE, FileOp.getFileTypeImg(file.getName()));
                    fMap.put(KEY_SIZE, FileOp.getFileSizeStr(file_size) + " | ");
                }
                String rw = "d";
                if (file.canRead()) {
                    rw += "r";
                } else {
                    rw += "-";
                }
                if (file.canWrite()) {
                    rw += "w";
                } else {
                    rw += "-";
                }
                fMap.put(KEY_RDWR, rw);
            }
        }
        return list;
    }

    /** getFileListAdapterSorted */
    private SimpleAdapter getFileListAdapterSorted(String path, String sort_type) {
        if (path.equals(FileListManager.STORAGE)) {
            return SimpleAdapterHelper.setSdcardList(FileBrower.this, getDeviceListData(), isThumb, (GridView) lv);
        }
        else {
            if (!isThumb) {
                return SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                        getFileListDataSorted(path, sort_type), false, (GridView) lv);
            } else {
                return SimpleAdapterHelper.setFlieListAdapter(FileBrower.this,
                        getFileListDataSorted(path, sort_type), true, (GridView) lv);
            }
        }
    }

    private List<Map<String, Object>> getFileListDataSorted(String path, String sort_type) {
        FileOp.updatePathShow(this, mFileListManager, path, false);
        if (!mListLoaded) {
            mListLoaded = true;
            showDialog(LOAD_DIALOG_ID);

            final String ppath = path;
            final String ssort_type = sort_type;
            new Thread("getFileListDataSortedAsync") {
                @Override
                public void run() {
                    mList = getFileListDataSortedAsync(ppath, ssort_type);
                    if (null != mProgressHandler)
                        mProgressHandler.sendMessage(Message.obtain(mProgressHandler, 10));
                }
            }.start();
            return new ArrayList<Map<String, Object>>();
        }
        else {
            return mList;
        }
    }

    private void read(String path) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (isPause) {
                    return;
                }
                File file = new File(path);
                File[] files = file.listFiles();
                List<Map<String, Object>> list109 =   getFileListDataSortedAsync(path, "by_name");
                readList.addAll(list109);
                if (files == null || files.length < 1) {
                    return;
                }
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        read(files[i].getAbsolutePath());
                    }
                }
            }
        });
    }

    private List<Map<String, Object>> sortType(int findBy) {
        List<Map<String, Object>> list1 = new ArrayList<>();
        if (findBy == 0) {
            list1.addAll(readList);
        } else {
            for (int i=0; i<readList.size(); i++) {
                if (readList.get(i) == null) {
                    return list1;
                }
                File file = new File(((String) readList.get(i).get(KEY_PATH)));
                if (file.isDirectory()) {
                    continue;
                }
                if (findBy == 1) {
                    if (FileUtils.isVideo(file.getName()) ) {
                        list1.add(readList.get(i));
                    }
                } else if (findBy == 2) {
                    if (FileUtils.isMusic(file.getName()) ) {
                        list1.add(readList.get(i));
                    }
                } else if (findBy == 3) {
                    if (FileUtils.isPhoto(file.getName()) ) {
                        list1.add(readList.get(i));
                    }
                } else if (findBy == 4) {
                    if (FileUtils.isApk(file.getName()) ) {
                        list1.add(readList.get(i));
                    }
                } else if (findBy == 5) {
                    if (FileUtils.isDocument(file.getName()) ) {
                        list1.add(readList.get(i));
                    }
                }
            }
        }
        return list1;
    }

    /** getFileListDataSorted */
    private List<Map<String, Object>> getFileListDataSortedAsync(String path, String sort_type) {
        List<Map<String, Object>> list = FileUtils.getFiles(path);
        int fileCnt = list.size();
        String tmpPath = null;
        for (int i = 0; i < fileCnt; i++) {
            Map<String, Object> fMap = list.get(i);
            tmpPath = (String)fMap.get(KEY_PATH);
            if (tmpPath !=null) {
                File file = new File(tmpPath);
                long file_date = file.lastModified();
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm")
                        .format(new Date(file_date));
                fMap.put(KEY_DATE, date + " | ");
                long file_size = file.length();
                if (FileOp.isFileSelected(tmpPath,"list")) {
                    fMap.put(KEY_SELE, R.drawable.item_img_sel);
                } else {
                    fMap.put(KEY_SELE, R.drawable.item_img_unsel);
                }
                if (file.isDirectory()) {
                    fMap.put(KEY_TYPE, R.drawable.item_type_dir);
                    fMap.put(KEY_SIZE, " | ");
                } else {
                    fMap.put(KEY_TYPE, FileOp.getFileTypeImg(file.getName()));
                    fMap.put(KEY_SIZE, FileOp.getFileSizeStr(file_size) + " | ");
                }
                String rw = "d";
                if (file.canRead()) {
                    rw += "r";
                } else {
                    rw += "-";
                }
                if (file.canWrite()) {
                    rw += "w";
                } else {
                    rw += "-";
                }
                fMap.put(KEY_RDWR, rw);
            }
        }
        /* sorting */
        if (!list.isEmpty()) {
            if (sort_type.equals("by_name")) {
                Collections.sort(list, new Comparator<Map<String, Object>>() {
                    public int compare(Map<String, Object> object1, Map<String, Object> object2) {
                        File file1 = new File((String) object1.get(KEY_PATH));
                        File file2 = new File((String) object2.get(KEY_PATH));

                        if ((file1.isFile() && file2.isFile()) || (file1.isDirectory() && file2.isDirectory())) {
                            return ((String) object1.get(KEY_NAME)).toLowerCase()
                                .compareTo(((String) object2.get(KEY_NAME)).toLowerCase());
                        } else {
                            return file2.isFile() ? -1 : 1;
                        }
                    }
                });
            }
            else if (sort_type.equals("by_date")) {
                Collections.sort(list, new Comparator<Map<String, Object>>() {
                    public int compare(Map<String, Object> object1,
                    Map<String, Object> object2) {
                        File file1 = new File((String)object1.get(KEY_PATH));
                        File file2 = new File((String)object2.get(KEY_PATH));
                        long file_date1 = file1.lastModified();
                        long file_date2 = file2.lastModified();
                        long diff = file_date1 - file_date2;
                        if (diff > 0) {
                            return 1;
                        } else if (diff == 0) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
            }
            else if (sort_type.equals("by_size")) {
                Collections.sort(list, new Comparator<Map<String, Object>>() {
                    public int compare(Map<String, Object> object1,
                    Map<String, Object> object2) {
                        File file1 = new File((String)object1.get(KEY_PATH));
                        File file2 = new File((String)object2.get(KEY_PATH));
                        long file_size1 = file1.length();
                        long file_size2 = file2.length();
                        long diff = file_size1 - file_size2;
                        if (diff > 0) {
                            return 1;
                        } else if (diff == 0) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                });
            }
        }
        return list;
    }

    /** getDialogListAdapter */
    private SimpleAdapter getDialogListAdapter(int id) {
        return new SimpleAdapter(FileBrower.this,
            getDialogListData(id),
            R.layout.dialog_item,
            new String[]{
                "item_type",
                "item_name",
                "item_sel",},
            new int[]{
                R.id.dialog_item_type,
                R.id.dialog_item_name,
                R.id.dialog_item_sel,}
        );
    }

    /** getFileListData */
    private List<Map<String, Object>> getDialogListData(int id) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        switch (id) {
            case SORT_DIALOG_ID:
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_item_type_name);
                map.put("item_name", getText(R.string.sort_dialog_name_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_item_type_date);
                map.put("item_name", getText(R.string.sort_dialog_date_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_item_type_size);
                map.put("item_name", getText(R.string.sort_dialog_size_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
            break;

            case EDIT_DIALOG_ID:
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_item_type_cut);
                map.put("item_name", getText(R.string.edit_dialog_cut_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_item_type_copy);
                map.put("item_name", getText(R.string.edit_dialog_copy_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_item_type_paste);
                map.put("item_name", getText(R.string.edit_dialog_paste_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_item_type_delete);
                map.put("item_name", getText(R.string.edit_dialog_delete_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_item_type_rename);
                map.put("item_name", getText(R.string.edit_dialog_rename_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_item_type_size);
                map.put("item_name", getText(R.string.edit_dialog_share_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
            break;

            case CLICK_DIALOG_ID:
                for (int i = 0; i < open_mode.length; i++) {
                    map = new HashMap<String, Object>();
                    map.put("item_type", R.drawable.dialog_item_img_unsel);
                    map.put("item_name", open_mode[i]);
                    map.put("item_sel", R.drawable.dialog_item_img_unsel);
                    list.add(map);
                }
            break;

            case HELP_DIALOG_ID:
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_help_item_home);
                map.put("item_name", getText(R.string.dialog_help_item_home_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_help_item_mode);
                map.put("item_name", getText(R.string.dialog_help_item_mode_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_help_item_edit);
                map.put("item_name", getText(R.string.dialog_help_item_edit_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_help_item_sort);
                map.put("item_name", getText(R.string.dialog_help_item_sort_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_help_item_parent);
                map.put("item_name", getText(R.string.dialog_help_item_parent_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
                map = new HashMap<String, Object>();
                map.put("item_type", R.drawable.dialog_help_item_thumb);
                map.put("item_name", getText(R.string.dialog_help_item_thumb_str));
                map.put("item_sel", R.drawable.dialog_item_img_unsel);
                list.add(map);
            break;
        }
        return list;
    }

    //option menu
    public boolean onCreateOptionsMenu(Menu menu) {
        String ver_str = null;
        try {
            ver_str = getPackageManager().getPackageInfo("com.droidlogic.FileBrower", 0).versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        menu.add(0, 0, 0, getText(R.string.app_name) + " v" + ver_str);
        return true;
    }

    private boolean isOperateInDirectory(String umount_path, String path) {
        String str = path.substring(0, umount_path.length());
        return str.equals(umount_path);
    }

    private void scanAll() {
        Intent intent = new Intent();
        intent.setClassName("com.android.providers.media","com.android.providers.media.MediaScannerService");
        Bundle argsa = new Bundle();
        argsa.putString("path", FileListManager.NAND);
        argsa.putString("volume","external");
        startService(intent.putExtras(argsa));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isSearch && isSearchItemClick) {
                lv.setAdapter(simpleAdapter2);
                isSearchItemClick = false;
                return true;
            }
            if (!cur_path.equals(FileListManager.STORAGE) && !cur_path.equals("") && !isSearch) {
                File file = new File(cur_path);
                String parent_path = file.getParent();
                if (cur_path.equals(FileListManager.NAND) || parent_path.equals(FileListManager.MEDIA_RW)) {
                    cur_path = FileListManager.STORAGE;
                    lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                }
                else {

                    cur_path = parent_path;
                    if (cur_path.equals(FileListManager.STORAGE)) {
                        lv.setAdapter(getFileListAdapterSorted(cur_path, lv_sort_flag));
                    } else {
                        getFileListAdapterSorted(parent_path, lv_sort_flag);
                    }

                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

package com.coinport.odin.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.coinport.odin.R;
import com.coinport.odin.network.NetworkAsyncTask;
import com.coinport.odin.network.NetworkRequest;
import com.coinport.odin.network.OnApiResponseListener;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateManager {

    private final String TAG = "update_manager";
    private Context mContext;

    private String apkUrl = "http://softfile.3g.qq.com:8080/msoft/179/24659/43549/qq_hd_mini_1.4.apk";


    private Dialog noticeDialog;

    private Dialog downloadDialog;
    private static final String savePath = "/sdcard/updatedemo/";

    private static final String saveFileName = savePath + "coinport.apk";

    private ProgressBar mProgress;


    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    private OnUpdateChecked afterUpdateCheckedHandler = null;

    private boolean needUpdate = false;

    private boolean forceUpdate = false;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:

                    installApk();
                    break;
                default:
                    break;
            }
        };
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    public void checkUpdateInfo() {
        NetworkAsyncTask task = new NetworkAsyncTask(Constants.ANDROID_APP_VERSION_URL, Constants.HttpMethod.GET)
            .setOnSucceedListener(new OnApiResponseListener())
            .setOnFailedListener(new OnApiResponseListener())
            .setRenderListener(new NetworkAsyncTask.OnPostRenderListener() {
                @Override
                public void onRender(NetworkRequest s) {
                    if (s.getApiStatus() == NetworkRequest.ApiStatus.SUCCEED) {
                        try {
                            int latestVersion = Util.getJsonObjectByPath(s.getApiResult(), "data").getInt("v");
                            int currentVersion = getVersionCode();
                            if (latestVersion > currentVersion)
                                needUpdate = true;
                            if ((latestVersion / 100) > (currentVersion / 100)) {
                                forceUpdate = true;
                            }
                            if (needUpdate) {
                                showNoticeDialog();
                            } else {
                                if (afterUpdateCheckedHandler != null)
                                    afterUpdateCheckedHandler.onChecked(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    }

    public void setOnUpdateChecked(OnUpdateChecked afterUpdateCheckHandler) {
        this.afterUpdateCheckedHandler = afterUpdateCheckHandler;
    }

    private int getVersionCode() {
        int verCode = -1;
        try {
            verCode = mContext.getPackageManager().getPackageInfo("com.coinport.odin", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verCode;
    }

    private String getVersionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo("com.coinport.odin", 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return verName;
    }

    private void showNoticeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.auto_update_title);
        builder.setMessage(R.string.auto_update_msg);
        builder.setPositiveButton(R.string.auto_update_download, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        if (forceUpdate) {
            builder.setNegativeButton(R.string.auto_update_exit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (afterUpdateCheckedHandler != null) {
                        afterUpdateCheckedHandler.onChecked(false);
                    }
                }
            });
        } else {
            builder.setNegativeButton(R.string.auto_update_later, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (afterUpdateCheckedHandler != null) {
                        afterUpdateCheckedHandler.onChecked(true);
                    }
                }
            });
        }
        noticeDialog = builder.create();
        noticeDialog.show();
    }

    private void showDownloadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.auto_update_downloading);

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress = (ProgressBar)v.findViewById(R.id.progress);

        builder.setView(v);
        builder.setNegativeButton(R.string.auto_update_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();

        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkUrl);

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if(!file.exists()){
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do{
                    int numread = is.read(buf);
                    count += numread;
                    progress =(int)(((float)count / length) * 100);
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if(numread <= 0){
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf,0,numread);
                } while(!interceptFlag);

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }

        }
    };

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    private void installApk(){
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);

    }

    public interface OnUpdateChecked {
        public void onChecked(boolean goHome);
    }
}
package com.sky.lamp.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import com.orhanobut.logger.Logger;
import com.sky.lamp.BaseActivity;
import com.sky.lamp.R;
import com.vondear.rxtools.RxFileTool;
import com.vondear.rxtools.view.RxToast;

import java.io.File;

/**
 * Created by sky on 2015/9/28
 */
public class CheckUpdateTask {
    boolean mIsSilence = false;
    BaseActivity mActivity;
    private long downloadId = 0;
    public static final String DOWNLOAD_FILE_NAME = "aohua.apk";
    private CompleteReceiver completeReceiver;
    private DownloadManagerPro downloadManagerPro;
    DownloadManager downloadManager;

    /**
     * @param activity
     * @param isSilence 是否静默检查
     */
    public CheckUpdateTask(BaseActivity activity, boolean isSilence) {
        mActivity = activity;
        mIsSilence = isSilence;
        downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManagerPro = new DownloadManagerPro(downloadManager);
        completeReceiver = new CompleteReceiver();
        mActivity.registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    public void destory() {
        if (completeReceiver != null) {
            mActivity.unregisterReceiver(completeReceiver);
            completeReceiver = null;
        }
    }


    /**
     * 使用Http下载gif文件
     */
    public void downloadByHttp(String url) {
        File downFile = new File(RxFileTool.getRootPath() +"/borrow.apk");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationUri(Uri.fromFile(downFile));
        request.setTitle(mActivity.getResources().getString(R.string.app_name));
        request.setDescription(mActivity.getResources().getString(R.string.app_name)+"正在下载");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.setMimeType("application/vnd.android.package-archive");
        DownloadManager downloadManager = (DownloadManager) mActivity.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);
        Logger.i("downloadId "+downloadId);
        RxToast.showToast("开始下载");
    }


    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * get the id of download which have download success, if the id is my id and it's status is successful,
             * then install it
             **/
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            Logger.i( "下载完成！" + completeDownloadId +" "+downloadId);
            if (completeReceiver != null) {
                mActivity.unregisterReceiver(completeReceiver);
                completeReceiver = null;
            }
            if (completeDownloadId == downloadId) {
                // if download successful, install apk
                if (downloadManagerPro.getStatusById(downloadId) == DownloadManager.STATUS_SUCCESSFUL) {
                    String apkFilePath = RxFileTool.getRootPath() + "/borrow.apk";
                    install(context, apkFilePath);
                }
            }
        }
    }

    /**
     * install app
     * @param context
     * @param filePath
     * @return whether apk exist
     */
    public static boolean install(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
            i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        }
        return false;
    }
}

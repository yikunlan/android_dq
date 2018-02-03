package com.modo.core.util;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class UpdateUtil {
    private Context mContext;
    private Long mTaskId;
    private DownloadManager downloadManager;
    private String versionName;
    File file;
    static Boolean needUpdate;

    public UpdateUtil(Context context){
        this.mContext = context.getApplicationContext();

    }


    //使用系统下载器下载
    public void downloadAPK(String versionUrl, String versionName) {
        this.versionName = versionName;
        //将下载请求加入下载队列
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        //创建下载任务
        DownloadManager.Request request = null;
        try {
            request = new DownloadManager.Request(Uri.parse(URLDecoder.decode(versionUrl,"UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setAllowedOverRoaming(true);//漫游网络是否可以下载

        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(versionUrl));
        Log.i("文件类型：",mimeString);
        request.setMimeType(mimeString);

        // request.setMimeType("application/vnd.android.package-archive");
        request.allowScanningByMediaScanner();
        request.setVisibleInDownloadsUi(true);

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        request.setVisibleInDownloadsUi(true);

        // 设置下载apk的路径
        file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), versionName+".apk");
        request.setDestinationUri(Uri.fromFile(file));


        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        mTaskId = downloadManager.enqueue(request);

        //注册广播接收者，监听下载状态
        mContext.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }



    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();//检查下载状态
        }
    };

    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    Log.i("download",">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    Log.i("download",">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    Log.i("download",">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    Log.i("download",">>>下载完成");
                    //下载完成安装APK
                    installAPK(this.file);
                    break;
                case DownloadManager.STATUS_FAILED:
                    Log.i("download",">>>下载失败");
                    break;
            }
        }
    }


    //下载到本地后执行安装
    protected void installAPK(File file) {
        if (!file.exists()){
            Log.i("installAPK","安装包不存在");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + file.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        //在服务中开启activity必须设置flag,后面解释
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }



}

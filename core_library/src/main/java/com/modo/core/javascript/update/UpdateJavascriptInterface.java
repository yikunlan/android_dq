package com.modo.core.javascript.update;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.modo.core.util.UpdateUtil;
import com.modo.core.util.VersionUtil;

/**
 * 供SDK调用的
 */
public class UpdateJavascriptInterface {

    public static String PREFIX = "updateAndroid";

    private static UpdateJavascriptInterface updateJavascriptInterface;

    private Context context;

    private UpdateJavascriptInterface(Context context){
        this.context   = context;
    }


    public static UpdateJavascriptInterface getInstance(Context context){
        if(updateJavascriptInterface == null){
            updateJavascriptInterface = new UpdateJavascriptInterface(context);
        }
        return updateJavascriptInterface;
    }


    @JavascriptInterface
    public void getRemoteVersion(final String version, final String url){
        Log.i("版本：",version);
        Log.i("地址：",url);
        try{
            Integer remoteVersion = Integer.parseInt(version);
            Integer currentVersion = VersionUtil.getAPPVersionCode(context.getApplicationContext());
            if(remoteVersion > currentVersion){
                // 偶数强更新，奇数弱更新
                if(remoteVersion % 2 == 0){
                    UpdateUtil updateUtil = new UpdateUtil(this.context);
                    updateUtil.downloadAPK(url,"temp_"+version+".apk");
                }else{

                    final AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(this.context);
                    normalDialog.setTitle("更新提示");
                    normalDialog.setMessage("发现新版本，是否要更新?");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i("按钮点击","确定");
                                    UpdateUtil updateUtil = new UpdateUtil(context);
                                    updateUtil.downloadAPK(url,"temp_"+version+".apk");
                                }
                            });
                    normalDialog.setNegativeButton("关闭",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //...To-do
                                    Log.i("按钮点击：","取消");
                                }
                            });
                    // 显示
                    normalDialog.show();



                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}

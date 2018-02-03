package com.modo.core.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 判断远端服务器上apk版本与当前版本是否一致
 *
 */
public class VersionUtil {

    /**
     * 获取apk的版本号 currentVersionCode
     * @param ctx
     * @return
     */
    public static int getAPPVersionCode(Context ctx) {
        ctx = ctx.getApplicationContext();
        int currentVersionCode = 0;
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            String appVersionName = info.versionName; // 版本名
            currentVersionCode = info.versionCode; // 版本号
            System.out.println(currentVersionCode + " " + appVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionCode;
    }
}

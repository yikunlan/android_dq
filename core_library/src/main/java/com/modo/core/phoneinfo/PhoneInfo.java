package com.modo.core.phoneinfo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;


public class PhoneInfo {


    private static String[] specialModelArray = {"vivo Y51", "vivo Y37A", "m1 note", "HUAWEI G750-T20", "OPPO R7", "Lenovo A808t", "Lenovo A938t", "H3", "OPPO R7s", "Meitu M4", "Lenovo X2-TO", "vivo X6Plus A", "M5", "ONEPLUS A3010", "vivo X6A", "ZTE GEEK II 4G"};

    public static int isExistSpecialModel() {
        int res = 0;
        //获取当前手机型号
        String currentModel = getPhoneModel();
        //循环需要过滤的手机型号
        for (int i = 0; i < specialModelArray.length; i++) {
            //逐一匹配 如果存在返回true终止循环
            String temp = specialModelArray[i].replaceAll(" +", "");
            if (temp.equals(currentModel)) {
                res = 1;
                break;
            }
        }
        return res;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取SDK版本
     *
     * @return SDK版本
     */
    public static String getSDKVersion() {
        return Build.VERSION.SDK;
    }

    /**
     * 获取系统版本
     *
     * @return 系统版本
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取设备id
     *
     * @return 设备id
     */
    public static String getDeviceId() {
        return Settings.Secure.ANDROID_ID;
    }

    /**
     * 获取手机IMEI号
     *
     * 需要动态权限: android.permission.READ_PHONE_STATE
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "没有获取IMEI的权限";
        }
        String imei = telephonyManager.getDeviceId();
        return imei;
    }
    public static String getAndroidId(Context context){
        String androidId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return androidId;
    }

}

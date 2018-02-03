package com.modo.core.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2017/12/7.
 */

public class NetUtil {
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            context = context.getApplicationContext();
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
            //mNetworkInfo.isAvailable();
                return true;//有网
            }
        }
        return false;//没有网
    }
}

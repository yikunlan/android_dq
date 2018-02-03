package com.modo.core.cache;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

public class LocalSharedPreferencesUtil {

    private Context context;

    private static LocalSharedPreferencesUtil localSharedPreferencesUtil;

    private static String PACKAGE_INFO = "packageInfo";

    private LocalSharedPreferencesUtil(Context context) {
        this.context = context;
    }

    public static LocalSharedPreferencesUtil getInstance(Context context){
        if (localSharedPreferencesUtil == null){
            localSharedPreferencesUtil = new LocalSharedPreferencesUtil(context);
        }
        return localSharedPreferencesUtil;
    }


    private void parseJsonToLocal(JSONObject jsonObject){
        SharedPreferences preferences = context.getSharedPreferences(PACKAGE_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();


    }

}

package com.modo.core.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by hyk on 2018/1/8.
 * 在application里面实例化
 */

public class SPUtil {

    public static SPUtil instance;
    private SharedPreferences.Editor mEditor;
    SharedPreferences mSharedPreferences;
    public static SPUtil getInstance(Context context) {
        return instance==null?new SPUtil(context):instance;
    }
    private SPUtil(Context context){
        mSharedPreferences = context.getSharedPreferences("zyzy", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    /**
     * 把文件最近一次使用的时间保存（或者更新）
     * @param key 文件的名字
     * @param time 时间
     */
    public void putFileUseTime(String key ,long time){
        mEditor.putLong(key,time);
        mEditor.commit();
    }

    /**
     * 获取文件最近一次使用的时间
     * @param key 文件名称
     * @return 最近一次使用的时间
     */
    public long getFileLastSaveTime(String key){

        return mSharedPreferences.getLong(key,0);
    }
}

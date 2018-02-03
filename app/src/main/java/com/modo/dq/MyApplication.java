package com.modo.dq;

import android.app.Application;

import com.modo.core.cache.LocalResourcesCacheIndex;
import com.modo.core.util.SPUtil;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by hyk on 2018/1/4.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        if(BuildConfig.LOG_DEBUG){
            //如果是debug模式的话，就开启debug模式
            Config.DEBUG = true;
        }
        UMShareAPI.get(this);
        //初始化SharedPreferences工具,这个一定要在缓存之前初始化
        SPUtil.getInstance(getApplicationContext());
        //循环遍历缓存
        LocalResourcesCacheIndex.getInstance(getApplicationContext());
    }
    {
        PlatformConfig.setWeixin("wx26ff6f8cd7314c18", "0e9558fd35edbe2666efe0dac1a866d3");
        PlatformConfig.setQQZone("101455248", "16bb85c613f93ecfc1bc7f90d9070cef");
    }
}

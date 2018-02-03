package com.modo.dq;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.modo.core.javascript.progress.ProgressJavascriptInterface;
import com.modo.core.javascript.reyun.ReYunJavaScriptInterface;
import com.modo.core.javascript.update.UpdateJavascriptInterface;
import com.modo.core.util.Auth.AuthorityInfo;
import com.modo.core.util.InputUtil;
import com.modo.core.util.NetUtil;
import com.modo.core.util.PackageURL;
import com.modo.core.widgets.MyProgressBar;
import com.modo.dq.x5webViewClient.X5WebviewClient;
import com.tencent.smtt.sdk.WebView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.IOException;
import java.util.Map;


public class X5WebviewActivity extends BasicActivity {


    private WebView mWebView;

    private MyProgressBar myProgressBar;

    private boolean needRefresh = true; // 是否需要刷新

    public static X5WebviewActivity x5WebviewActivity;


    // 是否调试模式
    private final boolean isDebug = false;


    private StringBuffer webUrl = new StringBuffer(PackageURL.DQ_301453).append("&providecp=1").append("&isapk=1");
//    private final String localHtml = "http://debugx5.qq.com";
    private final String localHtml = "file:///android_asset/index.html";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x5WebviewActivity = X5WebviewActivity.this;
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.x5test);

        //获取权限
        AuthorityInfo.requestMultiplePermissions(X5WebviewActivity.this);

        myProgressBar = (MyProgressBar) findViewById(R.id.myprogressbar);

        //全屏化
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//remove notification bar  即全屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //解决输入法挡住输入框的问题
        InputUtil.assistActivity(this);

        try {
            initX5WebView();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ///登录随便写的
       /* findViewById(R.id.auth_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //授权QQ登录
                UMShareAPI.get(X5WebviewActivity.this).doOauthVerify(X5WebviewActivity.this, SHARE_MEDIA.QQ, authListener);
                //授权微信登录
                //UMShareAPI.get(X5WebviewActivity.this).doOauthVerify(X5WebviewActivity.this, SHARE_MEDIA.WEIXIN, authListener);

                //Toast.makeText(X5WebviewActivity.this,PhoneInfo.getIMEI(X5WebviewActivity.this),Toast.LENGTH_LONG).show();
                //Toast.makeText(X5WebviewActivity.this,PhoneInfo.getAndroidId(X5WebviewActivity.this),Toast.LENGTH_LONG).show();
            }
        });*/
        /*
        findViewById(R.id.request_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               *//* BaseHttp.getOkHttpClient(X5WebviewActivity.this);
                BaseHttp.request();
                try {
                    BaseHttp.okhttpPostRequest(X5WebviewActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }*//*
            }
        });*/

        Log.e("TAG",mWebView.toString());

    }


    /**
     * 加载x5内核
     */
    private void initX5WebView() throws IOException {
        mWebView = (WebView) findViewById(R.id.forum_context);

        com.tencent.smtt.sdk.WebSettings settings = mWebView.getSettings();

        settings.setUseWideViewPort(true); //自适应屏幕
        //允许JavaScript使用本地存储
        settings.setDomStorageEnabled(true);
        //启用支持javascript
        settings.setJavaScriptEnabled(true);
        //解决全屏字体大小bug
        settings.setTextZoom(100);


        //添加热云支持
        mWebView.addJavascriptInterface(ReYunJavaScriptInterface.getInstance(this.getApplicationContext()), ReYunJavaScriptInterface.PREFIX);
        //添加热更新功能支持
        mWebView.addJavascriptInterface(UpdateJavascriptInterface.getInstance(this), UpdateJavascriptInterface.PREFIX);

        // 添加进度条控制
        mWebView.addJavascriptInterface(ProgressJavascriptInterface.getInstance(myProgressBar), ProgressJavascriptInterface.PREFIX);

        if (!NetUtil.isNetworkConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),"网络连接不可用",Toast.LENGTH_SHORT).show();
            needRefresh = true;
            return;
        }else {
            needRefresh = false;
        }
        mWebView.setWebViewClient(X5WebviewClient.getWeviewClient(X5WebviewActivity.this));

        // 取40到60之间的数字
        ProgressJavascriptInterface.getInstance(myProgressBar).setmCurrentProgress(99);

        // android 5.0以上默认不支持Mixed Content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // android 7.0以后播放声音需要用户触发，设置不需要触发解决无法播放音效的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }

        if (isDebug) {
            Log.i("urlAddr:", localHtml);
            mWebView.loadUrl(localHtml);
        } else {
            //        webUrl.append("&providecp=1&mdcanvas=1");沈雨说这个有用，要问下什么时候需要打开这个配置
            Log.i("urlAddr:", webUrl.toString());
            mWebView.loadUrl(webUrl.toString());
//            mWebView.loadUrl(webUrl.toString());
        }

        progressBarHandler.post(runnable);


    }

    @Override
    public void onPause() {
        mWebView.pauseTimers();
        if (isFinishing()) {
            mWebView.loadUrl("about:blank");
            setContentView(new FrameLayout(this));
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.resumeTimers();
        Log.e("TAG",mWebView.toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("TAG",mWebView.toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mWebView.destroy();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Handler progressBarHandler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(ProgressJavascriptInterface.isneedReload){
                ProgressJavascriptInterface.isneedReload = false;
                progressBarHandler.postDelayed(this,6000);
            }else {
                ProgressJavascriptInterface.getInstance(myProgressBar).setmCurrentProgress(100);
            }
        }
    };

    // 点击两次返回退出app
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };


    private static boolean isExit = false;

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideNavigationBar();
        }
    }

    private void hideNavigationBar() {
        // TODO Auto-generated method stub
        final View decorView = getWindow().getDecorView();
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(flags);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });
    }

    public void refresh() {
        if(needRefresh){
            onCreate(null);
        }
    }

    /**
     * 授权的监听
     */
    UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            Toast.makeText(X5WebviewActivity.this, "分享的平台："+platform, Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(X5WebviewActivity.this, "成功了", Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(X5WebviewActivity.this, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(X5WebviewActivity.this, "取消了", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}

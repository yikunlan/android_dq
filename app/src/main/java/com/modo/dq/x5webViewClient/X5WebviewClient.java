package com.modo.dq.x5webViewClient;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.modo.core.cache.LocalResourcesCache;
import com.modo.core.cache.LocalResourcesManager;
import com.modo.core.cache.NetCacheBean;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class X5WebviewClient {
    //这边是静态变量，有问题，要修改
    private static X5WebviewClient x5WebviewClient;
    private static Activity inneractivity;
    private WebViewClient webViewClient;


    public X5WebviewClient(Activity inneractivity) {
        this.inneractivity = inneractivity;
    }

    public static WebViewClient getWeviewClient(final Activity activity) {
        if (x5WebviewClient == null) {
            x5WebviewClient = new X5WebviewClient(activity);
        }
        if (x5WebviewClient.webViewClient == null) {
            x5WebviewClient.webViewClient = new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                    /**
                     * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
                     */
                    final PayTask task = new PayTask(inneractivity);
                    boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                        @Override
                        public void onPayResult(final H5PayResultModel result) {
                            final String url=result.getReturnUrl();
                            if(!TextUtils.isEmpty(url)){
                                inneractivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.loadUrl(url);
                                    }
                                });
                            }
                        }
                    });

                    /**
                     * 判断是否成功拦截
                     * 若成功拦截，则无需继续加载该URL；否则继续加载
                     */
                    if(!isIntercepted) {
                        view.loadUrl(url);
                        return true;
                    }
                    //支付宝的支付结束

                    // 如下方案可在非微信内部WebView的H5页面中调出微信支付
                    if (url.startsWith("weixin://wap/pay?")) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        x5WebviewClient.inneractivity.startActivity(intent);
                        return true;
                    } else {
                        Map<String, String> extraHeaders = new HashMap<String, String>();
                        extraHeaders.put("Referer", "http://boboxia.cn");
                        view.loadUrl(url, extraHeaders);
                    }
                    return true;
                }

                @Override
                public void onPageFinished(WebView webView, String s) {
                    super.onPageFinished(webView, s);
                }

                @Override
                public void onReceivedSslError(WebView webView, com.tencent.smtt.export.external.interfaces.SslErrorHandler sslErrorHandler, com.tencent.smtt.export.external.interfaces.SslError sslError) {
                    if (sslError.getPrimaryError() == SslError.SSL_DATE_INVALID
                            || sslError.getPrimaryError() == SslError.SSL_EXPIRED
                            || sslError.getPrimaryError() == SslError.SSL_INVALID
                            || sslError.getPrimaryError() == SslError.SSL_UNTRUSTED) {
                        sslErrorHandler.proceed();
                    } else {
                        sslErrorHandler.cancel();
                    }

                    super.onReceivedSslError(webView, sslErrorHandler, sslError);
                }


                @Override
                public void onLoadResource(WebView webView, String s) {
                    Log.i(":资源URL：", s);
                    super.onLoadResource(webView, s);
                }


                @Override
                public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
                    Log.i("String:资源", s);
                    Uri uri = Uri.parse(s);
                    return getWebResourceResponse(uri, activity);
//                    return super.shouldInterceptRequest(webView,s);
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest request) {
//                    return  super.shouldInterceptRequest(webView,request);
                    return getWebResourceResponse(request.getUrl(), activity);
                }
            };
        }
        return x5WebviewClient.webViewClient;
    }

    @Nullable
    private static WebResourceResponse getWebResourceResponse(Uri request, Activity activity) {
        WebResourceResponse webResourceResponse = null;
//                    Log.i("原始请求：",request.getUrl().toString());

        InputStream inputStream = LocalResourcesCache.getInstance(activity).getResourceByCache(request);
        if (inputStream != null) {
            webResourceResponse = new WebResourceResponse(LocalResourcesManager.getMimeTypeByUrl(request), "utf-8", inputStream);
        } else {
            String contextType = LocalResourcesManager.getMimeTypeByUrl(request);
            if (contextType == null) {
                return webResourceResponse;
            }
            try {
                NetCacheBean netCacheBean = LocalResourcesCache.getInstance(activity).saveRemoteResourceByURL(request);
                if(netCacheBean!=null){
                    inputStream = netCacheBean.inputStream;
                    webResourceResponse = new WebResourceResponse(contextType, "utf-8", inputStream);
                    if(netCacheBean.headMap!=null){
                        webResourceResponse.setResponseHeaders(netCacheBean.headMap);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        if (webResourceResponse == null) {
            Log.i("原始请求：", request.toString());
        }
        return webResourceResponse;
    }
}

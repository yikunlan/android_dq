package com.modo.core.cache;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;


import com.modo.core.http.BaseHttp;
import com.modo.core.util.SPUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


/*import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import zlc.season.rxdownload.DownloadStatus;
import zlc.season.rxdownload.RxDownload;*/
public class LocalResourcesCache {
    private final String TAG = "LocalResourcesCache";

    private Context context;

    private static LocalResourcesCache localResourcesCache;

    private LocalResourcesCache(Context context) {
        this.context = context;
    }

    public static LocalResourcesCache getInstance(Activity activity){
        if(localResourcesCache == null){
            localResourcesCache = new LocalResourcesCache(activity);
        }
        return localResourcesCache;
    }

    /**
     * 获取本地Assest内的资源
     * @param resourcePath
     * @return
     */
    private InputStream getAssestCache(String resourcePath){
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(resourcePath);
        } catch (IOException e) {
            Log.i(getClass().getName(),">>>没有assst缓存");
        }
        return inputStream;
    }

    private InputStream getLocalCache(String resourcePath){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(resourcePath));
        } catch (FileNotFoundException e) {
            Log.i(getClass().getName(),">>>没有本地缓存");
        }
        return inputStream;
    }



    /**
     * 获取本地缓存资源
     * @param httpURL
     * @return
     */
    public InputStream getResourceByCache(Uri httpURL){

        InputStream inputStream = null;
        //判断是否是需要缓存的数据格式，如果不是的话就跳过
        if (LocalResourcesManager.getMimeTypeByUrl(httpURL) == null) {
            return inputStream;
        }
        String resourceUrl = LocalResourcesManager.getAssestUrl(httpURL);
        if (LocalResourcesCacheIndex.getInstance(context).isExistAssetsCache(resourceUrl)) {
            inputStream = this.getAssestCache(resourceUrl);
            Log.i("assets缓存》》》》",resourceUrl);
            resourceUrl = null;
            return  inputStream;
        }
        resourceUrl = LocalResourcesManager.getLocalCachePath(context,httpURL);
        if (LocalResourcesCacheIndex.getInstance(context).isExistLocalCache(resourceUrl)) {
            inputStream = this.getLocalCache(resourceUrl);
            Log.i("本地缓存》》》》",resourceUrl);
            resourceUrl = null;
            return inputStream;
        }
        return inputStream;
    }

    /**
     * 将远程资源缓存到本地
     * @param httpURL
     * @throws IOException
     */
    public NetCacheBean saveRemoteResourceByURL(Uri httpURL) throws IOException {

//        Log.i("地址：",httpURL.toString());

        NetCacheBean netCacheBean = new NetCacheBean();
        String resourceUrl = LocalResourcesManager.getLocalCachePath(context,httpURL);

        OutputStream outputStream = null;

        InputStream inputStream = null;

        File file = LocalResourcesManager.createLocalFile(resourceUrl);

        URL url = new URL(httpURL.toString());

        URLConnection urlConnection = url.openConnection();

        urlConnection.setConnectTimeout(4500);

        urlConnection.setReadTimeout(4500);

        inputStream = urlConnection.getInputStream();

        outputStream = new FileOutputStream(file);

        int bytesRead = 0;

        byte[] buffer = new byte[1024];

        while ((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {

            outputStream.write(buffer, 0, bytesRead);

        }

        outputStream.flush();

        outputStream.close();

        inputStream.close();

        LocalResourcesCacheIndex.getInstance(context).addLocalResourcesIndex(resourceUrl);

        Log.i("缓存资源到本地：",resourceUrl);

        inputStream = new FileInputStream(file);

        //放到sharepreference
        SPUtil.getInstance(context.getApplicationContext()).putFileUseTime(file.getName(),System.currentTimeMillis());

        /*//下载缓存
        downLoadFile(httpURL);*/
        netCacheBean.inputStream = inputStream;
        Map<String, List<String>> headMap = urlConnection.getHeaderFields();
        if(headMap!=null){
            for (Map.Entry<String, List<String>> entry : headMap.entrySet()) {
                if(entry.getKey()!=null && entry.getValue()!=null && entry.getValue().size()>0) {
                    netCacheBean.headMap.put(entry.getKey(), entry.getValue().get(0));
                }
            }
        }
        return netCacheBean;

        /*return downLoadFile(httpURL);*/
    }


    /**
     * 这个是okhttp异步回调得到文件流的，下载的时候可以用，但是像本项目的话暂时不用，暂时用原来的可以及时建立流的方式缓存
     * 下载文件
     */
    boolean downSuccess = false;
    public void downLoadFile(final Uri httpURL)  throws IOException{

        String resourceUrl = LocalResourcesManager.getLocalCachePath(context,httpURL);

        final File file = LocalResourcesManager.createLocalFile(resourceUrl);

        final Request request = new Request.Builder().url(httpURL.toString()).build();
        BaseHttp.getOkHttpClient(context).newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                downSuccess = false;
                Log.e(TAG,"缓存下载失败："+httpURL.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                    }
                    fos.flush();
                    // 下载完成
                    downSuccess = true;
                    Log.i(TAG,"下载缓存:"+file.getName());
                    //放到sharepreference
                    SPUtil.getInstance(context.getApplicationContext()).putFileUseTime(file.getName(),System.currentTimeMillis());
                } catch (Exception e) {
                    Log.e(TAG,"缓存下载失败："+httpURL.toString());
                    downSuccess = false;
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });

        /*if(downSuccess){
            inputStream = new FileInputStream(file);
        }*/
    }
}

package com.modo.core.cache;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;


public class RemoteResourcesManager {

    private LinkedList<String> zipUrls;

    private static Activity activity;

    private static RemoteResourcesManager remoteResourcesManager;



    private RemoteResourcesManager() {
        zipUrls = new LinkedList<>();

    }

    public static RemoteResourcesManager getInstance(Activity activity){
        if(remoteResourcesManager==null){
            remoteResourcesManager = new RemoteResourcesManager();
            RemoteResourcesManager.activity = activity;
        }
        return  remoteResourcesManager;
    }



    /**
     * 获取远程包更新的情况，将资源包连接初始化到本地
     */
    public void  getRemoteResourcesInfo(){
        try {
            String resJson = requestByGet();
            JSONObject jsonObject = new JSONObject(resJson);
            parseJsonToLocal(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseJsonToLocal(JSONObject jsonObject){
        // TODO: 2017/12/23 解析远程地址
        SharedPreferences preferences = activity.getApplication().getSharedPreferences("", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = preferences.edit();


    }


    public LinkedList<String> getZipUrls() {
        return zipUrls;
    }

    public static String requestByGet() throws Exception {
        String data = null;
//        String path =  activity.getResources().getString(R.string.resourceUrl);
        String path =  "";
        // 新建一个URL对象
        URL url = new URL(path);
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        // 设置连接超时时间
        urlConn.setConnectTimeout(5 * 1000);
        // 开始连接
        urlConn.connect();
        // 判断请求是否成功
        if (urlConn.getResponseCode() == 200) {
            // 获取返回的数据
            data = readStreamToString(urlConn.getInputStream());
            Log.i("Get方式请求成功，返回数据如下：", data);
        } else {
            Log.e("Get Error", "Get方式请求失败");
        }
        // 关闭连接
        urlConn.disconnect();
        return data;
    }


    public static String readStreamToString(InputStream inputStream) throws IOException {
        //创建字节数组输出流 ，用来输出读取到的内容
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //创建读取缓存,大小为1024
        byte[] buffer = new byte[1024];
        //每次读取长度
        int len = 0;
        //开始读取输入流中的文件
        while( (len = inputStream.read(buffer) ) != -1){ //当等于-1说明没有数据可以读取了
            byteArrayOutputStream.write(buffer,0,len); // 把读取的内容写入到输出流中
        }
        //把读取到的字节数组转换为字符串
        String result = byteArrayOutputStream.toString();

        //关闭输入流和输出流
        inputStream.close();
        byteArrayOutputStream.close();
        //返回字符串结果
        return result;
    }
}

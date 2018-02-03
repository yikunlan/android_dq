package com.modo.core.cache;


import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class LocalResourcesManager {
    public static final String ASSEST = "gameResources";
    public static final String LOCAL_CACHE = "/cache";

    private static Map<String,String> mimeTypeMap;

    private static StringBuilder sb;


    /**
     * 得到请求的文件地址，得到文件在Assets中对应的路径
     * @param httpURL
     * @return
     */
    public static String getAssestUrl(Uri httpURL){
        sb = new StringBuilder(ASSEST);
        sb.append(httpURL.getPath());
        return sb.toString();
    }

    /**
     * 根据请求的文件地址，得到文件在本地缓存目录下对应的地址
     * @param context
     * @param httpURL
     * @return
     */
    public static String getLocalCachePath(Context context, Uri httpURL){
        sb = new StringBuilder();
        sb.append(context.getApplicationContext().getExternalFilesDir(LOCAL_CACHE).toString());
        sb.append("/");
        sb.append(httpURL.getHost());
        // 判断URL中是否有？参数，如果有，？参数后的字符串将组成文件夹
        if (httpURL.toString().indexOf("?")>0) {
            sb.append("/");
            sb.append(httpURL.toString().substring(httpURL.toString().indexOf("?")+1));
        }
        sb.append(httpURL.getPath());
        return sb.toString();
    }


    /**
     * 根据请求得到对应的Context-Type
     * @param httpURL
     * @return
     */
    public static String getMimeTypeByUrl(Uri httpURL){
        if(mimeTypeMap == null){
            initMimeTypeMap();
        }
        String str = httpURL.getPath();
        if(str.indexOf("?")>-1){
            str = str.substring(str.indexOf(".")+1,str.indexOf("?"));
        }{
            str = str.substring(str.indexOf(".")+1);
        }
        Set<String> keys = mimeTypeMap.keySet();
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            String key = (String)iter.next();
            if (key.equals(str)){
                return mimeTypeMap.get(key);
            }
        }
        return null;

    }

    /**
     * 初始化，根据文件类型初始化返回的Context-Type
     */
    private static void initMimeTypeMap(){
        if(mimeTypeMap == null){
            mimeTypeMap = new HashMap<>();
            mimeTypeMap.put("png","image/png");
            mimeTypeMap.put("jpg","image/jpeg");
            mimeTypeMap.put("js","application/javascript");
            mimeTypeMap.put("json","application/json");
        }
    }

    /**
     * 根据链接创建本地目录以及文件
     * @param filePath 文件路径
     * @return
     * @throws IOException
     */
    public static File createLocalFile(String filePath) throws IOException{
        String dirs = filePath.substring(0,filePath.lastIndexOf("/"));
        File file = new File(dirs);
        if(!file.exists()) {
            file.mkdirs();
        }
        file = new File(filePath);
        if(!file.exists()) {
            file.createNewFile();
        }
        return file;
    }


}

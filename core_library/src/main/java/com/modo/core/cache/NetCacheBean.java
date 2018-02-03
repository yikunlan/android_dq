package com.modo.core.cache;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by HYK on 2018/1/15.
 * 网络缓存的实体类
 */

public class NetCacheBean implements Serializable{
    /**
     * 放网络的输入流
     */
    public InputStream inputStream;
    /**
     * 放response的header
     */
    public Map<String,String> headMap = new HashMap<String, String>();
}

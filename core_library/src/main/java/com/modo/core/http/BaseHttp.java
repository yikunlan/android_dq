package com.modo.core.http;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hyk on 2018/1/5.
 */

public class BaseHttp {
    private static OkHttpClient.Builder httpClient;

    @NonNull
    public static OkHttpClient getOkHttpClient(Context context) {
        context = context.getApplicationContext();
        //增加token
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder();
            Interceptor interceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Response response = null;
                    //不需要添加token
                    response = chain.proceed(request);
                    /*//添加token
                    response = chain.proceed(addTokent(request));*/
                    return response;
                }
            };
            httpClient.addInterceptor(interceptor);
        }

        return httpClient.build();
    }

    /**
     * 在请求里面添加token，具体字段要跟后台商量统一
     * @param request
     */
    public static Request addTokent(Request request){
        Request newRequest = request.newBuilder().header("accessToken", "needtoken")
                .header("referer","needReferer")
                .header("timestamp", "token过期的时间")
                .header("nonce", "生成之后的签名")
                .removeHeader("User-Agent")
                .build();
        return newRequest;
    }

    /**
     * 开始进行网络请求
     */
   /* public static void request(String emei,String sign){
        String BASE_URL = "http://apimodo.s1.natapp.cc/";
        *//*String BASE_URL = "https://api.douban.com/v2/movie/";*//*
        *//*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())//添加数据解析器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加访问处理适配器,Call类型是默认支持的(内部由DefaultCallAdapterFactory支持)，而如果要支持Observable，我们就需要自己添加RxJava2CallAdapterFactory.create()
                .client(httpClient.build())
                .build();*//*
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())//添加数据解析器
                .client(httpClient.build())
                .build();

        BaseService baseService = retrofit.create(BaseService.class);
        //调用方法得到一个Call
        Call<BaseJson<CoolpadConfig>> call;
        call = baseService.getCoolpadConfig(emei,sign);

        //进行网络请求
        call.enqueue(new Callback<BaseJson<CoolpadConfig>>() {
            @Override
            public void onResponse(Call<BaseJson<CoolpadConfig>> call, retrofit2.Response<BaseJson<CoolpadConfig>> response) {
                Log.e("TAG",response.toString());
            }

            @Override
            public void onFailure(Call<BaseJson<CoolpadConfig>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }*/

/*
    public static void okhttpPostRequest(final Context context, final String imei,final String sign)throws IOException{
           *//* RequestBody formBody = new FormEncodingBuilder()
                    .add("platform", "android")
                    .add("name", "bug")
                    .add("subject", "XXXXXXXXXXXXXXX")
                    .build();*//*


            new Thread(new Runnable() {
                @Override
                public void run() {
                    String BASE_URL = "http://apimodo.s1.natapp.cc/game/open/mdcl/c188/mdgid/15/?emei="+imei+"&sign="+sign;
                    Request request = new Request.Builder()
                            .url(BASE_URL)
                            .build();

                    Response response = null;
                    try{
                        response = getOkHttpClient(context).newCall(request).execute();

                        if (response.isSuccessful()) {
                                Log.e("TAG",response.body().string());
                        } else {
                                throw new IOException("Unexpected code " + response);
                        }

                    }catch (IOException e){
                        e.printStackTrace();
                    }

                   *//* try {
                        response = getOkHttpClient(context).newCall(request).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response.isSuccessful()) {
                        try {
                            Log.e("TAG",response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            throw new IOException("Unexpected code " + response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }*//*
                }
            }).start();

    }*/
}

package com.modo.core.javascript.reyun;

import android.content.Context;
import android.webkit.JavascriptInterface;


import com.reyun.sdk.TrackingIO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @author 沈雨
 * 调用热云SDK相关
 */
public class ReYunJavaScriptInterface{


    private static ReYunJavaScriptInterface reYunJavaScriptInterface;



    public static String PREFIX = "reyunAndroid";

    Context context;

    private ReYunJavaScriptInterface(Context c) {
        this.context= c;
    }

    public static ReYunJavaScriptInterface getInstance(Context c){
        if(reYunJavaScriptInterface == null){
            reYunJavaScriptInterface = new ReYunJavaScriptInterface(c);
        }
        return reYunJavaScriptInterface;
    }




    /**
     * 初始化方法
     * @param appKey 热云APPKey
     * @param channelId 渠道ID
     */
    @JavascriptInterface
    public String initWithKeyAndChannelId(String appKey,String channelId) {
        String res = "";
        try {
            TrackingIO.initWithKeyAndChannelId(context.getApplicationContext(), appKey, channelId);
            res = "success";
        } catch (Exception e) {
            e.printStackTrace();
            res = "error";
        }finally {
            return res;
        }
    }

    /**
     * 统计用户注册数据（首次进入服务器)
     * @param accountId 设定账号的唯一标识ID
     */
    @JavascriptInterface
    public String setRegisterWithAccountID(String accountId) {
        String res = "";
        try {
            TrackingIO.setRegisterWithAccountID(accountId);
            res = "success";
        } catch (Exception e) {
            e.printStackTrace();
            res = "error";
        }finally {
            return res;
        }
    }

    /**
     * 统计用户登陆数据（每次进入服务器）
     * @param accountId 设定账号的唯一标识ID
     */
    @JavascriptInterface
    public String setLoginSuccessBusiness(String accountId) {
        String res = "";
        try {
            TrackingIO.setLoginSuccessBusiness(accountId);
            res = "success";
        } catch (Exception e) {
            e.printStackTrace();
            res = "error";
        }finally {
            return res;
        }
    }


    /**
     * 统计用户开始充值数据</p>
     * 用户在应用中开始发起充值时调用，进行统计开始充值数据.
     * 当系统通过不同形式赠送虚拟货币给用户时，调用此方法来记录系统赠送的虚拟币数据，当paymentType为FREE时，系统认为是系统赠送的
     * @param transactionId 	交易的流水号，最长64个字符
     * @param paymentType       支付类型，例如支付宝(alipay)，银联(unionpay)，微信支付（weixinpay）,易宝支付（yeepay），最多16个字符，如果是系统赠送的，paymentType为：FREE
     * @param currencyType      货币类型，按照国际标准组织ISO 4217中规范的3位字母，例如CNY人民币、USD美金等
     * @param currencyAmountStr    支付的真实货币的金额，最长16个字符,人民币单位是元
     */
    @JavascriptInterface
    public String setPaymentStart(String transactionId, String paymentType, String  currencyType,String currencyAmountStr) {
        String res = "";
        try {
            Float currencyAmount = Float.parseFloat(currencyAmountStr);
            TrackingIO.setPaymentStart(transactionId,paymentType,currencyType,currencyAmount);
            res = "success";
        } catch (Exception e) {
            e.printStackTrace();
            res =  "error";
        }finally {
            //System.out.printf(res);
            return res;
        }
    }

    /**
     * 统计用户的充值成功数据
     * 用户在应用中充值成功后，进行统计充值数据，所有付费相关分析的数据报表均依赖此方法。
     * @param transactionId 	交易的流水号，最长64个字符
     * @param paymentType       支付类型，例如支付宝(alipay)，银联(unionpay)，微信支付（weixinpay）,易宝支付（yeepay），最多16个字符，如果是系统赠送的，paymentType为：FREE
     * @param currencyType      货币类型，按照国际标准组织ISO 4217中规范的3位字母，例如CNY人民币、USD美金等
     * @param currencyAmountStr    支付的真实货币的金额，最长16个字符,人民币单位是元
     */
    @JavascriptInterface
    public String setPayment(String transactionId, String paymentType, String  currencyType, String currencyAmountStr ){
        String res = "";
        try {
            Float currencyAmount = Float.parseFloat(currencyAmountStr);
            TrackingIO.setPayment(transactionId,paymentType,currencyType,currencyAmount);
            res =  "success";
        } catch (Exception e) {
            e.printStackTrace();
            res =  "error";
        }finally {
            //System.out.printf(res);
            return res;
        }
    }

    /**
     * 自定义事件
     * @param eventName 事件名称
     * @param extra     Extra参数的value目前支持字符串、数字、日期和布尔类型
     */
    @JavascriptInterface
    public String setEvent(String eventName, String extra){
        String res = "";
        try {
            JSONObject json = new JSONObject(extra);
            Map<String, Object> resultMap = new HashMap<String, Object>();
            Iterator<String> iter = json.keys();
            String key=null;
            Object value=null;
            while (iter.hasNext()) {
                key=iter.next();
                value=json.get(key);
                resultMap.put(key, value);
            }
            TrackingIO.setEvent(eventName,resultMap);
            res = "success";
        } catch (JSONException e) {
            e.printStackTrace();
            res =  "error";
        }finally {
            //System.out.printf(res);
            return res;
        }

    }



}

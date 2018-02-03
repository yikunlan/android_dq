package com.modo.core.javascript.progress;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;


import com.modo.core.widgets.MyProgressBar;

import java.util.ArrayList;
import java.util.List;


public class ProgressJavascriptInterface {

    public static String PREFIX = "progress";

    public static boolean isneedReload = true; // 是否需要重新加载

    private static ProgressJavascriptInterface progressJavascriptInterface;

    private MyProgressBar view;
    private List<String> mylist;
    private static int currentProgress;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (view.getCurrProgress() == 99) {
                view.setCurrProgress(1F);
                view.setTextmessage(mylist.get((int) (Math.random() * mylist.size())));
                 handler.postDelayed(runnable, 20);
            } else if (view.getCurrProgress() < currentProgress) {
                view.setCurrProgress(view.getCurrProgress() + 0.5F);
                if (view.getTextmessage() == null || "".equals(view.getTextmessage())) {
                    view.setTextmessage(mylist.get((int) (Math.random() * mylist.size())));
                }
                view.postInvalidate();
                 handler.postDelayed(runnable, 20);
            }
        }
    };;

    private ProgressJavascriptInterface(MyProgressBar view) {
        this.view = view;
    }

    public static ProgressJavascriptInterface getInstance(MyProgressBar view) {
        if (progressJavascriptInterface == null) {
            progressJavascriptInterface = new ProgressJavascriptInterface(view);
            progressJavascriptInterface.mylist = new ArrayList<>();
            progressJavascriptInterface.mylist.add("正在为您开启奇迹之路");
            progressJavascriptInterface.mylist.add("传承不朽，延续奇迹");
            progressJavascriptInterface.mylist.add("奇迹征途再度起航");
            progressJavascriptInterface.mylist.add("忆当年峥嵘岁月等你归来");
            progressJavascriptInterface.mylist.add("找回曾经热血盟战的激情");

        }
        return progressJavascriptInterface;
    }

    @JavascriptInterface
    public void currentProgress(String mcurrentProgress) {
        isneedReload = false;
        Log.i("currentProgress", mcurrentProgress);
        if (mcurrentProgress != null && !"".equals(mcurrentProgress) && !"undefined".equals(mcurrentProgress))
            currentProgress = Integer.parseInt(mcurrentProgress);
        setmCurrentProgress(currentProgress);
    }

    public void setmCurrentProgress(int mcurrentProgress) {
        currentProgress = mcurrentProgress;
        if (view.getCurrProgress() < 100 && currentProgress < 100) {
            handler.post(runnable);
        } else {
//            handler.removeCallbacks(runnable);
            view.post(new Runnable() {
                @Override
                public void run() {
                    if (view.getVisibility() != view.GONE) {
                        view.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}

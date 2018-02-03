package com.modo.core.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/1/17.
 */

public class ToastUtil {
    public static void showToast(Context context,String toast){
        Toast.makeText(context,toast,Toast.LENGTH_SHORT).show();
    }
}

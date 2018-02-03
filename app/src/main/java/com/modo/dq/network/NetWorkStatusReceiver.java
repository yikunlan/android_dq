package com.modo.dq.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.modo.core.util.NetUtil;
import com.modo.dq.X5WebviewActivity;


public class NetWorkStatusReceiver extends BroadcastReceiver {



    public NetWorkStatusReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//            Toast.makeText(context, "network changed", Toast.LENGTH_LONG).show();
            if (NetUtil.isNetworkConnected(context)) {
                X5WebviewActivity.x5WebviewActivity.refresh();
            } else {
                Toast.makeText(context,"网络连接不可用",Toast.LENGTH_SHORT).show();
            }


        }
    }
}

package com.modo.core.util.Auth;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于获取权限操作
 */
public class AuthorityInfo {

    public static final int REQUEST_CODE = 1;

    /**
     * 获取权限
     *
     * @param activity
     */
    public static boolean requestMultiplePermissions(Activity activity) {
        boolean isNeedNewPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            String[] permission = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
            List<String> permissionList = new ArrayList<>();
            for (int i = 0; i < permission.length; i++) {
                if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), permission[i]) != PackageManager.PERMISSION_GRANTED) {
                    //如果无权限
                    permissionList.add(permission[i]);
                }
            }
            if (permissionList.size() > 0) {
                isNeedNewPermission = true;
                String[] noPermission = new String[permissionList.size()];
                for (int k = 0; k < permissionList.size(); k++) {
                    noPermission[k] = permissionList.get(k);
                }

                activity.requestPermissions(noPermission, REQUEST_CODE);
            }
        }
        return isNeedNewPermission;
    }
}

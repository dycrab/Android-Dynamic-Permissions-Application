package com.example.callphone;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permission {
    public static String[] permissions;

    public int REQUEST_CODE = 1000;

    public List<String> permissionList = new ArrayList<>();

    public void setPermissions(String[] permissions) {
        Permission.permissions = permissions;
    }

    public void checkPermission(Activity activity){
        // 判断 SDK版本是否大于 API23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
            }
            if (permissionList.size() > 0) {
                requestPermission(activity);
            }
        }
    }

    public void requestPermission(Activity activity){
        Log.e("Permission","requestpermission");
        ActivityCompat.requestPermissions(activity,permissionList.toArray(new String[permissionList.size()]),REQUEST_CODE);
    }

}

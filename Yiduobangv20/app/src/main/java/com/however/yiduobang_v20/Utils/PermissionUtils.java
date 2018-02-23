package com.however.yiduobang_v20.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by tansibin on 2018/2/11.
 */

public class PermissionUtils {

    public static final String Permission_Location = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String Permission_Call = Manifest.permission.CALL_PHONE;
    public static final String Permission_Write = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String Permission_ReadPhone = Manifest.permission.READ_PHONE_STATE;

    public static final int REQUEST_CODE = 2;
    public static final int REQUEST_CALL_PHONE_CODE = 3;

    public static final String[] defaultPermissions = {
            Permission_Location, Permission_Call, Permission_Write, Permission_ReadPhone
    };

    /**
     * 检测权限
     *
     * @return true：已授权； false：未授权；
     */
    public static boolean checkPermission(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    /**
     * 请求权限
     */
    public static void requestPermission(Context context, String permission, int requestCode) {
        requestMorePermissions(context, new String[]{permission}, requestCode);
    }

    /**
     * 请求默认数组中的权限
     *
     * @param context
     * @param requestCode
     */
    public static void requestDefaultPermission(Context context, int requestCode) {
        requestMorePermissions(context, defaultPermissions, requestCode);
    }

    /**
     * 请求多个权限
     *
     * @param context
     * @param permissions
     * @param requestCode
     */
    public static void requestMorePermissions(Context context, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions((Activity) context, permissions, requestCode);
    }

    /**
     * 判断是否已拒绝过权限
     *
     * @return
     * @describe :如果应用之前请求过此权限但用户拒绝，此方法将返回 true;
     * -----------如果应用第一次请求权限或 用户在过去拒绝了权限请求，
     * -----------并在权限请求系统对话框中选择了 Don't ask again 选项，此方法将返回 false。
     */
    public static boolean judgePermission(Context context, String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission))
            return true;
        else
            return false;
    }
}

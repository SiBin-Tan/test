package com.however.yiduobang_v20.Utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by tansibin on 2018/1/10.
 */

public class ToastUtils {

    public static Context mContext = null;

    /**
     * 长时间系统提示
     *
     * @param msg
     */
    public static void showLongToast(String msg) {
        if (mContext != null)
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * 短时间系统提示
     *
     * @param msg
     */
    public static void showShortToast(String msg) {
        if (mContext != null)
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间系统提示，显示位置中间
     *
     * @param msg
     */
    public static void showLongToastCenter(String msg) {
        if (mContext != null) {
            Toast toast = Toast.makeText(mContext,msg,Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 短时间系统提示，显示位置中间
     *
     * @param msg
     */
    public static void showShortToastCenter(String msg) {
        if (mContext != null) {
            Toast toast = Toast.makeText(mContext,msg,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 显示系统提示
     *
     * @param context
     * @param msg
     * @param duration 时间
     * @param center   是否在中间 true 是 false 否
     */
    public static void showToast(Context context, String msg, int duration, boolean center) {
        Toast toast = Toast.makeText(context,msg,duration);
        if (center)
            toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

}

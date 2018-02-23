package com.however.yiduobang_v20.Utils;

import android.util.Log;

/**
 * Created by tansibin on 2018/1/21.
 */

public class LogUtils {

    public static boolean allowAll = true;
    public static boolean allowD = true;
    public static boolean allowE = true;
    public static boolean allowI = true;
    public static boolean allowV = true;
    public static boolean allowW = true;
    public static boolean allowWtf = true;

    /**
     * 获取访问本方法的类以及多少行，作为Tag
     * @return
     */
    public static String getTag(){
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        StringBuffer tag = new StringBuffer();
        tag.append(stackTraceElement.getClassName()+"--");
        tag.append(stackTraceElement.getLineNumber());
        return tag.toString();
    }

    public static void d(String msg){
        if (!(allowAll&&allowD))
            return;
        String tag = getTag();
        Log.d(tag,msg);
    }

    public static void e(String msg){
        if (!(allowAll&&allowE))
            return;
        String tag = getTag();
        Log.e(tag,msg);
    }

    public static void i(String msg){
        if (!(allowAll&&allowI))
            return;
        String tag = getTag();
        Log.i(tag,msg);
    }

    public static void v(String msg){
        if (!(allowAll&&allowV))
            return;
        String tag = getTag();
        Log.v(tag,msg);
    }

    public static void w(String msg){
        if (!(allowAll&&allowW))
            return;
        String tag = getTag();
        Log.w(tag,msg);
    }

    public static void wtf(String msg){
        if (!(allowAll&&allowWtf))
            return;
        String tag = getTag();
        Log.wtf(tag,msg);
    }


}

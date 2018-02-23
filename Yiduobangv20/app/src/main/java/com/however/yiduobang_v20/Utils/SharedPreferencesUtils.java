package com.however.yiduobang_v20.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tansibin on 2018/1/27.
 */

public class SharedPreferencesUtils {

    private static final String FILE_NAME = "share_date";

    private final static SharedPreferencesUtils instance = new SharedPreferencesUtils();

    private SharedPreferencesUtils() {
    }

    public static SharedPreferencesUtils getInstance() {
        return instance;
    }

    /**
     * 保存数据
     *
     * @param context
     * @param key
     * @param object  保存数据，同时也会分辨数据类型
     */
    public void saveParams(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String type = object.getClass().getSimpleName();
        if (type.equals("String")) {
            editor.putString(key, (String) object);
        } else if (type.equals("Integer")) {
            editor.putInt(key, (Integer) object);
        } else if (type.equals("Boolean")) {
            editor.putBoolean(key, (Boolean) object);
        } else if (type.equals("Float")) {
            editor.putFloat(key, (Float) object);
        } else if (type.equals("Long")) {
            editor.putLong(key, (Long) object);
        }
        editor.commit();
    }

    /**
     * 获取数据
     *
     * @param context
     * @param key
     * @param object  获取的数据类型，同时也是默认值
     * @return
     */
    public Object getParams(Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String type = object.getClass().getSimpleName();
        if (type.equals("String"))
            return sp.getString(key, (String) object);
        else if (type.equals("Integer"))
            return sp.getInt(key, (Integer) object);
        else if (type.equals("Boolean"))
            return sp.getBoolean(key, (Boolean) object);
        else if (type.equals("Float"))
            return sp.getFloat(key, (Float) object);
        else if (type.equals("Long"))
            return sp.getLong(key, (Long) object);
        return null;
    }

    /**
     * 清除数据
     *
     * @param context
     * @param key
     */
    public void removeParams(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除数据
     * @param context
     * @param keys
     */
    public void removeParams(Context context, String[] keys) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        for (String key : keys)
            editor.remove(key);
        editor.commit();
    }
}

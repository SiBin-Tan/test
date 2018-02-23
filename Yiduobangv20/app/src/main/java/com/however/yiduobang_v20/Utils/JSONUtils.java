package com.however.yiduobang_v20.Utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tansibin on 2018/1/10.
 */

public class JSONUtils {

    private static final String TAG = "JSONUtils";

    private static final String JSON_ERROR = "json错误";

    /**
     * 获取int类型数据
     * @param content jsonStr
     * @param key
     * @param initial 如果没找到key的默认值
     * @return
     */
    public static int getInt(String content, String key, int initial) {
        int msg = initial;
        try {
            JSONObject jsonObject = new JSONObject(content);
            if (!jsonObject.isNull(key))
                msg = jsonObject.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, JSON_ERROR);
        } finally {
            return msg;
        }
    }

    public static String getString(String content, String key, String initial) {
        String msg = initial;
        try {
            JSONObject jsonObject = new JSONObject(content);
            if (!jsonObject.isNull(key))
                msg = jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, JSON_ERROR);
        } finally {
            return msg;
        }
    }

    public static Boolean getBoolean(String content, String key, boolean initial) {
        boolean msg = initial;
        try {
            JSONObject jsonObject = new JSONObject(content);
            if (!jsonObject.isNull(key))
                msg = jsonObject.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, JSON_ERROR);
        } finally {
            return msg;
        }
    }

    public static double getDouble(String content, String key, double initial) {
        double msg = initial;
        try {
            JSONObject jsonObject = new JSONObject(content);
            if (!jsonObject.isNull(key))
                msg = jsonObject.getDouble(key);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, JSON_ERROR);
        } finally {
            return msg;
        }
    }

    public static Long getString(String content, String key, long initial) {
        long msg = initial;
        try {
            JSONObject jsonObject = new JSONObject(content);
            if (!jsonObject.isNull(key))
                msg = jsonObject.getLong(key);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, JSON_ERROR);
        } finally {
            return msg;
        }
    }
}

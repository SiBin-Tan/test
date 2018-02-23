package com.however.yiduobang_v20.Utils;

import com.google.gson.Gson;
import com.however.yiduobang_v20.Controller.BaseController;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by tansibin on 2018/1/21.
 */

public class OkHttpUtils {

    public final static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");

    private OkHttpClient okHttpClient = null;

    private CallBack callBack;

    private Request.Builder builder;

    private int requestCode;

    private static final OkHttpUtils instance = new OkHttpUtils();

    private OkHttpUtils() {
        okHttpClient = new OkHttpClient();
    }

    public static OkHttpUtils getInstance() {
        return instance;
    }

    public interface CallBack {
        //成功
        void onSuccess(int requestCode, String content);

        //失败
        void onFailure(int requestCode, int errorCode, String errorMsg);

        //服务器失败
        void onServersFailure(int requestCode);
    }

    /**
     * 开始访问
     */
    public void execute() {
        okHttpClient.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callBack != null)
                    callBack.onServersFailure(requestCode);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callBack != null) {
                    String content = response.body().string();
                    int code = JSONUtils.getInt(content, "code", -1);
                    String msg = JSONUtils.getString(content, "msg", "");

                    //模拟数据
                    //取消订单数据
                    if (requestCode == BaseController.CANCEL_ORDER_CODE){
                        code = 2;
                    }
                    //订单来袭数据
                    Gson gson = new Gson();
                    if (requestCode == BaseController.START_ORDER_CODE ||
                            requestCode == BaseController.GET_ORDER_CODE){
                        BaseController.TestModel model = new BaseController.TestModel();
                        code = 2;
                        msg = gson.toJson(model);
                    }
                    //接受订单数据
                    if (requestCode == BaseController.RECEIVE_ORDER_CODE){
                        BaseController.TestModel model = new BaseController.TestModel();
                        model.orderState = "已接单";
                        code = 2;
                        msg = gson.toJson(model);
                    }
                    //发送账单数据
                    if (requestCode == BaseController.SEND_ORDER_CODE){
                        BaseController.TestModel model = new BaseController.TestModel();
                        model.orderState = "等待客户付款";
                        code = 2;
                        msg = gson.toJson(model);
                    }
                    if (requestCode == BaseController.SEND_CHANGE_MONEY_CODE){
                        BaseController.TestModel model = new BaseController.TestModel();
                        int x = (int)(Math.random()*100);
                        if (x%2 ==0) {
                            model.changeResult = true;
                            code = 2;
                        }else {
                            model.changeResult = false;
                            code = 1;
                        }
                        msg = gson.toJson(model);
                    }
                    //模拟数据结束

                    if (requestCode == BaseController.GET_HISTORY_CODE){
                        msg = JSONUtils.getString(content,"results","");
                        code = 2;
                    }
                    if (code == 2) {
                        callBack.onSuccess(requestCode, msg);
                    } else {
                        callBack.onFailure(requestCode, code, msg);
                    }
                }
            }
        });
    }

    /**
     * 给请求添加头部
     *
     * @param headerMap
     */
    public void setRequsetHeader(Map<String, String> headerMap) {
        if (headerMap != null) {
            for (String key : headerMap.keySet()) {
                builder.addHeader(key, headerMap.get(key));
            }
        }
    }

    /**
     * 不带参数get请求
     *
     * @param requestCode
     * @param url
     * @param callBack
     */
    public void get(int requestCode, String url, CallBack callBack) {
        get(requestCode, url, null, null, callBack);
    }

    /**
     * 带参数get请求
     *
     * @param requestCode
     * @param url
     * @param paramsMap
     * @param callBack
     */
    public void get(int requestCode, String url, Map<String, String> paramsMap, CallBack callBack) {
        get(requestCode, url, paramsMap, null, callBack);
    }

    /**
     * 带参数和头部get请求
     *
     * @param requestCode
     * @param url
     * @param paramsMap
     * @param headerMap
     * @param callBack
     */
    public void get(int requestCode, String url, Map<String, String> paramsMap, Map<String, String> headerMap, CallBack callBack) {
        this.requestCode = requestCode;
        this.callBack = callBack;
        builder = new Request.Builder();
        url = url + "?";
        if (paramsMap != null) {
            for (String key : paramsMap.keySet())
                url = url + key + "=" + paramsMap.get(key) + "&";
            url = url.substring(0, url.length() - 1);
        }
        builder.url(url);
        setRequsetHeader(headerMap);
        execute();
    }

    /**
     * json类型数据post请求
     *
     * @param requestCode
     * @param url
     * @param jsonStr
     * @param callBack
     */
    public void postJson(int requestCode, String url, String jsonStr, CallBack callBack) {
        postJson(requestCode, url, jsonStr, null, callBack);
    }

    /**
     * json类型数据带头部post 请求
     *
     * @param requestCode
     * @param url
     * @param jsonStr
     * @param headerMap
     * @param callBack
     */
    public void postJson(int requestCode, String url, String jsonStr, Map<String, String> headerMap, CallBack callBack) {
        this.requestCode = requestCode;
        this.callBack = callBack;
        builder = new Request.Builder();
        builder.url(url);
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, jsonStr);
        builder.post(body);
        setRequsetHeader(headerMap);
        execute();
    }

    /**
     * 不带参数post请求
     *
     * @param requestCode
     * @param url
     * @param callBack
     */
    public void post(int requestCode, String url, CallBack callBack) {
        post(requestCode, url, null, null, callBack);
    }

    /**
     * 带参数post请求
     *
     * @param requestCode
     * @param url
     * @param headerMap
     * @param callBack
     */
    public void post(int requestCode, String url, Map<String, String> headerMap, CallBack callBack) {
        post(requestCode, url, headerMap, null, callBack);
    }

    /**
     * 带参数和头部的post请求
     *
     * @param requestCode
     * @param url
     * @param paramsMap
     * @param headerMap
     * @param callBack
     */
    public void post(int requestCode, String url,  Map<String, String> headerMap, Map<String, String> paramsMap,CallBack callBack) {
        this.requestCode = requestCode;
        this.callBack = callBack;
        builder = new Request.Builder();
        builder.url(url);
        FormBody.Builder formBody = new FormBody.Builder();
        if (paramsMap != null) {
            for (String key : paramsMap.keySet()) {
                formBody.add(key, paramsMap.get(key));
            }
        }
        builder.post(formBody.build());
        setRequsetHeader(headerMap);
        execute();
    }
}

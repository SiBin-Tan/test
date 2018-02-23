package com.however.yiduobang_v20.Controller;

import android.util.Log;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.google.gson.Gson;
import com.however.yiduobang_v20.Model.ErrorModel;
import com.however.yiduobang_v20.Model.RequestModel;
import com.however.yiduobang_v20.Model.ResponseModel;
import com.however.yiduobang_v20.Utils.JSONUtils;
import com.however.yiduobang_v20.Utils.LogUtils;
import com.however.yiduobang_v20.Utils.MyUtils;
import com.however.yiduobang_v20.Utils.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tansibin on 2018/1/24.
 */

public class BaseController implements OkHttpUtils.CallBack {

    public final String serverUrl = "http://api.iotio.org";

    public static final String PHONE = "token";
    public static final String TOKEN = "token";
    public static final String JWT = "jwt";
    public static final String INI = "ini";
    public static final String APP_PHONE = "app_phone";
    public static final String START = "start";
    public static final String END = "end";
    public static final String GEO = "geo";
    public static final String FL = "fl";
    public static final String CITY = "city";


    public static final int SERVERS_ERROR_CODE = 1000;
    public static final int SEND_CODE_CODE = 1001;
    public static final int USER_LOGIN_CODE = 1002;
    public static final int TOKEN_LOGIN_CODE = 1003;
    public static final int GET_ORDER_CODE = 1004;
    public static final int RECEIVE_ORDER_CODE = 1005;
    public static final int START_ORDER_CODE = 1006;
    public static final int SEND_CHANGE_MONEY_CODE = 1007;
    public static final int CANCEL_ORDER_CODE = 1008;
    public static final int SEND_ORDER_CODE = 1009;
    public static final int GET_HISTORY_CODE = 1010;

    public Gson gson;

    public RequestModel requestModel;

    public Map<String, String> headerMap = new HashMap<>();

    public BaseController() {
        gson = new Gson();
    }

    /**
     * token登录
     *
     * @param ini
     * @param token
     * @param jwt
     */
    public void tokenLogin(String ini, String token, String jwt) {
        String url = serverUrl + "/login/tokenlogin";
        headerMap.clear();
        headerMap.put(TOKEN, token);
        headerMap.put(JWT, jwt);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(INI, ini);
        OkHttpUtils.getInstance().get(TOKEN_LOGIN_CODE, url, paramsMap, headerMap, this);
    }

    /**
     * token登录成功
     *
     * @param requestCode
     * @param content
     */
    public void tokenLoginResponseSuccess(int requestCode, String content) {
        ResponseModel model = gson.fromJson(content, ResponseModel.class);
        model.requestCode = requestCode;
        EventBus.getDefault().post(model);
    }

    /**
     * 发送验证码
     *
     * @param phone
     */
    public void sendCode(String phone) {
        String url = serverUrl + "/login/send";
        requestModel = new RequestModel();
        requestModel.phone = phone;
        String jsonStr = gson.toJson(requestModel);
        OkHttpUtils.getInstance().postJson(SEND_CODE_CODE, url, jsonStr, this);
    }

    /**
     * 验证码发送成功
     *
     * @param requestCode
     * @param content
     */
    public void sendCodeResponseSuccess(int requestCode, String content) {
        ResponseModel model = new ResponseModel();
        model.requestCode = requestCode;
        model.msg = content;
        EventBus.getDefault().post(model);
    }

    /**
     * 用户登录
     *
     * @param phone
     * @param code
     * @param ini
     * @param token
     */
    public void userLogin(String phone, String code, String ini, String token) {
        String url = serverUrl + "/login/verify";
        headerMap.clear();
        headerMap.put(TOKEN, token);
        requestModel = new RequestModel();
        requestModel.phone = phone;
        requestModel.code = code;
        requestModel.ini = ini;
        String jsonStr = gson.toJson(requestModel);
        OkHttpUtils.getInstance().postJson(USER_LOGIN_CODE, url, jsonStr, headerMap, this);
    }

    /**
     * 用户登录成功
     *
     * @param requestCode
     * @param content
     */
    public void userLoginResponseSuccess(int requestCode, String content) {
        if (content.equals(""))
            return;
        ResponseModel model = gson.fromJson(content, ResponseModel.class);
        model.requestCode = requestCode;
        EventBus.getDefault().post(model);
    }

    /**
     * 获取订单信息
     *
     * @param token
     * @param jwt
     */
    public void getOrder(int requesCode, String token, String jwt) {
        String url = serverUrl + "/order/app";
        headerMap.clear();
        headerMap.put(TOKEN, token);
        headerMap.put(JWT, jwt);
        OkHttpUtils.getInstance().get(requesCode, url, null, headerMap, this);
    }

    public void getOrderResponseSuccess(int reuqestCode, String content) {
        ResponseModel model = gson.fromJson(content, ResponseModel.class);
        model.requestCode = reuqestCode;
        EventBus.getDefault().post(model);
    }

    /**
     * 获取历史订单数据
     *
     * @param phone
     * @param start
     * @param end
     */
    public void getHistoryData(String phone, String start, String end) {
        String url = serverUrl + "/order/history";
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(APP_PHONE, phone);
        paramsMap.put(START, start);
        paramsMap.put(END, end);
        OkHttpUtils.getInstance().get(GET_HISTORY_CODE, url, paramsMap, this);
    }

    public void getHistoryResponseSuccess(int reuqestCode, String content) {
        EventBus.getDefault().post(content);
    }

    /**
     * 开始接单(模拟)
     *
     * @param token
     * @param jwt
     */
    public void startOrder(String token, String jwt) {
        getOrder(START_ORDER_CODE, token, jwt);
    }

    public void startOrderSuccess(int requestCode, String content) {
        ResponseModel model = gson.fromJson(content, ResponseModel.class);
        model.requestCode = requestCode;
        EventBus.getDefault().post(model);
    }

    /**
     * 取消订单(模拟)
     *
     * @param token
     * @param jwt
     */
    public void cancelOrder(String token, String jwt) {
        getOrder(CANCEL_ORDER_CODE, token, jwt);
    }

    public void cancelOrderSuccess(int requestCode, String content) {
        ResponseModel model = new ResponseModel();
        model.requestCode = requestCode;
        model.msg = content;
        EventBus.getDefault().post(model);
    }

    /**
     * 接受订单(模拟)
     *
     * @param token
     * @param jwt
     */
    public void receiveOrder(String token, String jwt) {
        getOrder(RECEIVE_ORDER_CODE, token, jwt);
    }

    public void receiveOrderSuccess(int requestCode, String content) {
        ResponseModel model = new ResponseModel();
        model.requestCode = requestCode;
        model.msg = content;
        EventBus.getDefault().post(model);
    }

    /**
     * 发送账单(模拟)
     *
     * @param token
     * @param jwt
     */
    public void sendOrder(String token, String jwt) {
        getOrder(SEND_ORDER_CODE, token, jwt);
    }

    public void sendOrderSuccess(int requestCode, String content) {
        ResponseModel model = new ResponseModel();
        model.requestCode = requestCode;
        model.msg = content;
        EventBus.getDefault().post(model);
    }

    /**
     * 发送变更金额(模拟)
     * @param token
     * @param jwt
     */
    public void sendChangeMoney(String token,String jwt){
        getOrder(SEND_CHANGE_MONEY_CODE,token,jwt);
    }

    public void sendChangeMoneySuccess(int requestCode, String content){
        ResponseModel model = new ResponseModel();
        model.requestCode = requestCode;
        model.msg = content;
        EventBus.getDefault().post(model);
    }

    @Override
    public void onSuccess(int requestCode, String content) {
        switch (requestCode) {
            case SEND_CODE_CODE:
                sendCodeResponseSuccess(requestCode, content);
                break;
            case USER_LOGIN_CODE:
                userLoginResponseSuccess(requestCode, content);
                break;
            case TOKEN_LOGIN_CODE:
                tokenLoginResponseSuccess(requestCode, content);
                break;
            case GET_ORDER_CODE:
                getOrderResponseSuccess(requestCode, content);
                break;
            case GET_HISTORY_CODE:
                getHistoryResponseSuccess(requestCode, content);
                break;
            case CANCEL_ORDER_CODE:
                cancelOrderSuccess(requestCode, content);
                break;
            case RECEIVE_ORDER_CODE:
                receiveOrderSuccess(requestCode, content);
                break;
            case START_ORDER_CODE:
                startOrderSuccess(requestCode, content);
                break;
            case SEND_ORDER_CODE:
                sendOrderSuccess(requestCode, content);
                break;
            case SEND_CHANGE_MONEY_CODE:
                sendChangeMoneySuccess(requestCode, content);
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int errorCode, String errorMsg) {
        ErrorModel errorModel = new ErrorModel();
        errorModel.requestCode = requestCode;
        errorModel.errorCode = errorCode;
        errorModel.errorMsg = errorMsg;
        EventBus.getDefault().post(errorModel);
    }

    @Override
    public void onServersFailure(int requestCode) {
        ErrorModel errorModel = new ErrorModel();
        errorModel.requestCode = requestCode;
        errorModel.errorCode = SERVERS_ERROR_CODE;
        EventBus.getDefault().post(errorModel);
    }

    //模拟数据
    public static class TestModel {
        public String orderTime = "时间:未模拟";
        public double orderLat = 27.625777;
        public double orderLng = 113.8555;
        public String orderAddress = "雅天大厦3楼303";
        public String orderMoney = "60";
        public String orderPhone = "18720757019";
        public String orderState = "未接单";
        public boolean changeResult = false;
    }
}

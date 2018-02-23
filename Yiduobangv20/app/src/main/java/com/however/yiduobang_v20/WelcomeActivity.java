package com.however.yiduobang_v20;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.SaveCallback;
import com.however.yiduobang_v20.Base.BaseActivity;
import com.however.yiduobang_v20.Controller.BaseController;
import com.however.yiduobang_v20.Model.ErrorModel;
import com.however.yiduobang_v20.Model.ResponseModel;
import com.however.yiduobang_v20.Utils.LogUtils;
import com.however.yiduobang_v20.Utils.PermissionUtils;
import com.however.yiduobang_v20.Utils.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tansibin on 2018/1/20.
 */

public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.tvCompany)
    TextView tvCompany;

    private int delayTime = 1000;

    private int isFinish = 0;

    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initLeanCloud();
        delayFinish();
        tokenLogin();
        requestDefaultPermissions();

        Map<String,String> map = new HashMap<>();
        map.clear();
    }

    /**
     * 延时销毁
     */
    private void delayFinish() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isFinish();
            }
        }, delayTime);
    }

    /**
     * 初始化LeanCloud通讯包
     */
    private void initLeanCloud() {
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    LogUtils.e("推送ID：" + installationId);
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                    LogUtils.e("推送ID：保存失败");
                }
            }
        });
    }

    private void requestDefaultPermissions(){
        PermissionUtils.requestDefaultPermission(this,PermissionUtils.REQUEST_CODE);
    }

    /**
     * token登录
     */
    private void tokenLogin() {
        if (getDeviceId() == ""){
            startLoginActivity();
        }else{
            String jwt = (String) SharedPreferencesUtils.getInstance().getParams(this, BaseController.JWT, "");
            if (jwt.equals("")) {
                startLoginActivity();
            } else {
                String ini = AVInstallation.getCurrentInstallation().getInstallationId();
                baseController.tokenLogin(ini, getDeviceId(), jwt);
            }
        }
    }

    /**
     * 是否销毁，等待操作完成
     */
    private void isFinish() {
        isFinish++;
        if (isFinish == 3) {
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        }
    }

    /**
     * 跳往LoginActivity
     */
    private void startLoginActivity() {
        intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        intent.putExtra(BaseController.TOKEN, getDeviceId());
        isFinish();
    }

    /**
     * 跳往MainActivity
     */
    private void startMainActivity(ResponseModel model) {
        model.token = getDeviceId();
        String jwt = (String) SharedPreferencesUtils.getInstance().getParams(this, BaseController.JWT, "");;
        model.jwt = jwt;
        intent = new Intent(WelcomeActivity.this, MainActivity.class);
        String jsonStr = gson.toJson(model);
        intent.putExtra(KEY, jsonStr);
        isFinish();
    }

    @SuppressLint("MissingPermission")
    public String getDeviceId() {
        if (PermissionUtils.checkPermission(this,PermissionUtils.Permission_ReadPhone)) {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        }else
            return "";
    }

    /**
     * EventBus 接收方法，接收服务器访问成功相关
     *
     * @param model
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseSuccess(ResponseModel model) {
        if (model.requestCode == BaseController.TOKEN_LOGIN_CODE)
            startMainActivity(model);
    }

    /**
     * EventBus 接收方法，接收服务器访问失败相关
     *
     * @param model
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseFailure(ErrorModel model) {
        if (model.requestCode == BaseController.TOKEN_LOGIN_CODE)
            startLoginActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.REQUEST_CODE)
            isFinish();
    }
}

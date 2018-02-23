package com.however.yiduobang_v20;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVInstallation;
import com.however.yiduobang_v20.Base.BaseActivity;
import com.however.yiduobang_v20.Controller.BaseController;
import com.however.yiduobang_v20.Model.ErrorModel;
import com.however.yiduobang_v20.Model.IntentModel;
import com.however.yiduobang_v20.Model.ResponseModel;
import com.however.yiduobang_v20.Utils.SharedPreferencesUtils;
import com.however.yiduobang_v20.Utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by tansibin on 2018/1/20.
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.editPhone)
    EditText editPhone;
    @BindView(R.id.editPassword)
    EditText editPassword;
    @BindView(R.id.btnSend)
    Button btnSend;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindColor(R.color.tvGray)
    int colorGray;
    @BindColor(R.color.appTheme)
    int colorRed;

    private int SEND_CODE_YES = 1;
    private int SEND_CODE_NO = 0;
    private int SEND_CODE_STATE = SEND_CODE_NO;

    private String token;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        getIntentData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.btnSend, R.id.btnLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSend:
                sendCode();
                break;
            case R.id.btnLogin:
                userLogin();
                break;
        }
    }

    /**
     * 获取Intent中数据
     */
    private void getIntentData(){
        Bundle bundle = this.getIntent().getExtras();
        token = bundle.getString(BaseController.TOKEN);
    }

    /**
     * 初始化view相关
     */
    private void initView() {
        editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.toString().length();
                if (length < 11) {
                    if (SEND_CODE_STATE == SEND_CODE_NO)
                        btnSend.setTextColor(colorGray);
                } else if (length == 11) {
                    if (SEND_CODE_STATE == SEND_CODE_NO)
                        btnSend.setTextColor(colorRed);
                } else
                    editPhone.getText().delete(11, 12);

            }
        });
        String phone = getPhoneFromLocal();
        if (!phone.equals(""))
            editPhone.setText(phone);
    }

    /**
     * 发送验证码
     */
    private void sendCode() {
        String phone = editPhone.getText().toString();
        if (phone.length() == 11 && SEND_CODE_STATE == SEND_CODE_NO) {
            baseController.sendCode(phone);
        }
    }

    /**
     * 用户登录
     */
    private void userLogin() {
        String phone = editPhone.getText().toString();
        String password = editPassword.getText().toString();
        if (phone.length() != 11) {
            ToastUtils.showLongToast("请输入正确手机号");
            return;
        }
        if (password == null || password.equals("") || password.isEmpty()) {
            ToastUtils.showLongToast("请输入正确验证码");
            return;
        }
        String ini = AVInstallation.getCurrentInstallation().getInstallationId();
        baseController.userLogin(phone, password, ini, token);
        loadingDialog.show();
    }

    /**
     * 开始倒计时（验证码发送成功后）
     */
    private void startCountDown() {
        btnSend.setTextColor(colorGray);
        CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String result = String.valueOf(millisUntilFinished).substring(0, 2);
                if (String.valueOf(millisUntilFinished).length() < 5)
                    result = String.valueOf(millisUntilFinished).substring(0, 1);
                btnSend.setText(result + "S");
            }

            @Override
            public void onFinish() {
                btnSend.setText("发送验证码");
                int length = editPhone.getText().length();
                if (length == 11)
                    btnSend.setTextColor(colorRed);
                else
                    btnSend.setTextColor(colorGray);
                SEND_CODE_STATE = SEND_CODE_NO;
            }
        };
        countDownTimer.start();
        SEND_CODE_STATE = SEND_CODE_YES;
    }

    /**
     * 打开MainActivity
     */
    private void startMainActivity(ResponseModel model) {
        model.token = token;
        String jsonStr = gson.toJson(model);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(KEY,jsonStr);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
    }

    /**
     * 保存登录成功的手机号到本地
     * @param phone
     */
    private void saveDataToLocal(String phone,String jwt){
        SharedPreferencesUtils.getInstance().saveParams(this,BaseController.PHONE,phone);
        SharedPreferencesUtils.getInstance().saveParams(this,BaseController.JWT,jwt);

    }

    /**
     * 从本地数据获取登录过的手机号
     * @return
     */
    private String getPhoneFromLocal(){
        return (String) SharedPreferencesUtils.getInstance().getParams(
                this,BaseController.PHONE,"");
    }

    /**
     * EventBus 接收方法，接收服务器访问成功相关
     * @param model
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseSuccess(ResponseModel model) {
        cancelLoadingDialog();
        switch (model.requestCode) {
            case BaseController.SEND_CODE_CODE:
                startCountDown();
                ToastUtils.showLongToast(model.msg);
                break;
            case BaseController.USER_LOGIN_CODE:
                ToastUtils.showLongToast("登录成功");
                saveDataToLocal(model.phone,model.jwt);
                startMainActivity(model);
                break;
        }
    }

    /**
     * EventBus 接收方法，接收服务器访问失败相关
     * @param model
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResponseFailure(ErrorModel model) {
        cancelLoadingDialog();
        if (model.errorCode == BaseController.SERVERS_ERROR_CODE) {
            //服务器错误
            ToastUtils.showLongToast("服务器连接超时，请检查网络状况!");
        }
        switch (model.requestCode) {
            case BaseController.SEND_CODE_CODE:
                ToastUtils.showLongToast(model.errorMsg);
                break;
            case BaseController.USER_LOGIN_CODE:
                ToastUtils.showLongToast(model.errorMsg);
                break;
        }
    }
}

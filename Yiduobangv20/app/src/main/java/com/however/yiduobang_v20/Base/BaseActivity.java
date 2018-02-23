package com.however.yiduobang_v20.Base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.however.yiduobang_v20.Controller.BaseController;
import com.however.yiduobang_v20.Dialog.LoadingDialog;
import com.however.yiduobang_v20.Dialog.MoneyDialog;
import com.however.yiduobang_v20.Dialog.MyDialog;
import com.however.yiduobang_v20.Utils.PermissionUtils;
import com.however.yiduobang_v20.Utils.SystemUtil;
import com.however.yiduobang_v20.Utils.ToastUtils;

import butterknife.ButterKnife;

/**
 * Created by tansibin on 2018/1/20.
 */

public class BaseActivity extends AppCompatActivity {

    protected final String KEY = "activity";

    protected Handler handler;
    protected BaseController baseController;
    protected LoadingDialog loadingDialog;
    protected Gson gson;

    private MyDialog permissionDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        baseController = new BaseController();
        loadingDialog = new LoadingDialog(this);
        gson = new Gson();
    }

    /**
     * 取消加载中Dialog
     */
    protected void cancelLoadingDialog() {
        if (loadingDialog.getIsShow())
            loadingDialog.cancel();
    }

    protected void showPermissionDialog(final String permission) {
        if (permissionDialog == null) {
            permissionDialog = new MyDialog(this);
            permissionDialog.setTvTitle("应用缺少权限");
            permissionDialog.setBtnCancel(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    permissionDialog.cancel();
                }
            });

        }
        String msg;
        switch (permission) {
            case PermissionUtils.Permission_Location:
                msg = "应用缺少'定位'权限，部分功能无法使用，请开启'定位'权限";
                break;
            case PermissionUtils.Permission_Call:
                msg = "应用缺少'电话'权限，部分功能无法使用，请开启'电话'权限";
                break;
            default:
                msg = "应用出错,请取消";
                break;
        }
        permissionDialog.setTvMsg(msg);
        permissionDialog.setBtnConfirm(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PermissionUtils.judgePermission(BaseActivity.this, permission)) {
                    PermissionUtils.requestPermission(BaseActivity.this,
                            permission, PermissionUtils.REQUEST_CODE);
                } else {
                    SystemUtil.openApplicationDetails(BaseActivity.this, getPackageName());
                }
                permissionDialog.cancel();
            }
        });

        permissionDialog.show();
    }


}

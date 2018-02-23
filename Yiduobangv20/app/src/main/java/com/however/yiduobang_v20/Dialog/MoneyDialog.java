package com.however.yiduobang_v20.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.however.yiduobang_v20.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tansibin on 2017/7/3.
 */

public abstract class MoneyDialog extends Dialog implements View.OnClickListener {


    private final String error_point = "小数点后最多保留一位";
    private final String error_notZero = "金额开头不能为零";
    private final String error_notPoint = "金额开头不能为小数点";
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.btnCancel)
    Button btnCancel;
    @BindView(R.id.btnConfirm)
    Button btnConfirm;

    private String title = "变更金额";
    private String btnConfirmMsg = "确定变更";
    private String btnCancelMsg = "取消";


    private int point = 0;
    private int moneyLength = 0;

    private Context context;

    public MoneyDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_dialog);
        ButterKnife.bind(this);

        WindowManager m = ((Activity)context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.85); // 宽度设置为屏幕的0.65
        getWindow().setAttributes(p);

        initView();
    }

    private void initView() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0 && s.toString().equals("0")) {
                    editText.getText().delete(0, 1);
                    return;
                }
                if (start == 0 && s.toString().equals(".")) {
                    editText.getText().delete(0, 1);
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int a = s.length();
                if (point != 0) {
                    if (a < point)
                        point = 0;
                    if (a > point + 1)
                        editText.getText().delete(a - 1, a);
                } else {
                    if (a < 1)
                        return;
                    if (s.toString().substring(a - 1, a).equals(".")) {
                        point = a;
                    }
                    if (a > 4 && point == 0) {
                        s.delete(a - 1, a);
                    }
                }
            }
        });

        tvTitle.setText(title);
        btnConfirm.setText(btnConfirmMsg);
        btnConfirm.setOnClickListener(this);
        btnCancel.setText(btnCancelMsg);
        btnCancel.setOnClickListener(this);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setBtnConfirm(String btnConfirmMsg) {
        this.btnConfirmMsg = btnConfirmMsg;
    }

    public void setBtnCancel(String btnCancelMsg) {
        this.btnCancelMsg = btnCancelMsg;
    }

    public int getMoneyLength() {
        return moneyLength;
    }

    private void clearEdit() {
        editText.setText("");
        point = 0;
    }


    @Override
    public void dismiss() {
        clearEdit();
        super.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancel:
                onListener(false,"");
                this.dismiss();
                break;
            case R.id.btnConfirm:
                onListener(true,editText.getText().toString());
                clearEdit();
                break;
        }
    }

    public abstract void onListener(boolean is,String msg);
}

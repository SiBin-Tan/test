package com.however.yiduobang_v20.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by tansibin on 2018/2/23.
 */

public class MyDialog extends Dialog {

    private int tvGray = Color.parseColor("#999999");
    private int touchColor = Color.parseColor("#e9e9ea");

    private Context context;
    private LinearLayout lineMain;
    private TextView tvTitle;
    private TextView tvMsg;
    private Button btnConfirm;
    private Button btnCancel;

    private int tvTitleSize = 18;
    private int tvMsgSize = 16;
    private int btnConfirmMsgSize = 14;
    private int btnCancelMsgSize = 14;

    private boolean show = false;


    public MyDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public MyDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lineMain = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        lineMain.setLayoutParams(layoutParams);
        lineMain.setOrientation(LinearLayout.VERTICAL);
        lineMain.setBackgroundColor(Color.WHITE);

        setContentView(lineMain);

        WindowManager m = ((Activity)context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.85); // 宽度设置为屏幕的0.65
        getWindow().setAttributes(p);

        initView();
    }


    private void initView() {
        if (tvTitle != null)
            lineMain.addView(tvTitle);
        if (tvMsg != null)
            lineMain.addView(tvMsg);
        if (btnConfirm == null && btnCancel == null)
            return;
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dip2px(50));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(layoutParams);
        if (btnCancel != null)
            linearLayout.addView(btnCancel);
        if (btnConfirm != null)
            linearLayout.addView(btnConfirm);
        lineMain.addView(linearLayout);
    }

    public void setTvTitle(String title) {
        if (tvTitle == null) {
            tvTitle = new TextView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            tvTitle.setLayoutParams(layoutParams);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, tvTitleSize);
            tvTitle.setTextColor(Color.BLACK);
            int padding = 20;
            tvTitle.setPadding(0, dip2px(padding), 0, dip2px(padding));
            tvTitle.setGravity(Gravity.CENTER);
        }
        tvTitle.setText(title);
    }

    public void setTvMsg(String msg) {
        if (tvMsg == null) {
            tvMsg = new TextView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            tvMsg.setLayoutParams(layoutParams);
            tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, tvMsgSize);
            tvMsg.setTextColor(Color.BLACK);
            int padding = dip2px(20);
            tvMsg.setPadding(padding, 0, padding, padding);
            tvMsg.setGravity(Gravity.CENTER);
        }
        tvMsg.setText(msg);
    }

    public void setBtnConfirm(View.OnClickListener onClickListener){
        String  msg = "立即申请";
        setBtnConfirm(msg,onClickListener);
    };

    public void setBtnConfirm(String msg, View.OnClickListener onClickListener) {
        if (btnConfirm == null) {
            btnConfirm = new Button(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            btnConfirm.setLayoutParams(layoutParams);
            btnConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, btnConfirmMsgSize);
            btnConfirm.setTextColor(Color.BLACK);
            btnConfirm.setBackgroundColor(Color.WHITE);
            btnConfirm.setOnClickListener(onClickListener);
            btnConfirm.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if (action == ACTION_DOWN){
                        btnConfirm.setBackgroundColor(touchColor);
                    }else if (action == ACTION_UP){
                        btnConfirm.setBackgroundColor(Color.WHITE);
                    }
                    return false;
                }
            });
        }
        btnConfirm.setText(msg);
    }

    public void setBtnCancel(View.OnClickListener onClickListener){
        String msg = "取消";
        setBtnCancel(msg,onClickListener);
    }

    public void setBtnCancel(String msg, View.OnClickListener onClickListener) {
        if (btnCancel == null) {
            btnCancel = new Button(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            btnCancel.setLayoutParams(layoutParams);
            btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, btnCancelMsgSize);
            btnCancel.setTextColor(tvGray);
            btnCancel.setBackgroundColor(Color.WHITE);
            btnCancel.setOnClickListener(onClickListener);
            btnCancel.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if (action == ACTION_DOWN){
                        btnCancel.setBackgroundColor(touchColor);
                    }else if (action == ACTION_UP){
                        btnCancel.setBackgroundColor(Color.WHITE);
                    }
                    return false;
                }
            });
        }
        btnCancel.setText(msg);
    }

    public boolean getShow(){
        return show;
    }

    @Override
    public void show() {
        super.show();
        show = true;
    }

    @Override
    public void cancel() {
        super.cancel();
        show = false;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

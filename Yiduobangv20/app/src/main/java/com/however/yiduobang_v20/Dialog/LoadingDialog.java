package com.however.yiduobang_v20.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.however.yiduobang_v20.R;
import com.however.yiduobang_v20.Utils.MyUtils;

/**
 * Created by tansibin on 2018/1/26.
 */

public class LoadingDialog extends Dialog {

    private Context context;
    //dp
    private int viewLength = 80;

    private boolean isShow = false;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        viewLength = MyUtils.dip2px(context,viewLength);
        linearLayout.setMinimumWidth(viewLength);
        linearLayout.setMinimumHeight(viewLength);
        ProgressBar progressBar = new ProgressBar(context);
        LinearLayout.LayoutParams proLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(proLayoutParams);
        linearLayout.addView(progressBar);
        getWindow().setBackgroundDrawableResource(R.drawable.dialog_loading_bg);
        setContentView(linearLayout);
        this.setCancelable(false);
    }

    /**
     * 获取LoadingDialog是否显示
     * @return
     */
    public boolean getIsShow(){
        return isShow;
    }

    @Override
    public void show() {
        super.show();
        isShow = true;
    }

    @Override
    public void cancel() {
        super.cancel();
        isShow = false;
    }
}

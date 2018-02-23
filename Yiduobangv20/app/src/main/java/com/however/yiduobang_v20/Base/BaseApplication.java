package com.however.yiduobang_v20.Base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.however.yiduobang_v20.Utils.LogUtils;
import com.however.yiduobang_v20.Utils.ToastUtils;
import com.however.yiduobang_v20.WelcomeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tansibin on 2018/1/21.
 */

public class BaseApplication extends Application {

    //activity 集合
    private static List<Activity> myActivitys = new ArrayList<>();
    private static Activity topActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        initLeanCloud();
        registerActivityListener();
    }

    /**
     * 初始化第三方包LeanCloud相关
     */
    public void initLeanCloud(){
        //leanCloud相关
        AVOSCloud.initialize(this,"m4ejpVlMAjejhBjOFsbW3SV8-gzGzoHsz","OJUEABNnW53zldAGJWd20bpm");
        PushService.setDefaultPushCallback(this, WelcomeActivity.class);
    }

    /**
     * 注册activity监听
     */
    private void registerActivityListener(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                topActivity = activity;
                ToastUtils.mContext = getApplicationContext();
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                removeActivity(activity);
            }
        });
    }

    /**
     * 添加activity到列表
     *
     * @param activity 添加的activity
     */
    public void addActivity(Activity activity) {
        if (myActivitys == null)
            return;
        myActivitys.add(activity);
    }

    /**
     * 从列表移除指定activity
     *
     * @param activity 移除的activity
     */
    public void removeActivity(Activity activity) {
        if (myActivitys == null || myActivitys.isEmpty())
            return;
        myActivitys.remove(activity);
    }

    /**
     * 根据类名从列表获取activity
     *
     * @param className 类名
     * @return 指定activity，没有则返回 null
     */
    public static Activity getActivity(String className) {
        Activity myActivity = null;
        if (myActivitys != null) {
            for (Activity activity : myActivitys)
                if (activity.getClass().getName().equals(className))
                    myActivity = activity;
        }
        return myActivity;
    }

    /**
     * 销毁指定activity
     *
     * @param activity 销毁的activity
     */
    public static void finishActivity(Activity activity) {
        if (myActivitys == null || myActivitys.isEmpty())
            return;
        if (activity != null) {
            myActivitys.remove(activity);
            activity.finish();
        }
    }

    /**
     * 销毁所有activity
     */
    public static void finishAllActivity() {
        if (myActivitys == null || myActivitys.isEmpty())
            return;
        for (Activity activity : myActivitys)
            activity.finish();
        myActivitys.clear();
    }

    /**
     * 应用在前台时，当前页面
     * @return
     */
    public static Activity getTopActivity(){
        return topActivity;
    }
}

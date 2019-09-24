package com.aotuo.h3officeplat;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

public class BaseApplication extends Application {
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        JPushInterface.init(this); // 初始化极光推送
    }

    public static BaseApplication getContext() {
        return instance;
    }
}

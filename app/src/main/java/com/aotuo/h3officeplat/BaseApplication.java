package com.aotuo.h3officeplat;

import android.app.Application;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JPushInterface.init(this); // 初始化极光推送
    }
}

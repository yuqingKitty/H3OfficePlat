package com.aotuo.h3officeplat;

import android.app.Application;
import android.os.Build;

import com.aotuo.h3officeplat.utils.LanguageUtil;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;

import cn.jpush.android.api.JPushInterface;

import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE;
import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE_ZH;

public class BaseApplication extends Application {
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        JPushInterface.init(this); // 初始化极光推送

        /**
         * 对于7.0以下，需要在Application创建的时候进行语言切换
         */
        String language = SharedPreferencesHelper.getInstance().getAppData(KEY_APP_USE_LANGUAGE, KEY_APP_USE_LANGUAGE_ZH);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            LanguageUtil.changeAppLanguage(this, language);
        }
    }

    public static BaseApplication getContext() {
        return instance;
    }
}

package com.aotuo.h3officeplat.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.Toast;

import com.aotuo.h3officeplat.utils.LanguageUtil;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;

import butterknife.ButterKnife;

import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE;
import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE_ZH;

public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayout());
        //初始化ButterKnife
        ButterKnife.bind(this);
        initView();
    }

    //设置布局
    protected abstract int getLayout();

    //初始化布局
    protected abstract void initView();

    /**
     * 跳转Activity
     *
     * @param cls
     */
    public void changeView(Class<? extends Activity> cls) {
        changeView(cls, null);
    }

    /**
     * 带数据跳转Activity
     *
     * @param cls
     * @param bd
     */
    public void changeView(Class<? extends Activity> cls, Bundle bd) {
        Intent intent = new Intent(this, cls);
        if (bd != null) {
            intent.putExtras(bd);
        }
        startActivity(intent);
    }

    public void showToast(String str) {
        if (str == null || str.trim().length() < 1)
            return;
        Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String language = SharedPreferencesHelper.getInstance().getAppData(KEY_APP_USE_LANGUAGE, KEY_APP_USE_LANGUAGE_ZH);
        //attach 对应语言环境下的context
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase, language));
    }

}

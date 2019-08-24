package com.aotuo.h3officeplat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import butterknife.ButterKnife;

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
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

}

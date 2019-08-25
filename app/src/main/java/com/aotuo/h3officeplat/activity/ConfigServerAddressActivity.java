package com.aotuo.h3officeplat.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.view.TitleView;

import butterknife.BindView;

/**
 * 配置服务器地址
 */
public class ConfigServerAddressActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_view)
    TitleView title_view;
    @BindView(R.id.et_input_server)
    EditText et_input_server;
    @BindView(R.id.tv_config_next)
    TextView tv_config_next;


    @Override
    protected int getLayout() {
        return R.layout.activity_config_server_address;
    }

    @Override
    protected void initView() {
        title_view.getBackBtn().setOnClickListener(this);
        title_view.getRightTitle().setOnClickListener(this);
        tv_config_next.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                // 跳转到登录页面

                break;
            case R.id.tv_right_title:
                // 检测

                break;
            case R.id.tv_config_next:
                // 跳转到登录页面

                break;
        }
    }

    @Override
    public void onBackPressed() {
        //点击返回按钮，跳转到登录页面

    }

}

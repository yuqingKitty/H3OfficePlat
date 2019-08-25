package com.aotuo.h3officeplat.activity;

import android.view.View;
import android.widget.TextView;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.view.TitleView;

import butterknife.BindView;

public class ServerAddressActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_view)
    TitleView title_view;
    @BindView(R.id.tv_saved_serve_address)
    TextView tv_saved_serve_address;
    @BindView(R.id.tv_check_next)
    TextView tv_check_next;


    @Override
    protected int getLayout() {
        return R.layout.activity_server_address;
    }

    @Override
    protected void initView() {
        title_view.getBackBtn().setOnClickListener(this);
        title_view.getRightTitle().setOnClickListener(this);
        tv_check_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:

                break;
            case R.id.tv_right_title:
                // 编辑
                changeView(EditServerAddressActivity.class);
                break;
            case R.id.tv_check_next:
                // 检测

                break;
        }
    }
}

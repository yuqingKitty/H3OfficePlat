package com.aotuo.h3officeplat.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.view.TitleView;

import butterknife.BindView;

public class EditServerAddressActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.title_view)
    TitleView title_view;
    @BindView(R.id.iv_delete_server)
    ImageView iv_delete_server;
    @BindView(R.id.et_serve_address)
    EditText et_serve_address;


    @Override
    protected int getLayout() {
        return R.layout.activity_edit_server_address;
    }

    @Override
    protected void initView() {
        title_view.getBackBtn().setOnClickListener(this);
        title_view.getRightTitle().setOnClickListener(this);
        iv_delete_server.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right_title:
                // 保存
                break;
            case R.id.iv_delete_server:
                et_serve_address.setText("");
                break;
        }
    }
}

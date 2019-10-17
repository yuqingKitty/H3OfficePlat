package com.aotuo.h3officeplat.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.utils.CommonTools;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;
import com.aotuo.h3officeplat.view.CommonDialog;
import com.aotuo.h3officeplat.view.TitleView;

import butterknife.BindView;

/**
 * 设置-服务地址-编辑
 */
public class EditServerAddressActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.iv_net_back)
    ImageView iv_net_back;
    @BindView(R.id.ll_no_net)
    LinearLayout ll_no_net;
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
        if (CommonTools.isNetWorkConnected(this)) {
            ll_no_net.setVisibility(View.GONE);
        } else {
            ll_no_net.setVisibility(View.VISIBLE);
        }
        title_view.getBackBtn().setOnClickListener(this);
        title_view.getRightTitle().setOnClickListener(this);
        iv_delete_server.setOnClickListener(this);
        et_serve_address.addTextChangedListener(mTextWatcher);
        iv_net_back.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String serverAddress = SharedPreferencesHelper.getInstance().getAppData(SharedPreferencesHelper.KEY_APP_SERVER_ADDRESS, "");
        et_serve_address.setText(serverAddress);
        et_serve_address.setSelection(serverAddress.length());
        if (!TextUtils.isEmpty(serverAddress)) {
            iv_delete_server.setVisibility(View.VISIBLE);
        } else {
            iv_delete_server.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_net_back:
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right_title:
                // 保存
                String serverAddress = et_serve_address.getText().toString();
                if (TextUtils.isEmpty(serverAddress)) {
                    new CommonDialog(this, "", getResources().getString(R.string.server_address_is_empty), getResources().getString(R.string.know_it));
                } else if (serverAddress.startsWith("http:") || serverAddress.startsWith("https:")) {
                    SharedPreferencesHelper.getInstance().setAppData(SharedPreferencesHelper.KEY_APP_SERVER_ADDRESS, serverAddress);
                    finish();
                } else {
                    showToast(getString(R.string.server_address_format_error));
                }
                break;
            case R.id.iv_delete_server:
                et_serve_address.setText("");
                break;
        }
    }

    TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(et_serve_address.getText().toString().trim())) {
                iv_delete_server.setVisibility(View.INVISIBLE);
            } else {
                iv_delete_server.setVisibility(View.VISIBLE);
            }
        }
    };

}

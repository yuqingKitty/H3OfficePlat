package com.aotuo.h3officeplat.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.utils.CommonTools;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;
import com.aotuo.h3officeplat.view.TitleView;

import butterknife.BindView;

/**
 * 配置服务器地址
 */
public class ConfigServerAddressActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.iv_net_back)
    ImageView iv_net_back;
    @BindView(R.id.ll_no_net)
    LinearLayout ll_no_net;
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
        if (CommonTools.isNetWorkConnected(this)) {
            ll_no_net.setVisibility(View.GONE);
        } else {
            ll_no_net.setVisibility(View.VISIBLE);
        }
        title_view.getBackBtn().setOnClickListener(this);
        title_view.getRightTitle().setOnClickListener(this);
        tv_config_next.setOnClickListener(this);
        iv_net_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back || view.getId() == R.id.iv_net_back) {
            finish();
        } else {
            String serverAddress = et_input_server.getText().toString();
            if (TextUtils.isEmpty(serverAddress)) {
                showToast(getString(R.string.server_address_is_empty));
            } else {
                if (serverAddress.startsWith("http:") || serverAddress.startsWith("https:")) {
                    SharedPreferencesHelper.getInstance().setAppData(SharedPreferencesHelper.KEY_APP_SERVER_ADDRESS, serverAddress);
                    switch (view.getId()) {
                        case R.id.tv_config_next:
                            // 跳转H5
                            changeView(WebViewActivity.class);
                            break;
                        case R.id.tv_right_title:
                            // 检测
                            Uri uri = Uri.parse(serverAddress);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                            break;
                    }
                } else {
                    showToast(getString(R.string.server_address_format_error));
                }
            }
        }
    }

}

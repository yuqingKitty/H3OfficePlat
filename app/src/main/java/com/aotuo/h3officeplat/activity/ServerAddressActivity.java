package com.aotuo.h3officeplat.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.utils.CommonTools;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;
import com.aotuo.h3officeplat.view.TitleView;

import butterknife.BindView;

/**
 * 设置-服务地址
 */
public class ServerAddressActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.iv_net_back)
    ImageView iv_net_back;
    @BindView(R.id.ll_no_net)
    LinearLayout ll_no_net;
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
        if (CommonTools.isNetWorkConnected(this)) {
            ll_no_net.setVisibility(View.GONE);
        } else {
            ll_no_net.setVisibility(View.VISIBLE);
        }
        title_view.getBackBtn().setOnClickListener(this);
        title_view.getRightTitle().setOnClickListener(this);
        tv_check_next.setOnClickListener(this);
        iv_net_back.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String serverAddress = SharedPreferencesHelper.getInstance().getAppData(SharedPreferencesHelper.KEY_APP_SERVER_ADDRESS, "");
        tv_saved_serve_address.setText(serverAddress);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_net_back:
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right_title:
                // 编辑
                changeView(EditServerAddressActivity.class);
                break;
            case R.id.tv_check_next:
                // 检测
                String serverAddress = tv_saved_serve_address.getText().toString();
                if (TextUtils.isEmpty(serverAddress)) {
                    showToast(getString(R.string.server_address_is_empty));
                } else if (serverAddress.startsWith("http:") || serverAddress.startsWith("https:")) {
                    Uri uri = Uri.parse(serverAddress);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else {
                    showToast(getString(R.string.server_address_format_error));
                }
                break;
        }
    }
}

package com.aotuo.h3officeplat.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;
import com.aotuo.h3officeplat.view.TitleView;

import butterknife.BindView;
import butterknife.OnClick;

import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE;
import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE_ZH;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity {
    @BindView(R.id.title_view)
    TitleView title_view;
    @BindView(R.id.tv_serve_address)
    TextView tv_serve_address;
    @BindView(R.id.tv_current_language)
    TextView tv_current_language;
    @BindView(R.id.tv_current_version)
    TextView tv_current_version;


    @Override
    protected int getLayout() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        tv_current_version.setText(getVersionName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String serverAddress = SharedPreferencesHelper.getInstance().getAppData(SharedPreferencesHelper.KEY_APP_SERVER_ADDRESS, "");
        tv_serve_address.setText(serverAddress);
        String language = SharedPreferencesHelper.getInstance().getAppData(KEY_APP_USE_LANGUAGE, KEY_APP_USE_LANGUAGE_ZH);
        tv_current_language.setText(language);
    }

    private String getVersionName() {
        String versionName = "";
        try {
            PackageManager pm = getPackageManager();
            PackageInfo packInfo = pm.getPackageInfo(getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    @OnClick({R.id.tv_serve_address, R.id.tv_current_language})
    void click(View view) {
        switch (view.getId()) {
            case R.id.tv_serve_address:
                // 服务地址
                changeView(ServerAddressActivity.class);
                break;
            case R.id.tv_current_language:
                // 多语言
                changeView(MoreLanguageActivity.class);
                break;
        }
    }
}

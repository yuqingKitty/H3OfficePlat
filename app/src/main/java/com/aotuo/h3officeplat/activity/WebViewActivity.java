package com.aotuo.h3officeplat.activity;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.bean.MessageEvent;
import com.aotuo.h3officeplat.utils.LanguageUtil;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;
import com.aotuo.h3officeplat.view.CommonDialog;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE;
import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE_EN;
import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE_ZH;

public class WebViewActivity extends BaseActivity {
    @BindView(R.id.bridge_webview)
    BridgeWebView bridgeWebView;
    @BindView(R.id.iv_setting)
    ImageView iv_setting;

    private Context mContext;

    @Override
    protected int getLayout() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        mContext = this;
        initWebView();
        String loadUrl = SharedPreferencesHelper.getInstance().getAppData(SharedPreferencesHelper.KEY_APP_SERVER_ADDRESS, "");
        bridgeWebView.loadUrl(loadUrl);
        EventBus.getDefault().register(this);
    }

    private void initWebView() {
        bridgeWebView.setWebContentsDebuggingEnabled(true);
        bridgeWebView.getSettings().setDefaultFontSize(16);
        bridgeWebView.getSettings().setTextZoom(100);
        bridgeWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        bridgeWebView.getSettings().setJavaScriptEnabled(true);
        bridgeWebView.getSettings().setUserAgentString(bridgeWebView.getSettings().getUserAgentString() + " " + "h3officeplat");
        bridgeWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        bridgeWebView.getSettings().setDomStorageEnabled(true);  // 设置可以使用localStorage
        bridgeWebView.getSettings().setLoadsImagesAutomatically(true);
        bridgeWebView.getSettings().setBlockNetworkImage(false);
        bridgeWebView.getSettings().setDatabaseEnabled(true);   // 应用可以有数据库
        bridgeWebView.getSettings().setAppCacheEnabled(true);   // 应用可以有缓存
        bridgeWebView.setDefaultHandler(new DefaultHandler());
        // Register a Java handler function so that js can call(JS调用Android，Android接收数据)
        bridgeWebView.registerHandler("submitFromWeb", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    JSONObject paramJson = new JSONObject(data);
                    String param = paramJson.getString("param");
                    switch (param) {
                        case "JIGUANG":
                            function.onCallBack(JPushInterface.getRegistrationID(mContext)); //回传极光ID数据给js
                            break;
                        case "SETTING_SHOW":
                            iv_setting.setVisibility(View.VISIBLE);
                            break;
                        case "SETTING_HIDE":
                            iv_setting.setVisibility(View.GONE);
                            break;
                        case "OPEN_CAMERA":
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                int storePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                int cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
                                if (storePermission != PackageManager.PERMISSION_GRANTED || cameraPermission != PackageManager.PERMISSION_GRANTED) {
                                    new CommonDialog(mContext, getResources().getString(R.string.access_request), getResources().getString(R.string.grant_camera_permission), getResources().getString(R.string.confirm));
                                }
                            }
                            break;
                        case "LANGUAGE_ZH":
                            SharedPreferencesHelper.getInstance().setAppData(KEY_APP_USE_LANGUAGE, KEY_APP_USE_LANGUAGE_ZH);
                            changeLanguage();
                            break;
                        case "LANGUAGE_EN":
                            SharedPreferencesHelper.getInstance().setAppData(KEY_APP_USE_LANGUAGE, KEY_APP_USE_LANGUAGE_EN);
                            changeLanguage();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        bridgeWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (bridgeWebView != null && bridgeWebView.canGoBack()) {
            // 防止重定位，无法正确返回，goBack两次
            bridgeWebView.goBack();
            bridgeWebView.goBack();
        } else {
            super.onBackPressed();
            finish();
        }
    }


    @OnClick({R.id.iv_setting})
    void click(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                changeView(SettingActivity.class);
                break;
        }
    }


    /**
     * 如果是7.0以下，我们需要调用changeAppLanguage方法
     * 如果是7.0及以上系统，直接把我们想要切换的语言类型保存在SharedPreferences中,然后重新启动SettingActivity即可
     */
    private void changeLanguage() {
        String language = SharedPreferencesHelper.getInstance().getAppData(KEY_APP_USE_LANGUAGE, KEY_APP_USE_LANGUAGE_ZH);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            LanguageUtil.changeAppLanguage(this, language);
        }
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEventMessage(MessageEvent messageEvent){
        // Register a JavaScript handler function so that Java can call(Android调用JS，Android发送数据)
        bridgeWebView.callHandler("functionInJs", messageEvent.getMessage(), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}

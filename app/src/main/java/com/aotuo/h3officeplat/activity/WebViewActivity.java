package com.aotuo.h3officeplat.activity;


import android.content.Context;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class WebViewActivity extends BaseActivity {
    @BindView(R.id.bridge_webview)
    BridgeWebView bridgeWebView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
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
    }

    private void initWebView() {
        bridgeWebView.setWebContentsDebuggingEnabled(true);
        bridgeWebView.getSettings().setJavaScriptEnabled(true);
        bridgeWebView.getSettings().setUserAgentString(bridgeWebView.getSettings().getUserAgentString() + " " + "h3officeplat");
        bridgeWebView.setDefaultHandler(new DefaultHandler());
        // Register a Java handler function so that js can call(JS调用Android，Android接收数据)
        bridgeWebView.registerHandler("submitFromWeb", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                try {
                    JSONObject paramJson = new JSONObject(data);
                    String param = paramJson.getString("param");
                    String msg = "android 接收到js的数据：" + param;
                    showToast(msg);
                    switch (param) {
                        case "GIGUANG":
                            function.onCallBack(JPushInterface.getRegistrationID(mContext)); //回传数据给js
                            break;
                        case "SETTING_SHOW":
                            iv_setting.setVisibility(View.VISIBLE);
                            break;
                        case "SETTING_HIDE":
                            iv_setting.setVisibility(View.GONE);
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
                progressBar.setProgress(newProgress);
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
//                // Register a JavaScript handler function so that Java can call(Android调用JS，Android发送数据)
//                bridgeWebView.callHandler("functionInJs", "android send " + JPushInterface.getRegistrationID(mContext), new CallBackFunction() {
//                    @Override
//                    public void onCallBack(String data) {
//                        showToast(data);
//                    }
//                });
                break;
        }
    }


}

package com.aotuo.h3officeplat.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;

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
        bridgeWebView.setDownloadListener(new MyDownLoadListener(this));   // 下载响应
        bridgeWebView.setDefaultHandler(new DefaultHandler());
        // Register a Java handler function so that js can call(JS调用Android，Android接收数据)
        bridgeWebView.registerHandler("submitFromWeb", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                String msg = "android 接收到js的数据：" + data;
                showToast(msg);
                function.onCallBack("android接收完毕，并可回传数据给js"); //回传数据给js
            }
        });

        // Register a JavaScript handler function so that Java can call(Android调用JS，Android发送数据)
        bridgeWebView.callHandler("functionInJs", "android send " + JPushInterface.getRegistrationID(mContext), new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
                showToast(data);
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
        bridgeWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            /**
             * 通知主机应用程序加载资源时发生SSL错误。它要执行proceed函数
             * @param view
             * @param handler
             * @param error
             */
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
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

    class MyDownLoadListener implements DownloadListener {
        private Context context;

        public MyDownLoadListener(Context context) {
            this.context = context;
        }

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
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


}

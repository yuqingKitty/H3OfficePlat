package com.aotuo.h3officeplat.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.aotuo.h3officeplat.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class WebViewActivity extends BaseActivity {
    public static final String URL_KEY = "url";
    public static final String URL_H5_LOGIN = "http://xyz.h3bpm.com:8085/Portal/Mobile/#/login";

    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.iv_setting)
    ImageView iv_setting;

    private String loadUrl;
    private Handler mHandler;
    private Map<String, String> mCallBackCacheMap;  // JS事件回调接口缓存

    @Override
    protected int getLayout() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        mHandler = new Handler(Looper.getMainLooper());
        mCallBackCacheMap = new HashMap<>();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            loadUrl = bundle.getString(URL_KEY);
        }
        initWebView();
        mWebView.loadUrl(loadUrl);
    }

    private void initWebView() {
        mWebView.setWebContentsDebuggingEnabled(true);
        mWebView.getSettings().setDefaultFontSize(16);
        mWebView.getSettings().setTextZoom(100);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString() + " " + "h3officeplat");
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 设置可以使用localStorage
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setBlockNetworkImage(false);
        mWebView.getSettings().setSavePassword(false);
        mWebView.getSettings().setUserAgentString(mWebView.getSettings().getUserAgentString());
        mWebView.getSettings().setDatabaseEnabled(true);  // 应用可以有数据库
        mWebView.getSettings().setAppCacheEnabled(true);  // 应用可以有缓存
        mWebView.setDownloadListener(new MyDownLoadListener(this));  // 下载响应
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "WebViewJavascriptBridge");
        mWebView.addJavascriptInterface(new Object() {

            @JavascriptInterface
            public void closeWindow() {
                finish();
            }
        }, "MyHandler");


        mWebView.setWebChromeClient(new WebChromeClient() {
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
        mWebView.setWebViewClient(new WebViewClient() {
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
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
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

    // js本地接口
    class MyJavaScriptInterface {
        /**
         * @param eventCode      JS请求native处理的功能编号
         * @param paramString    json格式化的字符串参数
         * @param callbackString native需回调的JS方法（没有返回则不需要回调）
         */
        @JavascriptInterface
        public void callHandler(final String eventCode, final String paramString, String callbackString) {
            mCallBackCacheMap.put(eventCode, callbackString);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    nativeExecute(eventCode, paramString);
                }
            });
        }
    }

    private void nativeExecute(String eventCode, String paramString) {

    }


    /**
     * WebView回调JS方法
     */
    private void callJsHandle(String eventCode, JSONObject param) {
        String method = mCallBackCacheMap.get(eventCode);
        if (method == null || "".equals(method)) {
            return;
        }

        // JSWEBVIEW　html直接传递可执行方法体  V4.0.5版本之后加入兼容
        if (method.contains("{")) {
            if (param == null) {
                mWebView.loadUrl("javascript:(" + method + "())");
            } else {
                mWebView.loadUrl("javascript:(" + method + "('" + param.toString() + "'))");
            }
            return;
        }
        Log.i("JsWebView", "eventCode:" + eventCode + "   method:" + method + "  param:" + param);
        try {
            if (param == null) {
                mWebView.loadUrl("javascript:" + method + "()");
            } else {
                mWebView.loadUrl("javascript:" + method + "('" + param.toString() + "')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            // 防止重定位，无法正确返回，goBack两次
            mWebView.goBack();
            mWebView.goBack();
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

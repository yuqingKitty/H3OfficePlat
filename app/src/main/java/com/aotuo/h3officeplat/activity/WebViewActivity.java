package com.aotuo.h3officeplat.activity;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.bean.MessageEvent;
import com.aotuo.h3officeplat.utils.CommonTools;
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
    @BindView(R.id.ll_no_net)
    LinearLayout ll_no_net;

    private Context mContext;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 2;

    @Override
    protected int getLayout() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        if (CommonTools.isNetWorkConnected(this)){
            ll_no_net.setVisibility(View.GONE);
        } else {
            ll_no_net.setVisibility(View.VISIBLE);
        }
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

            // For 3.0+ Devices (Start)
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            // For Lollipop 5.0+ Devices
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                uploadMessage = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(getBaseContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            //for Android <3.0
            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(getBaseContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
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


    @OnClick({R.id.iv_net_back, R.id.iv_setting})
    void click(View view) {
        switch (view.getId()) {
            case R.id.iv_net_back:
                finish();
                break;
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
    public void onEventMessage(MessageEvent messageEvent) {
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

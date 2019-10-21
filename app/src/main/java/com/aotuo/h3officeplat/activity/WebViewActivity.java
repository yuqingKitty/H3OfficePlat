package com.aotuo.h3officeplat.activity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aotuo.h3officeplat.R;
import com.aotuo.h3officeplat.bean.MessageEvent;
import com.aotuo.h3officeplat.utils.CommonTools;
import com.aotuo.h3officeplat.utils.LanguageUtil;
import com.aotuo.h3officeplat.utils.SharedPreferencesHelper;
import com.aotuo.h3officeplat.view.CommonDialog;
import com.aotuo.h3officeplat.view.TitleView;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE;
import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE_EN;
import static com.aotuo.h3officeplat.utils.SharedPreferencesHelper.KEY_APP_USE_LANGUAGE_ZH;

public class WebViewActivity extends BaseActivity {
    @BindView(R.id.ll_no_net)
    LinearLayout ll_no_net;
    @BindView(R.id.title_view)
    TitleView title_view;
    @BindView(R.id.bridge_webview)
    BridgeWebView bridgeWebView;
    @BindView(R.id.iv_setting)
    ImageView iv_setting;

    private Context mContext;
    private android.webkit.ValueCallback<Uri[]> mUploadCallbackAboveL;
    private android.webkit.ValueCallback<Uri> mUploadCallbackBelow;
    private Uri imageUri;
    private int REQUEST_CODE = 1234;
    private boolean isChangedUrl = false;

    @Override
    protected int getLayout() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        if (CommonTools.isNetWorkConnected(this)) {
            ll_no_net.setVisibility(View.GONE);
        } else {
            ll_no_net.setVisibility(View.VISIBLE);
            title_view.setVisibility(View.GONE);
        }
        if (getIntent() != null && getIntent().getExtras() != null){
            title_view.setVisibility(View.VISIBLE);
        } else {
            title_view.setVisibility(View.GONE);
        }
        mContext = this;
        initWebView();
        String loadUrl = SharedPreferencesHelper.getInstance().getAppData(SharedPreferencesHelper.KEY_APP_SERVER_ADDRESS, "");
        bridgeWebView.loadUrl(loadUrl);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isChangedUrl) {
            String loadUrl = SharedPreferencesHelper.getInstance().getAppData(SharedPreferencesHelper.KEY_APP_SERVER_ADDRESS, "");
            bridgeWebView.loadUrl(loadUrl);
            isChangedUrl = false;
        }
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
        bridgeWebView.setDownloadListener(new MyDownLoadListener(this));  // 下载响应
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

            /**
             * // For Android < 3.0
             */
            private void openFileChooser(ValueCallback<Uri> uploadMsg) {
                // (2)该方法回调时说明版本API < 21，此时将结果赋值给 mUploadCallbackBelow，使之 != null
                mUploadCallbackBelow = uploadMsg;
                showFileChooser();
            }

            /**
             * // For Android >= 3.0
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                // 这里我们就不区分input的参数了，直接用拍照
                mUploadCallbackBelow = uploadMsg;
                showFileChooser();
            }

            /**
             * //For Android  >= 4.1
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                // 这里我们就不区分input的参数了，直接用拍照
                mUploadCallbackBelow = uploadMsg;
                showFileChooser();
            }

            /**
             * //For Android  >= 5.0
             */
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
                // (1)该方法回调时说明版本API >= 21，此时将结果赋值给 mUploadCallbackAboveL，使之 != null
                mUploadCallbackAboveL = valueCallback;
                showFileChooser();
                return true;
            }
        });
    }

    /**
     * 调用相机
     */
    private void showFileChooser() {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        fileIntent.setType("*/*");

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定拍照存储位置的方式调起相机
        String filePath = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES + File.separator;
        String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        imageUri = Uri.fromFile(new File(filePath + fileName));
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);


        Intent Photo = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooserIntent = Intent.createChooser(Photo, "");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent, fileIntent});
        startActivityForResult(chooserIntent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            // 经过上边(1)、(2)两个赋值操作，此处即可根据其值是否为空来决定采用哪种处理方法
            if (mUploadCallbackBelow != null) {
                chooseBelow(resultCode, data);
            } else if (mUploadCallbackAboveL != null) {
                chooseAbove(resultCode, data);
            } else {
                showToast("发生错误");
            }
        }
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


    @OnClick({R.id.iv_net_back, R.id.iv_back, R.id.iv_setting})
    void click(View view) {
        switch (view.getId()) {
            case R.id.iv_net_back:
            case R.id.iv_back:
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
        if (messageEvent.getMessage().equals("EDIT_URL_CHANGED")) {
            isChangedUrl = true;
        } else {
            // Register a JavaScript handler function so that Java can call(Android调用JS，Android发送数据)
            bridgeWebView.callHandler("functionInJs", messageEvent.getMessage(), new CallBackFunction() {
                @Override
                public void onCallBack(String data) {
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Android API < 21(Android 5.0)版本的回调处理
     *
     * @param resultCode 选取文件或拍照的返回码
     * @param data       选取文件或拍照的返回结果
     */
    private void chooseBelow(int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // 这里是针对文件路径处理
                Uri uri = data.getData();
                if (uri != null) {
                    mUploadCallbackBelow.onReceiveValue(uri);
                } else {
                    mUploadCallbackBelow.onReceiveValue(null);
                }
            } else {
                // 以指定图像存储路径的方式调起相机，成功后返回data为空
                mUploadCallbackBelow.onReceiveValue(imageUri);
            }
        } else {
            mUploadCallbackBelow.onReceiveValue(null);
        }
        mUploadCallbackBelow = null;
    }

    /**
     * Android API >= 21(Android 5.0) 版本的回调处理
     *
     * @param resultCode 选取文件或拍照的返回码
     * @param data       选取文件或拍照的返回结果
     */
    private void chooseAbove(int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // 这里是针对从文件中选图片的处理
                Uri[] results;
                Uri uriData = data.getData();
                if (uriData != null) {
                    results = new Uri[]{uriData};
                    mUploadCallbackAboveL.onReceiveValue(results);
                } else {
                    mUploadCallbackAboveL.onReceiveValue(null);
                }
            } else {
                mUploadCallbackAboveL.onReceiveValue(new Uri[]{imageUri});
            }
        } else {
            mUploadCallbackAboveL.onReceiveValue(null);
        }
        mUploadCallbackAboveL = null;
    }

    private void updatePhotos() {
        // 该广播即使多发（即选取照片成功时也发送）也没有关系，只是唤醒系统刷新媒体文件
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageUri);
        sendBroadcast(intent);
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

}
